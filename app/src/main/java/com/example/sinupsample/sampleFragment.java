package com.example.sinupsample;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class sampleFragment extends Fragment {
    private GridView imageGridView;
    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseReference;
    private TextView dogskindTextView;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample, container, false);
        imageGridView = view.findViewById(R.id.imageGridView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");

        dogskindTextView = view.findViewById(R.id.dogkind_text_view);
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firebase Storageの初期化
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Firebase Realtime Databaseの参照を取得
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("albums")
                .child("albumId1"); // アルバムIDを適切なものに置き換えてください

        // 画像を選択するボタンの設定
        ImageButton uploadButton = view.findViewById(R.id.upload_Button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        userRef.child(userUid).child("dogkinds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String dogkind = dataSnapshot.getValue(String.class);
                    dogskindTextView.setText(dogkind);
                } else {
                    // メールアドレスが存在しない場合の処理
                    dogskindTextView.setText("犬種が取得できませんでした");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー処理
            }
        });
        // Firebase Realtime Databaseから画像を読み込む
        loadPhotosFromDatabase();

        return view;
    }

    // 画像を選択するメソッド
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 画像ファイルの拡張子を取得するメソッド
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // 選択した画像をFirebase Storageにアップロードするメソッド
    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // アップロードが成功したら、Firebase Realtime Databaseに写真のメタデータを保存
                        String photoId = databaseReference.push().getKey(); // 新しい写真のユニークなIDを生成
                        databaseReference.child(photoId).setValue(fileRef.getPath());

                        Toast.makeText(getContext(), "アップロードが成功しました", Toast.LENGTH_SHORT).show();

                        // アップロード後に写真を再読み込み
                        loadPhotosFromDatabase();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "アップロードエラー: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Firebase Realtime Databaseから画像を読み込むメソッド
    private void loadPhotosFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] imagePaths = new String[(int) dataSnapshot.getChildrenCount()];

                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imagePath = snapshot.getValue(String.class);
                    if (imagePath != null) {
                        imagePaths[i] = imagePath;
                        i++;
                    }
                }

                // カスタムアダプターを作成
                ImageAdapter adapter = new ImageAdapter(requireContext(), imagePaths);

                // GridViewにアダプターをセット
                imageGridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "データの読み込みエラー: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadFile();
        }
    }
}
