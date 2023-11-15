package com.example.sinupsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    public static final int RESULT_CODE_CREATE_ACCOUNT = 2000;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        activitySetup();

        // Firebase Realtime Databaseの参照を取得
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void activitySetup() {
        EditText dogkind = findViewById(R.id.dogkind);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText rePassword = findViewById(R.id.rePassword);
        EditText before_name = findViewById(R.id.befor_name);
        EditText after_name = findViewById(R.id.after_name);
        Button create = findViewById(R.id.createButton);
        TextView loginAccount = findViewById(R.id.loginAccount);

        mAuth = FirebaseAuth.getInstance();

        create.setOnClickListener(v -> {
            if (password.getText().toString().equals(rePassword.getText().toString())) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // アカウント作成成功時に名前とメールアドレスをデータベースに登録
                                String firstName = before_name.getText().toString();
                                String lastName = after_name.getText().toString();
                                String userEmail = email.getText().toString();
                                String dogkinds = dogkind.getText().toString();

                                // ユーザーごとに一意のキーを生成
                                String userId = mAuth.getCurrentUser().getUid();

                                // データベースにユーザー情報を登録
                                databaseReference.child("users").child(userId).child("firstName").setValue(firstName);
                                databaseReference.child("users").child(userId).child("lastName").setValue(lastName);
                                databaseReference.child("users").child(userId).child("email").setValue(userEmail);
                                databaseReference.child("users").child(userId).child("dogkinds").setValue(dogkinds);
                                databaseReference.child("users").child(userId).child("points").setValue(0);


                                // 新たなアルバムノードを作成
                                String albumId = databaseReference.child("albums").push().getKey();
                                // ユーザーのアルバム情報を登録
                                databaseReference.child("users").child(userId).child("albums").child(albumId).setValue(true);

                                Toast.makeText(CreateAccountActivity.this, "アカウントを作成しました！", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(CreateAccountActivity.this, "アカウント作成に失敗しました", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        loginAccount.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

