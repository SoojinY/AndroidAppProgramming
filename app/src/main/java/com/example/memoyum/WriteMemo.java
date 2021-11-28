package com.example.memoyum;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WriteMemo extends AppCompatActivity {
    private View 	decorView;
    private int	uiOption;

    private final int ERR_FULLFILLED = 0;
    private final int SAVED = 1;
    private final int ERR_UNSAVED = 2;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    int memoId;
    boolean edit;

    String name="";
    String place="";
    String tags="";
    String contents="";
    Boolean visited=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_memo);

        Intent intent = getIntent();
        edit = intent.getBooleanExtra("edit",false);
        if(edit){
            memoId = intent.getIntExtra("id",-1);
        }
        
        // 하단 네비게이션 바 숨기기
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility( uiOption );

        // 베튼
        Button saveBt = findViewById(R.id.saveNew);
        Button backBt = findViewById(R.id.back1);

        saveBt.setOnClickListener(v->{saveMemo();});
        backBt.setOnClickListener(v -> {finish();});

        initDatabase();
        if(edit){
            setEdit();
        }
    }
    public void setEdit(){
        EditText name = findViewById(R.id.name);
        EditText place = findViewById(R.id.place);
        EditText tags = findViewById(R.id.tags);
        EditText contents = findViewById(R.id.contents);
        SwitchMaterial visited = findViewById(R.id.visitSwt);

        Cursor c;
        String sql = "SELECT * FROM memos WHERE _id="+memoId+";";
        c = database.rawQuery(sql,null);
        c.moveToNext();

        int i = 1;
        name.setText(c.getString(i++));

        String longPlace = c.getString(i++);
        place.setText(longPlace);

        StringBuilder tag = new StringBuilder();
        String[] tagIds = (c.getString(i++)).split(",");
        for(String s : tagIds){
            String sql1 = "SELECT tagnm FROM tags WHERE _id="+s+";";
            Cursor c1 = database.rawQuery(sql1, null);
            c1.moveToNext();
            tag.append(c1.getString(0)).append(", ");
        }
        tag.deleteCharAt(tag.length()-1);
        tag.deleteCharAt(tag.length()-1);
        tags.setText(tag.toString());

        contents.setText(c.getString(i++));

        if(c.getInt(i)>0){
            visited.setChecked(true);
        }else{
            visited.setChecked(false);
        }

    }
    // 데이터 베이스 설정
    protected void initDatabase(){
        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        if(database == null){
            showMsg(ERR_FULLFILLED);
            return;
        }
        dbHelper.onCreate(database);
    }

    // 메모 저장
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveMemo(){
        if(!getData()){ return; }

        // 태그 가공
        String tagSet = tags.replace(" ",",");
        String[] tArr = tagSet.split(",");
        ArrayList<String> tagArr = new ArrayList<String>();
        for(String s : tArr){
            if(s.isEmpty() || s.equals(" ")) continue;
            tagArr.add(s);
        }
        StringBuilder tagStr = new StringBuilder();

        if(edit){
            dbHelper.editMemos(database, memoId,name, place, contents, visited);
        }
        else{
            if(dbHelper.addMemos(database, name, place, contents, visited)<0){
                showMsg(ERR_UNSAVED);
                return;
            }
        }

        String q1 = "nm='"+name+"' and place='"+place+"'";
        Cursor c1 = database.query("memos",null,q1,null,null,null,null);

        c1.moveToNext();
        String ID = c1.getString(0);

        for (String s : tagArr) {
            // 중복되지 않게 태그 저장
            ContentValues cv = new ContentValues();
            cv.put("tagnm", s);
            database.insertWithOnConflict("tags", null, cv, SQLiteDatabase.CONFLICT_IGNORE);

            // 태그 데이터 생성
            String q2 = "SELECT * FROM tags WHERE tagnm='" + s + "';";
            c1 = database.rawQuery(q2, null);
            c1.moveToNext();
            String tagId = Integer.toString(c1.getInt(0));
            tagStr.append(tagId).append(",");
        }
        ContentValues cv = new ContentValues();
        LocalDate now = LocalDate.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String date = now.format(f);
        cv.put("tags", tagStr.toString());
        if(!edit){
            cv.put("writedt",date);
        }
        cv.put("editdt",date);
        database.update("memos", cv,"_id=?", new String[]{ID});

        c1.close();
        dbHelper.closeDatabase(dbHelper,database);

        showMsg(SAVED);
        finish();
    }

    // 알림 대화상자
    public void showMsg(int op){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg="";
        builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        switch (op){
            case ERR_FULLFILLED:
                msg = "필수 항목을 적어주세요.";
                break;
            case ERR_UNSAVED:
                msg = "저장에 실패했습니다.\n다시 시도해주세요";
                break;
            case SAVED:
                msg = "저장되었습니다.";
                break;
        }
        builder.setMessage(msg);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.dismiss();
    }

    // 데이터 읽기
    public Boolean getData(){
        EditText nameT = findViewById(R.id.name);
        EditText placeT = findViewById(R.id.place);
        EditText tagsT = findViewById(R.id.tags);
        EditText contentsT = findViewById(R.id.contents);
        SwitchMaterial visitedBt = findViewById(R.id.visitSwt);

        name = nameT.getText().toString();
        place = placeT.getText().toString();
        tags = tagsT.getText().toString();
        contents = contentsT.getText().toString();
        visited = visitedBt.isChecked();

        if(name.isEmpty()||place.isEmpty()){
            showMsg(ERR_FULLFILLED);
            return false;
        }

        return true;
    }
}
