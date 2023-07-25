package com.example.weatherapplication;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private ArrayList<Weather> weatherList = new ArrayList<>();
    private final HashMap<String, ArrayList<Weather>> WeatherData = new HashMap<>();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private SwipeRefreshLayout swiper;
    private String location = "Chicago,Illinois";
    private String degree_metric="metric";
    private boolean has_net_connectivity;
    MenuItem unit_logo, location_menu, calendar_menu;
    WeatherLoaderRunnable clr = new WeatherLoaderRunnable(this, degree_metric, location);
    private List<HourWeather> hourWeatherList = new ArrayList<>();
    private List<HourWeather> hourWeatherList_new = new ArrayList<>();
    private RecyclerView recyclerView;
    private HourWeatherAdapter mAdapter;
    ConstraintLayout mainlayout;
    TextView current_date_time, temprature, feels_like, weather_descp, wind_descp, humidity, uv_indx, morn_temp, noon_temp, eve_tmp, night_temp, visibilty, sunset, sunrise;
    TextView morning_descp,noon_descp,eve_descp,night_descp;
    ImageView weather_icon;
    private String lattitude = "";
    private String longitude = "";
    double lat = 0.0,lng = 0.0;
    private String location_acti;
    private boolean updated_data=false;
    private String location_check=" ";


    private SharedPreferences prefs;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    private String locationString = "Unspecified Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.hourlyRecycler);
        mAdapter = new HourWeatherAdapter(hourWeatherList_new, this);
        recyclerView.setAdapter(mAdapter);
        mainlayout = (ConstraintLayout) this.findViewById(R.id.mainLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        has_net_connectivity = hasNetworkConnection();
        swiper = findViewById(R.id.swiper);
        current_date_time = (TextView) findViewById(R.id.current_date_time);
        temprature = (TextView) findViewById(R.id.temprature);
        feels_like = (TextView) findViewById(R.id.feels_like);
        weather_descp = (TextView) findViewById(R.id.weather_descp);
        wind_descp = (TextView) findViewById(R.id.winds_descp);
        humidity = (TextView) findViewById(R.id.humidity);
        uv_indx = (TextView) findViewById(R.id.uv_index);
        morn_temp = (TextView) findViewById(R.id.morning_temp);
        noon_temp = (TextView) findViewById(R.id.afternoon_temp);
        night_temp = (TextView) findViewById(R.id.night_temp);
        eve_tmp = (TextView) findViewById(R.id.evening_temp);
        visibilty = (TextView) findViewById(R.id.visibility);
        sunrise = (TextView) findViewById(R.id.sunrise_info);
        sunset = (TextView) findViewById(R.id.sunset_info);
        weather_icon = (ImageView) findViewById(R.id.weather_icon);
        morning_descp=(TextView) findViewById(R.id.descp_morning);
        noon_descp=(TextView) findViewById(R.id.descp_afternoon);
        eve_descp=(TextView) findViewById(R.id.descp_evening);
        night_descp=(TextView) findViewById(R.id.descp_night);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        morning_descp.setVisibility(View.INVISIBLE);
        eve_descp.setVisibility(View.INVISIBLE);
        night_descp.setVisibility(View.INVISIBLE);
        noon_descp.setVisibility(View.INVISIBLE);


        //draw action_bar
        draw_actionbar();

        setDataloc();


        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
       determineLocation();

        if (savedInstanceState != null) {
            ArrayList<Weather> weatherdata_saved = (ArrayList<Weather>) savedInstanceState.getSerializable("KEY");
            if (!weatherdata_saved.isEmpty()) {
                String loc = savedInstanceState.getString("KEY_LOC");
                weatherList = weatherdata_saved;
                setValues();
                for (Weather c : weatherdata_saved) {
                    for (HourWeather h : c.hourWeatherList) {
                        hourWeatherList.add(h);
                    }
                }

                for (int i = 0; i < 48; i++) {
                    hourWeatherList_new.add(hourWeatherList.get(i));
                }

                mAdapter.notifyItemInserted(hourWeatherList_new.size());
                mAdapter.notifyDataSetChanged();
            }
        }

        if (!has_net_connectivity) {
            setTitle(getString(R.string.app_name));
            TextView textView = (TextView) findViewById(R.id.current_date_time);
            textView.setText("No internet connection");
            for (int i = 0; i < mainlayout.getChildCount(); i++) {
                View child = mainlayout.getChildAt(i);
                if (child.getId() != R.id.current_date_time) {
                    child.setVisibility(View.GONE);
                }
            }
        }


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);


        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String locationString_current=locationString.split("\n")[0];
                //Toast.makeText();
                if(!locationString_current.equals("Unspecified Location")){
                    location_acti=locationString_current;
                }
                Toast.makeText(MainActivity.this,
                        locationString_current , Toast.LENGTH_LONG).show();
                clr.setLocation(locationString_current);
                new Thread(clr).start();
                swiper.setRefreshing(false); // This stops the busy-circle
            }
        });

    }



    private void  determineLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        locationString = getPlace(location);
                    }
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    Toast.makeText(this, R.string.denied_acess, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s%n%nProvider: %s%n%n%.5f, %.5f",
                    city, state, loc.getProvider(), loc.getLatitude(), loc.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void draw_actionbar()
    {
        ActionBar actionBar;
        actionBar = getSupportActionBar();


        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FF786A50"));

        actionBar.setBackgroundDrawable(colorDrawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weathermenu, menu);
        unit_logo=menu.findItem(R.id.unit_option);

        location_menu=menu.findItem(R.id.location_menu);
        calendar_menu=menu.findItem(R.id.calendar_option);
        String deg_metric= prefs.getString("degreeselected","metric");
        if (deg_metric.equals("metric")){
            unit_logo.setIcon(R.drawable.units_c);
        }
        else{
            unit_logo.setIcon(R.drawable.units_f);
        }

        return true;
    }


    public void downloadFailed() {
        updated_data=false;
        Toast.makeText(this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
    }


    public void updateData(ArrayList<Weather> listIn) {
        WeatherData.clear();
        hourWeatherList.clear();
        hourWeatherList_new.clear();
        weatherList.clear();
        for (Weather c : listIn) {
            if (!WeatherData.containsKey(c.getDayDate())) {
                WeatherData.put(c.getDayDate(), new ArrayList<>());
            }
            ArrayList<Weather> clist = WeatherData.get(c.getDayDate());
            if (clist != null) {
                clist.add(c);
            }
            for (HourWeather h: c.hourWeatherList)
                hourWeatherList.add(h);
        }

        for (int i=0;i<48;i++){
            hourWeatherList_new.add(hourWeatherList.get(i));
        }
        mAdapter.notifyItemInserted(hourWeatherList_new.size());
        mAdapter.notifyDataSetChanged();
        WeatherData.put("All", listIn);

        ArrayList<String> tempList = new ArrayList<>(WeatherData.keySet());
        Collections.sort(tempList);

        weatherList.clear();
        weatherList.addAll(listIn);

        setValues();
        updated_data=true;
        if (location_check.equals(weatherList.get(0).getLocation())){
            location_acti=location_check;
            setTitle(location_acti);
        }
        else if(lat==Double.parseDouble(weatherList.get(0).getLattitude()) && lng == Double.parseDouble(weatherList.get(0).getLongitude())){
            setTitle(location_acti);
        }
        else{
            location_acti=weatherList.get(0).getLocation();
           // location_acti="shree";
        }

    }

    public void checkNetwork(){

        if (!hasNetworkConnection())
        {
            Toast.makeText(this," The function cannot be used when there is no network connection ", Toast.LENGTH_SHORT).show();
            unit_logo.setEnabled(false);
            location_menu.setEnabled(false);
            calendar_menu.setEnabled(false);

        }

    }

    public void setValues()
    {

        morning_descp.setVisibility(View.VISIBLE);
        eve_descp.setVisibility(View.VISIBLE);
        noon_descp.setVisibility(View.VISIBLE);
        night_descp.setVisibility(View.VISIBLE);

        //Set all textview
        current_date_time.setText(weatherList.get(0).getDayDate());
        temprature.setText(weatherList.get(0).getTemprature());
        feels_like.setText("Feels like:"+ weatherList.get(0).getFeelslike());
        weather_descp.setText(weatherList.get(0).getCondition()+"("+weatherList.get(0).getCloudcover()+"% clouds)");
        wind_descp.setText("Winds:"+weatherList.get(0).getWinddir()+" at "+weatherList.get(0).getWindspeed()+" mph guesting to "+weatherList.get(0).getWindgust()+" mph");
        humidity.setText("Humidity:"+weatherList.get(0).getHumidity()+"%");
        uv_indx.setText("UV Index:"+weatherList.get(0).getUVIndx());
        visibilty.setText("Visibility:"+weatherList.get(0).getVisibility()+" mi");
        noon_temp.setText(weatherList.get(0).getAfternoonTemp());
        morn_temp.setText(weatherList.get(0).getMorningTemp());
        night_temp.setText(weatherList.get(0).getNigthTemp());
        eve_tmp.setText(weatherList.get(0).getEvenTemp());
        sunrise.setText("Sunrise:"+weatherList.get(0).getSunrise());
        sunset.setText("Sunset:"+weatherList.get(0).getSunset());


        //setImageView
        weather_icon.setImageResource(Integer.parseInt(weatherList.get(0).getWeatherIcon()));

    }

    private void getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address =
                    geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty()) {
                // Nothing returned!
                return;
            }
            lat = address.get(0).getLatitude();
            lng = address.get(0).getLongitude();

        } catch (Exception e) {
            // Failure to get an Address object
            Log.e("Error",e.getMessage());
        }
    }

    public void setDataloc(){
        setTitle(prefs.getString("locationname","Chicago , Illinois"));
        location_acti=prefs.getString("locationname","Chicago , Illinois");
        lat = Double.parseDouble(prefs.getString("lat","41.8675766"));
        lng = Double.parseDouble(prefs.getString("long","-87.616232"));
        String deg_metric = prefs.getString("degreeselected","metric");
        location=String.valueOf(lat)+","+String.valueOf(lng);
        clr.setUnit(deg_metric);
        clr.setLocation(location);
        new Thread(clr).start();
    }


    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.calendar_option) {
            //checkNetwork();
            if (hasNetworkConnection()) {
                Intent intent = new Intent(this, DayWeatherActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("location", location_acti);
                bundle.putSerializable("weatherlist", weatherList);
                intent.putExtras(bundle);
                activityResultLauncher.launch(intent);
                ;
            }
            else{
                Toast.makeText(this, "The function cannot be used when there is no network connection ", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId()==R.id.unit_option) {
            //checkNetwork();
            if (hasNetworkConnection() ){
                if (clr.getUnit().equals("us") ){
                    clr.setUnit("metric");
                    clr.setLocation(location_acti);
                    new Thread(clr).start();
                    unit_logo.setIcon(R.drawable.units_c);
                    prefs.edit().putString("degreeselected", "metric").apply();
                } else {
                    clr.setUnit("us");
                    clr.setLocation(location_acti);
                    new Thread(clr).start();
                    unit_logo.setIcon(R.drawable.units_f);
                    prefs.edit().putString("degreeselected", "us").apply();
                }
            }
            else {
                Toast.makeText(this, "The function cannot be used when there is no network connection ", Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId()==R.id.location_menu) {
            //checkNetwork();
            if (hasNetworkConnection()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText input = new EditText(MainActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String loc = input.getText().toString();
                        clr.setLocation(loc);
                        prefs.edit().putString("locationname", location_acti).apply();
                        prefs.edit().putString("lat", ""+lat).apply();
                        prefs.edit().putString("long", ""+lng).apply();
                        prefs.edit().putString("degreeselected", clr.getUnit()).apply();
                        //location_acti=loc;
                        location_check=loc;
                        new Thread(clr).start();
                        location = loc;
                    }
                });
                builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
                builder.setTitle("Enter a Location");
                builder.setView(input);
                builder.setMessage("For US location,enter as 'City',or 'City,State'" + "\n \n" + "For international locations enter as 'City,Country'");
                AlertDialog dialog = builder.create();
                dialog.show();


            }
            else{
                Toast.makeText(this, "The function cannot be used when there is no network connection ", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("KEY", weatherList);
        savedInstanceState.putString("KEY_LOC",location);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void handleResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }
         else {
            Toast.makeText(this, "OTHER result not OK!", Toast.LENGTH_SHORT).show();
        }
    }


}