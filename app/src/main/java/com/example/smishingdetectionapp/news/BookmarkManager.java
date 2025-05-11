package com.example.smishingdetectionapp.news;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smishingdetectionapp.news.Models.RSSFeedModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarkManager {

    private static final String PREFS_NAME = "BookmarksPrefs";
    private static final String KEY_BOOKMARKS = "bookmarked_articles";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public BookmarkManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveBookmark(RSSFeedModel.Article article) {
        List<RSSFeedModel.Article> savedArticles = getBookmarks();

        // Avoid duplicates based on link
        for (RSSFeedModel.Article a : savedArticles) {
            if (a.link != null && a.link.equals(article.link)) return;
        }

        savedArticles.add(article);
        String json = gson.toJson(savedArticles);
        sharedPreferences.edit().putString(KEY_BOOKMARKS, json).apply();
    }

    public void removeBookmark(String link) {
        List<RSSFeedModel.Article> savedArticles = getBookmarks();
        savedArticles.removeIf(article -> article.link != null && article.link.equals(link));
        String json = gson.toJson(savedArticles);
        sharedPreferences.edit().putString(KEY_BOOKMARKS, json).apply();
    }

    public boolean isBookmarked(String link) {
        for (RSSFeedModel.Article article : getBookmarks()) {
            if (article.link != null && article.link.equals(link)) {
                return true;
            }
        }
        return false;
    }

    public List<RSSFeedModel.Article> getBookmarks() {
        String json = sharedPreferences.getString(KEY_BOOKMARKS, null);
        if (json == null) return new ArrayList<>();
        Type listType = new TypeToken<List<RSSFeedModel.Article>>() {}.getType();
        try {
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
