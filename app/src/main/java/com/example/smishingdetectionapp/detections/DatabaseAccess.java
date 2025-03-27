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
import java.util.Locale;

public class DatabaseAccess {
    private static SQLiteOpenHelper openHelper;
    static SQLiteDatabase db;
    private static DatabaseAccess instance;
    Context context;

    public static boolean sendFeedback(String name, String feedback, float rating) {
        // Here you would add your logic to send feedback to the database.
        // This could involve inserting the feedback into a SQLite database,
        // sending it to a remote server via an API call, etc.

        // For now, we will simulate a successful insertion
        // by always returning true.

        if (name.isEmpty() || feedback.isEmpty()) {
            return false; // Fail if name or feedback is empty
        }

        // Simulated success
        return true;
    }

    // Simulate submission of thoughts
    public static boolean submitThoughts(String thoughts) {
        // Add your database logic or API call here
        return !thoughts.isEmpty(); // Simulating success
    }

    // Simulate submission of comments
    public static boolean submitComment(String comment) {
        // Add your database logic or API call here
        return !comment.isEmpty(); // Simulating success
    }

    public static class DatabaseOpenHelper extends SQLiteAssetHelper {

        private static final String DATABASE_NAME="detectlist.db";
        private static final int DATABASE_VERSION=1;
        private static final String TABLE_DETECTIONS = "Detections";
        private static final String TABLE_REPORTS = "Reports";
        public static final String KEY_ROWID = "_id";
        public static final String KEY_PHONENUMBER="Phone_Number";
        public static final String KEY_MESSAGE = "Message";
        public static final String KEY_DATE = "Date";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }
    }

    DatabaseAccess(Context context) {

        openHelper= new DatabaseOpenHelper(context);
        this.context = context;
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance==null){
            instance=new DatabaseAccess(context);
        }
        return instance;
    }

    public void open(){
        this.db=openHelper.getWritableDatabase();
        System.out.println("Database Opened!");
    }

    public void close(){
        if(db!=null){
            this.db.close();
            System.out.println("Database Closed!");
        }
    }

    //Total detections counter
    public int getCounter() {
        Cursor cursor = db.rawQuery("select * from Detections", null);
        System.out.println("Number of Records: "+cursor.getCount());
        return cursor.getCount();
    }

    //Used to get current device time
    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //Report sending function with database
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
    return db.rawQuery("SELECT * FROM " + DatabaseOpenHelper.TABLE_DETECTIONS + " ORDER BY " + DatabaseOpenHelper.KEY_DATE + " DESC", null);
}

public Cursor getDetectionsForDate(String date) {
    return db.rawQuery(
        "SELECT * FROM " + DatabaseOpenHelper.TABLE_DETECTIONS +
        " WHERE " + DatabaseOpenHelper.KEY_DATE + " LIKE ? ORDER BY " + DatabaseOpenHelper.KEY_DATE + " DESC",
        new String[]{"%" + date + "%"}
    );
}

public Cursor getAllReports() {
    return db.rawQuery("SELECT * FROM " + DatabaseOpenHelper.TABLE_REPORTS + " ORDER BY " + DatabaseOpenHelper.KEY_DATE + " DESC", null);
}

