package com.example.memoyum;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

public class Photo {
    int _id;
    String filepath;
    int memoId;

    public Bitmap filepathToBitmap(Context context) throws IOException{
        String path = this.filepath;
        File imgFile = new File(path);


        Bitmap bitmap = null;
        if(imgFile.exists()) {
            Uri img = path2uri(context, path);
            if(img!=null) bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), img);
        }
        return bitmap;
    }

    //Path(파일경로) -> Uri
    public static Uri path2uri(Context context, String filePath) {
        String u = MediaStore.Images.Media.DATA;
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data = '" + filePath + "'", null, null);

        Uri uri = null;
        if(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        }

        return uri;
        }



}
