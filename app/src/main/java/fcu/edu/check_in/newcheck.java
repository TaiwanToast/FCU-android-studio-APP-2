package fcu.edu.check_in;

import android.annotation.SuppressLint;
import android.content.Context;
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
            db.collection("task").document("totalcounter").get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String count = documentSnapshot.getString("count");  // ✅ 正確取得為 Long 類型
                    if (count != null) {
                        Integer newId = new Integer(count) + 1;
                        Log.d("Firestore", "取得 count: " + newId);
                        addNewTask(String.format("%04d", newId));  // ✅ 正確格式化成 4 位數的字串

                        // 更新 count 欄位
                        db.collection("task").document("totalcounter")
                                .update("count", newId)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "count 更新為 " + newId))
                                .addOnFailureListener(e -> Log.e("Firestore", "更新失敗", e));
                    }
                } else {
                    Log.d("Firestore", "文件不存在");
                }
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "讀取失敗", e);
            });
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
                    clearFields();
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
