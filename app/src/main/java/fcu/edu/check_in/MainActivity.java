package fcu.edu.check_in;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (!isLoggedIn) {
            // 第一次使用或尚未登入
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // 關閉 MainActivity
            return;
        }
        // Step 2: 若已登入，顯示主畫面
        setContentView(R.layout.activity_main); // 你的主畫面 layout 檔

        // Step 3: 預設顯示 HomeFragment
        loadFragment(new HomeFragment());

        // Step 4: 設定 BottomNavigation 切換
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_follow) {
                selectedFragment = new FollowFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
