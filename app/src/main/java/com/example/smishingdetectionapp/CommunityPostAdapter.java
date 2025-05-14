package com.example.smishingdetectionapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder> {

    private final List<CommunityPost> originalPost;
    private final List<CommunityPost> filteredPost; //to work with the search bar

    public CommunityPostAdapter(List<CommunityPost> postList) {
        this.originalPost = postList;
        this.filteredPost = new ArrayList<>(postList);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_post, parent, false);
        return new PostViewHolder(view);
    }

    // link to CommunityPost.java to display the post
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = filteredPost.get(position);
        holder.username.setText(post.getUsername());
        holder.posttitle.setText(post.getPosttitle());
        holder.postdescription.setText(post.getPostdescription());
        holder.likes.setText(String.valueOf(post.getLikes()));
        holder.comments.setText(String.valueOf(post.getComments()));
    }

    @Override
    public int getItemCount() {
        return filteredPost.size();
    }

    // convert the searched content to lowercase before comparing so that it will be case insensitive
    public void filter(String query) {
        filteredPost.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredPost.addAll(originalPost);
        } else {
            String lower = query.toLowerCase();
            for (CommunityPost post : originalPost) {
                if (post.getUsername().toLowerCase().contains(lower) ||
                        post.getPosttitle().toLowerCase().contains(lower) ||
                        post.getPostdescription().toLowerCase().contains(lower)) {
                    filteredPost.add(post);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, posttitle, postdescription, likes, comments;
        ImageView userIcon, likeIcon, commentIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            posttitle = itemView.findViewById(R.id.posttitle);
            postdescription = itemView.findViewById(R.id.postdescription);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            userIcon = itemView.findViewById(R.id.userIcon);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
        }
    }
}