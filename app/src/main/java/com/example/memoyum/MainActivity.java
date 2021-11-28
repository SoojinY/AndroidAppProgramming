package com.example.memoyum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Asset a = new Asset();
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    RecyclerView cardLayout;
    MemoAdapter adapter;

    private final int ALL = 0;
    private final int NOT_VISITED=1;
    private final int VISITED=2;

    public static final int REQUEST_RETURN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //레이아웃 지정
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //뷰
        ImageView menuBt = findViewById(R.id.menuBt);
        FloatingActionButton newMemoBt = (FloatingActionButton) findViewById(R.id.addMemoBt);
        RadioGroup visitGrp = findViewById(R.id.visitGrp);
        cardLayout = findViewById(R.id.cardLayout);



        // 카드 리스트 생성
        makeCardList(ALL);

        //뷰 이벤트
        //햄버거메뉴
        menuBt.setOnClickListener(v -> {});
        //새메모
        newMemoBt.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, WriteMemo.class);
            startActivityForResult(intent, REQUEST_RETURN);
        });
        //방문 라디오 버튼
        visitGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int state = 0;
                switch (checkedId){
                    case R.id.visitAllBt:
                        state = ALL;
                        break;
                    case R.id.visitNoBt:
                        state = NOT_VISITED;
                        break;
                    case R.id.visitYesBt:
                        state = VISITED;
                        break;
                }
                makeCardList(state);
            }
        });

    }

    protected void initDatabase(){
        dbHelper = new DatabaseHelper(this);
        try{
            database = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database = dbHelper.getReadableDatabase();
        }
    }

    public int makeCardList(int state){
        initDatabase();
        ArrayList<Memo> memoLst = getMemoLst();
//        dbHelper.closeDatabase(dbHelper, database);

        if(memoLst.isEmpty()){
            Toast toast = Toast.makeText(this.getApplicationContext(),"입력된 메모가 없습니다.",Toast.LENGTH_SHORT);
            toast.show();
            return -1;
        }

        adapter = new MemoAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        cardLayout.setLayoutManager(manager);

        for(Memo m : memoLst){
            switch (state){
                case ALL:
                    adapter.addItem(m);
                    break;
                case NOT_VISITED:
                    if(!m.visited) adapter.addItem(m);
                    break;
                case VISITED:
                    if(m.visited) adapter.addItem(m);
                    break;
            }
        }


        // 카드 클릭 이벤트
        // 길게 누를 때 (삭제)
        adapter.setOnItemLongClickListener(new MemoAdapter.OnItemLongClickEventListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                final Memo item = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //메모 삭제
                        deleteMemo(item);
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소
                    }
                });
                AppCompatDialog dialog = builder.create();
                dialog.show();
            }
        });

        // 짧게 누를 때 (상세 조회)
        adapter.setOnItemClickListener(new MemoAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {
                final Memo item = adapter.getItem(a_position);

                Intent intent = new Intent(MainActivity.this, MemoDetail.class);
                intent.putExtra("id",item._id);
                intent.putExtra("tag",item.tagsToString());
                startActivityForResult(intent, REQUEST_RETURN);
            }
        });

        cardLayout.setAdapter(adapter);

        return memoLst.size();
    }

    public ArrayList<Memo> getMemoLst(){
        ArrayList<Memo> memoLst= new ArrayList<Memo>();
        Cursor c;
        String sql = "SELECT * FROM memos;";
        c = database.rawQuery(sql, null);

        while(c.moveToNext()){
            Memo m = new Memo();
            int j = 0;
            m._id=c.getInt(j++);
            m.name=c.getString(j++);
            m.place=c.getString(j++);
            String hashTags = c.getString(j++);
            m.setTags(hashTags,this);
            m.contents = c.getBlob(j++);
            if(c.getInt(j++)>0){
                m.visited = true;
            }else{
                m.visited = false;
            }
            m.writedt = c.getString(j++);
            m.editdt = c.getString(j);
            memoLst.add(m);
        }
        c.close();
        return memoLst;
    }

    public void deleteMemo(Memo item){
        int id = item._id;
        dbHelper.deleteMemo(database, id);
        makeCardList(ALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_RETURN){
            makeCardList(ALL);
        }
    }
}