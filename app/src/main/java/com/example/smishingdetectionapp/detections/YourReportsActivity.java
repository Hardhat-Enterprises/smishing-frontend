package com.example.smishingdetectionapp.detections;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

import androidx.appcompat.app.AppCompatActivity;

public class YourReportsActivity extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private DatabaseAccess databaseAccess;
    private ReportsAdapter adapter;

    private EditText searchBox;
    private static TextView reportCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportlog);

        // Initialize Views

        // Initialize RecyclerView and database access
        searchBox = findViewById(R.id.searchTextBox2);
        reportsRecyclerView = findViewById(R.id.reportrecycler);
        reportCountTextView = findViewById(R.id.reportCountText);

        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        // Load reports and update count
        loadReports();


        // Filtering Logic
        // Filter button setup
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, filterBtn);
            popup.getMenuInflater().inflate(R.menu.reports_filter_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                Cursor cursor;
                if (itemId == R.id.filter_newest) {
                    cursor = databaseAccess.getReportsNewestFirst();
                } else if (itemId == R.id.filter_oldest) {
                    cursor = databaseAccess.getReportsOldestFirst();
                } else {
                    return false;
                }
                adapter.updateCursor(cursor);
                updateReportCount(cursor); // Update count after filtering
                return true;
            });

            popup.show();
        });

        // Search functionality
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchReportDB(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ImageButton backbtn = findViewById(R.id.report_back);
        backbtn.setOnClickListener(v -> finish());
    }

//    private void searchReportDB(String search) {
//        try {
//            String searchQuery;
//            if (search.toLowerCase().startsWith("phone:")) {
//                String phoneNumber = search.substring(6).trim();
//                searchQuery = "SELECT * FROM Reports WHERE Phone_Number LIKE '%" + phoneNumber + "%'";
//            } else {
//                searchQuery = "SELECT * FROM Reports WHERE Phone_Number LIKE '%" + search +
//                        "%' OR Message LIKE '%" + search +
//                        "%' OR Date LIKE '%" + search + "%'";
//            }
//
//            Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
//            if (cursor != null) {
//                adapter.updateCursor(cursor);
//                updateReportCount(); // Update count after search
//            } else {
//                Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error searching reports", Toast.LENGTH_SHORT).show();
//        }
//    }

//

    private void searchReportDB(String search) {
        try {
            String searchQuery;
            if (search.toLowerCase().startsWith("phone:")) {
                // Extract the phone number part after "phone:"
                String phoneNumber = search.substring(6).trim();
                searchQuery = "SELECT * FROM Reports WHERE Phone_Number LIKE '%" + phoneNumber + "%'";
            }

            else if (search.toLowerCase().startsWith("message:")) {
                String message = search.substring(8).trim();
                searchQuery = "SELECT * FROM Reports WHERE Message LIKE '%" + message + "%'";
            }
            else if (search.toLowerCase().startsWith("date:")) {
                String date = search.substring(5).trim();
                searchQuery = "SELECT * FROM Reports WHERE Date LIKE '%" + date + "%'";
            }

            else {
                searchQuery = "SELECT * FROM Reports WHERE Phone_Number LIKE '%" + search + "%' " +
                        "OR Message LIKE '%" + search + "%' " +
                        "OR Date LIKE '%" + search + "%'";
            }

            Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
            if (cursor != null) {
                adapter.updateCursor(cursor);
                updateReportCount(cursor); // Update count after search
            } else {
                Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error searching reports", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadReports() {
        try {
            Cursor cursor = databaseAccess.getReports();
            if (cursor != null) {
                adapter = new ReportsAdapter(this, cursor);
                reportsRecyclerView.setAdapter(adapter);
                updateReportCount(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading reports", Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateReportCount(Cursor cursor) {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount(); // Get count from the filtered search query
        }
        reportCountTextView.setText("Reports: " + count);
    }

}
