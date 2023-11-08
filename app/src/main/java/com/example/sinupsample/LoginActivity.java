package com.example.sinupsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    public static final int RESULT_CODE_LOGIN = 1000;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activitySetup();
    }

    private void activitySetup() {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.loginButton);
        TextView createAccount = findViewById(R.id.createAccount);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//ログイン状態記録
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {


            login.setOnClickListener(v -> {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if (emailText.isEmpty() || passwordText.isEmpty()) {
                    // ユーザーにエラーメッセージを表示
                    Toast.makeText(LoginActivity.this, "Emailとパスワードは必須項目です", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // ログイン成功
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // ユーザー情報をFirebase Realtime Databaseに保存
                                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
                                        databaseRef.child(user.getUid()).child("email").setValue(user.getEmail());
                                        // 他のユーザー情報も必要に応じて保存
                                    }

                                    // MemoListActivityに画面遷移
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // ログインエラー処理
                                    String errorMessage = "ログインに失敗しました";
                                    Exception exception = task.getException();

                                    if (exception != null) {
                                        errorMessage = exception.getLocalizedMessage();
                                    }

                                    Log.e("LoginActivity", "ログインエラー: " + errorMessage);
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }

            createAccount.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                resultLauncher.launch(intent);
            });
        }

        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == CreateAccountActivity.RESULT_CODE_CREATE_ACCOUNT) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        setResult(RESULT_CODE_LOGIN, intent);
                        finish();
                    }
                });
    }
