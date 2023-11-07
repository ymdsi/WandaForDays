package com.example.sinupsample;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class folderFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    private StorageReference storageRef;
    private DatabaseReference databaseReference;
    private LinearLayout photoGallery; // 写真を表示するView

    public folderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        // Firebase Storageの初期化
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Firebase Realtime Databaseの参照を取得
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("albums")
                .child("albumId1"); // アルバムIDを適切なものに置き換えてください

        imageView = view.findViewById(R.id.imageView); // 画像を表示するImageView
        FloatingActionButton uploadButton = view.findViewById(R.id.uploadButton); // アップロードボタン
        photoGallery = view.findViewById(R.id.photoGallery); // 写真を表示するView

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

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

    // 選択した画像をFirebase Storageにアップロード
    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // アップロードが成功したら、Firebase Realtime Databaseに写真のメタデータを保存
                        String photoId = databaseReference.push().getKey(); // 新しい写真のユニークなIDを生成
                        databaseReference.child(photoId).setValue(fileRef.getPath());

                        Toast.makeText(getContext(), "アップロードが成功しました", Toast.LENGTH_SHORT).show();
                        // ここで成功時の処理を追加できます

                        // アップロード後に写真を再読み込み
                        loadPhotosFromDatabase();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "アップロードエラー: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // 画像のファイル拡張子を取得
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(uri));
    }

    // データベースから写真を読み込んで表示
    private void loadPhotosFromDatabase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photoGallery.removeAllViews(); // 既存の写真をクリア

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String filePath = snapshot.getValue(String.class);
                    if (filePath != null) {
                        // 写真を表示するImageViewを作成
                        ImageView photoImageView = new ImageView(getContext());
                        photoImageView.setPadding(8, 8, 8, 8);
                        photoImageView.setImageURI(Uri.parse(filePath));
                        photoGallery.addView(photoImageView);
                    }
                }
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
            imageView.setImageURI(imageUri);
            uploadFile();
        }
    }
}
