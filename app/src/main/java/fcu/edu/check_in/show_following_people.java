package fcu.edu.check_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.edu.check_in.adapter.OtherTaskAdapter;
import fcu.edu.check_in.model.MyTask;

public class show_following_people extends AppCompatActivity {

    TextView tvNickname, tvInterview;
    RecyclerView rvFollowingTask;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email;
    Button btnBack, btnHome;

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

        email = getIntent().getStringExtra("email");
        tvNickname = findViewById(R.id.tv_nickname_showFollowingPeople);
        tvInterview = findViewById(R.id.tv_interview_showFollowingPeople);
        rvFollowingTask = findViewById(R.id.rv_friendFollowingTask_showFollowingPeople);
        btnBack = findViewById(R.id.btn_back_showFollowingPeople);
        btnHome = findViewById(R.id.btn_home_showFollowingPeople);

        btnBack.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        if (email != null) {
            loadUserData();
        } else {
            tvNickname.setText("暱稱：未知使用者");
        }
    }

    private void loadUserData() {
        db.collection("users").document(email).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String nickname = userDoc.contains("nickName") ? userDoc.getString("nickName") : "未知";
                String interview = userDoc.contains("bio") ? userDoc.getString("bio") : "";
                tvNickname.setText("暱稱：" + nickname);
                tvInterview.setText("簡介：" + interview);

                Object rawMap = userDoc.get("followingTaskID");
                if (rawMap instanceof Map) {
                    Map<String, Object> followingMap = (Map<String, Object>) rawMap;
                    if (followingMap != null && !followingMap.isEmpty()) {
                        List<String> taskIDs = new ArrayList<>(followingMap.keySet());
                        loadFollowingTasks(taskIDs);
                    } else {
                        // 若無關注任務
                        tvInterview.append("\n尚未關注任何任務");
                    }
                } else {
                    // 若 followingTaskID 欄位不存在或不是 Map
                    tvInterview.append("\n尚未關注任何任務");
                }
            }
        }).addOnFailureListener(e -> Log.e("Firebase", "讀取使用者資料失敗：" + e.getMessage()));
    }


    private void loadFollowingTasks(List<String> taskIDs) {
        List<MyTask> taskList = new ArrayList<>();
        final int[] completed = {0};

        for (String taskID : taskIDs) {
            db.collection("task").document(taskID).get().addOnSuccessListener(taskDoc -> {
                if (taskDoc.exists()) {
                    String title = taskDoc.contains("title") ? taskDoc.getString("title") : "(無標題)";
                    String initiator = taskDoc.contains("ownerEmail") ? taskDoc.getString("ownerEmail") : "(未知)";
                    taskList.add(new MyTask(title, initiator, taskID));
                }
                completed[0]++;
                if (completed[0] == taskIDs.size()) {
                    showTasks(taskList);
                }
            }).addOnFailureListener(e -> {
                completed[0]++;
                if (completed[0] == taskIDs.size()) {
                    showTasks(taskList);
                }
            });
        }
    }

    private void showTasks(List<MyTask> taskList) {
        rvFollowingTask.setLayoutManager(new LinearLayoutManager(this));
        rvFollowingTask.setAdapter(new OtherTaskAdapter(taskList));
    }
}
