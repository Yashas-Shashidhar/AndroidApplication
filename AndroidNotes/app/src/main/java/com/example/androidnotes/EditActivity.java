package com.example.androidnotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;
import java.util.Date;


public class EditActivity extends AppCompatActivity {
    private EditText notename;
    private EditText notedescp;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        notename=findViewById(R.id.Notename);
        notedescp=findViewById(R.id.Note_descp);
        Intent intent=getIntent();
        if(intent.hasExtra("EDIT_NOTE")){
            note = (Note)intent.getSerializableExtra("EDIT_NOTE");
            notename.setText((note.getTitle()));
            notedescp.setText(note.getNoteText());

        }
    }
    protected void onResume(){
        if (note != null) {
            notename.setText(note.getTitle());
            notedescp.setText(note.getNoteText());
        }
        super.onResume();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_menu) {
            TitleCheckDialog();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void SaveNote(View v) {
        String notetitle = notename.getText().toString();
        String notedescription = notedescp.getText().toString();
        String notesavedtime=new Date().toString();
        if (note != null) {
            if (note.getTitle().equals(notetitle) &&
                    note.getNoteText().equals(notedescription)) {
                     notesavedtime=note.getSavedTime();
            }
        }
        Note note=new Note(notetitle,notedescription,notesavedtime);

        String key ="NOTE_INSERTION";

        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_NOTE")){
            key = "UPDATE_NOTE";
        }

        Intent data =new Intent();
        data.putExtra(key,note);
        if(intent.hasExtra("EDIT_POSITION")){
            int pos=intent.getIntExtra("EDIT_POSITION",0);
                    data.putExtra("UPDATE_POSITION",pos);
        }
        setResult(RESULT_OK, data);
        finish();
    }


    public void TitleCheckDialog(){
        if(TextUtils.isEmpty(notename.getText().toString())){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Ok", (dialog, which) -> {
                dialog.dismiss();
                EditActivity.super.onBackPressed();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.setTitle("Your Cannot save a note without Title!\n Are you sure you want to exit?");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        SaveNote(null);
    }

    public void onBackPressed() {
        String notenam = notename.getText().toString().trim();
        String notedesc= notedescp.getText().toString().trim();
        if (note != null) {
            if (note.getTitle().equals(notenam.trim()) &&
                    note.getNoteText().equals(notedesc.trim())) {
                super.onBackPressed();
            }
        }
       if (notenam.isEmpty() && notedesc.isEmpty()){
            super.onBackPressed();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TitleCheckDialog();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditActivity.super.onBackPressed();
            }
        });
        builder.setTitle("Unsaved Changes");
        builder.setMessage("Do you want to save your changes before exiting?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}