package com.example.smishingdetectionapp.detections;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.detections.DatabaseAccess.DatabaseOpenHelper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DetectionsAdapter extends SimpleCursorAdapter {
    private final Set<Integer> selectedIds = new HashSet<>();
    private final Random rnd = new Random();

    // Columns for SimpleCursorAdapter to bind (phone, message, date)
    private static final String[] FROM = {
            DatabaseOpenHelper.KEY_PHONENUMBER,
            DatabaseOpenHelper.KEY_MESSAGE,
            DatabaseOpenHelper.KEY_DATE
    };
    private static final int[] TO = {
            R.id.detectionPhoneText,
            R.id.detectionMessageText,
            R.id.detectionDateText
    };

    public DetectionsAdapter(Context context, Cursor c) {
        super(context, R.layout.detection_items, c, FROM, TO, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Let SimpleCursorAdapter bind phone/message/date
        super.bindView(view, context, cursor);

        // 1) Random status badge
        TextView statusTv = view.findViewById(R.id.detectionStatus);
        boolean isSafe = rnd.nextBoolean();
        statusTv.setText(isSafe ? "Safe" : "Potential Smishing");
        statusTv.setTextColor(isSafe ? Color.GREEN : Color.RED);

        // 2) Checkbox state
        CheckBox cb = view.findViewById(R.id.checkBoxSelect);
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_ROWID));

        // Reset listener & checked state
        cb.setOnCheckedChangeListener(null);
        cb.setChecked(selectedIds.contains(id));

        // Re‑attach listener
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedIds.add(id);
            else selectedIds.remove(id);
        });
    }

    /** Select all rows in the current cursor */
    public void selectAll() {
        selectedIds.clear();
        Cursor c = getCursor();
        int idx = c.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_ROWID);
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            selectedIds.add(c.getInt(idx));
        }
        notifyDataSetChanged();
    }

    /** Clear all selections */
    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    /** Return IDs of checked rows */
    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }
}