package com.example.smishingdetectionapp.detections;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    DatabaseAccess databaseAccess;

    public void searchDB(String search) {
        String searchQuery = "SELECT * FROM Detections WHERE Phone_Number LIKE '%" + search + "%' OR Message LIKE '%" + search + "%' OR Date LIKE '%" + search + "%'";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void refreshList() {
        Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections", null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void DeleteRow(String id) {
        DatabaseAccess.db.delete("Detections", "_id = ?", new String[]{id});
    }

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

        ImageButton detections_back = findViewById(R.id.detections_back);
        detections_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        detectionLV = findViewById(R.id.lvDetectionsList);
        databaseAccess = new DatabaseAccess(getApplicationContext());
        databaseAccess.open();

        refreshList();

        EditText detSearch = findViewById(R.id.searchTextBox);
        detSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDB(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            SmartFilterBottomSheet filterFragment = new SmartFilterBottomSheet();
            filterFragment.setFilterListener((newestFirst, containsLink, todayOnly, last7DaysOnly, selectedYears, startDate, endDate) -> {
                StringBuilder query = new StringBuilder("SELECT * FROM Detections");
                boolean hasCondition = false;

                // Contains Link
                if (containsLink) {
                    query.append(" WHERE (Message LIKE '%http%' OR Message LIKE '%www%')");
                    hasCondition = true;
                }

                // Today Only
                if (todayOnly) {
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    if (!hasCondition) {
                        query.append(" WHERE Date LIKE '").append(today).append("%'");
                    } else {
                        query.append(" AND Date LIKE '").append(today).append("%'");
                    }
                    hasCondition = true;
                }

                // Last 7 Days
                if (last7DaysOnly) {
                    long sevenDaysAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String sevenDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(sevenDaysAgoMillis));

                    if (!hasCondition) {
                        query.append(" WHERE Date BETWEEN '").append(sevenDaysAgo).append("' AND '").append(today).append("'");
                    } else {
                        query.append(" AND Date BETWEEN '").append(sevenDaysAgo).append("' AND '").append(today).append("'");
                    }
                    hasCondition = true;
                }

                // Custom Date Range
                if (startDate != null && endDate != null) {
                    if (!hasCondition) {
                        query.append(" WHERE Date BETWEEN '").append(startDate).append("' AND '").append(endDate).append("'");
                    } else {
                        query.append(" AND Date BETWEEN '").append(startDate).append("' AND '").append(endDate).append("'");
                    }
                    hasCondition = true;
                }

                // Filter by Year
                if (!selectedYears.isEmpty()) {
                    StringBuilder yearCondition = new StringBuilder();
                    for (int i = 0; i < selectedYears.size(); i++) {
                        if (i > 0) yearCondition.append(" OR ");
                        yearCondition.append("SUBSTR(Date, 1, 4) = '").append(selectedYears.get(i)).append("'");
                    }

                    if (!hasCondition) {
                        query.append(" WHERE (").append(yearCondition).append(")");
                    } else {
                        query.append(" AND (").append(yearCondition).append(")");
                    }
                }

                // Sort Order
                query.append(newestFirst ? " ORDER BY Date DESC" : " ORDER BY Date ASC");

                // Apply and refresh list
                Cursor filteredCursor = DatabaseAccess.db.rawQuery(query.toString(), null);
                DisplayDataAdapterView filteredAdapter = new DisplayDataAdapterView(this, filteredCursor);
                detectionLV.setAdapter(filteredAdapter);
                filteredAdapter.notifyDataSetChanged();
            });

            filterFragment.show(getSupportFragmentManager(), filterFragment.getTag());
        });

        detectionLV.setOnItemLongClickListener((parent, view, position, id) -> {
            View bottomSheetDel = getLayoutInflater().inflate(R.layout.popup_deleteitem, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheetDel);
            bottomSheetDialog.show();

            Button cancel = bottomSheetDel.findViewById(R.id.delItemCancel);
            Button confirm = bottomSheetDel.findViewById(R.id.DelItemConfirm);

            cancel.setOnClickListener(v1 -> bottomSheetDialog.dismiss());
            confirm.setOnClickListener(v12 -> {
                DeleteRow(String.valueOf(id));
                refreshList();
                bottomSheetDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Detection Deleted!", Toast.LENGTH_SHORT).show();
            });

            return true;
        });
    }
}
