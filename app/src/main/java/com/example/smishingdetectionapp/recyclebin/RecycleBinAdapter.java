package com.example.smishingdetectionapp.recyclebin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.detections.DetectionItem;

import java.util.ArrayList;

public class RecycleBinAdapter extends ArrayAdapter<DetectionItem> {
    private final Context context;
    private final ArrayList<DetectionItem> items;
    private final RecycleBinManager rbManager;
    private final Runnable onRestoreCallback;

    public RecycleBinAdapter(Context context, ArrayList<DetectionItem> items, RecycleBinManager rbManager, Runnable onRestoreCallback) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.rbManager = rbManager;
        this.onRestoreCallback = onRestoreCallback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetectionItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recycle_bin, parent, false);
        }

        TextView phoneText = convertView.findViewById(R.id.tvPhoneNumber);
        TextView messageText = convertView.findViewById(R.id.tvMessage);
        TextView dateText = convertView.findViewById(R.id.tvDate);

        phoneText.setText(item.getPhoneNumber());
        messageText.setText(item.getMessage());
        dateText.setText(item.getDate());

        return convertView;
    }
}


