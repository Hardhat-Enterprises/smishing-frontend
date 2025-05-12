package com.example.smishingdetectionapp.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

import java.util.List;

public class SavedNewsActivity extends AppCompatActivity implements SelectListener {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private BookmarkManager bookmarkManager;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);

        bookmarkManager = new BookmarkManager(this);

        ImageButton backButton = findViewById(R.id.news_bookmark_back);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_saved_news);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        emptyMessage = findViewById(R.id.text_no_saved_news);

        List<RSSFeedModel.Article> savedArticles = bookmarkManager.getBookmarks();

        if (savedArticles == null || savedArticles.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new NewsAdapter(this, this);   // <-- two-arg constructor
            recyclerView.setAdapter(adapter);
            adapter.submitList(savedArticles);       // <-- pass the data her
        }
    }

    @Override
    public void OnNewsClicked(RSSFeedModel.Article article) {
        if (article != null && article.link != null && !article.link.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
            startActivity(browserIntent);
        }
    }
}
