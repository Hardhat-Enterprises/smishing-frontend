package com.example.smishingdetectionapp.Community;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.smishingdetectionapp.Community.CommunityDatabase;
import com.example.smishingdetectionapp.Community.CommunityPost;

public class CommunityDatabaseAccess {
    private SQLiteDatabase database;
    private CommunityDatabase dbHelper;

    public CommunityDatabaseAccess(Context context) {
        dbHelper = new CommunityDatabase(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public long insertPost(CommunityPost post) {
        ContentValues values = new ContentValues();
        values.put(CommunityDatabase.COL_USERNAME, post.getUsername());
        values.put(CommunityDatabase.COL_DATE, post.getDate());
        values.put(CommunityDatabase.COL_TITLE, post.getPosttitle());
        values.put(CommunityDatabase.COL_DESCRIPTION, post.getPostdescription());
        values.put(CommunityDatabase.COL_LIKES, post.getLikes());
        values.put(CommunityDatabase.COL_COMMENTS, post.getComments());

        return database.insert(CommunityDatabase.TABLE_POSTS, null, values);
    }

    public List<CommunityComment> getCommentsByPostId(int postId) {
        List<CommunityComment> comments = new ArrayList<>();
        Cursor cursor = database.query(
                CommunityDatabase.TABLE_COMMENTS,
                null,
                CommunityDatabase.COL_POST_ID + "=?",
                new String[]{String.valueOf(postId)},
                null, null,
                CommunityDatabase.COL_COMMENT_ID + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_COMMENT_ID));
                String user = cursor.getString(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_COMMENT_USER));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_COMMENT_DATE));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_COMMENT_TEXT));

                comments.add(new CommunityComment(id, postId, user, date, text));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return comments;
    }

    public void insertComment(int postId, String username, String date, String text) {
        ContentValues values = new ContentValues();
        values.put(CommunityDatabase.COL_POST_ID, postId);
        values.put(CommunityDatabase.COL_COMMENT_USER, username);
        values.put(CommunityDatabase.COL_COMMENT_DATE, date);
        values.put(CommunityDatabase.COL_COMMENT_TEXT, text);
        database.insert(CommunityDatabase.TABLE_COMMENTS, null, values);
    }

    public List<CommunityPost> getAllPosts() {
        List<CommunityPost> posts = new ArrayList<>();
        Cursor cursor = database.query(CommunityDatabase.TABLE_POSTS, null, null, null, null, null, "id DESC");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_USERNAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_DATE));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_DESCRIPTION));
                int likes = cursor.getInt(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_LIKES));
                int comments = cursor.getInt(cursor.getColumnIndexOrThrow(CommunityDatabase.COL_COMMENTS));
                posts.add(new CommunityPost(id, username, date, title, description, likes, comments));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return posts;
    }

    public void updatePostComments(int postId, int newCount) {
        ContentValues values = new ContentValues();
        values.put(CommunityDatabase.COL_COMMENTS, newCount);
        database.update(CommunityDatabase.TABLE_POSTS, values, CommunityDatabase.COL_ID + " = ?", new String[]{String.valueOf(postId)});
    }

    public boolean isEmpty() {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + CommunityDatabase.TABLE_POSTS, null);
        boolean empty = true;
        if (cursor.moveToFirst()) {
            empty = cursor.getInt(0) == 0;
        }
        cursor.close();
        return empty;
    }

    // comments section
    public void insertComment(CommunityComment comment) {
        ContentValues values = new ContentValues();
        values.put(CommunityDatabase.COL_POST_ID, comment.getPostId());
        values.put(CommunityDatabase.COL_COMMENT_USER, comment.getUser());
        values.put(CommunityDatabase.COL_COMMENT_DATE, comment.getDate());
        values.put(CommunityDatabase.COL_COMMENT_TEXT, comment.getCommentText());
        database.insert(CommunityDatabase.TABLE_COMMENTS, null, values);
    }

    // update likes count
    public void updatePostLikes(int postId, int newLikes) {
        ContentValues values = new ContentValues();
        values.put(CommunityDatabase.COL_LIKES, newLikes);
        database.update(CommunityDatabase.TABLE_POSTS, values,
                CommunityDatabase.COL_ID + " = ?", new String[]{String.valueOf(postId)});
    }

    // delete post function
    public void deletePost(int postId) {
        // delete all comments linked to the post
        database.delete(CommunityDatabase.TABLE_COMMENTS,
                CommunityDatabase.COL_POST_ID + "=?",
                new String[]{String.valueOf(postId)});

        // delete the post
        database.delete(CommunityDatabase.TABLE_POSTS,
                CommunityDatabase.COL_ID + "=?",
                new String[]{String.valueOf(postId)});
    }

    // delete comment function
    public void deleteCommentsByPostId(int postId) {
        database.delete(CommunityDatabase.TABLE_COMMENTS, CommunityDatabase.COL_POST_ID + "=?", new String[]{String.valueOf(postId)});
    }

    public void deleteSingleComment(int commentId) {
        database.delete(CommunityDatabase.TABLE_COMMENTS,
                CommunityDatabase.COL_COMMENT_ID + "=?", new String[]{String.valueOf(commentId)});
    }

    // get most liked post(s)
    public List<CommunityPost> getTopLikedPosts() {
        List<CommunityPost> posts = new ArrayList<>();
        SQLiteDatabase db = this.database;

        // Step 1: Get the max likes
        Cursor maxCursor = db.rawQuery("SELECT MAX(likes) FROM posts", null);
        int maxLikes = 0;
        if (maxCursor.moveToFirst()) {
            maxLikes = maxCursor.getInt(0);
        }
        maxCursor.close();

        // Step 2: Get all posts with that like count
        Cursor cursor = db.rawQuery(
                "SELECT id, username, date, title AS posttitle, description AS postdescription, likes, comments FROM posts WHERE likes = ?",
                new String[]{String.valueOf(maxLikes)}
        );
        if (cursor.moveToFirst()) {
            do {
                CommunityPost post = new CommunityPost(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("posttitle")),
                        cursor.getString(cursor.getColumnIndexOrThrow("postdescription")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("likes")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("comments"))
                );
                posts.add(post);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return posts;
    }
}