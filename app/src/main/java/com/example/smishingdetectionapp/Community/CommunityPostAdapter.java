package com.example.smishingdetectionapp.Community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

import java.util.ArrayList;
import java.util.List;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder> {

    private final List<CommunityPost> originalPost;
    private final List<CommunityPost> filteredPost;
    private final Context context;
    private final String currentUserId;

    public CommunityPostAdapter(List<CommunityPost> postList, Context context) {
        this.context = context;
        this.originalPost = postList;
        this.filteredPost = new ArrayList<>(postList);

        // Retrieve user ID from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getString("user_id", "you");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = filteredPost.get(position);
        holder.username.setText(post.getUsername());
        holder.date.setText(post.getDate());
        holder.posttitle.setText(post.getPosttitle());
        holder.postdescription.setText(post.getPostdescription());
        holder.likes.setText(String.valueOf(post.getLikes()));
        holder.comments.setText(String.valueOf(post.getComments()));

        // share button listener
        holder.shareIcon.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = post.getUsername() + " wrote:\n\n" + post.getPostdescription();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            v.getContext().startActivity(Intent.createChooser(shareIntent, "Share post via"));
        });

        // Allow deletion only if current user posted comment or post
        if (post.getUsername().equals(currentUserId)) {
            holder.deleteIcon.setVisibility(View.VISIBLE);
            holder.deleteIcon.setOnClickListener(v -> {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Yes", (dialogInterface, which) -> {
                            CommunityDatabaseAccess dbAccess = new CommunityDatabaseAccess(context);
                            dbAccess.open();
                            dbAccess.deletePost(post.getId());
                            dbAccess.close();

                            originalPost.remove(post);
                            filteredPost.remove(post);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .create();

                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            });
        } else {
            holder.deleteIcon.setVisibility(View.GONE);
        }

        // Open post
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommunityOpenPost.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("username", post.getUsername());
            intent.putExtra("date", post.getDate());
            intent.putExtra("posttitle", post.getPosttitle());
            intent.putExtra("postdescription", post.getPostdescription());
            intent.putExtra("likes", post.getLikes());
            intent.putExtra("comments", post.getComments());
            intent.putExtra("position", holder.getAdapterPosition());
            ((Activity) context).startActivityForResult(intent, 200);
        });
    }

    @Override
    public int getItemCount() {
        return filteredPost.size();
    }

    // case sensitive search and advanced filter based on category
    public void filter(String query, String field) {
        filteredPost.clear();
        String lower = query == null ? "" : query.toLowerCase();

        if (query.trim().isEmpty()) {
            filteredPost.addAll(originalPost);
        } else if (field.equals("all")) {
            for (CommunityPost post : originalPost) {
                if (post.getUsername().toLowerCase().contains(lower) ||
                        post.getPosttitle().toLowerCase().contains(lower) ||
                        post.getPostdescription().toLowerCase().contains(lower) ||
                        String.valueOf(post.getLikes()).contains(lower) ||
                        String.valueOf(post.getComments()).contains(lower) ||
                        (post.getDate() != null && post.getDate().toLowerCase().contains(lower))) {
                    filteredPost.add(post);
                }
            }
        } else {
            for (CommunityPost post : originalPost) {
                switch (field) {
                    case "username":
                        if (post.getUsername().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                    case "title":
                        if (post.getPosttitle().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                    case "description":
                        if (post.getPostdescription().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                    case "likes":
                        if (String.valueOf(post.getLikes()).contains(lower)) filteredPost.add(post);
                        break;
                    case "comments":
                        if (String.valueOf(post.getComments()).contains(lower)) filteredPost.add(post);
                        break;
                    case "date":
                        if (post.getDate() != null && post.getDate().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                }
            }
        }

        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, date, posttitle, postdescription, likes, comments;
        ImageView userIcon, likeIcon, commentIcon, deleteIcon, shareIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            posttitle = itemView.findViewById(R.id.posttitle);
            postdescription = itemView.findViewById(R.id.postdescription);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            userIcon = itemView.findViewById(R.id.userIcon);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            shareIcon = itemView.findViewById(R.id.shareIcon);
        }
    }
}