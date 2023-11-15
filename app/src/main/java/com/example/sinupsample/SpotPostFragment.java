package com.example.sinupsample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SpotPostFragment extends Fragment {

//    FirebaseAuth mAuth;
    DatabaseReference databaseReference;


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

//        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        post_button.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(spot_title.getText()) && !TextUtils.isEmpty(spot_detail.getText()) && !TextUtils.isEmpty(spot_address.getText())) {
                String title = spot_title.getText().toString();
                String detail = spot_detail.getText().toString();
                String address = spot_address.getText().toString();

                databaseReference.child("spot").child(title).child("スポット名").setValue(title);
                databaseReference.child("spot").child(title).child("詳細").setValue(detail);
                databaseReference.child("spot").child(title).child("住所").setValue(address);

                Toast.makeText(requireContext(), "投稿に成功しました", Toast.LENGTH_SHORT).show();
                //データベースの設定呼び出し
                loadSpotFromDatabase();

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

        return view;
    }
        private void loadSpotFromDatabase() {

        }
    }
