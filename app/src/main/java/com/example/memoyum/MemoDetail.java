package com.example.memoyum;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MemoDetail extends AppCompatActivity {
    int memoId;
    String hashTags;
    ImageView img;
    TextView nm;
    AppCompatButton place;
    AppCompatButton visited;
    TextView tag;
    TextView writeDt;
    TextView editDt;
    AppCompatButton alarm;
    TextView content;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    Intent intent;
    public static final int REQUEST_RETURN = 101;
    public static final int NO_MATCH_ID = 400;
    public static final int RESULT_OK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);
        intent = getIntent();
        memoId = intent.getIntExtra("id",-1);
        hashTags = intent.getStringExtra("tag");
        if(memoId<0){
            setResult(NO_MATCH_ID, intent);
            finish();
        }

        FloatingActionButton editBt = findViewById(R.id.editMemoBt);
        LinearLayout backBt = findViewById(R.id.goBack);

        editBt.setOnClickListener(v -> {
            Intent i = new Intent(MemoDetail.this, WriteMemo.class);
            i.putExtra("id", memoId);
            i.putExtra("edit",true);
            startActivityForResult(i,REQUEST_RETURN);
        });
        backBt.setOnClickListener(v -> {finish();});

        initComponents();
        initDatabase();
        try {
            getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void initDatabase(){
        dbHelper = new DatabaseHelper(this);
        try{
            database = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database = dbHelper.getReadableDatabase();
        }
    }

    public void getData() throws IOException {
        Cursor c;
        //메모 정보
        String sql = "SELECT * FROM memos WHERE _id="+memoId+";";
        c = database.rawQuery(sql, null);
        if(c.moveToNext()) setMemoData(c);

        //이미지 정보
        sql = "SELECT filepath FROM photos WHERE memo_id="+memoId+";";
        c = database.rawQuery(sql, null);
        if(c.moveToNext()) {
            Photo p = new Photo();
            p.filepath = c.getString(0);
            p.memoId = memoId;
            img.setImageBitmap(p.filepathToBitmap(getApplicationContext()));
        }
        c.close();
    }

    public void setMemoData(Cursor c){
        int i = 1;
        nm.setText(c.getString(i++));
        String longPlace = c.getString(i++);
        String[] placeArr = longPlace.split(" ");
        place.setText(placeArr[1]);
        tag.setText(hashTags);
        i++;
        content.setText(c.getString(i++));
        if(c.getInt(i++)==0){
            visited.setVisibility(View.GONE);
        }
        writeDt.setText(c.getString(i++));
        editDt.setText(c.getString(i));
    }

    public void initComponents(){
        img = findViewById(R.id.foodImg);
        nm = findViewById(R.id.memoDtNm);
        place = findViewById(R.id.memoDtPlace);
        visited = findViewById(R.id.memoDtVisit);
        tag = findViewById(R.id.memoDtTag);
        writeDt = findViewById(R.id.memoDtWriteDate);
        editDt = findViewById(R.id.memoDtEditDate);
        alarm = findViewById(R.id.memoDtAlarm);
        content = findViewById(R.id.memoDtContent);
    }

    public void goEditMemo(View view){
        Intent intent = new Intent(MemoDetail.this, WriteMemo.class);
        startActivityForResult(intent, REQUEST_RETURN);
    }
    public void goBack(View view){
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_RETURN){
            if(resultCode == RESULT_OK){
                initComponents();
                initDatabase();
                try {
                    getData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
