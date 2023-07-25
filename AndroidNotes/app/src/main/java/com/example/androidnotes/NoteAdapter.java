package com.example.androidnotes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;


public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder>  {
    private final List<Note> noteslist;
    private final MainActivity mainAct;

    NoteAdapter(List<Note> notelist,MainActivity ma){
        this.noteslist=notelist;
        mainAct=ma;
    }

    public NoteViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NoteViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note note = noteslist.get(position);

        holder.notetitle.setText(note.getTitle());
        holder.saveddate.setText(note.getSavedTime());
        holder.notedescp.setText(note.getNoteText());

        if (note.getNoteText().length() >= 80) {
            holder.notedescp.setText(note.getNoteText().substring(0,80) + "...");
        } else {
            holder.notedescp.setText(note.getNoteText());
        }
    }

    public int getItemCount() {
        return noteslist.size();
    }

}





