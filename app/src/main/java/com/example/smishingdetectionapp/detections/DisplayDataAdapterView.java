package com.example.smishingdetectionapp.detections;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

//Used for populating listview with correct data while searching.
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

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("DEBUG", "bindView called");
        TextView PhoneTextView = view.findViewById(R.id.detectionPhoneText);
        TextView MessageTextView = view.findViewById(R.id.detectionMessageText);
        TextView DateTextView = view.findViewById(R.id.detectionDateText);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Log.d("DEBUG", "deleteButton = " + deleteButton); // Should not be null

        int Phone_Number =
                cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_PHONENUMBER);
        int Message =
                cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_MESSAGE);
        int Date =
                cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_DATE);

        String Phone = cursor.getString(Phone_Number);
        String Messages = cursor.getString(Message);
        String Dates = cursor.getString(Date);

        PhoneTextView.setText(Phone);
        MessageTextView.setText(Messages);
        DateTextView.setText(Dates);

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

}
