package com.example.smishingdetectionapp.detections;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button
        ImageButton detectionsBack = findViewById(R.id.detections_back);
        detectionsBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Open database
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        // ListView + custom adapter
        detectionLV = findViewById(R.id.lvDetectionsList);
        adapter = new DetectionsAdapter(this, databaseAccess.getAllDetections());
        detectionLV.setAdapter(adapter);

        // Select All / Delete Selected UI
        selectAllCheckbox = findViewById(R.id.selectAllCheckbox);
        deleteSelectedBtn    = findViewById(R.id.deleteSelectedBtn);

        selectAllCheckbox.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) adapter.selectAll();
            else adapter.clearSelection();
        });

        deleteSelectedBtn.setOnClickListener(v -> {
            for (Integer id : adapter.getSelectedIds()) {
                databaseAccess.DeleteRow(String.valueOf(id));
            }
            adapter.clearSelection();
            selectAllCheckbox.setChecked(false);
            adapter.changeCursor(databaseAccess.getAllDetections());
            Toast.makeText(this, "Deleted selected detections!", Toast.LENGTH_SHORT).show();
        });

        // Search box
        EditText detSearch = findViewById(R.id.searchTextBox);
        detSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString();
                String sql = "SELECT * FROM Detections WHERE Phone_Number LIKE ? OR Message LIKE ? OR Date LIKE ? ORDER BY Date DESC";
                Cursor cursor = DatabaseAccess.db.rawQuery(sql, new String[]{"%"+q+"%","%"+q+"%","%"+q+"%"});
                adapter.changeCursor(cursor);
            }
        });

        // Filter popup (unchanged)
        SharedPreferences prefs = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            View bottomSheet = getLayoutInflater().inflate(R.layout.popup_filter, null);
            BottomSheetDialog dialog = new BottomSheetDialog(DetectionsActivity.this);
            dialog.setContentView(bottomSheet);
            dialog.show();

            RadioButton oldToNew = bottomSheet.findViewById(R.id.OldToNewRB);
            RadioButton newToOld = bottomSheet.findViewById(R.id.NewToOldRB);

            oldToNew.setChecked(prefs.getBoolean("OldToNewRB", false));
            newToOld.setChecked(prefs.getBoolean("NewToOldRB", false));

            oldToNew.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (oldToNew.isChecked()) {
                    newToOld.setChecked(false);
                    sortONDB();
                }
                prefs.edit().putBoolean("OldToNewRB", isChecked).apply();
            });
            newToOld.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (newToOld.isChecked()) {
                    oldToNew.setChecked(false);
                    sortNODB();
                }
                prefs.edit().putBoolean("NewToOldRB", isChecked).apply();
            });
        });

        // Long‑press delete (unchanged)
        detectionLV.setOnItemLongClickListener((parent, view, position, id) -> {
            View bottom = getLayoutInflater().inflate(R.layout.popup_deleteitem, null);
            BottomSheetDialog dlg = new BottomSheetDialog(DetectionsActivity.this);
            dlg.setContentView(bottom);
            dlg.show();

            Button cancel = bottom.findViewById(R.id.delItemCancel);
            Button confirm = bottom.findViewById(R.id.DelItemConfirm);

            cancel.setOnClickListener(v -> dlg.dismiss());
            confirm.setOnClickListener(v -> {
                databaseAccess.DeleteRow(String.valueOf(id));
                refreshList();
                dlg.dismiss();
                Toast.makeText(getApplicationContext(), "Detection Deleted!", Toast.LENGTH_SHORT).show();
            });

            return true;
        });
    }

    // Search / sort / refresh now drive the same adapter:
    public void searchDB(String search) {
        String q = "%" + search + "%";
        String sql = "SELECT * FROM Detections WHERE Phone_Number LIKE ? OR Message LIKE ? OR Date LIKE ? ORDER BY Date DESC";
        Cursor c = DatabaseAccess.db.rawQuery(sql, new String[]{q, q, q});
        adapter.changeCursor(c);
    }

    public void sortONDB() {
        Cursor c = DatabaseAccess.db.rawQuery("SELECT * FROM Detections ORDER BY Date ASC", null);
        adapter.changeCursor(c);
    }

    public void sortNODB() {
        Cursor c = DatabaseAccess.db.rawQuery("SELECT * FROM Detections ORDER BY Date DESC", null);
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