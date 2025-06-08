package fcu.edu.check_in;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;
public class show_check_detail extends AppCompatActivity {
    Button btnBack,btnFollow;
    TextView checkTitle,checkBio;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email, taskID;
    SharedPreferences prefs;
    TextView ownerEmailText;
    String owneremail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_check_detale);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.check_detail_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        email = prefs.getString("email", null);
        checkTitle = findViewById(R.id.checkTitle);
        checkBio = findViewById(R.id.checkBio);
        btnBack = findViewById(R.id.btnBack);
        btnFollow = findViewById(R.id.btnFollow);
        ownerEmailText = findViewById(R.id.ownerEmailText);
        taskID = getIntent().getStringExtra("taskID");
        initFromFirebase();
        btnBack.setOnClickListener(v -> finish());
        btnFollow.setOnClickListener(v -> {
            String currentText = btnFollow.getText().toString();
            if (currentText.equals("取消關注")) {
                // 顯示確認視窗
                new AlertDialog.Builder(this)
                        .setTitle("確定取消關注？")
                        .setMessage("取消後將無法收到此打卡單的更新通知。")
                        .setPositiveButton("確定", (dialog, which) -> {
                            db.collection("users").document(email)
                                    .update("followingTaskID." + taskID, FieldValue.delete())
                                    .addOnSuccessListener(unused -> {
                                        btnFollow.setText("關注");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firebase", "取消關注失敗：" + e.getMessage());
                                    });
                        })
                        .setNegativeButton("取消", null)
                        .show();
            } else {
                // 前往 edit_check 頁面
                Intent intent = new Intent(show_check_detail.this, edit_check.class);
                intent.putExtra("taskID", taskID);
                startActivity(intent);
            }
        });
        ownerEmailText.setOnClickListener(v -> {
            if (!owneremail.isEmpty()) {
                Intent intent = new Intent(show_check_detail.this, show_following_people.class);
                intent.putExtra("email", owneremail);
                startActivity(intent);
            } else {
                Toast.makeText(this, "尚未載入製作者 Email", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initFromFirebase() {
        if (email == null || taskID == null) return;
        db.collection("users").document(email).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                Map<String, Object> data = userDoc.getData();
                if (data != null && data.containsKey("followingTaskID")) {
                    Map<String, Object> followingMap = (Map<String, Object>) data.get("followingTaskID");
                    if (followingMap != null && followingMap.containsKey(taskID)) {
                        btnFollow.setText("取消關注");
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e("Firebase", "讀取使用者資料失敗：" + e.getMessage()));

        db.collection("task").document(taskID).get().addOnSuccessListener(taskDoc -> {
            if (taskDoc.exists()) {
                Map<String, Object> taskData = taskDoc.getData();
                String bio = "";
                String title = "";
                if (taskData != null) {
                    bio = taskData.containsKey("bio") ? (String) taskData.get("bio") : "";
                    title = taskData.containsKey("title") ? (String) taskData.get("title") : "";
                    owneremail = taskData.containsKey("ownerEmail") ? (String) taskData.get("ownerEmail") : "";
                }
                // 更新畫面文字
                checkTitle.setText("打卡單名稱: "+title);
                checkBio.setText("簡介: "+bio);
                ownerEmailText.setText(owneremail);
            }
        }).addOnFailureListener(e -> Log.e("Firebase", "讀取任務資料失敗：" + e.getMessage()));
    }
}