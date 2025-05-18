package com.example.smishingdetectionapp.Community;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

import java.util.List;

public class CommunityCommentAdapter extends RecyclerView.Adapter<CommunityCommentAdapter.CommentViewHolder> {

    private final List<CommunityComment> comments;
    private final Context context;
    private final int postId;

    public CommunityCommentAdapter(Context context, List<CommunityComment> comments, int postId) {
        this.context = context;
        this.comments = comments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommunityComment comment = comments.get(position);
        holder.commentText.setText(comment.getUser() + " • " + comment.getDate() + "\n" + comment.getCommentText());

        // Get stored user ID
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserId = prefs.getString("user_id", "");

        // Show delete icon only if user matches
        if (comment.getUser().equals(currentUserId)) {
            holder.deleteIcon.setVisibility(View.VISIBLE);
            holder.deleteIcon.setOnClickListener(v -> {
                CommunityDatabaseAccess db = new CommunityDatabaseAccess(context);
                db.open();
                db.deleteSingleComment(comment.getCommentId());
                db.updatePostComments(postId, comments.size() - 1); // update count
                db.close();

                comments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, comments.size());
                Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.deleteIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        ImageView deleteIcon;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.commentText);
            deleteIcon = itemView.findViewById(R.id.deleteCommentIcon);
        }
    }
}