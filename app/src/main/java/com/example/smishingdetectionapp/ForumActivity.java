package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.ui.CommunityPost;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private List<CommunityPost> posts = new ArrayList<>();
    private CommunityPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums);

        // Back button setup
        ImageButton report_back = findViewById(R.id.forum_back_button);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityPostAdapter(posts);
        recyclerView.setAdapter(adapter);


        // Submit new post functionality
        EditText userThoughtsInput = findViewById(R.id.userThoughtsInput);
        Button submitThoughtsButton = findViewById(R.id.submitThoughtsButton);

        submitThoughtsButton.setOnClickListener(v -> {
            String content = userThoughtsInput.getText().toString().trim();
            if (!content.isEmpty()) {
                // Pass an empty list for comments
                posts.add(new CommunityPost("New Post", content, "You", new ArrayList<>()));
                adapter.notifyItemInserted(posts.size() - 1);
                userThoughtsInput.setText("");
                Toast.makeText(this, "Post added successfully!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Adapter for RecyclerView
    private static class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.CommunityPostViewHolder> {

        private final List<CommunityPost> posts;

        public CommunityPostAdapter(List<CommunityPost> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public CommunityPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the updated item layout for community posts (item_community_post.xml)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post, parent, false);
            return new CommunityPostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommunityPostViewHolder holder, int position) {
            CommunityPost post = posts.get(position);

            // Bind post title and content
            holder.title.setText(post.getTitle());
            holder.content.setText(post.getContent());

            // Set up the comments adapter for the nested RecyclerView
            CommentsAdapter commentsAdapter = new CommentsAdapter(post.getComments());
            holder.commentsRecyclerView.setAdapter(commentsAdapter);
            holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

            // Add new comment logic
            holder.addCommentButton.setOnClickListener(v -> {
                String newComment = holder.addCommentInput.getText().toString().trim();
                if (!newComment.isEmpty()) {
                    post.getComments().add(newComment); // Add the new comment to the list
                    commentsAdapter.notifyItemInserted(post.getComments().size() - 1); // Notify adapter of the new comment
                    holder.addCommentInput.setText(""); // Clear the input field
                }
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        static class CommunityPostViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView content;
            RecyclerView commentsRecyclerView; // Nested RecyclerView for comments
            EditText addCommentInput; // Input field for new comments
            Button addCommentButton; // Button to add a new comment

            public CommunityPostViewHolder(@NonNull View itemView) {
                super(itemView);

                // Reference the views from the updated item layout
                title = itemView.findViewById(R.id.post_title);
                content = itemView.findViewById(R.id.post_content);
                commentsRecyclerView = itemView.findViewById(R.id.comments_recycler_view);
                addCommentInput = itemView.findViewById(R.id.add_comment_input);
                addCommentButton = itemView.findViewById(R.id.add_comment_button);
            }
        }
    }

    // Adapter for the comments RecyclerView
    private static class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

        private final List<String> comments;

        public CommentsAdapter(List<String> comments) {
            this.comments = comments;
        }

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate a simple item layout for comments (e.g., item_comment.xml)
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            // Bind the comment data
            String comment = comments.get(position);
            holder.commentText.setText(comment);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        static class CommentViewHolder extends RecyclerView.ViewHolder {
            TextView commentText;

            public CommentViewHolder(@NonNull View itemView) {
                super(itemView);
                commentText = itemView.findViewById(android.R.id.text1); // Simple TextView in the layout
            }
        }
    }


}
