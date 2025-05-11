package com.example.smishingdetectionapp.detections;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DisplayDataAdapterView extends CursorAdapter {
    public DisplayDataAdapterView(DetectionsActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;

    }
    private DetectionsActivity activity;



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.detection_items, parent,
                false);
    }
    // Bind data to each list item view
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();

        TextView numberTextView = view.findViewById(R.id.detectionNumber);
        TextView phoneTextView = view.findViewById(R.id.detectionPhoneText);
        TextView messageTextView = view.findViewById(R.id.detectionMessageText);
        TextView dateTextView = view.findViewById(R.id.detectionDateText);

        Button deleteButton = view.findViewById(R.id.deleteButton);
        Log.d("DEBUG", "deleteButton = " + deleteButton); // Should not be null

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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Delete Clicked", Toast.LENGTH_SHORT).show();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View bottomSheetDel = LayoutInflater.from(context).inflate(R.layout.popup_deleteitem, null);
                bottomSheetDialog.setContentView(bottomSheetDel);
                bottomSheetDialog.show();

                Button cancel = bottomSheetDel.findViewById(R.id.delItemCancel);
                Button confirm = bottomSheetDel.findViewById(R.id.DelItemConfirm);

                cancel.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

                confirm.setOnClickListener(v2 -> {
                    int idColumn = cursor.getColumnIndex("_id");
                    int id = cursor.getInt(idColumn);

                    activity.DeleteRow(String.valueOf(id));
                    activity.refreshList(); // Ensure this actually refreshes your list adapter
                    bottomSheetDialog.dismiss();
//                    Toast.makeText(context, "Detection Deleted!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
