package com.example.weatherapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder>{

    private final List<Weather> weatherList;
    private final DayWeatherActivity dayWeatherActivity;

    WeatherAdapter(List<Weather> weatherList, DayWeatherActivity ma){
        this.weatherList=weatherList;
        dayWeatherActivity=ma;
    }

    public WeatherViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weatherlayout, parent, false);



        return new WeatherViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {

        Weather weather = weatherList.get(position);

        holder.day_date.setText(weather.getDay_recy_format());
        holder.min_max_temp.setText(weather.getMaxTemp()+"/"+weather.getMinTemp());
        holder.descp.setText(weather.getDescp());
        holder.uv_index.setText("UV Index:"+weather.getUVIndx());
        holder.prob_presp.setText("("+weather.getProbPrecp()+"%precip)");
        holder.morning_temp.setText(weather.getMorningTemp());
        holder.eve_temp.setText(weather.getEvenTemp());
        holder.noon_temp.setText(weather.getAfternoonTemp());
        holder.night_temp.setText(weather.getNigthTemp());
        holder.weather_icon.setImageResource(Integer.parseInt(weather.getWeatherIcon()));

    }

    public int getItemCount() {
        return weatherList.size();
    }
}
