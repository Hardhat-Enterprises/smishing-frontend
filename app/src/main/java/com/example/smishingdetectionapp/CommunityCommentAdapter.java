package com.example.smishingdetectionapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommunityCommentAdapter extends RecyclerView.Adapter<CommunityCommentAdapter.CommentViewHolder> {

    private final ArrayList<String> comments;

    public CommunityCommentAdapter(ArrayList<String> comments) {
        this.comments = comments;
    }

    // converting the comments to view objects using inflat and bind
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.commentText.setText(comments.get(position));
    }

    // counts number of comments
    @Override
    public int getItemCount() {
        return comments.size();
    }

    // renders the comments
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        CommentViewHolder(View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.commentText);
        }
    }
}