package com.example.smishingdetectionapp.detections;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.smishingdetectionapp.R;

public class DisplayDataAdapterView extends CursorAdapter {

    private LayoutInflater inflater;

    public DisplayDataAdapterView(Context context, Cursor c) {
        super(context, c, 0);
        inflater = LayoutInflater.from(context);
    }

    // Inflate the view for each list item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.detection_items, parent, false);
    }

    // Bind data to each list item view
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();

        TextView numberTextView = view.findViewById(R.id.detectionPhoneLabel);
        TextView phoneTextView = view.findViewById(R.id.detectionPhoneText);
        TextView messageTextView = view.findViewById(R.id.detectionMessageText);
        TextView dateTextView = view.findViewById(R.id.detectionDateText);

        // Set dynamic detection number
        numberTextView.setText(String.valueOf(position + 1));

        // Get values from database
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAccess.DatabaseOpenHelper.KEY_PHONENUMBER));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAccess.DatabaseOpenHelper.KEY_MESSAGE));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAccess.DatabaseOpenHelper.KEY_DATE));

        // Bind data to views
        phoneTextView.setText(phone);
        messageTextView.setText(message);
        dateTextView.setText(date);
    }
}
