package com.example.smishingdetectionapp.recyclebin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.detections.DetectionItem;
import com.example.smishingdetectionapp.detections.DetectionsActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class RecycleBinActivity extends AppCompatActivity {

    private ListView lvDeletedItems;
    private ImageButton btnBack;
    private RecycleBinManager rbManager;
    private ArrayList<DetectionItem> detectionList;
    private RecycleBinAdapter adapter;
    private JSONArray recycleBin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);

        lvDeletedItems = findViewById(R.id.lvDeletedItems);
        btnBack = findViewById(R.id.btnBack);
        rbManager = new RecycleBinManager(this);
        detectionList = new ArrayList<>();

        adapter = new RecycleBinAdapter(this, detectionList, rbManager, () -> {
            loadRecycleBin();
            adapter.notifyDataSetChanged();
        });

        lvDeletedItems.setAdapter(adapter);
        loadRecycleBin();

        lvDeletedItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                rbManager.restoreDetection(position);
                loadRecycleBin();
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(RecycleBinActivity.this, DetectionsActivity.class));
            finish();
        });
    }

    private void loadRecycleBin() {
        detectionList.clear();
        recycleBin = rbManager.getRecycleBin();
        for (int i = 0; i < recycleBin.length(); i++) {
            JSONObject detection = recycleBin.optJSONObject(i);
            if (detection != null) {
                DetectionItem item = new DetectionItem(
                        detection.optString("Phone_Number"),
                        detection.optString("Message"),
                        detection.optString("Date")
                );
                detectionList.add(item);
            }
        }
    }
}