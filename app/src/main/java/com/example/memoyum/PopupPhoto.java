package com.example.memoyum;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.IOException;

public class PopupPhoto extends Activity {
    final int OPEN_GALLERY = 102;

    ImageView photoImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_add_photo);

        Button addPhoto = findViewById(R.id.addPhoto);
        ImageView photoExit = findViewById(R.id.photoExit);
        photoImg = findViewById(R.id.photoImg);

        addPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, OPEN_GALLERY);
        });
        photoExit.setOnClickListener(v->{
            //이미지 경로 저장
            finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_GALLERY:
                if (data != null && resultCode == RESULT_OK)
                {
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
                    String picturePath = cursor.getString(columnIndex);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    photoImg.setImageBitmap(bitmap);
                    cursor.close();
                }
                break;
        }
    }

}
