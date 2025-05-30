package fcu.edu.check_in;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class edit_Profile extends AppCompatActivity {
    private EditText etName, etbio;
    private Button btnSave, btnBack;
    private SharedPreferences prefs;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etName = findViewById(R.id.etnickname);
        etbio = findViewById(R.id.etbio);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnback);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString();
            String newBio = etbio.getText().toString();

            prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String email = prefs.getString("email", null);

            if (email != null) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newName);
                updates.put("bio", newBio);

                db.collection("users").document(email)
                        .update(updates)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "資料更新成功", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "儲存資料失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            } else {
                Toast.makeText(this, "找不到使用者 Email", Toast.LENGTH_SHORT).show();
            }

        });

    }
}
