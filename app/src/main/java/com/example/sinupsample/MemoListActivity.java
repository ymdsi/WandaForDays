package com.example.sinupsample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MemoListActivity extends AppCompatActivity {
    private FirebaseUser user;
    private String uid;
    private CustomListAdapter customListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);
        setSupportActionBar(findViewById(R.id.tool_bar));

    }
    @Override
    protected void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Intent intent = new Intent(MemoListActivity.this, LoginActivity.class);
            resultLauncher.launch(intent);
        } else {
            appSetup();
        }
    }
    private void appSetup(){
        Toolbar toolbar = findViewById(R.id.tool_bar);
        ListView memoListView = findViewById(R.id.memo_list);
        ArrayList<MemoData> memoListItem = new ArrayList<>();
        toolbar.setTitle("Memo List");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid = user.getUid();
        customListAdapter = new CustomListAdapter(this, R.layout.custom_memo_card, memoListItem);
        memoListView.setAdapter(customListAdapter);
        databaseListener();
        FloatingActionButton addMemo = findViewById(R.id.add_new_memo);
        addMemo.setOnClickListener(v -> {
            Intent intent = new Intent(MemoListActivity.this, CreateMemoActivity.class);
            resultLauncher.launch(intent);
        });
        memoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            MemoData memoData =(MemoData) memoListView.getItemAtPosition(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("メモの削除")
                    .setMessage("選択したメモを削除しますか？")
                    .setPositiveButton("削除",(dialog, which) -> {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                        userRef.child("Memo").child(memoData.get_memoTitle()).removeValue((error, ref) -> {
                            Toast.makeText(MemoListActivity.this, "メモを削除しました", Toast.LENGTH_SHORT).show();
                            databaseListener();
                        });
                    })
                    .setNegativeButton("やめる",(dialog, which) -> {})
                    .show();
            return false;
        });
    }
    private void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(MemoListActivity.this, LoginActivity.class);
                    Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show();
                    resultLauncher.launch(intent);
                });
    }
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == LoginActivity.RESULT_CODE_LOGIN){
                    Toast.makeText(this, "ログインしました", Toast.LENGTH_SHORT).show();
                }
                if (result.getResultCode() == CreateMemoActivity.RESULT_CODE_CREATE_MEMO){
                    Intent intent = result.getData();
                    assert intent != null;
                    String[] memo = intent.getStringArrayExtra("memo");
                    MemoData memoData = new MemoData(uid, memo[0], memo[1], memo[2], memo[3], memo[4], memo[5]);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                    userRef.child("Memo").child(memo[1]).setValue(memoData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            databaseListener();
                            Toast.makeText(MemoListActivity.this,"メモを追加しました",Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MemoListActivity.this,"メモの追加に失敗しました",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            });
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memo_list_tool_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ログアウト確認")
                    .setMessage("ログアウトしますか？")
                    .setPositiveButton("はい", (dialog, which) -> {
                        logout();
                    })
                    .setNegativeButton("いいえ", (dialog, which) -> {
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void databaseListener(){
        customListAdapter.clear();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.child("Memo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MemoData memoData = snapshot.getValue(MemoData.class);
                customListAdapter.add(memoData);
                customListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MemoData memoData = snapshot.getValue(MemoData.class);

                // CustomListAdapter から該当のメモを削除
                customListAdapter.remove(memoData);

                // CustomListAdapter を更新して変更を通知
                customListAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}
