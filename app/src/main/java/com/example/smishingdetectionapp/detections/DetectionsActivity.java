package com.example.smishingdetectionapp.detections;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.Settings.SettingsActivity;
import com.example.smishingdetectionapp.news.NewsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    DatabaseAccess databaseAccess;

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
        DatabaseAccess.db.delete("Detections", "_id" + "=" + id, null);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detections);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        nav.setSelectedItemId(R.id.nav_detections);
        nav.setOnItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_detections) {
                nav.setActivated(true);
                return true;
            }
            return false;
        });

        //Back button to go back to main dashboard
        ImageButton detections_back = findViewById(R.id.detections_back);
        detections_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            clearRadioButtonState(); //clears checked state of radio buttons when returning to app dashboard.
        });

        //Defining and populating listview from database.
        detectionLV = findViewById(R.id.lvDetectionsList);
        databaseAccess = new DatabaseAccess(getApplicationContext());
        databaseAccess.open();
        final SimpleCursorAdapter simpleCursorAdapter = databaseAccess.populateDetectionList();
        detectionLV.setAdapter(simpleCursorAdapter);

        EditText detSearch = findViewById(R.id.searchTextBox);
        detSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = s.toString();
                searchDB(search);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            //determining which activity to show when filter button is clicked.
            View bottomSheet = getLayoutInflater().inflate(R.layout.popup_filter, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();

            //defining radio buttons in the PopupFilter window.
            RadioButton OldToNewRB = bottomSheet.findViewById(R.id.OldToNewRB);
            RadioButton NewToOldRB = bottomSheet.findViewById(R.id.NewToOldRB);

            //default value of radio buttons before saving preference.
            OldToNewRB.setChecked(sharedPreferences.getBoolean("OldToNewRB", false));
            NewToOldRB.setChecked(sharedPreferences.getBoolean("NewToOldRB", false));

            OldToNewRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(OldToNewRB.isChecked()) {
                    NewToOldRB.setChecked(false);//unchecks other radio button when this one is checked.
                    sortONDB();//uses sort function
                }
                saveRadioButtonState("OldToNewRB", isChecked); //saves the radio button checked state.
            });
            NewToOldRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(NewToOldRB.isChecked()) {
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

    }
    @Override
    public void onBackPressed() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        super.onBackPressed();
    }
}