package com.example.smishingdetectionapp.detections;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.AboutMeActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.recyclebin.RecycleBinActivity;
import com.example.smishingdetectionapp.recyclebin.RecycleBinManager;
import com.example.smishingdetectionapp.ui.Register.RegisterMain;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONObject;

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
        String searchQuery = "SELECT * FROM Detections";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void DeleteRow(String id) {
        DatabaseAccess.db.delete("Detections", "_id = ?", new String[]{id});
    }

    private void saveRadioButtonState(String key, boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    private void clearRadioButtonState() {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearRadioButtonState();
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
            clearRadioButtonState();
        });

        detectionLV = findViewById(R.id.lvDetectionsList);
        databaseAccess = new DatabaseAccess(getApplicationContext());
        databaseAccess.open();

        Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections", null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        EditText detSearch = findViewById(R.id.searchTextBox);

        //populate data
        String searchQuery = ("SELECT * FROM Detections");
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button exportCsvBtn = findViewById(R.id.exportCsvBtn);
        exportCsvBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                exportDetectionsToCSV(); // no permissions needed
            }
        });

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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDB(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            View bottomSheet = getLayoutInflater().inflate(R.layout.popup_filter, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();

            RadioButton OldToNewRB = bottomSheet.findViewById(R.id.OldToNewRB);
            RadioButton NewToOldRB = bottomSheet.findViewById(R.id.NewToOldRB);

            OldToNewRB.setChecked(sharedPreferences.getBoolean("OldToNewRB", false));
            NewToOldRB.setChecked(sharedPreferences.getBoolean("NewToOldRB", false));

            OldToNewRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (OldToNewRB.isChecked()) {
                    NewToOldRB.setChecked(false);
                    sortONDB();
                }
                saveRadioButtonState("OldToNewRB", isChecked);
            });

            NewToOldRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (NewToOldRB.isChecked()) {
                    OldToNewRB.setChecked(false);
                    sortNODB();
                }
                saveRadioButtonState("NewToOldRB", isChecked);
            });
        });

        detectionLV.setOnItemLongClickListener((parent, view, position, id) -> {
            View bottomSheetDel = getLayoutInflater().inflate(R.layout.popup_deleteitem, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheetDel);
            bottomSheetDialog.show();

            Button Cancel = bottomSheetDel.findViewById(R.id.delItemCancel);
            Button Confirm = bottomSheetDel.findViewById(R.id.DelItemConfirm);

            Cancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

            Confirm.setOnClickListener(v -> {
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

    public void searchDB(String search){
        String searchQuery = ("SELECT * FROM Detections WHERE Phone_Number LIKE '%" + search + "%' OR Message Like '%" + search + "%' OR Date Like '%" + search + "%'");
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Sorting Oldest Date to Newest Date
    public void sortONDB(){
        String searchQuery = ("SELECT * FROM Detections ORDER BY Date ASC");

        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Sorting Newest Date to Oldest Date
    public void sortNODB(){
        String searchQuery = ("SELECT * FROM Detections ORDER BY Date DESC");

        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void refreshList(){
        String searchQuery = ("SELECT * FROM Detections");

        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void DeleteRow(String id) {
        Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections WHERE _id = ?", new String[]{id});
        if (cursor.moveToFirst()) {
            JSONObject detection = new JSONObject();
            try {
                detection.put("Phone_Number", cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number")));
                detection.put("Message", cursor.getString(cursor.getColumnIndexOrThrow("Message")));
                detection.put("Date", cursor.getString(cursor.getColumnIndexOrThrow("Date")));
                RecycleBinManager rbManager = new RecycleBinManager(this);
                rbManager.addToRecycleBin(detection);
            } catch (Exception e) {
                Log.e("DeleteRow", "Error adding to recycle bin", e);
            }
        }
        cursor.close();
        DatabaseAccess.db.delete("Detections", "_id = ?", new String[]{id});
    }


    //Saving checked state of radio buttons in the filter popup.
    private void saveRadioButtonState(String key, boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    //function used to clear checked state of radio buttons
    private void clearRadioButtonState() {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //clears checked state of radio buttons when app is closed.
    @Override
    protected void onStop() {
        super.onStop();
        clearRadioButtonState();
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void exportDetectionsToCSV() {
        String csvHeader = "ID,Phone Number,Message,Date";
        String fileName = "detections_export.csv";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
        contentValues.put(MediaStore.Downloads.IS_PENDING, 1);

        ContentResolver resolver = getContentResolver();
        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri fileUri = resolver.insert(collection, contentValues);

        if (fileUri == null) {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_LONG).show();
            return;
        }

        try (OutputStream outputStream = resolver.openOutputStream(fileUri);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

            writer.write(csvHeader);
            writer.newLine();

            Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections", null);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                    String number = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("Message")).replace(",", " ");
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));

                    writer.write(id + "," + number + "," + message + "," + date);
                    writer.newLine();
                } while (cursor.moveToNext());
            }
            cursor.close();
            writer.flush();

            // Mark as not pending so it's visible to the user
            contentValues.clear();
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(fileUri, contentValues, null, null);

            Toast.makeText(this, "CSV exported to Downloads!", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
}

