package com.example.weatherapplication;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Weather implements Serializable{
    public String day_date;
    public String max_temp;
    public String min_temp;
    public String descp;
    public String prob_precp;
    public String uv_index;
    public String weather_icon;
    public String mornin_temp;
    public String afternoon_temp;
    public String night_temp;
    public String evening_temp;
    public String humidity;
    public String winddir;
    public String windspeed;
    public String windgust;
    public String sunrise;
    public String sunset;
    public String temprature;
    public String visibility;
    public String feels_like;
    public List<HourWeather> hourWeatherList;
    public String cloudcover;
    public String day_recy_format;
    public String location;
    public String lattitude;
    public String longitude;
    public String condition;

//    Weather(String day_date){
//        this.day_date=day_date;
//    }
    Weather(String day_date,String day_recy_format,String max_temp,String min_temp,String descp,String prob_precp,String humidity,String uv_index,
            String weather_icon, String winddir,String windgust,String windspeed,String mornin_temp,String afternoon_temp,String night_temp,String evening_temp,
            String sunrise,String sunset,String temprature,String feels_like,String visibility,String cloudcover,List<HourWeather> hourWeatherList,String location,String lattitde,String longitude,String condition){
        this.day_date=day_date;
        this.max_temp=max_temp;
        this.min_temp=min_temp;
        this.descp=descp;
        this.prob_precp=prob_precp;
        this.uv_index=uv_index;
        this.weather_icon=weather_icon;
        this.mornin_temp=mornin_temp;
        this.afternoon_temp=afternoon_temp;
        this.night_temp=night_temp;
        this.evening_temp=evening_temp;
        this.humidity=humidity;
        this.winddir=winddir;
        this.windspeed=windspeed;
        this.windgust=windgust;
        this.sunrise=sunrise;
        this.sunset=sunset;
        this.temprature=temprature;
        this.feels_like=feels_like;
        this.visibility=visibility;
        this.hourWeatherList=hourWeatherList;
        this.cloudcover=cloudcover;
        this.day_recy_format=day_recy_format;
        this.location=location;
        this.lattitude=lattitde;
        this.longitude=longitude;
        this.condition=condition;
    }


    public void setDayDate(String day_date) {
        this.day_date = day_date;
    }

    public void setMaxTemp(String max_temp) {
        this.max_temp = max_temp;
    }

    public void setMinTemp(String min_temp) {
        this.min_temp = min_temp;
    }

    public void setProbPrecp(String prob_precp) {
        this.prob_precp = prob_precp;
    }

    public void setDescp(String desp) {
        this.descp = descp;
    }

    public void setUVIndx(String uv_index) {
        this.uv_index = uv_index;
    }
    public void set_DayRecyFormat(String day_recy){this.day_recy_format=day_recy;}

    public void setWeatherIcon(String weather_icon) {
        this.weather_icon = weather_icon;
    }

    public void setMorningTemp(String mornin_temp) {
        this.mornin_temp = mornin_temp;
    }

    public void setAfternoonTemp(String afternoon_temp) {
        this.afternoon_temp = afternoon_temp;
    }

    public void setNigthTemp(String night_temp) {
        this.night_temp = night_temp;
    }

    public void setEvenTemp(String evening_temp) {
        this.evening_temp = evening_temp;
    }

    public void setLocation(String location){this.location=location;}
    public void setHumidity(String humidity){this.humidity=humidity;}
    public void setLattitude(String lattitude){this.lattitude=lattitude;}
    public void setLongitude(String longitude){this.longitude=longitude;}


    public void setWinddir(String winddir) {
        this.winddir = winddir;
    }

    public void setFeelslike(String feels_like){this.feels_like=feels_like;}
     public void setWindspeed(String windspeed){
        this.windspeed=windspeed;
    }
    public void setVisibility(String visibility){this.visibility=visibility;}
    public void setWindgust(String windgust){
        this.windgust=windgust;
    }
    public void setCloudcover(String cloudcover){this.cloudcover=cloudcover;}

    public void setSunrise(String sunrise){
        this.sunrise=sunrise;
    }

    public void setSunset(String sunset){
        this.sunset=sunset;
    }

    public void setTemprature(String temp){
        this.temprature=temp;
    }
    public void setCondition(String condition){this.condition=condition;}

    public String getDayDate(){return this.day_date;}
    public String getMaxTemp(){return this.max_temp;}
    public String getMinTemp(){return this.min_temp;}
    public String getProbPrecp(){return this.prob_precp;}
    public String getDescp(){return this.descp;}
    public String getUVIndx(){return this.uv_index;}
    public String getWeatherIcon(){return this.weather_icon;}
    public String getMorningTemp(){return this.mornin_temp;}
    public String getAfternoonTemp(){return this.afternoon_temp;}
    public String getNigthTemp(){return this.night_temp;}
    public String getEvenTemp(){return this.evening_temp;}
    public String getWinddir(){return this.winddir;}
    public String getWindspeed(){return this.windspeed;}
    public String getWindgust(){return this.windgust;}
    public String getSunrise(){return this.sunrise;}
    public String getSunset(){return this.sunset;}
    public String getTemprature(){return this.temprature;}
    public String getFeelslike(){return this.feels_like;}
    public String getHumidity(){return this.humidity;}
    public String getVisibility(){return this.visibility;}
    public String getCloudcover(){return this.cloudcover;}
    public String getDay_recy_format(){return this.day_recy_format;}
    public String getLattitude(){return this.lattitude;}
    public String getLongitude(){return this.longitude;}
    public String getLocation(){return this.location;}
    public String getCondition(){return  this.condition;}

    public String toString(){
            try{
                StringWriter sw=new StringWriter();
                JsonWriter jsonWriter=new JsonWriter(sw);
                jsonWriter.setIndent(" ");
                jsonWriter.beginObject();

                jsonWriter.name("day_date").value(getDayDate());

                jsonWriter.endObject();
                jsonWriter.close();
                return sw.toString();
            }catch (IOException e){
                e.printStackTrace();
            }
            return "";
        }

    }

