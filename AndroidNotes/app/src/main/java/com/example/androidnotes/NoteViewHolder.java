package com.example.androidnotes;


import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


public class NoteViewHolder extends RecyclerView.ViewHolder {
    TextView notetitle;
    TextView saveddate;
    TextView notedescp;

    NoteViewHolder(View view){
        super(view);
        notetitle=view.findViewById(R.id.recy_notetitle);
        saveddate=view.findViewById(R.id.recy_savedate);
        notedescp=view.findViewById(R.id.recy_notedescp);
    }


}
