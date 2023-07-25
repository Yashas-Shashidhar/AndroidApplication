package com.example.civiladvocacy;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    TextView official_posiitonview;
    TextView official_nameview;
    ImageView official_imageview,divier_img;
    OfficialViewHolder(View view){
        super(view);
        official_posiitonview=view.findViewById(R.id.recy_official_position);
        official_nameview= view.findViewById(R.id.recy_official_name);
        official_imageview=view.findViewById(R.id.recy_image_official);
        divier_img=view.findViewById(R.id.divider_img);


    }

}
