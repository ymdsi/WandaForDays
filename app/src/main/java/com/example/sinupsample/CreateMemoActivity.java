package com.example.sinupsample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateMemoActivity extends AppCompatActivity {
    public static final int RESULT_CODE_CREATE_MEMO = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memo);
        EditText memoTitle = findViewById(R.id.memo_title);
        EditText memoContents = findViewById(R.id.memo_contents);
        Button addButton = findViewById(R.id.add_new_memo);
        addButton.setOnClickListener(v -> {
            //メモタイトルに何も入力されていない場合は、必須項目であることをユーザに知らせる。
            if (!memoTitle.getText().toString().isEmpty()){
                //コンテンツに何も入力されていない場合は「詳細なし」で登録する。
                if (memoContents.getText().toString().isEmpty()) {
                    memoContents.setText("詳細なし");
                }
                String[] memo = {getCurrentDate(), memoTitle.getText().toString(), memoContents.getText().toString()};
                Intent intent = getIntent();
                intent.putExtra("memo", memo);
                setResult(RESULT_CODE_CREATE_MEMO, intent);
                finish();
            } else {
                memoTitle.setError("必須入力の項目です");
            }
        });
    }
    //現在日時を取得するメソッド
    private String getCurrentDate(){
        @SuppressLint
                ("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date d = new Date();
        return dateFormat.format(d);
    }
}