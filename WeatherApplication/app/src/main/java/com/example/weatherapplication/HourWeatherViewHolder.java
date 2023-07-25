package com.example.weatherapplication;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
public class HourWeatherViewHolder extends  RecyclerView.ViewHolder{
    TextView day,time ,temp,descp;
    ImageView icon;
    HourWeatherViewHolder(View view){
        super(view);
        day=view.findViewById(R.id.recy_hday);
        time=view.findViewById(R.id.recy_htime);
        temp=view.findViewById(R.id.recy_htemp);
        icon= view.findViewById(R.id.recy_hicon);
        descp= view.findViewById(R.id.recy_hdescp);
    }

}
