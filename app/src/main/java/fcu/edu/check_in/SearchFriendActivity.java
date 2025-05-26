package fcu.edu.check_in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class SearchFriendActivity extends AppCompatActivity {

    private Button btn_back;
    private Button btn_home;
    private Button btn_search;
    private EditText et_email;
    private TextView tv_output;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_friend);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_back = findViewById(R.id.btn_back_searchFriend);
        btn_home = findViewById(R.id.btn_to_home_searchFriend);
        et_email = findViewById(R.id.et_email_searchFriend);
        btn_search = findViewById(R.id.btn_search_searchFriend);
        tv_output = findViewById(R.id.tv_output_searchFriend);
        db = FirebaseFirestore.getInstance();

        btn_back.setOnClickListener(v -> finish());

        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(SearchFriendActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                if (!email.isEmpty()) {
                    searchUserByEmail(email);
                } else {
                    tv_output.setText("請輸入 Email");
                }
            }
        });
    }

    private void searchUserByEmail(String email) {
        db.collection("users").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String bio = documentSnapshot.getString("bio");
                        String nickname = documentSnapshot.getString("nickName");
                        String followingEmail = documentSnapshot.getString("follwPersonEmail");

                        String resultText = "找到用戶:\n" +
                                "暱稱: " + nickname + "\n" +
                                "Bio: " + bio + "\n" +
                                "追蹤中: " + followingEmail;

                        tv_output.setText(resultText);
                    } else {
                        tv_output.setText("找不到該 Email");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    tv_output.setText("查詢失敗: " + e.getMessage());
                });
    }
}
