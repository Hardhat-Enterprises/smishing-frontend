package com.example.smishingdetectionapp.Community;

import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CommunityDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "community.db";
    public static final int DB_VERSION = 6;

    // post table

    public static final String TABLE_POSTS = "posts";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_DATE = "date";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_LIKES = "likes";
    public static final String COL_COMMENTS = "comments";

    // comments table

    public static final String TABLE_COMMENTS = "comments";
    public static final String COL_COMMENT_ID = "id";
    public static final String COL_POST_ID = "post_id";
    public static final String COL_COMMENT_USER = "comment_user";
    public static final String COL_COMMENT_DATE = "comment_date";
    public static final String COL_COMMENT_TEXT = "comment_text";

    // report table
    public static final String TABLE_REPORTS = "reports";
    public static final String COL_REPORT_ID = "id";
    public static final String COL_REPORTED_NUMBER = "reported_number";
    public static final String COL_REPORT_MESSAGE = "report_message";
    public static final String COL_REPORT_COUNT = "report_count";
    public static final String COL_REPORT_LAST_DATE = "last_reported_date";


    public CommunityDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPostsTable = "CREATE TABLE " + TABLE_POSTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_TITLE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_LIKES + " INTEGER, " +
                COL_COMMENTS + " INTEGER)";
        db.execSQL(createPostsTable);

        String createCommentsTable = "CREATE TABLE " + TABLE_COMMENTS + " (" +
                COL_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POST_ID + " INTEGER, " +
                COL_COMMENT_USER + " TEXT, " +
                COL_COMMENT_DATE + " TEXT, " +
                COL_COMMENT_TEXT + " TEXT)";
        db.execSQL(createCommentsTable);

        String createReportsTable = "CREATE TABLE " + TABLE_REPORTS + " (" +
                COL_REPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REPORTED_NUMBER + " TEXT UNIQUE, " + // to allow multiple reported numbers
                COL_REPORT_MESSAGE + " TEXT, " +
                COL_REPORT_COUNT + " INTEGER, " +
                COL_REPORT_LAST_DATE + " TEXT)";
        db.execSQL(createReportsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
        onCreate(db);
    }
}