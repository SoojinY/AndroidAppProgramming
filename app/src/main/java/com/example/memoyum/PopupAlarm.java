package com.example.memoyum;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;


public class PopupAlarm extends AppCompatActivity {
    Intent intent;
    int memoId;

    public static final int RESULT_OK = 200;
    public static final int REQUEST_RETURN = 101;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    LinearLayoutManager manager;
    RecyclerView alarmLayout;

    ArrayList<Alarm> alarmLst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_add_alarm);

        intent = getIntent();
        memoId = intent.getIntExtra("id", -1);

        ImageView exitBt = findViewById(R.id.alarmExit);
        Button addAlarm = findViewById(R.id.addAlarm);
        alarmLst = new ArrayList<Alarm>();

        alarmLayout = findViewById(R.id.alarmLayout);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        alarmLayout.setLayoutManager(manager);

        if(memoId>0){
            initDatabase();
            getAlarmLst();
        }


        exitBt.setOnClickListener(v -> {
            setResult(RESULT_OK, intent);
            finish();
        });
        addAlarm.setOnClickListener(v -> {
            setAlarmTime();
        });
    }
    //밖의 여백 눌렀을 때 창이 꺼지지 않도록 하는 함수
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    protected void initDatabase(){
        dbHelper = new DatabaseHelper(this);
        try{
            database = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database = dbHelper.getReadableDatabase();
        }
    }


    public void getAlarmLst(){
        Cursor c;
        String sql = "SELECT * FROM alarms WHERE memo="+memoId+";";
        c = database.rawQuery(sql, null);

        while(c.moveToNext()){
            Alarm a = new Alarm();
            int i = 0;
            a._id =c.getInt(i++);
            a.date=c.getString(i++);
            a.name=c.getString(i);

            alarmLst.add(a);
        }
        c.close();
    }

    public void setAlarmTime(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Alarm a = new Alarm();
                a.setDate(year, month, dayOfMonth);
                alarmLst.add(a);
            }
        },mYear, mMonth, mDay);

        datePicker.show();
    }

}
