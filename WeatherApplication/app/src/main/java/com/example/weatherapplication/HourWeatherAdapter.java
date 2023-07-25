package com.example.weatherapplication;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class HourWeatherAdapter extends RecyclerView.Adapter<HourWeatherViewHolder> {
    private final List<HourWeather> hourWeatherList;
    private final MainActivity mainAct;

    HourWeatherAdapter(List<HourWeather> hourWeatherList, MainActivity ma) {
        this.hourWeatherList = hourWeatherList;
        mainAct = ma;
    }

    public HourWeatherViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_each_day_weather, parent, false);

        // itemView.setOnClickListener(mainAct);
        //itemView.setOnLongClickListener(mainAct);

        return new HourWeatherViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull HourWeatherViewHolder holder, int position) {

        HourWeather hourWeather = hourWeatherList.get(position);

        holder.time.setText(hourWeather.getTime());
        holder.temp.setText(hourWeather.getTemp());
        holder.descp.setText(hourWeather.getDescp());
        holder.day.setText(hourWeather.getDay());
        holder.icon.setImageResource(Integer.parseInt(hourWeather.getIcon()));
    }


    public int getItemCount() {
        return hourWeatherList.size();
    }
}



