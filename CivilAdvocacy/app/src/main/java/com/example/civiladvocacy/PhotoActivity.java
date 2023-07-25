package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class PhotoActivity extends AppCompatActivity {
    private Official official;
    private TextView name,position,user_loc;
    private long start;
    private ImageView official_img,office_logo;
    private static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent=getIntent();
        getValView();
        if (intent.hasExtra("PICTURE_ACT")){
            official=(Official) intent.getSerializableExtra("PICTURE_ACT");
        }
        setValView();
    }

    public void getValView(){
        name=findViewById(R.id.name_photo);
        position=findViewById(R.id.position_photo);
        user_loc=findViewById(R.id.users_current_loc);
        official_img=findViewById(R.id.official_photo);
        office_logo=findViewById(R.id.logo_photo);
    }

    public void setValView(){
        View view = this.getWindow().getDecorView();
        name.setText(official.getOffical_name());
        position.setText(official.getOfficial_position());
        user_loc.setText(official.getUser_present_location());
        queue = Volley.newRequestQueue(this);
        downloadImage(official.getImage_url());

        if(official.getOfficial_party().equals("Democratic Party")){
            office_logo.setImageResource(official_picture("dem_logo"));
            view.setBackgroundColor(0xff0000ff);
        }
        else if(official.getOfficial_party().equals("Republican Party")){
            office_logo.setImageResource(official_picture("rep_logo"));
            view.setBackgroundColor(0xffff0000);
        }
        else{
            office_logo.setVisibility(View.INVISIBLE);
            view.setBackgroundColor(000000);
        }

    }

    private int official_picture(String icon) {
        int iconID =
                this.getResources().getIdentifier(icon, "drawable", this.getPackageName());
        if (iconID == 0) {
        }
        return iconID;
    }


    private void downloadImage( String urlString) {

        Response.Listener<Bitmap> listener = response -> {
            long time = System.currentTimeMillis() - start;
            official_img.setImageBitmap(response);
        };
        urlString=urlString.replace("http://", "https://");
        Response.ErrorListener error = error1 ->
                official_img.setImageResource(official_picture("brokenimage"));


        ImageRequest imageRequest =
                new ImageRequest(urlString, listener, 0, 0,
                        ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, error);
        start = System.currentTimeMillis();
        queue.add(imageRequest);
    }

    public void logowebsite(View v){
        String urllink="";
        if (official.getOfficial_party().equals("Democratic Party")){
            urllink="https://democrats.org/";
        }
        else{
            urllink="https://www.gop.com";
        }
        clickcivicapi(urllink);
    }


    public void clickcivicapi(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }





}