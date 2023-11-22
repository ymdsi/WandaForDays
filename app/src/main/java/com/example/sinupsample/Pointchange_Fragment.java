package com.example.sinupsample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Pointchange_Fragment extends Fragment {


    public Pointchange_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pointchange_, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageButton myImageButton = view.findViewById(R.id.back_button);
        Button Button2500 = view.findViewById(R.id.pointchange2500);
        Button Button5000 = view.findViewById(R.id.pointchange5000);
        Button Button10000 = view.findViewById(R.id.pointchange10000);

        Button2500.setEnabled(false);
        Button5000.setEnabled(false);
        Button10000.setEnabled(false);

        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MissionFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        userRef.child(userUid).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long userPoints = dataSnapshot.getValue(Long.class);
                    if (userPoints >= 2500) {
                        Button2500.setEnabled(true);
                    }
                    if (userPoints >= 5000) {
                        Button5000.setEnabled(true);
                    }
                    if (userPoints >= 10000) {
                        Button10000.setEnabled(true);
                    }

                }
                Button2500.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference userPointsRef = userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points");
                        userPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // データがHashMap型であるか確認
                                    if (dataSnapshot.getValue() instanceof Long) {
                                        // データをLong型に変換
                                        Long userPoints = dataSnapshot.getValue(Long.class);

                                        // +30して更新
                                        userPointsRef.setValue(userPoints - 2500);
                                        Toast.makeText(requireContext(), "5%クーポン獲得しました！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // エラー処理
                            }
                        });
                        Button2500.setEnabled(false);
                        Button2500.setText("交換完了");
                    }
                });

                Button5000.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference userPointsRef = userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points");
                        userPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // データがHashMap型であるか確認
                                    if (dataSnapshot.getValue() instanceof Long) {
                                        // データをLong型に変換
                                        Long userPoints = dataSnapshot.getValue(Long.class);

                                        // +30して更新
                                        userPointsRef.setValue(userPoints - 5000);
                                        Toast.makeText(requireContext(), "10%クーポン獲得しました！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // エラー処理
                            }
                        });
                        Button5000.setEnabled(false);
                        Button5000.setText("交換完了");
                    }
                });

                Button10000.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference userPointsRef = userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points");
                        userPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // データがHashMap型であるか確認
                                    if (dataSnapshot.getValue() instanceof Long) {
                                        // データをLong型に変換
                                        Long userPoints = dataSnapshot.getValue(Long.class);

                                        // +30して更新
                                        userPointsRef.setValue(userPoints - 10000);
                                        Toast.makeText(requireContext(), "わんこのおもちゃ引換券獲得しました！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // エラー処理
                            }
                        });
                        Button10000.setEnabled(false);
                        Button10000.setText("交換完了");
                    }
                });
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー処理
            }
        });

        return view;
    }
}