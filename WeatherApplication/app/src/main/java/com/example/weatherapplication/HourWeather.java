package com.example.weatherapplication;

import java.io.Serializable;

public class HourWeather implements Serializable {
    private String day;
    private String time;
    private String icon;
    private String temp;
    private String descp;

    public  HourWeather(String day,String hour,String icon,String temp,String descp){
        this.day=day;
        this.time=hour;
        this.icon=icon;
        this.temp=temp;
        this.descp=descp;
    }

    public void setDay(String day){
        this.day=day;
    }

    public void setTime(String hour){
        this.time=hour;
    }

    public void setIcon(String icon){
        this.icon=icon;
    }

    public void setTemp(String temp){
        this.temp=temp;
    }

    public void descp(String descp){
        this.descp=descp;
    }


    public String getDescp(){return this.descp;}
    public String getIcon(){return this.icon;}
    public String getTemp(){return this.temp;}
    public String getDay(){return this.day;}
    public String getTime(){return this.time;}
}



