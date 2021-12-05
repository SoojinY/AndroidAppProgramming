package com.example.memoyum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PopupSearch extends Activity {
    String searchWord;
    String searchTag;
    String searchPlace;
    Intent intent;

    public static final int RETURN_SEARCH = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_search);
        ImageView searchExit = findViewById(R.id.searchExit);
        EditText edWord = findViewById(R.id.searchWord);
        EditText edTag = findViewById(R.id.searchTag);
        EditText edPlace = findViewById(R.id.searchPlace);
        Button searchInit = findViewById(R.id.searchInit);
        Button searchBt = findViewById(R.id.searchOn);

        intent = getIntent();

        // 창 닫기
        searchExit.setOnClickListener(v -> {
            finish();
        });
        searchInit.setOnClickListener(v->{
            edWord.setText(null);
            edTag.setText(null);
            edPlace.setText(null);
        });
        searchBt.setOnClickListener(v->{
            searchWord = edWord.getText().toString();
            searchTag = edTag.getText().toString();
            searchPlace = edPlace.getText().toString();

            intent.putExtra("words", searchWord);
            intent.putExtra("tags",searchTag.split(","));
            intent.putExtra("place",searchPlace.split(" "));

            setResult(RETURN_SEARCH, intent);
            finish();
        });
    }
}
