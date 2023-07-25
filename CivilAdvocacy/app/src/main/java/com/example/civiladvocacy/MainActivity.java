package com.example.civiladvocacy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private final ArrayList<Official> OfficialList = new ArrayList<>();
    private TextView locationview,nonettextview;
    private RecyclerView recyclerView;
    private OfficialAdapter mAdapter;
    MenuItem location_menu, about_menu;
    private LinearLayoutManager linearLayoutManager;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private RequestQueue queue;
    private long start;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private String locationString = "Unable to fetch location";
    //private SharedPreferences prefs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationview=(TextView) findViewById(R.id.location);
        nonettextview=(TextView) findViewById(R.id.nonetwork);
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new OfficialAdapter(OfficialList,this);
        recyclerView.setAdapter(mAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
        locationview.setText(locationString);
        queue = Volley.newRequestQueue(this);
       // prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        //setDataloc();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()){
            String savedlocation = savedInstanceState.getString("KEY_locationstring");
            locationview.setText(savedlocation);
            performDownload(savedlocation);
        }
        else
        {
            if (!hasNetworkConnection()){
                locationview.setText("No Data for Location");
                nonettextview.setText("No Network Connection "+"\n "+"Data cannote be accessed");
                setTitle("Know Your Government");
                ActionBar actionBar;
                actionBar = getSupportActionBar();


                ColorDrawable colorDrawable
                        = new ColorDrawable(Color.parseColor("#FFBAA2BD"));

                actionBar.setBackgroundDrawable(colorDrawable);

            }
            else{

                determineLocation();
            }
        }
    }


    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("KEY_locationstring", locationview.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }


    public void handleResult(ActivityResult result) {

        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data == null)
                return;
        } else {
        }
    }

    public void downloadFailed() {
        Toast.makeText(this, "Please specify the correct location", Toast.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        location_menu=menu.findItem(R.id.LocationEntrymenu);
        about_menu=menu.findItem(R.id.Aboutmenu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId()==R.id.LocationEntrymenu) {
            //checkNetwork();
            if (hasNetworkConnection()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText input = new EditText(MainActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String loc = input.getText().toString();
                        performDownload(loc);
                    }
                });
                builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
                builder.setTitle("Enter a Location");
                builder.setView(input);
                AlertDialog dialog = builder.create();
                dialog.show();

            }

            else{
                Toast.makeText(this, "The function cannot be used when there is no network connection ", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId()==R.id.Aboutmenu){
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }
        else{
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }


    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Official off = OfficialList.get(pos);

        Intent intent = new Intent(this, OfficialAcitivy.class);
        intent.putExtra("OFFICIAL_DISPLAY", off);
        //intent.putExtra("EDIT_POSITION", pos);


        activityResultLauncher.launch(intent);

    }


    public void performDownload(String location) {

        //String urlToUse = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyBH_iwAxe69ILf6OtImYPvJ5IDAolJM4DU&address=1600 W North Ave, Chicago, IL";
        String urlToUse="https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyBH_iwAxe69ILf6OtImYPvJ5IDAolJM4DU&address="+location;
        start = System.currentTimeMillis();

        Response.Listener<JSONObject> listener =
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //textView.setText(MessageFormat.format("Response: {0}", response.toString()));
                            String location_1="";
                            OfficialList.clear();
                            JSONObject jobj_loc=response.getJSONObject("normalizedInput");
                            //String location =jobj_loc.getString("line1")+","+jobj_loc.getString("city")+","+ jobj_loc.getString("state");
                            if (jobj_loc.has("line1")){
                                location_1=location_1+jobj_loc.getString("line1")+" ";
                                Log.d("MainAcitvity",location_1);
                            }
                            if (jobj_loc.has("city")){
                                location_1=location_1+jobj_loc.getString("city")+" ,";
                                Log.d("MainAcitvity",location_1);
                            }
                            if (jobj_loc.has("state")){

                                location_1=location_1+jobj_loc.getString("state")+" ";
                                Log.d("MainAcitvity",location_1);
                            }
                            if (jobj_loc.has("zip")){
                                location_1=location_1+jobj_loc.getString("zip");
                                Log.d("MainAcitvity",location_1);

                            }
                            Log.d("MainAcitvity",location_1);

                            JSONArray jr = response.getJSONArray("offices");
                            JSONArray jr_off=response.getJSONArray("officials");
                            for (int i = 0; i < jr.length(); i++) {
                                JSONObject jb1 = jr.getJSONObject(i);
                                String position = jb1.getString("name");

                                JSONArray jr2 = jb1.getJSONArray("officialIndices");
                                for (int j = 0; j < jr2.length(); j++) {
                                    int val = jr2.getInt(j);
                                    JSONObject jo_off = jr_off.getJSONObject(val);

                                    //name
                                    String name = jo_off.getString("name");
                                    String address = "";
                                    if (jo_off.has("address")) {
                                        JSONArray addressarr = jo_off.getJSONArray("address");
                                        JSONObject address_obj = addressarr.getJSONObject(0);
                                        address = address_obj.getString("line1") +","+ address_obj.getString("city") + ","+address_obj.getString("state") +","+ address_obj.getString("zip");
                                    }
                                    String party = "";
                                    if (jo_off.has("party")) {
                                        party = jo_off.getString("party");
                                    }
                                    String phnumber = "";
                                    if (jo_off.has("phones")) {
                                        JSONArray phnumber_arr = jo_off.getJSONArray("phones");
                                        for (int k = 0; k < phnumber_arr.length(); k++) {
                                            phnumber = phnumber + phnumber_arr.getString(k) + "\n";
                                        }
                                    }
                                    //String imge_url = String.valueOf(official_picture("missing"));
                                    String imge_url = "";
                                    if (jo_off.has("photoUrl")) {
                                        imge_url = jo_off.getString("photoUrl");
                                    }
                                    String email_link = "";
                                    if (jo_off.has("emails")) {
                                        JSONArray emails = jo_off.getJSONArray("emails");
                                        email_link = emails.getString(0);
                                    }
//
                                    String Web_url = "";
                                   if (jo_off.has("urls")) {
                                       JSONArray weburlarr = jo_off.getJSONArray("urls");
                                       Web_url = weburlarr.getString(0);
                                   }
//

                                    String yt_link="";
                                    String fb_link="";
                                    String tw_link="";
                                    if (jo_off.has("channels")){
                                        JSONArray channel_arr = jo_off.getJSONArray("channels");
                                        for (int k=0;k<channel_arr.length();k++){
                                            JSONObject channel_obj=channel_arr.getJSONObject(k);
                                            if (channel_obj.getString("type").equals("Facebook")){
                                                fb_link=channel_obj.getString("id");
                                            }
                                            if (channel_obj.getString("type").equals("Twitter")){
                                                tw_link=channel_obj.getString("id");
                                            }
                                            if (channel_obj.getString("type").equals("Youtube")){
                                                yt_link=channel_obj.getString("id");
                                            }

                                        }

                                    }

                                    OfficialList.add(new Official(imge_url, party, position, phnumber, name, address, email_link, location_1,fb_link,yt_link,tw_link, Web_url));
                                }
                            }
                            mAdapter.notifyItemInserted(OfficialList.size());
                            mAdapter.notifyDataSetChanged();
                            Log.d("MainAcitvity",OfficialList.get(0).getUser_present_location());
                            locationview.setText(OfficialList.get(0).getUser_present_location());
                           // prefs.edit().putString("locationstringpref",OfficialList.get(0).getUser_present_location()).apply();
                        } catch (Exception e) {
                            //textView.setText(MessageFormat.format("Response: {0}", e.getMessage()));
                        }
                    }
                };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));

                      //setTitle("Duration: " + (System.currentTimeMillis() - start));
                    //locationview.setText("APP is unable to read location or entered location might not be correct");
                    //OfficialList.clear();
                    //mAdapter.notifyDataSetChanged();
                    location_toast();

                    //Toast.makeText(this, R.string.wrong_location, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void location_toast(){
        Toast.makeText(this, R.string.wrong_location, Toast.LENGTH_SHORT).show();

    }

    private int official_picture(String icon) {
        int iconID =
                this.getResources().getIdentifier(icon, "drawable", this.getPackageName());
        if (iconID == 0) {
            Log.d(TAG, "parseCurrentRecord: CANNOT FIND ICON " + icon);
        }
        return iconID;
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
                        performDownload(locationString.split("\n")[0]);
                        //locationview.setText(locationString.split("\n")[0]);
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
}