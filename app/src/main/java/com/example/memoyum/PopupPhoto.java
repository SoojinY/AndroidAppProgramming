package com.example.memoyum;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PopupPhoto extends Activity {
    final int OPEN_GALLERY = 102;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Intent intent;
    Photo photo;
    ImageView photoImg;

    public static final int PHOTO_SAVED = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_add_photo);

        photo = new Photo();
        // db open
        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);

        Button addPhoto = findViewById(R.id.addPhoto);
        Button photoSave = findViewById(R.id.photoSave);
        Button photoCancel = findViewById(R.id.photoCancel);
        photoImg = findViewById(R.id.photoImg);

        intent = getIntent();
        //수정인 경우 이미지 불러오기
        photo.memoId = intent.getIntExtra("id", -1);
        String sql = "SELECT * FROM photos WHERE memo_id=" + photo.memoId + ";";
        Cursor c = database.rawQuery(sql, null);
        if(c.moveToNext()){
            photo.filepath = c.getString(c.getColumnIndex("filepath"));
            try {
                setImageViewFromPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        addPhoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, OPEN_GALLERY);
        });
        photoSave.setOnClickListener(v -> {
            //이미지 경로 저장
            savePhoto(photo);
            setResult(PHOTO_SAVED, intent);
            finish();
        });
        photoCancel.setOnClickListener(v -> {
            finish();
        });
    }

    // 이미지 저장
    public void savePhoto(Photo p) {
        if(p.filepath==null || p.filepath.isEmpty()) return;

        ContentValues cv = new ContentValues();

        cv.put("filepath", p.filepath);
        cv.put("memo_id", p.memoId);

        // 새로운 메모인 경우
        if (p.memoId < 0) {
            database.insert("photos", null, cv);
        }
        // 수정인 경우
        else {

            String sql = "SELECT * FROM photos WHERE memo_id="+ p.memoId+";";
            Cursor c = database.rawQuery(sql,null);
            if(c.moveToNext()){
                database.update("photos", cv, "memo_id=?", new String[]{String.valueOf(p.memoId)});
            }
            else{
                // 사진이 없는 메모에 사진 추가
                database.insert("photos",null, cv);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_GALLERY:
                if (data != null && resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    if (cursor == null || cursor.getCount() < 1) {
                        return; // no cursor or no record. DO YOUR ERROR HANDLING
                    }

                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    if (columnIndex < 0) // no column index
                        return; // DO YOUR ERROR HANDLING


                    // 선택한 파일 경로
                    photo.filepath = cursor.getString(columnIndex);

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoImg.setImageBitmap(bitmap);
                    cursor.close();
                }
                break;
        }
    }

    //파일경로로 ImageView setting
    public void setImageViewFromPath() throws IOException {

        Bitmap bitmap = photo.filepathToBitmap(getApplicationContext());
        if (bitmap != null) photoImg.setImageBitmap(bitmap);

    }
}
