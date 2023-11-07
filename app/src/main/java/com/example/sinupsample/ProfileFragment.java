package com.example.sinupsample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private TextView lastNameTextView;
    private TextView userEmailTextView;
    private TextView firstNameTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        lastNameTextView = view.findViewById(R.id.lastNameTextView);
        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        // ImageButtonを取得
        ImageButton myImageButton = view.findViewById(R.id.back_button);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef.child(userUid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userEmail = dataSnapshot.getValue(String.class);
                    userEmailTextView.setText(userEmail);
                } else {
                    // メールアドレスが存在しない場合の処理
                    userEmailTextView.setText("メールアドレスが取得できませんでした");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー処理
            }
        });
        userRef.child(userUid).child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userFirstName = dataSnapshot.getValue(String.class);
                    firstNameTextView.setText(userFirstName);
                } else {
                    // メールアドレスが存在しない場合の処理
                    firstNameTextView.setText("姓が取得できませんでした");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー処理
            }
        });

        userRef.child(userUid).child("lastName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userLastName = dataSnapshot.getValue(String.class);
                    lastNameTextView.setText(userLastName);
                } else {
                    // 姓が存在しない場合の処理
                    lastNameTextView.setText("名が取得できませんでした");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー処理
                lastNameTextView.setText("データベースエラー: " + databaseError.getMessage());
            }
        });
        // ImageButtonにクリックリスナーを設定
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new OtherFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }


}