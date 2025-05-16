package com.example.smishingdetectionapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecentFeedbackAdapter extends RecyclerView.Adapter<RecentFeedbackAdapter.FeedbackViewHolder> {

    private final List<String> feedbackList;
    private final Context context;
    private final List<Integer> selectedItems = new ArrayList<>();
    private boolean selectionMode = false;

    public RecentFeedbackAdapter(Context context, List<String> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_feedback_item, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        String[] parts = feedbackList.get(position).split("\\|");
        if (parts.length < 3) return;

        String name = parts[0];
        String message = parts[1];
        float rating = Float.parseFloat(parts[2]);

        holder.tvUsername.setText("\uD83D\uDC64 " + name);
        holder.tvMessage.setText("\uD83D\uDCAC \"" + message + "\"");
        holder.ratingBar.setRating(rating);

        holder.itemView.setBackgroundColor(selectedItems.contains(position) ? 0xFFE0F7FA : 0x00000000);

        holder.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            selectionMode = true;
            toggleSelection(position);
            return true;
        });
    }

    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(Integer.valueOf(position));
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
    }

    public void deleteSelected() {
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            int index = selectedItems.get(i);
            String entry = feedbackList.get(index);
            feedbackList.remove(index);
            FeedbackMemoryStore.getFeedbackHistory().remove(entry);
            notifyItemRemoved(index);
        }
        selectedItems.clear();
        selectionMode = false;
    }

    public void cancelSelectionMode() {
        selectionMode = false;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvMessage;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}