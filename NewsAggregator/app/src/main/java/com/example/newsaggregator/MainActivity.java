package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import java.util.Random;
import android.text.SpannableString;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.android.volley.AuthFailureError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private long start;
    private static RequestQueue queue;
    private static final String urlString =
            "https://newsapi.org/v2/sources?apiKey=08c9f6131f44496993367f14a405f7d9";
    //private final ArrayList<NewsChannel> channelList1 = new ArrayList<>();
    private Menu option_menu;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private final HashMap<String, HashSet<String>> topicToChannel = new HashMap<>();
    private final HashMap<String, String> ChannelToId = new HashMap<>();
    private final HashMap<String, Integer> col_topic = new HashMap<>();
    private final HashMap<String, Integer> col_channel = new HashMap<>();
    //private String[] ChannelList = new String[15];
    private String[] items;
    private final ArrayList<String> ChannelList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private final ArrayList<News> NewsList = new ArrayList<>();
    private ViewPager2 viewPager;
    private NewsAdapter newsAdapter;
    String chosenArticle = "";
    String chosenArticleID = "";
    String selectedTopic = "";
    HashMap<String, HashSet<String>> top_ToChannel = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        if (hasNetworkConnection()) {
            performDownload();
        }
        else{
            setTitle("No Network Connection");

        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

 //       arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_item, ChannelList);

//        mDrawerList.setAdapter(arrayAdapter);
        arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.drawer_item, ChannelList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(R.id.text_view1);
                int col =col_channel.get(textView.getText().toString());
                textView.setTextColor(col);
                return textView;
            }
        };
        mDrawerList.setAdapter(arrayAdapter);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,            /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        newsAdapter = new NewsAdapter(this, NewsList);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(newsAdapter);

        if (getSupportActionBar() != null) {  // <== Important!
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("Topic", selectedTopic);
        outState.putString("Article", chosenArticle);
        outState.putString("Article_ID", chosenArticleID);
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);


        selectedTopic = savedInstanceState.getString("Topic");
        chosenArticle = savedInstanceState.getString("Article");
        chosenArticleID = savedInstanceState.getString("Article_ID");

        //performDownload();


//         if(!selectedTopic.equals("")) {
//             //ChannelList.clear();
//             HashSet<String> lst = topicToChannel.get(selectedTopic.toString());
//             if (lst == null){
//                 Toast.makeText(this,"shr=="+"list null", Toast.LENGTH_SHORT).show();
//             }
//             if (lst != null) {
//                 ChannelList.addAll(lst);
//             }
//             arrayAdapter.notifyDataSetChanged();
//             setTitle("News Gateway"+ "("+Integer.toString(ChannelList.size())+")");
//             //setTitle("News Gateway"+"("+Integer.toString(ChannelList.size())+")");
//         }

        if(!TextUtils.isEmpty(chosenArticle)){
           performDownloadNewsArticle(chosenArticleID);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig); // <== IMPORTANT
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }



    private void selectItem(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        String channel = ChannelList.get(position);
        String channel_id = (ChannelToId.get(channel).toString());
        chosenArticle=channel;
        chosenArticleID=channel_id;

        //Pattern pattern = Pattern.compile("\\[(.*)\\]");
        //Matcher matcher = pattern.matcher(channel_id);
        setTitle( chosenArticle);
        NewsList.clear();
        performDownloadNewsArticle(channel_id);
    }

    public void clickcivicapi(View v) {
        int pos = (int) v.getTag();
        //Log.d("MainActivity","pos====="+String.valueOf(pos));
        String web_url=NewsList.get(pos).url;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(web_url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        option_menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        chosenArticle = "";
        chosenArticleID = "";
        selectedTopic = " ";

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d("mainActivity", "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
       // setTitle(item.getTitle()+"shree");
        selectedTopic = item.getTitle().toString();
        NewsList.clear();
        newsAdapter.notifyDataSetChanged();
        ChannelList.clear();
        top_ToChannel=topicToChannel;
        HashSet<String> lst = topicToChannel.get(item.getTitle().toString());
        if (lst != null) {
            ChannelList.addAll(lst);
        }
        arrayAdapter.notifyDataSetChanged();
        setTitle("News Gateway"+ "("+Integer.toString(ChannelList.size())+")");
        return super.onOptionsItemSelected(item);
    }


    public void performDownloadNewsArticle(String channelid) {
        setTitle( chosenArticle);
    String urllink="https://newsapi.org/v2/top-headlines?sources="+channelid+"&apiKey=08c9f6131f44496993367f14a405f7d9";

        start = System.currentTimeMillis();

        Response.Listener<JSONObject> listener =
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jr = response.getJSONArray("articles");
                            for (int j=0;j<jr.length();j++)
                            {
                                JSONObject jb=jr.getJSONObject(j);
                                String author_name=jb.getString("author");

                                String headline=jb.getString("title");

                                String url=jb.getString("url");

                                String urltoImg=jb.getString("urlToImage");

                                String publishedAt=jb.getString("publishedAt");

                                String descp=jb.getString("description");

                                NewsList.add(new News(author_name,headline,descp,url,urltoImg,publishedAt));

                            }
                            newsAdapter.notifyDataSetChanged();
                            viewPager.setCurrentItem(0);

                        } catch (Exception e) {
                        }
                    }
                };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urllink,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }
                };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    public void performDownload() {
        Random random = new Random();
        start = System.currentTimeMillis();

        Response.Listener<JSONObject> listener =
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray jr = response.getJSONArray("sources");
                            for (int i = 0; i < jr.length(); i++) {
                                JSONObject jb1 = jr.getJSONObject(i);
                                String channelid = jb1.getString("id");
                                String channelname=jb1.getString("name");
                                String channelcategory=jb1.getString("category");


                                if (!topicToChannel.containsKey(channelcategory))
                                    topicToChannel.put(channelcategory, new HashSet<>());
                                Objects.requireNonNull(topicToChannel.get(channelcategory)).add(channelname);
                                if (!topicToChannel.containsKey("All"))
                                    topicToChannel.put("All", new HashSet<>());
                                Objects.requireNonNull(topicToChannel.get("All").add(channelname));
                                if(!ChannelToId.containsKey(channelname))
                                     ChannelToId.put(channelname,channelid);
                                //channelList1.add(new NewsChannel(channelid,channelname,channelcategory));
                            }

                            ArrayList<String> tempList = new ArrayList<>(topicToChannel.keySet());
                            Random random = new Random();
                            SpannableString span_str;
                            int value;

                            Collections.sort(tempList);
                            for (String str_1 : tempList) {
                                span_str=new SpannableString(str_1);
                                value = Color.rgb(random.nextInt(255),
                                       random.nextInt(255), random.nextInt(255));
                                span_str.setSpan(new ForegroundColorSpan(value), 0, str_1.length(), 0);
                                col_topic.put(str_1, value);
                                HashSet<String> str_val=topicToChannel.get(str_1);
                                for (String st:str_val){
                                   col_channel.put(st,value);
                                }
                                option_menu.add(span_str);
                            }


                        } catch (Exception e) {
                        }
                    }
                };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlString,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }
                };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

}