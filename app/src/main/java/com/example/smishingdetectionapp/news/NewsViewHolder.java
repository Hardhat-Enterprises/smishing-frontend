package com.example.smishingdetectionapp.news;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

public class NewsViewHolder extends RecyclerView.ViewHolder {
    TextView text_title, text_description, text_pubDate;
    CardView cardView;
    ImageButton bookmarkButton;

    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);

        text_title = itemView.findViewById(R.id.text_title);
        text_description = itemView.findViewById(R.id.text_description);
        text_pubDate = itemView.findViewById(R.id.text_pubDate);
        cardView = itemView.findViewById(R.id.card_view);
        bookmarkButton = itemView.findViewById(R.id.bookmarkButton); //
    }
}
