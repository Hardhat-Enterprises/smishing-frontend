package com.example.smishingdetectionapp.recyclebin;

import android.content.Context;
import android.util.Log;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecycleBinManager {
    private static final String FILE_NAME = "recycle_bin.json";
    private Context context;

    public RecycleBinManager(Context context) {
        this.context = context;
    }

    public void addToRecycleBin(JSONObject detection) {
        try {
            JSONArray recycleBin = getRecycleBin();
            detection.put("deletedAt", System.currentTimeMillis());
            recycleBin.put(detection);
            saveRecycleBin(recycleBin);
        } catch (Exception e) {
            Log.e("RecycleBinManager", "Error adding to recycle bin", e);
        }
    }

    public JSONArray getRecycleBin() {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) {
                return new JSONArray();
            }
            FileInputStream fis = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return new JSONArray(sb.toString());
        } catch (Exception e) {
            Log.e("RecycleBinManager", "Error reading recycle bin", e);
            return new JSONArray();
        }
    }

    public void restoreDetection(int index) {
        try {
            JSONArray recycleBin = getRecycleBin();
            JSONObject detection = recycleBin.getJSONObject(index);
            // Remove from recycle bin
            recycleBin.remove(index);
            saveRecycleBin(recycleBin);
            // Restore to database
            DatabaseAccess dbAccess = DatabaseAccess.getInstance(context);
            dbAccess.open();
            dbAccess.insertDetection(
                    detection.getString("Phone_Number"),
                    detection.getString("Message"),
                    detection.getString("Date")
            );
            dbAccess.close();
        } catch (Exception e) {
            Log.e("RecycleBinManager", "Error restoring detection", e);
        }
    }

    public void purgeOldDetections() {
        try {
            JSONArray recycleBin = getRecycleBin();
            JSONArray updatedBin = new JSONArray();
            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < recycleBin.length(); i++) {
                JSONObject detection = recycleBin.getJSONObject(i);
                long deletedAt = detection.getLong("deletedAt");
                // 7 days = 604800000 milliseconds
                if (currentTime - deletedAt < 604800000) {
                    updatedBin.put(detection);
                }
            }
            saveRecycleBin(updatedBin);
        } catch (Exception e) {
            Log.e("RecycleBinManager", "Error purging old detections", e);
        }
    }

    private void saveRecycleBin(JSONArray recycleBin) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(recycleBin.toString());
            writer.close();
        } catch (Exception e) {
            Log.e("RecycleBinManager", "Error saving recycle bin", e);
        }
    }
}