public Cursor getReportsForDate(String date) {
    return db.rawQuery(
        "SELECT * FROM " + DatabaseOpenHelper.TABLE_REPORTS +
        " WHERE " + DatabaseOpenHelper.KEY_DATE + " LIKE ? ORDER BY " + DatabaseOpenHelper.KEY_DATE + " DESC",
        new String[]{"%" + date + "%"}
    );
}
public Cursor getReportsForSpecificDate(String specificDate) {
    return db.rawQuery(
        "SELECT * FROM " + DatabaseOpenHelper.TABLE_REPORTS +
        " WHERE DATE(" + DatabaseOpenHelper.KEY_DATE + ") = DATE(?) ORDER BY " + DatabaseOpenHelper.KEY_DATE + " DESC",
        new String[]{specificDate}
    );
}

    public SimpleCursorAdapter populateDetectionList(){

        String[] columns = {
                DatabaseOpenHelper.KEY_ROWID,
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_MESSAGE,
                DatabaseOpenHelper.KEY_DATE
        };

        Cursor cursor = db.query(
                DatabaseOpenHelper.TABLE_DETECTIONS,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);

        String[] columnsStr = new String[]{
                DatabaseOpenHelper.KEY_ROWID,
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_DATE,
                DatabaseOpenHelper.KEY_MESSAGE
        };

        int[] toViewIDs = new int[]{
                R.id.item_id,
                R.id.detectionPhoneText,
                R.id.detectionDateText,
                R.id.detectionMessageText
        };

        return new SimpleCursorAdapter(
                context,
                R.layout.detection_items,
                cursor,
                columnsStr,
                toViewIDs
        );
    }

    // Add this method to DatabaseAccess.java
    // Add this method to DatabaseAccess.java
    public ReportsAdapter populateReportsList() { // same function
        try {
            String query = "SELECT * FROM Reports ORDER BY Date DESC";
            Cursor cursor = db.rawQuery(query, null);

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
            // Delete the record where Phone_Number and Message both match
            int rowsDeleted = db.delete(
                    DatabaseOpenHelper.TABLE_REPORTS,
                    "Phone_Number = ? AND Message = ?", // WHERE clause
                    new String[] { phoneNumber, message } // WHERE arguments
            );
            return rowsDeleted > 0; // Return true if at least one row was deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an exception occurred
        }
    }


    public Cursor getReports() {
        try {
            // Check if the database contains any rows
            Cursor checkCursor = db.rawQuery("SELECT COUNT(*) FROM Reports", null);
            if (checkCursor != null) {
                checkCursor.moveToFirst(); // Move to the first row
                int rowCount = checkCursor.getInt(0); // Get the count of rows
                checkCursor.close(); // Close the cursor to avoid leaks

                if (rowCount == 0) {
                    // If there are no rows, return null
                    return null;
                }
            }
            // If rows exist, return the query cursor
            return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor getReportsNewestFirst() { // same function
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
        final String TAG = "DatabaseReports"; // Define a unique tag for Logcat

        String[] columns = {
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_MESSAGE,
                DatabaseOpenHelper.KEY_DATE
        };

        Cursor cursor = db.query(
                DatabaseOpenHelper.TABLE_REPORTS, // Table name
                columns,                          // Columns to retrieve
                null,                             // WHERE clause
                null,                             // WHERE arguments
                null,                             // GROUP BY clause
                null,                             // HAVING clause
                null                              // ORDER BY clause
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_PHONENUMBER));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_MESSAGE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_DATE));

                // Log the details of each report using Log.d
                Log.d(TAG, "Phone Number: " + phoneNumber +
                        ", Message: " + message +
                        ", Date: " + date);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No reports found in the table.");
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public boolean deleteReportByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        // Define the WHERE clause and arguments
        String whereClause = DatabaseOpenHelper.KEY_PHONENUMBER + "=?";
        String[] whereArgs = new String[]{phoneNumber};

        // Perform the deletion
        int rowsDeleted = db.delete(DatabaseOpenHelper.TABLE_REPORTS, whereClause, whereArgs);

        // Check if the deletion was successful
        if (rowsDeleted > 0) {
            Log.d("DatabaseAccess", "Deleted " + rowsDeleted + " report(s) for phone number: " + phoneNumber);
            return true;
        } else {
            Log.d("DatabaseAccess", "No report found for phone number: " + phoneNumber);
            return false;
        }
    }

    public boolean validateLogin(String email, String password) {
        String query = "SELECT * FROM Login WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
    public boolean validatePin(String pin) {
        try {
            String query = "SELECT * FROM Login WHERE pin = ?";
            Cursor cursor = db.rawQuery(query, new String[]{pin});

            boolean isValid = cursor.getCount() > 0; // Check if any row exists
            cursor.close(); // Close the cursor to prevent memory leaks

            return isValid; // Return true if the PIN is valid, otherwise false
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false in case of any error
        }
    }

    public boolean insertLogin(String name, String email, String phoneNumber, String password, String pin) {
        try {
            System.out.println("Hereee ::");
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", name);
            contentValues.put("Email", email);
            contentValues.put("PhoneNumber", phoneNumber); // Ensure this is a valid integer string
            contentValues.put("Password", password);
            contentValues.put("Pin", pin); // Ensure this is a valid integer string
            System.out.println("Hereee :");
            long result = db.insert("Login", null, contentValues);
            System.out.println("Hereee :"+ result);
            return result != -1; // Return true if insert was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an exception occurred
        }
    }
    public boolean createPIN(String newPIN) {
        try {
            String query = "SELECT * FROM Login";
            Cursor cursor = db.rawQuery(query, null);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Pin", newPIN); // Ensure "Pin" matches your column name in the Login table
            if (cursor != null && cursor.moveToFirst()) {
                int rowsUpdated = db.update("Login", contentValues, null, null);
                cursor.close();
                return rowsUpdated > 0; // Return true if at least one row was updated
            } else {
                long result = db.insert("Login", null, contentValues);
                return result != -1; // Return true if the insert was successful
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }
}
