package com.example.newsaggregator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class NewsViewHolder extends RecyclerView.ViewHolder {

    TextView author;
    TextView headline;
    TextView date;
    TextView description;
    TextView pagenum;
    ImageView article_Img;

    NewsViewHolder(View itemView) {
        super(itemView);
        author = itemView.findViewById(R.id.news_author);
        headline = itemView.findViewById(R.id.news_headline);
        date = itemView.findViewById(R.id.news_article_date);
        description = itemView.findViewById(R.id.news_text);
        pagenum = itemView.findViewById(R.id.news_pageno);
        article_Img=itemView.findViewById(R.id.news_image);
    }

}

