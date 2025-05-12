package com.example.smishingdetectionapp.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

/**
 * Displays RSS articles and lets the user bookmark them.
 * Uses ListAdapter + DiffUtil so NewsActivity can call submitList().
 */
public class NewsAdapter extends ListAdapter<RSSFeedModel.Article, NewsViewHolder> {

    private final SelectListener listener;
    private final BookmarkManager bookmarkManager;

    /* ---------- DiffUtil for efficient updates ---------- */
    private static final DiffUtil.ItemCallback<RSSFeedModel.Article> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RSSFeedModel.Article>() {
                @Override
                public boolean areItemsTheSame(@NonNull RSSFeedModel.Article o,
                                               @NonNull RSSFeedModel.Article n) {
                    return o.link.equals(n.link); // link is unique
                }
                @Override
                public boolean areContentsTheSame(@NonNull RSSFeedModel.Article o,
                                                  @NonNull RSSFeedModel.Article n) {
                    // include bookmark state so icon toggles properly
                    return o.equals(n) && o.isBookmarked() == n.isBookmarked();
                }
            };

    public NewsAdapter(Context ctx, SelectListener listener) {
        super(DIFF_CALLBACK);
        this.listener        = listener;
        this.bookmarkManager = new BookmarkManager(ctx);
    }

    /* ---------- View-holder creation ---------- */
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(
                LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.news_list_items, parent, false));
    }

    /* ---------- Bind article data + bookmark logic ---------- */
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder h, int pos) {
        RSSFeedModel.Article a = getItem(pos);

        /* Title & description */
        h.text_title.setText(a.title);
        String desc = a.description.replaceAll("\\<.*?\\>", "");
        try { desc = desc.substring(84, desc.length() - 14); }
        catch (Exception e) { Log.w("NewsAdapter", "Desc trim fallback", e); }
        h.text_description.setText(desc);
        h.text_pubDate.setText(a.getFormattedDate());

        /* Bookmark icon */
        boolean bookmarked = bookmarkManager.isBookmarked(a.link);
        a.setBookmarked(bookmarked);
        h.bookmarkButton.setImageResource(bookmarked
                ? R.drawable.ic_bookmark_filled
                : R.drawable.ic_bookmark_border);

        h.bookmarkButton.setOnClickListener(v -> {
            boolean now = !a.isBookmarked();
            a.setBookmarked(now);
            if (now) {
                bookmarkManager.saveBookmark(a);
                Toast.makeText(v.getContext(), "Bookmarked", Toast.LENGTH_SHORT).show();
            } else {
                bookmarkManager.removeBookmark(a.link);
                Toast.makeText(v.getContext(), "Bookmark removed", Toast.LENGTH_SHORT).show();
            }
            notifyItemChanged(pos);
        });

        h.cardView.setOnClickListener(v -> listener.OnNewsClicked(a));
    }
}
