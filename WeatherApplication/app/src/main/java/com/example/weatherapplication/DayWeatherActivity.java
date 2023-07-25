package com.example.weatherapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DayWeatherActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Weather> weatherList=new ArrayList<>();
    private WeatherAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_weather);
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new WeatherAdapter(weatherList,this);
        recyclerView.setAdapter(mAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);

        //Intent data = getIntent();
        Bundle bundle = getIntent().getExtras();
        //if (data.hasExtra("WEATHER_DATA")) {
        if (bundle!=null){
            String loc=bundle.getString("location");
            //Toast.makeText(this,"have reached here",Toast.LENGTH_SHORT).show();
            //ArrayList<Weather> weather_LI = ( ArrayList<Weather>)data.getSerializableExtra("WEATHER_DATA");
            ArrayList<Weather> weather_LI = ( ArrayList<Weather>)bundle.getSerializable("weatherlist");
            //Toast.makeText(this,"ppp==="+ weather_LI.get(0).getDayDate(),Toast.LENGTH_SHORT).show();
            for (Weather w : weather_LI) {
                //Toast.makeText(this,"inside loop==="+ w.getDayDate(),Toast.LENGTH_SHORT).show();
                weatherList.add(w);
                //mAdapter.notifyItemInserted(weatherList.size());
            }
            //setTitle(weatherList.get(0).location+" "+weatherList.size()+" Day");
            setTitle(loc+" "+weatherList.size()+" Day");

        }
        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FF786A50"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    public void handleResult(ActivityResult result) {
       // if (result.getResultCode() == RESULT_OK) {
            Toast.makeText(this,"have reached here111",Toast.LENGTH_SHORT).show();
            Intent data = result.getData();
            //if (data == null)
             //   return;
            if (data.hasExtra("WEATHER_DATA")) {
                Toast.makeText(this,"have reached here",Toast.LENGTH_SHORT).show();
                Weather weather = (Weather) data.getSerializableExtra("WEATHER_DATA");
                Toast.makeText(this,"ppp==="+ weather.getDayDate(),Toast.LENGTH_SHORT).show();
                //mAdapter.notifyDataSetChanged();
            } else {

            }

    }
}