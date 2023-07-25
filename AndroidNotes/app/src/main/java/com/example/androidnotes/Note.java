package com.example.androidnotes;
import android.util.JsonWriter;
import java.io.Serializable;
import java.io.IOException;
import java.io.StringWriter;


public class Note implements Serializable{
    private String title;
    private String noteText;
    private String savedDate;

    public Note(String title, String noteText,String savedDate){
        this.title=title;
        this.noteText=noteText;
        this.savedDate=savedDate;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public void setSavedDime(String lastsavedtime){this.savedDate=lastsavedtime;}

    public String getTitle(){
        return title;
    }

    public String getNoteText(){
        return  noteText;
    }

    public String getSavedTime(){ return savedDate;}


    @Override
    public String toString(){
        try{
            StringWriter sw=new StringWriter();
            JsonWriter jsonWriter=new JsonWriter(sw);
            jsonWriter.setIndent(" ");
            jsonWriter.beginObject();

            jsonWriter.name("title").value(getTitle());
            jsonWriter.name("notetext").value(getNoteText());
            jsonWriter.name("saveddate").value(getSavedTime());

            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }


}



