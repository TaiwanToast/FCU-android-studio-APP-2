package fcu.edu.check_in;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import fcu.edu.check_in.model.Person;
import fcu.edu.check_in.model.PersonManager;

public class MainActivity extends AppCompatActivity {

    private final String IS_SIGN_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 使用 EdgeToEdge
        setContentView(R.layout.activity_main); // Step 2: 若已登入，顯示主畫面

        // 處理系統 UI 邊距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Step 1: 檢查是否已登入
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(IS_SIGN_IN, false);

        if (!isLoggedIn) {
            // 第一次使用或尚未登入
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // 關閉 MainActivity
            return;
        }else{
            String email = prefs.getString("email", null).toString();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(email);
            docRef.get().addOnCompleteListener(task -> {
                String nickName = "", bio = "";
                List<String> followPersonEmail;
                Map<String, Map<String, String>> followingTaskID;
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> map = doc.getData();
                        if (map != null) {
                            if (map.containsKey("nickName")) {
                                nickName = Objects.requireNonNull(map.get("nickName")).toString();
//                                            Log.d(TAG, "nick name is " + nickName);
                            } else {
                                Log.d(TAG, "nick name does not exist");
                            }
                            if (map.containsKey("bio")) {
                                bio = Objects.requireNonNull(map.get("bio")).toString();
                            } else {
                                Log.d(TAG, "bio does not exist");
                            }

                            Person person = new Person(nickName, bio, email, null, null);
                            PersonManager.getInstance().setCurrentPerson(person);
                            } else {
                            Log.d(TAG, "map is null");
                        }
                    } else {
                        Log.w(TAG, "使用者資料不存在");
                    }
                } else {
                    Log.e(TAG, "Firebase 查詢失敗", task.getException());
                }

            });
        }
        // 建立 Fragment 實例
        Fragment homeFragment = new HomeFragment();
        Fragment profileFragment = new ProfileFragment();
        Fragment followFragment = new FollowFragment();

        // 根據傳入參數 navigate_to 決定初始 Fragment
        String destination = getIntent().getStringExtra("navigate_to");
        Fragment fragmentToShow;

        if ("profile".equals(destination)) {
            fragmentToShow = profileFragment;
        } else if ("follow".equals(destination)) {
            fragmentToShow = followFragment;
        } else {
            fragmentToShow = homeFragment; // Step 3: 預設顯示 HomeFragment
        }

        setCurrentFragment(fragmentToShow);

        // Step 4: 設定 BottomNavigation 切換
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                setCurrentFragment(homeFragment);
            } else if (itemId == R.id.nav_profile) {
                setCurrentFragment(profileFragment);
            } else if (itemId == R.id.nav_follow) {
                setCurrentFragment(followFragment);
            }
            return true;
        });
    }

    // 載入指定的 Fragment
    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
