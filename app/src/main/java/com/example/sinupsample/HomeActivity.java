package com.example.sinupsample;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    AsyncNetworkTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        task = new AsyncNetworkTask(this);
        task.execute("https://api.open-meteo.com/v1/forecast?latitude=35.7&longitude=139.6875&hourly=temperature_2m,soil_temperature_0cm&timezone=Asia%2FTokyo&forecast_days=1");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 初期フラグメントを表示
//        loadFragment(new HomeFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // "Home" アイテムが選択されたときの処理
                Intent intent = new Intent(HomeActivity.this, AsyncNetworkTask.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_map) {
                // "Map" アイテムが選択されたときの処理
                loadFragment(new MapsFragment());
                return true;
            } else if (itemId == R.id.navigation_board) {
                // "Board" アイテムが選択されたときの処理
                Intent intent = new Intent(HomeActivity.this, MemoListActivity.class);
            startActivity(intent);
            } else if (itemId == R.id.navigation_folder) {
                // "Folder" アイテムが選択されたときの処理
//                loadFragment(new FolderFragment());
                return true;
            } else if (itemId == R.id.navigation_Other) {
                // "Other" アイテムが選択されたときの処理
//                loadFragment(new OtherFragment());
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
