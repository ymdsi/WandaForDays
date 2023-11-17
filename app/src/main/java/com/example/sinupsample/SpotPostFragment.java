package com.example.sinupsample;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;


public class SpotPostFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    public SpotPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot_post, container, false);

        ImageButton myImageButton = view.findViewById(R.id.back_button);
        Button post_button = view.findViewById(R.id.Post_Button);
        EditText spot_title = view.findViewById(R.id.title_edittext);
        EditText spot_detail = view.findViewById(R.id.detail_edittext);
        EditText spot_address = view.findViewById(R.id.address_edittext);
        Button PhotoPicButton = view.findViewById(R.id.Photo_pic);
        ImageView cameraButton = view.findViewById(R.id.CameraButton);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageRef = FirebaseStorage.getInstance().getReference("おすすめスポット写真");

        post_button.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(spot_title.getText()) && !TextUtils.isEmpty(spot_detail.getText()) && !TextUtils.isEmpty(spot_address.getText())) {
                String title = spot_title.getText().toString();
                String detail = spot_detail.getText().toString();
                String address = spot_address.getText().toString();

                String spotId = UUID.randomUUID().toString();

                databaseReference.child("spot").child(spotId).child("スポット名").setValue(title);
                databaseReference.child("spot").child(spotId).child("詳細").setValue(detail);
                databaseReference.child("spot").child(spotId).child("住所").setValue(address);
                databaseReference.child("spot").child(spotId).child("作成者キー").setValue(userUid);
                databaseReference.child("spot").child(spotId).child("スポットID").setValue(spotId);

                if (imageUri != null) {
                    StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                    fileRef.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                // アップロードが成功したら、Firebase Realtime Databaseに写真のメタデータを保存
                                databaseReference.child("spot").child(spotId).child("写真URL").setValue(fileRef.getPath());

                                Toast.makeText(getContext(), "アップロードが成功しました", Toast.LENGTH_SHORT).show();


                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "アップロードエラー: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

                Toast.makeText(requireContext(), "投稿に成功しました", Toast.LENGTH_SHORT).show();


                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SpotFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(requireContext(), "おすすめスポット、詳細、住所を入力してください", Toast.LENGTH_SHORT).show();
            }

        });

        // ImageButtonにクリックリスナーを設定
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SpotFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        PhotoPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                openFileChooser();
            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // 画像が選択されたら、選択された画像のURIを取得
            ImageView cameraButton = getView().findViewById(R.id.CameraButton);
            cameraButton.setImageURI(imageUri);
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
