package fcu.edu.check_in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public class show_following_people extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_following_people);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String name = getIntent().getStringExtra("name");
        TextView nameFollowPeople = findViewById(R.id.tv_nickname_showFollowingPeople);
        Button btnBack = findViewById(R.id.btn_back_showFollowingPeople);
        Button btnHome = findViewById(R.id.btn_home_showFollowingPeople);

        if (name != null) {
            nameFollowPeople.setText("暱稱：" + name);
        } else {
            nameFollowPeople.setText("暱稱：未知使用者");
        }

        btnBack.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> switchToActivity(MainActivity.class));
    }

    private void switchToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(show_following_people.this, targetActivity);
        startActivity(intent);
    }
}