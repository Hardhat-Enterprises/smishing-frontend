package com.example.smishingdetectionapp.news;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

public class NewsAdapter extends ListAdapter<RSSFeedModel.Article, NewsViewHolder> {
    private final SelectListener listener;

    public NewsAdapter(SelectListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<RSSFeedModel.Article> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RSSFeedModel.Article>() {
                @Override
                public boolean areItemsTheSame(@NonNull RSSFeedModel.Article oldItem, @NonNull RSSFeedModel.Article newItem) {
                    return oldItem.link.equals(newItem.link); // assume link is unique
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull RSSFeedModel.Article oldItem, @NonNull RSSFeedModel.Article newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_items, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        RSSFeedModel.Article article = getItem(position);

        // Sanitize & clean the description (specific to your feed)
        String formattedDescription = article.description.replaceAll("\\<.*?\\>", "");
        try {
            formattedDescription = formattedDescription.substring(84, formattedDescription.length() - 14);
        } catch (Exception e) {
            Log.w("DescriptionParse", "Formatting failed, using raw description", e);
        }

        holder.text_title.setText(article.title);
        holder.text_description.setText(formattedDescription);
        holder.text_pubDate.setText(article.getFormattedDate());

        holder.cardView.setOnClickListener(v -> listener.OnNewsClicked(article));
    }
}
