package com.example.memoyum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NM = "memoyum.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NM[] = {
            "memos", "tags", "alarms", "photos"
    };

    public DatabaseHelper (Context context){
        super(context, DATABASE_NM, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS memos(_id INTEGER PRIMARY KEY Autoincrement, nm TEXT, place TEXT, tags TEXT, contents BLOB, visited BOOLEAN, writedt TEXT, editdt TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS tags(_id INTEGER PRIMARY KEY Autoincrement, tagnm TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS alarms(_id INTEGER PRIMARY KEY Autoincrement, dt TEXT, nm TEXT, memo_id INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS photos(_id INTEGER PRIMARY KEY Autoincrement, filepath BLOB, memo_id INTEGER);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public int addMemos(SQLiteDatabase db, String name, String place, String contents, Boolean visited){
        ContentValues cv = new ContentValues();
        cv.put("nm",name);
        cv.put("place",place);
        cv.put("contents",contents);
        cv.put("visited", visited);
        db.insert("memos",null, cv);

        // get memoId
        String sql = "SELECT _id FROM memos WHERE nm='"+name+"' AND place='"+place+"';";
        Cursor c = db.rawQuery(sql,null);
        c.moveToNext();
        int memoId = -1;
        try{
            memoId = c.getInt(c.getColumnIndex("_id"));
        }catch (Exception e){
            e.printStackTrace();
        }

        return memoId;
    }

    public void editMemos(SQLiteDatabase db, int id, String name, String place, String contents, Boolean visited){
        ContentValues cv = new ContentValues();
        cv.put("nm",name);
        cv.put("place",place);
        cv.put("contents",contents);
        cv.put("visited", visited);
        db.update("memos",cv,"_id=?", new String[]{String.valueOf(id)});
    }


    public void closeDatabase(DatabaseHelper dbHelper, SQLiteDatabase database){
        database.close();
        dbHelper.close();
    }

    public void deleteMemo(SQLiteDatabase db, int id){
        db.delete("memos","_id=?", new String[]{String.valueOf(id)});
        db.delete("alarms","memo=?",new String[]{String.valueOf(id)});
        db.delete("photos","memo=?",new String[]{String.valueOf(id)});
    }

    public void linkPhotoToMemo(SQLiteDatabase db, int memoId){
        ContentValues cv = new ContentValues();
        cv.put("memo_id",memoId);
        db.update("photos",cv,"memo_id<?",new String[]{String.valueOf(0)});
    }
    public void canclePhotoSave(SQLiteDatabase db){
        db.delete("photos","memo_id<?",new String[]{String.valueOf(0)});
    }
}
