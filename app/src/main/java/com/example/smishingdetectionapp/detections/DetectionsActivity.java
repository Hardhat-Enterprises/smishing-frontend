package com.example.smishingdetectionapp.detections;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    private DatabaseAccess databaseAccess;
    private DetectionsAdapter adapter;
    private CheckBox selectAllCheckbox;
    private Button deleteSelectedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detections);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Back button
        ImageButton back = findViewById(R.id.detections_back);
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Open DB and set up adapter
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        detectionLV = findViewById(R.id.lvDetectionsList);
        adapter = new DetectionsAdapter(this, databaseAccess.getAllDetections());
        detectionLV.setAdapter(adapter);

        // Select All / Delete Selected
        selectAllCheckbox = findViewById(R.id.selectAllCheckbox);
        deleteSelectedBtn    = findViewById(R.id.deleteSelectedBtn);

        selectAllCheckbox.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) adapter.selectAll();
            else          adapter.clearSelection();
        });

        deleteSelectedBtn.setOnClickListener(v -> {
            if (adapter.getSelectedIds().isEmpty()) {
                Toast.makeText(this, "No detections selected", Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete the selected detections?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dlg, which) -> {
                        for (Integer id : adapter.getSelectedIds()) {
                            databaseAccess.DeleteRow(String.valueOf(id));
                        }
                        adapter.clearSelection();
                        selectAllCheckbox.setChecked(false);
                        adapter.changeCursor(databaseAccess.getAllDetections());
                        Toast.makeText(this, "Deleted selected detections!", Toast.LENGTH_SHORT).show();
                    })
                    .create();

            dialog.show();

            Button cancelBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            if (cancelBtn != null) {
                cancelBtn.setTextColor(Color.BLACK);
            }

            Button deleteBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (deleteBtn != null) {
                deleteBtn.setTextColor(Color.RED);
            }
        });

        // Search
        EditText detSearch = findViewById(R.id.searchTextBox);
        detSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            @Override public void afterTextChanged(Editable s){}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                String q = "%" + s.toString() + "%";
                Cursor cur = DatabaseAccess.db.rawQuery(
                        "SELECT * FROM Detections WHERE Phone_Number LIKE ? OR Message LIKE ? OR Date LIKE ? ORDER BY Date DESC",
                        new String[]{q,q,q});
                adapter.changeCursor(cur);
            }
        });

        // Filter popup (unchanged)
        SharedPreferences prefs = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            View sheet = getLayoutInflater().inflate(R.layout.popup_filter, null);
            BottomSheetDialog dlg = new BottomSheetDialog(this);
            dlg.setContentView(sheet);
            dlg.show();

            RadioButton oldToNew = sheet.findViewById(R.id.OldToNewRB);
            RadioButton newToOld = sheet.findViewById(R.id.NewToOldRB);

            oldToNew.setChecked(prefs.getBoolean("OldToNewRB", false));
            newToOld.setChecked(prefs.getBoolean("NewToOldRB", false));

            oldToNew.setOnCheckedChangeListener((bView, checked) -> {
                if (checked) {
                    newToOld.setChecked(false);
                    sortONDB();
                }
                prefs.edit().putBoolean("OldToNewRB", checked).apply();
            });
            newToOld.setOnCheckedChangeListener((bView, checked) -> {
                if (checked) {
                    oldToNew.setChecked(false);
                    sortNODB();
                }
                prefs.edit().putBoolean("NewToOldRB", checked).apply();
            });
        });

        // Single‑item delete on long‑press with confirmation
        detectionLV.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this detection?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dlg, which) -> {
                        databaseAccess.DeleteRow(String.valueOf(id));
                        refreshList();
                        Toast.makeText(this, "Detection Deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .show();
            return true;
        });
    }

    // Refresh / search / sort helpers
    public void searchDB(String search) {
        String q = "%" + search + "%";
        Cursor c = DatabaseAccess.db.rawQuery(
                "SELECT * FROM Detections WHERE Phone_Number LIKE ? OR Message LIKE ? OR Date LIKE ? ORDER BY Date DESC",
                new String[]{q,q,q});
        adapter.changeCursor(c);
    }

    public void sortONDB() {
        Cursor c = DatabaseAccess.db.rawQuery(
                "SELECT * FROM Detections ORDER BY Date ASC", null);
        adapter.changeCursor(c);
    }

    public void sortNODB() {
        Cursor c = DatabaseAccess.db.rawQuery(
                "SELECT * FROM Detections ORDER BY Date DESC", null);
        adapter.changeCursor(c);
    }

    public void refreshList() {
        adapter.changeCursor(databaseAccess.getAllDetections());
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSharedPreferences("RadioPrefs", MODE_PRIVATE).edit().clear().apply();
    }
}