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
            // 先取得最後一筆文件ID
            db.collection("task")
                    .orderBy(FieldPath.documentId())
                    .limitToLast(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot lastDoc = task.getResult().getDocuments().get(0);
                            String lastId = lastDoc.getId();
                            Log.d("Firestore", "最後文件ID：" + lastId);

                            // 轉成數字並 +1
                            int newIdInt = Integer.parseInt(lastId) + 1;
                            // 格式化成4位數字字串，例如 0002
                            String newId = String.format("%04d", newIdInt);

                            // 新增資料
                            addNewTask(newId);
                        } else {
                            // 如果沒文件，第一筆ID為 0001
                            addNewTask("0001");
                        }
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
