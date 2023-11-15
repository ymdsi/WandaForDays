package com.example.sinupsample;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 初期フラグメントを表示
        loadFragment(new HomeFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_map) {
                // "Map" アイテムが選択されたときの処理
//                loadFragment(new MapsFragment());
//                loadFragment(new MapsearchFragment());
                loadFragment(new SpotFragment());
                return true;
            } else if (itemId == R.id.navigation_board) {
                // "Board" アイテムが選択されたときの処理
                loadFragment(new WebViewFragment());
                return true;
            } else if (itemId == R.id.navigation_folder) {
                // "Folder" アイテムが選択されたときの処理
                loadFragment(new sampleFragment());
                return true;
            } else if (itemId == R.id.navigation_Other) {
                loadFragment(new OtherFragment());
//
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
