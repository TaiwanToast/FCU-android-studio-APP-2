package fcu.edu.check_in;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class newcheck extends AppCompatActivity {

    private Button btnback, btnaddcheck;
    private EditText edTitle, edbio;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences prefs;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_newcheck);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        email = prefs.getString("email", null);

        btnback = findViewById(R.id.btnBack);
        btnaddcheck = findViewById(R.id.btnSubmit);
        edTitle = findViewById(R.id.editTitle);
        edbio = findViewById(R.id.editDescription);

        btnback.setOnClickListener(v -> finish());

        btnaddcheck.setOnClickListener(v -> {
            db.collection("task").document("totalcounter").get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long count = documentSnapshot.getLong("count");
                            if (count != null) {
                                int newId = count.intValue() + 1;
                                String formattedId = String.format("%04d", newId);
                                Log.d("Firestore", "取得 count: " + formattedId);
                                addNewTask(formattedId);

                                db.collection("task").document("totalcounter")
                                        .update("count", newId)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "count 更新為 " + newId))
                                        .addOnFailureListener(e -> Log.e("Firestore", "更新失敗", e));
                            }
                        } else {
                            Log.d("Firestore", "文件不存在");
                        }
                    }).addOnFailureListener(e -> Log.e("Firestore", "讀取失敗", e));
        });

    }

    private void addNewTask(String newId) {
        Map<String, Object> new_task = new HashMap<>();
        new_task.put("bio", edbio.getText().toString());
        new_task.put("ownerEmail", email);
        new_task.put("title", edTitle.getText().toString());

        db.collection("task")
                .document(newId)
                .set(new_task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(newcheck.this, "新增成功，ID：" + newId, Toast.LENGTH_SHORT).show();

                    // ✅ 更新 user collection 中 fofo... 欄位
                    Map<String, Object> nestedData = new HashMap<>();
                    nestedData.put("startime", "");
                    nestedData.put("week", "");

                    db.collection("user")
                            .document(email)
                            .update(FieldPath.of("followingTaskID", newId), nestedData)
                            .addOnSuccessListener(unused -> Log.d("Firestore", "fofollowingTaskID 更新成功"))
                            .addOnFailureListener(e -> Log.e("Firestore", "fofollowingTaskID 更新失敗", e));

                    // ✅ 導向 edit_check 並傳 taskID
                    Intent intent = new Intent(newcheck.this, edit_check.class);
                    intent.putExtra("taskID", newId);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(newcheck.this, "新增失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void clearFields() {
        edTitle.setText("");
        edbio.setText("");
    }
}
