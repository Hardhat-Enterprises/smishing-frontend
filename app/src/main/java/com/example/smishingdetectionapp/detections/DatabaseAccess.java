package com.example.smishingdetectionapp.detections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.smishingdetectionapp.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DatabaseAccess {
    private static SQLiteOpenHelper openHelper;
    static SQLiteDatabase db;
    private static DatabaseAccess instance;
    Context context;

    public static boolean sendFeedback(String name, String feedback, float rating) {
        if (name.isEmpty() || feedback.isEmpty()) return false;
        return true;
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
        public static final String TABLE_DETECTIONS = "Detections";
        public static final String TABLE_REPORTS = "Reports";
        public static final String KEY_ROWID = "_id";
        public static final String KEY_PHONENUMBER = "Phone_Number";
        public static final String KEY_MESSAGE = "Message";
        public static final String KEY_DATE = "Date";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }

    private DatabaseAccess(Context context) {
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
        System.out.println("Database Opened!");
    }

    public void close() {
        if (db != null) {
            db.close();
            System.out.println("Database Closed!");
        }
    }

    public int getCounter() {
        Cursor cursor = db.rawQuery("SELECT * FROM Detections", null);
        return cursor.getCount();
    }

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public static boolean sendReport(String phonenumber, String message) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseOpenHelper.KEY_PHONENUMBER, phonenumber);
        contentValues.put(DatabaseOpenHelper.KEY_MESSAGE, message);
        contentValues.put(DatabaseOpenHelper.KEY_DATE, getDateTime());
        long result = db.insert(DatabaseOpenHelper.TABLE_REPORTS, null, contentValues);
        return result != -1;
    }

    public Cursor getAllDetections() {
        return db.rawQuery(
                "SELECT * FROM " + DatabaseOpenHelper.TABLE_DETECTIONS +
                        " ORDER BY " + DatabaseOpenHelper.KEY_DATE + " DESC",
                null
        );
    }

    public Cursor getDetectionsForDate(String date) {
        return db.rawQuery(
                "SELECT * FROM " + DatabaseOpenHelper.TABLE_DETECTIONS +
                        " WHERE " + DatabaseOpenHelper.KEY_DATE + " LIKE ? ORDER BY " +
                        DatabaseOpenHelper.KEY_DATE + " DESC",
                new String[]{ "%" + date + "%" }
        );
    }

    public Cursor getAllReports() {
        return db.rawQuery(
                "SELECT * FROM " + DatabaseOpenHelper.TABLE_REPORTS +
                        " ORDER BY " + DatabaseOpenHelper.KEY_DATE + " DESC",
                null
        );
    }

    public Cursor getReportsForDate(String date) {
        return db.rawQuery(
                "SELECT * FROM " + DatabaseOpenHelper.TABLE_REPORTS +
                        " WHERE " + DatabaseOpenHelper.KEY_DATE + " LIKE ? ORDER BY " +
                        DatabaseOpenHelper.KEY_DATE + " DESC",
                new String[]{ "%" + date + "%" }
        );
    }

    public Cursor getReportsForSpecificDate(String specificDate) {
        return db.rawQuery(
                "SELECT * FROM " + DatabaseOpenHelper.TABLE_REPORTS +
                        " WHERE DATE(" + DatabaseOpenHelper.KEY_DATE + ") = DATE(?) ORDER BY " +
                        DatabaseOpenHelper.KEY_DATE + " DESC",
                new String[]{ specificDate }
        );
    }

    public SimpleCursorAdapter populateDetectionList() {
        String[] columns = {
                DatabaseOpenHelper.KEY_ROWID,
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_MESSAGE,
                DatabaseOpenHelper.KEY_DATE
        };

        Cursor cursor = db.query(
                DatabaseOpenHelper.TABLE_DETECTIONS,
                columns, null, null, null, null, null, null
        );

        String[] fromCols = {
                DatabaseOpenHelper.KEY_ROWID,
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_DATE,
                DatabaseOpenHelper.KEY_MESSAGE,
                DatabaseOpenHelper.KEY_ROWID
        };
        int[] toViews = {
                R.id.item_id,
                R.id.detectionPhoneText,
                R.id.detectionDateText,
                R.id.detectionMessageText,
                R.id.detectionStatus
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                context,
                R.layout.detection_items,
                cursor,
                fromCols,
                toViews,
                0
        );

        adapter.setViewBinder((view, cur, columnIndex) -> {
            if (view.getId() == R.id.detectionStatus) {
                TextView statusTv = (TextView) view;
                boolean isSafe = new Random().nextBoolean();
                statusTv.setText(isSafe ? "Safe" : "Suspicious");
                statusTv.setTextColor(isSafe ? Color.GREEN : Color.RED);
                return true;
            }
            return false;
        });

        return adapter;
    }

    /**
     * Delete a single detection by its _id.
     */
    public void DeleteRow(String id) {
        db.delete(
                DatabaseOpenHelper.TABLE_DETECTIONS,
                DatabaseOpenHelper.KEY_ROWID + " = ?",
                new String[]{ id }
        );
    }

    public ReportsAdapter populateReportsList() {
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM Reports ORDER BY Date DESC", null
            );
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            return new ReportsAdapter(context, cursor);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteReport(String phoneNumber, String message) {
        try {
            int rowsDeleted = db.delete(
                    DatabaseOpenHelper.TABLE_REPORTS,
                    "Phone_Number = ? AND Message = ?",
                    new String[]{ phoneNumber, message }
            );
            return rowsDeleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Cursor getReports() {
        try {
            Cursor checkCursor = db.rawQuery("SELECT COUNT(*) FROM Reports", null);
            if (checkCursor != null && checkCursor.moveToFirst()) {
                int rowCount = checkCursor.getInt(0);
                checkCursor.close();
                if (rowCount == 0) return null;
            }
            return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor getReportsNewestFirst() {
        try {
            return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor getReportsOldestFirst() {
        try {
            return db.rawQuery("SELECT * FROM Reports ORDER BY Date ASC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void logAllReports() {
        final String TAG = "DatabaseReports";
        String[] cols = {
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_MESSAGE,
                DatabaseOpenHelper.KEY_DATE
        };
        Cursor cursor = db.query(
                DatabaseOpenHelper.TABLE_REPORTS, cols,
                null, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String phone = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_PHONENUMBER)
                );
                String msg = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_MESSAGE)
                );
                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_DATE)
                );
                Log.d(TAG, "Phone: " + phone + ", Msg: " + msg + ", Date: " + date);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.d(TAG, "No reports found in the table.");
        }
    }

    public boolean deleteReportByPhoneNumber(String phoneNumber) {
        String where = DatabaseOpenHelper.KEY_PHONENUMBER + "=?";
        String[] args = { phoneNumber };
        int rows = db.delete(DatabaseOpenHelper.TABLE_REPORTS, where, args);
        Log.d("DatabaseAccess", rows > 0
                ? "Deleted " + rows + " report(s) for phone: " + phoneNumber
                : "No report found for phone: " + phoneNumber
        );
        return rows > 0;
    }

    public boolean validateLogin(String email, String password) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Login WHERE email = ? AND password = ?",
                new String[]{ email, password }
        );
        boolean ok = cursor.getCount() > 0;
        cursor.close();
        return ok;
    }

    public boolean validatePin(String pin) {
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Login WHERE pin = ?", new String[]{ pin });
            boolean ok = cursor.getCount() > 0;
            cursor.close();
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertLogin(String name, String email, String phoneNumber, String password, String pin) {
        try {
            ContentValues vals = new ContentValues();
            vals.put("Name", name);
            vals.put("Email", email);
            vals.put("PhoneNumber", phoneNumber);
            vals.put("Password", password);
            vals.put("Pin", pin);
            long res = db.insert("Login", null, vals);
            return res != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createPIN(String newPIN) {
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Login", null);
            ContentValues vals = new ContentValues();
            vals.put("Pin", newPIN);
            if (cursor != null && cursor.moveToFirst()) {
                int updated = db.update("Login", vals, null, null);
                cursor.close();
                return updated > 0;
            } else {
                long res = db.insert("Login", null, vals);
                return res != -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}