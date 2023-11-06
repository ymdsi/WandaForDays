package com.example.sinupsample;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.button.MaterialButton;

public class LogoutFragment extends Fragment {

    public LogoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        // ImageButtonを取得
        ImageButton myImageButton = view.findViewById(R.id.back_button);
        // ImageButtonを取得
        MaterialButton yesButton = view.findViewById(R.id.Yes_Button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ログアウト処理を実行
                performLogout();
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

    private void performLogout() {
        AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // ログアウト成功
                        Toast.makeText(requireContext(), "ログアウトしました", Toast.LENGTH_SHORT).show();

                        // 例：ログアウト後にログイン画面に遷移
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // ログアウト失敗
                        Toast.makeText(requireContext(), "ログアウト中にエラーが発生しました", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}