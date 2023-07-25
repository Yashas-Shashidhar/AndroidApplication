package com.example.civiladvocacy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.ActivityNotFoundException;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
public class OfficialAcitivy extends AppCompatActivity {

    private TextView name,party,office,current_location;
    private TextView address_tag,address,phone_tag,phone,email_tag,email;
    private TextView website_tag,website;
    private Official official;
    private static RequestQueue queue;
    private ImageView official_img,party_logo;
    private long start;
    private static final String TAG = "OfficialAcitivity";
    private ImageButton facebook_btn,twitter_btn,youtube_btn;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_acitivy);
        getValTextview();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
        Intent intent=getIntent();
        if (intent.hasExtra("OFFICIAL_DISPLAY")){
            official=(Official) intent.getSerializableExtra("OFFICIAL_DISPLAY");
            setValView();
        }
    }

    private void downloadImage( String urlString) {

        Response.Listener<Bitmap> listener = response -> {
            long time = System.currentTimeMillis() - start;
            Log.d(TAG, "downloadImage: " + time);
            official_img.setImageBitmap(response);
        };
        urlString=urlString.replace("http://", "https://");
        Response.ErrorListener error = error1 ->
                official_img.setImageResource(official_picture("brokenimage"));
        //urlString="https://bioguide.congress.gov/bioguide/photo/D/D000563.jpg";


        ImageRequest imageRequest =
                new ImageRequest(urlString, listener, 0, 0,
                        ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, error);
        start = System.currentTimeMillis();
        queue.add(imageRequest);
    }

    private int official_picture(String icon) {
        int iconID =
                this.getResources().getIdentifier(icon, "drawable", this.getPackageName());
        if (iconID == 0) {
            Log.d(TAG, "parseCurrentRecord: CANNOT FIND ICON " + icon);
        }
        return iconID;
    }

    public void getValTextview(){
        current_location=findViewById(R.id.current_location);
        name=findViewById(R.id.name);
        party=findViewById(R.id.party);
        office=findViewById(R.id.office);
        address_tag=findViewById(R.id.address_tag);
        phone_tag=findViewById(R.id.phone_tag);
        email_tag=findViewById(R.id.email_tag);
        website_tag=findViewById(R.id.website_tag);
        address=findViewById(R.id.address);
        phone=findViewById(R.id.phone);
        website=findViewById(R.id.website);
        email=findViewById(R.id.email);
        official_img=findViewById(R.id.official_img);
        facebook_btn=findViewById(R.id.facebook_button);
        twitter_btn=findViewById(R.id.twitter_button);
        youtube_btn=findViewById(R.id.youtube_button);
        party_logo=findViewById(R.id.party_logo);
    }

    public void setValView(){
        View view = this.getWindow().getDecorView();
        current_location.setText(official.getUser_present_location());
        name.setText(official.getOffical_name());
        party.setText("("+official.getOfficial_party()+")");
        office.setText(official.getOfficial_position());
        SpannableString content_2 = new SpannableString(official.getOfficial_phonenumber());
        content_2.setSpan(new UnderlineSpan(), 0, content_2.length(), 0);
        phone.setText(content_2);
        SpannableString content_3 = new SpannableString(official.getOfficial_email());
        content_3.setSpan(new UnderlineSpan(), 0, content_3.length(), 0);
        email.setText(content_3);
        SpannableString content_1 = new SpannableString(official.getOfficial_weburl());
        content_1.setSpan(new UnderlineSpan(), 0, content_1.length(), 0);
        website.setText(content_1);
        SpannableString content = new SpannableString(official.getOfficial_address());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        address.setText(content);
        //address.setText(official.getOfficial_address());

        if (!official.getImage_url().equals("")){
            queue = Volley.newRequestQueue(this);
            downloadImage(official.getImage_url());
        }
        else{
            official_img.setImageResource(official_picture("missing"));
        }

        if (official.getOfficial_phonenumber().equals("")){
            phone_tag.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
        }

        if (official.getOfficial_weburl().equals("")){
            website_tag.setVisibility(View.GONE);
            website.setVisibility(View.GONE);
        }

        if(official.getOfficial_email().equals("")){
            email_tag.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }
        if(official.getOfficial_address().equals("")){
            address_tag.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }
        if(official.getOfficial_ytlink().equals("")){
            youtube_btn.setVisibility(View.INVISIBLE);
        }
        if(official.getOfficial_twlink().equals("")){
            twitter_btn.setVisibility(View.INVISIBLE);
        }
        if(official.getOfficial_fblink().equals("")){
            facebook_btn.setVisibility(View.INVISIBLE);
        }

        if(official.getOfficial_party().equals("Democratic Party")){
            party_logo.setImageResource(official_picture("dem_logo"));
            view.setBackgroundColor(0xff0000ff);
        }
        else if(official.getOfficial_party().equals("Republican Party")){
            party_logo.setImageResource(official_picture("rep_logo"));
            view.setBackgroundColor(0xffff0000);
        }
        else{
            party_logo.setVisibility(View.INVISIBLE);
            view.setBackgroundColor(000000);
        }


    }

    public void clickFacebook(View v) {
        // You need the FB user's id for the url
        String fb_user=official.getOfficial_fblink();
        String FACEBOOK_URL = "https://www.facebook.com/"+fb_user;

        Intent intent;

        // Check if FB is installed, if not we'll use the browser
        if (isPackageInstalled("com.facebook.katana")) {
            String urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        // Check if there is an app that can handle fb or https intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (fb/https) intents");
        }

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

    public void clickwebsite(View v) {
        String websiteurl=official.getOfficial_weburl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(websiteurl));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }





    public void clickTwitter(View v) {
        //String user = "chicagotribune";
        String user=official.getOfficial_twlink();
        String twitterAppUrl = "twitter://user?screen_name=" + user;
        String twitterWebUrl = "https://twitter.com/" + user;

        Intent intent;
        // Check if Twitter is installed, if not we'll use the browser
        if (isPackageInstalled("com.twitter.android")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (twitter/https) intents");
        }
    }

    public void youTubeClicked(View v) {
        //String name = <the official’s youtube id from download>;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }




    public boolean isPackageInstalled(String packageName) {
        try {
            return getPackageManager().getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clickCall(View v) {
        //String number = "650-253-0000";
        String number=official.getOfficial_phonenumber();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_DIAL (tel) intents");
        }
    }

    public void clickMap(View v) {
        //String address = "Shedd Aquarium, 1200 S. Lake Shore Drive, Chicago, IL, 60605";
        String address=official.getOfficial_address();
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);

        // Check if there is an app that can handle geo intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (geo) intents");
        }
    }


    public void clickEmail(View v) {
        String[] addresses = new String[]{official.getOfficial_email()};

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "This comes from EXTRA_SUBJECT");
        intent.putExtra(Intent.EXTRA_TEXT, "Email text body from EXTRA_TEXT...");

        // Check if there is an app that can handle mailto intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles SENDTO (mailto) intents");
        }
    }

    public void openpictureact(View v){
        if (!official.getImage_url().equals("")){
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("PICTURE_ACT", official);
            activityResultLauncher.launch(intent);
        }
    }

    public void handleResult(ActivityResult result) {

        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data == null)
                return;
        } else {
        }
    }



}