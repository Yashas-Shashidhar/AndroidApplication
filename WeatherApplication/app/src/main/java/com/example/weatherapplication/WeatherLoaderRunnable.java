package com.example.weatherapplication;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;


public class WeatherLoaderRunnable implements Runnable {

    private static final String TAG = "WeatherLoaderRunnable";
    private final MainActivity mainActivity;
    private String unit;
    private String location;
    private static  String DATA_URL;
//    private static final String DATA_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/CHICAGO%2CIL?unitGroup=metric&include=events%2Cdays%2Chours%2Calerts%2Ccurrent&key=2MX5LKBC8M3T6MK5T4MKK3F5U";
    //private static final String DATA_URL = "https://restcountries.com/v3.1/all";
    WeatherLoaderRunnable(MainActivity mainActivity,String unit,String location) {
        this.mainActivity = mainActivity;
        this.unit=unit;
        this.location=location;
    }


    public void setUnit(String unit){this.unit=unit;}
    public void setLocation(String loc){this.location=loc;}

    public String getUnit(){return this.unit;}
    public String getLocation(){return this.location;}

   // private static final String DATA_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"+this.location.toString()+"?unitGroup=metric&include=events%2Cdays%2Chours%2Calerts%2Ccurrent&key=2MX5LKBC8M3T6MK5T4MKK3F5U";
    @Override
    public void run() {
        DATA_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"+this.location+"?unitGroup=metric&include=events%2Cdays%2Chours%2Calerts%2Ccurrent&key=2MX5LKBC8M3T6MK5T4MKK3F5U";
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run1: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb);

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());

    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }


    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        final ArrayList<Weather> weatherListList = parseJSON(s);
        if (weatherListList == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        mainActivity.runOnUiThread(() -> mainActivity.updateData(weatherListList));
    }

    private String gettimeformat(String date_time)
    {
        Long dateTimeEpoch=Long.parseLong(date_time);
        Date dateTime= new Date(dateTimeEpoch*1000);
        SimpleDateFormat fullDate =
                new SimpleDateFormat("EEE MMM dd h:mm a, yyyy", Locale.getDefault());
        SimpleDateFormat timeOnly = new SimpleDateFormat("h:mm a", Locale.getDefault());
        SimpleDateFormat dayDate = new SimpleDateFormat("EEEE MM/dd", Locale.getDefault());
        String timeOnlyStr = timeOnly.format(dateTime);
        return  timeOnlyStr;
    }

    private String tofahrenhiet(String temprature_cel){
        Integer temprature_float=Math.round((Float.parseFloat(temprature_cel)));
        Integer fahrenheit=Math.round((temprature_float *9/5)+ 32);
        String temp_val=fahrenheit.toString() +"\u2109";
        if (this.unit.equals("metric")) {
            //temp_val=String.format("%.2f", temprature_float);
            temp_val= temprature_float.toString() +"\u2103";
        }
        return temp_val;
    }

    private int weather_icon(String icon) {
        icon = icon.replace("-", "_"); // Replace all dashes with underscores
        int iconID =
                mainActivity.getResources().getIdentifier(icon, "drawable", mainActivity.getPackageName());
        if (iconID == 0) {
            Log.d(TAG, "parseCurrentRecord: CANNOT FIND ICON " + icon);
        }
        return iconID;
    }



    private ArrayList<Weather> parseJSON(String s) {

        ArrayList<Weather> weatherList = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(s);
            String lattitude = jObj.getString("latitude");

            String longitude =jObj.getString("longitude");

            //location
            String location_1=jObj.getString("address");


            JSONArray jr = jObj.getJSONArray("days");
            for (int i = 0; i < jr.length(); i++) {
                  JSONObject jb1 = jr.getJSONObject(i);

                  //dat_time_epoch
                  String date_time = jb1.getString("datetimeEpoch");


                  Long dateTimeEpoch=Long.parseLong(date_time);
                  Date dateTime= new Date(dateTimeEpoch*1000);
                  SimpleDateFormat fullDate =
                        new SimpleDateFormat("EEE MMM dd h:mm a, yyyy", Locale.getDefault());
                  SimpleDateFormat timeOnly = new SimpleDateFormat("h:mm a", Locale.getDefault());
                  SimpleDateFormat dayDate = new SimpleDateFormat("EEEE MM/dd", Locale.getDefault());
                  String fullDateStr = fullDate.format(dateTime);

                  //recy_dateday
                  String dayDate_recy= dayDate.format(dateTime);
                  dayDate_recy=dayDate_recy.replace(" ",",");

                  //condition
                  String condition= jb1.getString("conditions");

                  //descp
                  String descp=jb1.getString("description");

                  //precipprob
                  String precipprob="";
                  if (precipprob!="null"){
                      precipprob=jb1.getString("precipprob");
                  int i_precipprob=(int)(Float.parseFloat(precipprob));
                      precipprob=String.valueOf(i_precipprob);}



                  //UV_indx
                  String uv_index=jb1.getString("uvindex");
                  Integer i_uvindx=(int)(Float.parseFloat(uv_index));
                  uv_index=String.valueOf(i_uvindx);

                  //Humidity
                  String humidity=jb1.getString("humidity");
                  Integer i_humid=(int)(Float.parseFloat(humidity));
                  humidity=String.valueOf(i_humid);


                  //Visibilty
                  String visibilty=jb1.getString("visibility");

                  //Winds
                  String winds=jb1.getString("winddir");
                  Double wind_cel=Double.parseDouble(winds);
                  String wind_dir =getDirection(wind_cel);

                  //Windspeed
                  String windspeed=jb1.getString("windspeed");

                  //Windgust
                  String windgust=jb1.getString("windgust");

                  //Sunrise
                  String sunrise=jb1.getString("sunriseEpoch");
                  String sunrise_time=gettimeformat(sunrise);

                  //Sunset
                  String sunset=jb1.getString("sunsetEpoch");
                  String sunset_time=gettimeformat(sunset);

                  //Feelslike
                  String feels_like= jb1.getString("feelslike");
                  feels_like=tofahrenhiet(feels_like);


                  //tmep_max
                  String temp_max=jb1.getString("tempmax");
                  temp_max=tofahrenhiet(temp_max);

                 //temp_min
                 String temp_min=jb1.getString("tempmin");
                 temp_min=tofahrenhiet(temp_min);

                 //weather_icon
                 String icon=jb1.getString("icon");
                 int icon_i=weather_icon(icon);
                 icon=String.valueOf(icon_i);


                //cloudcover
                String cloudcover=jb1.getString("cloudcover");
                int i_cloudcover = Math.round(Float.parseFloat(cloudcover));
                cloudcover = String.valueOf(i_cloudcover);

                 //Current temp
                 String temp=jb1.getString("temp");
                 temp=tofahrenhiet(temp);

                JSONArray hour=jb1.getJSONArray("hours");

                 //Morning temprature
                JSONObject hour_obj = hour.getJSONObject(8);
                String morn_temp=hour_obj.getString("temp");
                morn_temp=tofahrenhiet(morn_temp);

                //Afternoon temprature
                JSONObject noon_obj = hour.getJSONObject(13);
                String noon_temp=noon_obj.getString("temp");
                noon_temp=tofahrenhiet(noon_temp);

                //Evening temprature
                JSONObject eve_obj = hour.getJSONObject(17);
                String eve_temp=eve_obj.getString("temp");
                eve_temp=tofahrenhiet(eve_temp);

                //Night temprature
                JSONObject night_obj = hour.getJSONObject(23);
                String night_temp= night_obj.getString("temp");
                night_temp=tofahrenhiet(night_temp);



                //HourWeatherli
                ArrayList<HourWeather> hourWeatherArrayList=new ArrayList<>();
                for (int j = 0; j < hour.length(); j++) {
                    JSONObject hour_val = hour.getJSONObject(j);

                    //day
                    String hday=hour_val.getString("datetimeEpoch");
                    Long hdateTimeEpoch=Long.parseLong(hday);
                    Date hdateTime = new Date(hdateTimeEpoch * 1000);
                    SimpleDateFormat hdayDate = new SimpleDateFormat("EEEE MM/dd", Locale.getDefault());
                    String dayDateStr = hdayDate.format(hdateTime);
                    String dayDateStr_new=dayDateStr.split(" ")[1];
                    dayDateStr=dayDateStr.split(" ")[0];


                    SimpleDateFormat dateFormat = new SimpleDateFormat("uuuu/MM/dd");
                    Date dated = new Date();
                    String s_dated = dateFormat.format(dated);

                    if ((s_dated.split("/")[1].equals(dayDateStr_new.split("/")[0])) && (s_dated.split("/")[2].equals(dayDateStr_new.split("/")[1]))){
                        dayDateStr="Today";
                    }


                    //time
                    String time=hour_val.getString("datetimeEpoch");
                    time=gettimeformat(time);

                    //temp
                    String htemp=hour_val.getString("temp");
                    temp=tofahrenhiet(htemp);

                    //icon
                    String hicon=hour_val.getString("icon");
                    int hicon_i= weather_icon(hicon);
                    icon = String.valueOf(hicon_i);

                    //desp
                    String hdescp=hour_val.getString("conditions");
                    hourWeatherArrayList.add(new HourWeather(dayDateStr,time,icon,temp,hdescp));


                }

                weatherList.add(
                        new Weather(fullDateStr,dayDate_recy, temp_max, temp_min, descp, precipprob, humidity, uv_index, icon, wind_dir, windgust, windspeed, morn_temp, noon_temp, night_temp, eve_temp,sunrise_time,sunset_time,temp,feels_like,visibilty,cloudcover,hourWeatherArrayList,location_1,lattitude,longitude,condition));
            }

            return weatherList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
