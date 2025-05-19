package fcu.edu.check_in;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.edu.check_in.model.Person;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword, etNickname;
    Button btnSubmit,btnback;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etNewUsername);
        etPassword = findViewById(R.id.etNewPassword);
        etNickname = findViewById(R.id.etNickname); // 新增的 EditText
        btnSubmit = findViewById(R.id.btnSubmitRegister);
        btnback = findViewById(R.id.btn_back);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String nickname = etNickname.getText().toString();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = auth.getCurrentUser().getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("nickname", nickname);
                        List<String> list = new ArrayList<>();
                        list.add("a");
                        list.add("b");
                        userData.put("list", list);
                        db.collection("users").document(email).set(userData)
                                .addOnSuccessListener(unused -> {

                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "儲存資料失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                        Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish(); // 結束註冊畫面
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "註冊失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
