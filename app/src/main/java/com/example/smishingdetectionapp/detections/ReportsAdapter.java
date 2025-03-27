package com.example.smishingdetectionapp.detections;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    private final Context context;
    private Cursor cursor;

    public ReportsAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public void updateCursor(Cursor newCursor) {
        // Safely close the old cursor and update to the new one
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detection_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
        String message = cursor.getString(cursor.getColumnIndexOrThrow("Message"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));

        holder.phoneTextView.setText(phoneNumber);
        holder.messageTextView.setText(message);
        holder.dateTextView.setText(date);

        // Long-click listener to copy report details to clipboard
        holder.itemView.setOnLongClickListener(v -> {
            String reportText = String.format("Phone Number: %s\nMessage: %s\nDate: %s",
                    phoneNumber, message, date);

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Report Details", reportText);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Report copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        });

        // Delete button functionality
        holder.deleteButton.setOnClickListener(v -> {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            boolean isDeleted = databaseAccess.deleteReport(phoneNumber, message);

            if (isDeleted) {
                Toast.makeText(context, "Report deleted successfully", Toast.LENGTH_SHORT).show();

                // Get updated data and refresh RecyclerView
                Cursor newCursor = databaseAccess.getReports();
                updateCursor(newCursor);
                YourReportsActivity.updateReportCount(cursor);


                if (newCursor == null || newCursor.getCount() == 0) {
                    Toast.makeText(context, "No reports remaining on the system", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Failed to delete report", Toast.LENGTH_SHORT).show();
            }

            //databaseAccess.close();
        });
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button deleteButton;
        TextView phoneTextView;
        TextView messageTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneTextView = itemView.findViewById(R.id.detectionPhoneText);
            messageTextView = itemView.findViewById(R.id.detectionMessageText);
            dateTextView = itemView.findViewById(R.id.detectionDateText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
}
