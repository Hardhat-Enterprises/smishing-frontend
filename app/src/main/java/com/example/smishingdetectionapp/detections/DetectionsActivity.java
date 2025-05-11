package com.example.smishingdetectionapp.detections;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.smishingdetectionapp.recyclebin.RecycleBinActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.File;
import android.os.Environment;
import android.media.MediaScannerConnection;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    DatabaseAccess databaseAccess;
    private static final int IMPORT_FILE_REQUEST_CODE = 101;

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

        //populate data
        String searchQuery = ("SELECT * FROM Detections");
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        Button importButton = findViewById(R.id.importButton);
        importButton.setOnClickListener(v -> openFilePicker());

        ImageButton imgBtnRecyclebin = findViewById(R.id.btnRecyclebin);

        imgBtnRecyclebin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetectionsActivity.this, RecycleBinActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

                if (containsLink) {
                    query.append(" WHERE (Message LIKE '%http%' OR Message LIKE '%www%')");
                    hasCondition = true;
                }

                if (todayOnly) {
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    query.append(hasCondition ? " AND " : " WHERE ").append("Date LIKE '").append(today).append("%'");
                    hasCondition = true;
                }

                if (last7DaysOnly) {
                    long sevenDaysAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String sevenDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(sevenDaysAgoMillis));
                    query.append(hasCondition ? " AND " : " WHERE ").append("Date BETWEEN '").append(sevenDaysAgo).append("' AND '").append(today).append("'");
                    hasCondition = true;
                }

                if (startDate != null && endDate != null) {
                    query.append(hasCondition ? " AND " : " WHERE ").append("Date BETWEEN '").append(startDate).append("' AND '").append(endDate).append("'");
                    hasCondition = true;
                }

                if (!selectedYears.isEmpty()) {
                    StringBuilder yearCondition = new StringBuilder();
                    for (int i = 0; i < selectedYears.size(); i++) {
                        if (i > 0) yearCondition.append(" OR ");
                        yearCondition.append("SUBSTR(Date, 1, 4) = '").append(selectedYears.get(i)).append("'");
                    }
                    query.append(hasCondition ? " AND (" : " WHERE (").append(yearCondition).append(")");
                }

                query.append(newestFirst ? " ORDER BY Date DESC" : " ORDER BY Date ASC");

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
        //export pdf
        Button exportBtn = findViewById(R.id.exportPdfBtn);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDetectionsToPDF();
            }
        });


    }
    public void searchDB(String search) {
        String searchQuery = "SELECT * FROM Detections WHERE Phone_Number LIKE '%" + search + "%' OR Message LIKE '%" + search + "%' OR Date LIKE '%" + search + "%'";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void sortONDB() {
        String searchQuery = "SELECT * FROM Detections ORDER BY Date ASC";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void sortNODB() {
        String searchQuery = "SELECT * FROM Detections ORDER BY Date DESC";
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

    private void exportDetectionsToPDF() {
        Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections", null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No detections to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the document
        Document document = new Document();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "detections_report.pdf");
        String filePath = file.getAbsolutePath();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("Smishing Detections Report\n\n"));

            while (cursor.moveToNext()) {
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("Message"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));

                document.add(new Paragraph("Phone: " + phone));
                document.add(new Paragraph("Message: " + message));
                document.add(new Paragraph("Date: " + date));
                document.add(new Paragraph("\n"));
            }

            document.close();

            MediaScannerConnection.scanFile(
                    this,
                    new String[] { file.getAbsolutePath() },
                    new String[] { "application/pdf" },
                    null
            );

            Toast.makeText(this, "PDF exported to: " + filePath, Toast.LENGTH_LONG).show();

            Toast.makeText(this, "PDF exported to: " + filePath, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Show all file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"text/csv", "application/csv", "text/comma-separated-values", "application/vnd.ms-excel"};

        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), IMPORT_FILE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                importDataFromFile(fileUri);
            }
        }
    }
    private void importDataFromFile(Uri fileUri)
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            boolean isHeader = true;

            DatabaseAccess dbAccess = DatabaseAccess.getInstance(this);
            dbAccess.open();

            dbAccess.deleteAllDetections();

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        // Extract data from the CSV
                        String phone = parts[1].trim();
                        String message = parts[2].trim();
                        String date = parts[3].trim();

                        dbAccess.insertDetection(phone, message, date);

                    } catch (Exception e) {
                        Log.e("ImportError", "Error processing line " + lineNumber + ": " + e.getMessage());
                    }
                } else {
                    Log.w("ImportWarning", "Skipping invalid line " + lineNumber + ": " + line);
                }
            }

            dbAccess.close();

            Toast.makeText(this, "Import successful", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this, "Import failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
