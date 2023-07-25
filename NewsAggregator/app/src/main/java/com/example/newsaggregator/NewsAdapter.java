package com.example.newsaggregator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Locale;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder>{

    private final MainActivity mainActivity;
    private final ArrayList<News> NewsList;
    private long start;
    private static RequestQueue queue;
    //public int arr_pos;
    private int currentPage;

    public NewsAdapter(MainActivity mainActivity, ArrayList<News> newsList) {
        this.mainActivity = mainActivity;
        this.NewsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.news_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News n = NewsList.get(position);
        holder.author.setText(n.getAuthor());
        holder.headline.setText(n.getTitle());
        //holder.date.setText(n.getPublishedAt());
        holder.description.setText(n.getDescp());
        holder.article_Img.setImageResource(R.drawable.loading);
        holder.pagenum.setText(String.format(
                Locale.getDefault(),"%d of %d", (position+1), NewsList.size()));
        if (!n.getUrl().equals("null")) {
            queue = Volley.newRequestQueue(this.mainActivity);
            downloadImage(holder, n.getUrlToImg());
        }
        else{
            holder.article_Img.setImageResource(R.drawable.noimage);

        }
        holder.headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String web_url= NewsList.get(holder.getAdapterPosition()).getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(web_url));
                if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
                    mainActivity.startActivity(intent);
                }

            }
        });

        holder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String web_url= NewsList.get(holder.getAdapterPosition()).getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(web_url));
                if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
                    mainActivity.startActivity(intent);
                }

            }
        });

        holder.article_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String web_url= NewsList.get(holder.getAdapterPosition()).getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(web_url));
                if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
                    mainActivity.startActivity(intent);
                }

            }
        });

        if((n.getAuthor().equals("null"))){
            holder.author.setVisibility(View.GONE);
        }

        if((n.getTitle().equals("null"))){
            holder.headline.setVisibility(View.GONE);
        }
        if(n.getDescp().equals("null")){
            holder.description.setVisibility(View.GONE);
        }
        if(n.getUrlToImg().equals("null")){
            holder.article_Img.setImageResource(R.drawable.noimage);
        }
        if((n.getPublishedAt().equals("null"))){
            holder.date.setVisibility(View.GONE);
        }
        else{
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = parser.parse(n.getPublishedAt().split("T")[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            String formattedDate = formatter.format(date);
            holder.date.setText(formattedDate +" " + n.getPublishedAt().split("T")[1].split(":")[0] + ":" + n.getPublishedAt().split("T")[1].split(":")[1]);
        }
    }


    public int getItemCount() {
        return NewsList.size();
    }

    //public int getPosition(){ return position;}

//
//    public void onPageSelected(int position) {
//        currentPage = position;
//    }
//
//    public final int getCurrentPage() {
//        return currentPage;
//    }


    private void downloadImage(@NonNull NewsViewHolder holder,String urlString) {

        Response.Listener<Bitmap> listener = response -> {
            long time = System.currentTimeMillis() - start;
            Log.d("NewsAdapter", "downloadImage: " + time);
            holder.article_Img.setImageBitmap(response);
        };
        urlString=urlString.replace("http://", "https://");
        Log.d("NewsAdapter",urlString);
        Response.ErrorListener error = error1 ->
                //Log.d("NotesAdapter","broken image");
                 holder.article_Img.setImageResource(R.drawable.brokenimage);

        ImageRequest imageRequest =
                new ImageRequest(urlString, listener, 0, 0,
                        ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, error);
        start = System.currentTimeMillis();
        queue.add(imageRequest);
    }

}

