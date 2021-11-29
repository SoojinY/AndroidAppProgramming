package com.example.memoyum;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;

public class Memo {
    int _id;
    String name;
    String place;
    ArrayList<String> tags;
    byte[] contents;
    boolean visited;
    String writedt;
    String editdt;

    Bitmap img;

    final String[] colNm = {
            "_id",
            "nm",
            "place",
            "tags",
            "contents",
            "visited",
            "writedt",
            "editdt"
    };
    final int COLSIZE = colNm.length;

    public int getColSize(){
        return COLSIZE;
    }

    public ArrayList<String> setTags(String _tid, Context context){
        if(_tid==null || _tid.isEmpty()){
            tags = null;
            return null;
        }
        String[] tid = _tid.split(",");
        ArrayList<String> t = new ArrayList<String>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db;
        Cursor c;
        try{
            db = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            db = dbHelper.getReadableDatabase();
        }

        for (String s : tid) {
            String sql = "SELECT tagnm FROM tags WHERE _id=" + s + ";";
            c = db.rawQuery(sql, null);
            c.moveToNext();
            String getTag = c.getString(0);
            t.add(getTag);
        }
        tags = t;
        return tags;
    }
    public String tagsToString(){
        if(this.tags==null || this.tags.isEmpty()){
            return "";
        }

        StringBuilder s = new StringBuilder();
        for(String t : this.tags){
            s.append(" #").append(t);
        }
        return s.toString();
    }
}
