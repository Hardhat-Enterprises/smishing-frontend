package com.example.smishingdetectionapp.dashboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smishingdetectionapp.DataBase.DBHelper;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager {

    private SQLiteDatabase db;

    public AnalyticsManager(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    public int getTotalAttacks() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections", null);
        int total = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return total;
    }

    public int getSuccessfulAttacks() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Result = 'Smishing'", null);
        int total = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return total;
    }

    public int getFailedAttacks() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Result != 'Smishing'", null);
        int total = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return total;
    }

    public Map<String, Integer> getAttackCategories() {
        Map<String, Integer> categories = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT Category, COUNT(*) FROM Detections GROUP BY Category", null);
        while (cursor.moveToNext()) {
            String category = cursor.getString(0);
            int count = cursor.getInt(1);
            categories.put(category, count);
        }
        cursor.close();
        return categories;
    }

    public Map<String, Integer> getDaywiseAttacks() {
        Map<String, Integer> daywise = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT substr(Date, 1, 10) as day, COUNT(*) FROM Reports GROUP BY day", null);
        while (cursor.moveToNext()) {
            daywise.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return daywise;
    }

    public Map<String, Integer> getMonthwiseAttacks() {
        Map<String, Integer> monthwise = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT substr(Date, 1, 7) as month, COUNT(*) FROM Reports GROUP BY month", null);
        while (cursor.moveToNext()) {
            monthwise.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return monthwise;
    }

    public Map<String, Integer> getAttacksByHour() {
        Map<String, Integer> hourwise = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT substr(Date, 12, 2) as hour, COUNT(*) FROM Reports GROUP BY hour", null);
        while (cursor.moveToNext()) {
            hourwise.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return hourwise;
    }

    public int getUniqueSenderCount() {
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT Phone_Number) FROM Reports", null);
        int total = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return total;
    }

    public double getAverageContentLength() {
        Cursor cursor = db.rawQuery("SELECT AVG(LENGTH(Message)) FROM Reports", null);
        double average = cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        cursor.close();
        return average;
    }
}
