package com.example.civiladvocacy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

public class OfficialAdapter  extends RecyclerView.Adapter<OfficialViewHolder> {
    private final List<Official> officialList;
    private final MainActivity OfficialActivity;
    private long start;
    private static RequestQueue queue;
    private static final String TAG = "OfficialAdapter";



    OfficialAdapter(List<Official> weatherList, MainActivity ma){
        this.officialList=weatherList;
        OfficialActivity=ma;
    }


    public OfficialViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recy_govtofficial, parent, false);

        itemView.setOnClickListener(OfficialActivity);
        //itemView.setOnLongClickListener(mainAct);

        return new OfficialViewHolder(itemView);
    }

    private int official_picture(String icon) {
        int iconID =
                OfficialActivity.getResources().getIdentifier(icon, "drawable", OfficialActivity.getPackageName());
        if (iconID == 0) {
            Log.d(TAG, "parseCurrentRecord: CANNOT FIND ICON " + icon);
        }
        return iconID;
    }

    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {

        Official official = officialList.get(position);
        holder.divier_img.setImageResource(official_picture("separator"));

        holder.official_posiitonview.setText(official.getOfficial_position());
        holder.official_nameview.setText(official.getOffical_name()+"("+official.getOfficial_party()+")");
        if (!official.getImage_url().equals("")){
            queue = Volley.newRequestQueue(OfficialActivity);
            downloadImage(holder,official.getImage_url());
        }
        else{
            holder.official_imageview.setImageResource(official_picture("missing"));
        }
    }

//    private void getIcon(String url,OfficialViewHolder holder) {
//        String imageURL = url;
//        final Bitmap[] response_bi = new Bitmap[1];
//        Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
//            @Override
//            public void onResponse(Bitmap response) {
//                holder.official_imageview.setImageBitmap(response);
//            }
//        };
//        //holder.official_imageview.setImageBitmap(response_bi[0]);
//       // holder.official_imageview.setImageResource(Integer.parseInt(official.getImage_url()));
//        Response.ErrorListener error = new Response.ErrorListener() {
////            @Override
//            public void onErrorResponse(VolleyError error) {
////               // setTitle(MessageFormat.format(
////                        "Image Error: {0}", error.networkResponse.statusCode));
//            }
//        };
//
//        ImageRequest imageRequest =
//                new ImageRequest(imageURL, listener, 0, 0,
//                        ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, error);
//        queue.add(imageRequest);
//    }


    private void downloadImage(@NonNull OfficialViewHolder holder,String urlString) {

        Response.Listener<Bitmap> listener = response -> {
            long time = System.currentTimeMillis() - start;
            Log.d(TAG, "downloadImage: " + time);
            holder.official_imageview.setImageBitmap(response);
        };
        urlString=urlString.replace("http://", "https://");
        Response.ErrorListener error = error1 ->
                holder.official_imageview.setImageResource(official_picture("brokenimage"));

        ImageRequest imageRequest =
                new ImageRequest(urlString, listener, 0, 0,
                        ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, error);
        start = System.currentTimeMillis();
        queue.add(imageRequest);
    }



    public int getItemCount() {
        return officialList.size();
    }
}
