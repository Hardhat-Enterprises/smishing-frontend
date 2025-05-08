package com.example.smishingdetectionapp.detections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import com.example.smishingdetectionapp.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseAccess {
    private static SQLiteOpenHelper openHelper;
    static SQLiteDatabase db;
    private static DatabaseAccess instance;
    Context context;

    public static boolean sendFeedback(String name, String feedback, float rating) {
        return !name.isEmpty() && !feedback.isEmpty();
    }

    public static boolean submitThoughts(String thoughts) {
        return !thoughts.isEmpty();
    }

    public static boolean submitComment(String comment) {
        return !comment.isEmpty();
    }

    public static class DatabaseOpenHelper extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "detectlist.db";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_DETECTIONS = "Detections";
        private static final String TABLE_REPORTS = "Reports";
        public static final String KEY_ROWID = "_id";
        public static final String KEY_PHONENUMBER = "Phone_Number";
        public static final String KEY_MESSAGE = "Message";
        public static final String KEY_DATE = "Date";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }

    public DatabaseAccess(Context context) {
        openHelper = new DatabaseOpenHelper(context);
        this.context = context;
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        db = openHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) db.close();
    }

    public int getCounter() {
        Cursor cursor = db.rawQuery("select * from Detections", null);
        return cursor.getCount();
    }

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public static boolean sendReport(String phonenumber, String message) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.KEY_PHONENUMBER, phonenumber);
        values.put(DatabaseOpenHelper.KEY_MESSAGE, message);
        values.put(DatabaseOpenHelper.KEY_DATE, getDateTime());
        long result = db.insert(DatabaseOpenHelper.TABLE_REPORTS, null, values);
        return result != -1;
    }

    public Cursor getAllDetections() {
        return db.rawQuery("SELECT * FROM Detections ORDER BY Date DESC", null);
    }

    public Cursor getDetectionsForDate(String date) {
        return db.rawQuery("SELECT * FROM Detections WHERE Date LIKE ? ORDER BY Date DESC", new String[]{"%" + date + "%"});
    }

    public Cursor getAllReports() {
        return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
    }

    public Cursor getReportsForDate(String date) {
        return db.rawQuery("SELECT * FROM Reports WHERE Date LIKE ? ORDER BY Date DESC", new String[]{"%" + date + "%"});
    }

    public Cursor getReportsForSpecificDate(String date) {
        return db.rawQuery("SELECT * FROM Reports WHERE DATE(Date) = DATE(?) ORDER BY Date DESC", new String[]{date});
    }

    public SimpleCursorAdapter populateDetectionList() {
        String[] from = {DatabaseOpenHelper.KEY_ROWID, DatabaseOpenHelper.KEY_PHONENUMBER, DatabaseOpenHelper.KEY_DATE, DatabaseOpenHelper.KEY_MESSAGE};
        int[] to = {R.id.item_id, R.id.detectionPhoneText, R.id.detectionDateText, R.id.detectionMessageText};
        Cursor cursor = db.query("Detections", from, null, null, null, null, null);
        return new SimpleCursorAdapter(context, R.layout.detection_items, cursor, from, to);
    }

    public ReportsAdapter populateReportsList() {
        Cursor cursor = db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return new ReportsAdapter(context, cursor);
    }

    public boolean deleteReport(String phone, String message) {
        int rows = db.delete("Reports", "Phone_Number = ? AND Message = ?", new String[]{phone, message});
        return rows > 0;
    }

    public Cursor getReports() {
        Cursor count = db.rawQuery("SELECT COUNT(*) FROM Reports", null);
        if (count != null && count.moveToFirst() && count.getInt(0) == 0) {
            count.close();
            return null;
        }
        return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
    }

    public Cursor getReportsNewestFirst() {
        return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
    }

    public Cursor getReportsOldestFirst() {
        return db.rawQuery("SELECT * FROM Reports ORDER BY Date ASC", null);
    }

    public void logAllReports() {
        Cursor cursor = db.query("Reports", new String[]{"Phone_Number", "Message", "Date"}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Log.d("DatabaseReports", "Phone Number: " + cursor.getString(0) + ", Message: " + cursor.getString(1) + ", Date: " + cursor.getString(2));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.d("DatabaseReports", "No reports found in the table.");
        }
    }

    public boolean deleteReportByPhoneNumber(String phone) {
        int rows = db.delete("Reports", "Phone_Number=?", new String[]{phone});
        return rows > 0;
    }

    public boolean validateLogin(String email, String password) {
        Cursor cursor = db.rawQuery("SELECT * FROM Login WHERE email = ? AND password = ?", new String[]{email, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public boolean validatePin(String pin) {
        Cursor cursor = db.rawQuery("SELECT * FROM Login WHERE pin = ?", new String[]{pin});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public boolean insertLogin(String name, String email, String phone, String password, String pin) {
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Email", email);
        values.put("PhoneNumber", phone);
        values.put("Password", password);
        values.put("Pin", pin);
        return db.insert("Login", null, values) != -1;
    }

    public boolean createPIN(String newPIN) {
        Cursor cursor = db.rawQuery("SELECT * FROM Login", null);
        ContentValues values = new ContentValues();
        values.put("Pin", newPIN);
        if (cursor != null && cursor.moveToFirst()) {
            int rows = db.update("Login", values, null, null);
            cursor.close();
            return rows > 0;
        } else {
            return db.insert("Login", null, values) != -1;
        }
    }

    // ===== INSIGHTS FEATURE METHODS =====

    public int getTotalDetections() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public String getMostTargetedYear() {
        Cursor cursor = db.rawQuery(
                "SELECT strftime('%Y', Date) AS year, COUNT(*) AS count FROM Detections GROUP BY year ORDER BY count DESC LIMIT 1",
                null);
        String year = "N/A";
        if (cursor.moveToFirst()) {
            year = cursor.getString(0);
        }
        cursor.close();
        return year;
    }

    public Map<String, Integer> getDetectionsGroupedByYear() {
        Map<String, Integer> detectionsByYear = new LinkedHashMap<>();
        Cursor cursor = db.rawQuery("SELECT strftime('%Y', Date) AS year, COUNT(*) FROM Detections GROUP BY year", null);
        while (cursor.moveToNext()) {
            String year = cursor.getString(0);
            int count = cursor.getInt(1);
            detectionsByYear.put(year, count);
        }
        cursor.close();
        return detectionsByYear;
    }

    public String getDetectionsGroupedByDate() {
        StringBuilder builder = new StringBuilder();
        Cursor cursor = db.rawQuery(
                "SELECT Date, COUNT(*) FROM Detections GROUP BY Date ORDER BY Date DESC",
                null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(0);
            int count = cursor.getInt(1);
            builder.append(date).append(": ").append(count).append(" detections\n");
        }
        cursor.close();
        return builder.toString();
    }

}
