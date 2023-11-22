package com.example.sinupsample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MissionFragment extends Fragment {
    private TextView NowPointTextView;

    public MissionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");


        // ImageButtonを取得
        ImageButton myImageButton = view.findViewById(R.id.back_button);
        NowPointTextView = view.findViewById(R.id.now_points);
        Button changebutton = view.findViewById(R.id.Change_Button);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef.child(userUid).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long userPoints = dataSnapshot.getValue(Long.class);
                    NowPointTextView.setText(String.valueOf(userPoints) + "pt");

                } else {

                    NowPointTextView.setText("ポイントが取得できませんでした");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー処理
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

        changebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new Pointchange_Fragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
