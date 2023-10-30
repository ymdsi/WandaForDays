package com.example.sinupsample;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;  // Log のインポートを追加

import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtherFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");

        TextView profileButton = view.findViewById(R.id.Profile_button);
        TextView walkButton = view.findViewById(R.id.walk_button);
        TextView missionButton = view.findViewById(R.id.mission_button);
        TextView questionButton = view.findViewById(R.id.question_button);
        TextView logoutButton = view.findViewById(R.id.logout_button);
        TextView firstNameTextView = view.findViewById(R.id.firstNameTextView);
        TextView lastNameTextView = view.findViewById(R.id.lastNameTextView);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    // データのログ出力
                    Log.d("FirebaseData", "First Name: " + firstName);
                    Log.d("FirebaseData", "Last Name: " + lastName);

                    if (firstName != null) {
                        firstNameTextView.setText("こんにちは、" + firstName + "さん");
                    } else {
                        firstNameTextView.setText("First Nameがnullです");
                    }

                    if (lastName != null) {
                        lastNameTextView.setText(lastName);
                    } else {
                        lastNameTextView.setText("Last Nameがnullです");
                    }
                } else {
                    // データが存在しない場合の処理
                    firstNameTextView.setText("データが存在しません");
                    lastNameTextView.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // データの読み取りに失敗した場合の処理
                // 必要に応じてエラーハンドリングを追加
            }
        });


        // 以下は、ボタンのクリック処理のコードです
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new WalktimeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        missionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MissionFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new QuestionFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new LogoutFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
