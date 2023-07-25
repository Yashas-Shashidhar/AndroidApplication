package com.example.androidnotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
      implements View.OnClickListener,View.OnLongClickListener {

    private List<Note> noteslist=new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new NoteAdapter(noteslist, this);
        recyclerView.setAdapter(mAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
         loadFile();


    }

     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.android_notes_menu,menu);
        return true;
     }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.info_menu){
            Toast.makeText(this, "opened about activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            }
        else if(item.getItemId()==R.id.edit_menu){
            Toast.makeText(this, "opened edit activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, EditActivity.class);
            activityResultLauncher.launch(intent);
        }
        else{
            return super.onOptionsItemSelected(item);
        }
        return true;

    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Note n = noteslist.get(pos);

        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("EDIT_NOTE", n);
        intent.putExtra("EDIT_POSITION", pos);

        activityResultLauncher.launch(intent);

    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Note n = noteslist.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                noteslist.remove(pos);
                savefileNote();
                mAdapter.notifyItemRemoved(pos);
            }
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> {
        });
        builder.setTitle("DeleteNote");
        builder.setMessage("Delete Note '" + n.getTitle() + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void loadFile() {
        setTitle("AndroidNotes ("+ noteslist.size()+")");
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i =0;i< jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String descp = jsonObject.getString("notetext");
                String savedtime=jsonObject.getString("saveddate");
                Note note = new Note(title,descp,savedtime);
                noteslist.add(note);
                setTitle("AndroidNotes ("+ noteslist.size()+")");
                mAdapter.notifyItemInserted(noteslist.size());
            }
            sortNoteList();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            noteslist.clear();
            mAdapter.notifyItemInserted(noteslist.size());
        } catch (Exception e) {
            e.printStackTrace();
            noteslist.clear();
            mAdapter.notifyItemInserted(noteslist.size());
        }
    }


    public void  savefileNote() {
            setTitle("AndroidNotes ("+ noteslist.size()+")");
            sortNoteList();
            try {
                FileOutputStream fos = getApplicationContext().
                        openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

                PrintWriter printWriter = new PrintWriter(fos);
                printWriter.print(noteslist);
                printWriter.close();
                fos.close();

                Toast.makeText(this, getString(R.string.updated_note), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.getStackTrace();
            }

        }

    public void sortNoteList() {
        Collections.sort(noteslist, (obj1, obj2) -> {
            if (obj1.getSavedTime() == null || obj2.getSavedTime() == null)
                return 0;
            return obj1.getSavedTime().compareTo(obj2.getSavedTime());
        });
        Collections.reverse(noteslist);
        mAdapter.notifyDataSetChanged();
    }

    public void handleResult(ActivityResult result) {

        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data == null)
                return;
            if (data.hasExtra("NOTE_INSERTION")){
                Note newnote = (Note) data.getSerializableExtra("NOTE_INSERTION");
                noteslist.add(newnote);
                savefileNote();
                mAdapter.notifyItemInserted(noteslist.size());
            }else if (data.hasExtra("UPDATE_NOTE")){
                Note editnote=(Note)data.getSerializableExtra("UPDATE_NOTE");
                int pos=data.getIntExtra("UPDATE_POSITION",0);

                Note updatednote = noteslist.get(pos);
                updatednote.setTitle(editnote.getTitle());
                updatednote.setNoteText(editnote.getNoteText());
                updatednote.setSavedDime(editnote.getSavedTime());
                savefileNote();
                mAdapter.notifyItemChanged(pos);
            }
        } else {
        }
    }

}