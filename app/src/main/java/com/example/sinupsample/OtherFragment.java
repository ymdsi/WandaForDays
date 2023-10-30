package com.example.sinupsample;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

public class OtherFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);

        // Profile_button を取得
        // OtherFragment.java の中で Profile_button のクリックイベントを処理
        TextView profileButton = view.findViewById(R.id.Profile_button);
        TextView walkButton = view.findViewById(R.id.walk_button);
        TextView missionButton = view.findViewById(R.id.mission_button);
        TextView questionButton = view.findViewById(R.id.question_button);
        TextView logoutButton = view.findViewById(R.id.logout_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ここにクリック時の処理を追加
                // 例: ProfileFragment に遷移
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ここにクリック時の処理を追加
                // 例: ProfileFragment に遷移
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new WalktimeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }
}

