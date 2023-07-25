package com.example.weatherapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class WeatherViewHolder extends RecyclerView.ViewHolder {
    TextView day_date;
    TextView min_max_temp;
    TextView descp;
    TextView prob_presp;
    TextView uv_index;
    ImageView weather_icon;
    TextView morning_temp, noon_temp, night_temp, eve_temp;

    WeatherViewHolder(View view) {
        super(view);
        day_date = view.findViewById(R.id.recy_day_date);
        min_max_temp = view.findViewById(R.id.recy_min_max);
        descp = view.findViewById(R.id.recy_descp);
        prob_presp = view.findViewById(R.id.recy_precp);
        uv_index = view.findViewById(R.id.recy_uv_indx);
        weather_icon = view.findViewById(R.id.recy_weather_icon);
        morning_temp = view.findViewById(R.id.recy_morning_temp);
        noon_temp = view.findViewById(R.id.recy_afternooon_temp);
        night_temp = view.findViewById(R.id.recy_night_temp);
        eve_temp = view.findViewById(R.id.recy_eve_temp);
    }
}

