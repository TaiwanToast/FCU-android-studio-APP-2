package fcu.edu.check_in;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.isEmpty()){
                Toast.makeText(this, "email不可為空 " , Toast.LENGTH_SHORT).show();
            }else if(password.isEmpty()){
                Toast.makeText(this, "密碼不可為空 " , Toast.LENGTH_SHORT).show();
            }else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("users").document(email);

                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            prefs.edit().putBoolean("is_logged_in", true).apply();
                            prefs.edit().putString("email", email).apply();

//                        String nickName, bio;
//                        List<String> followPersonEmail = new ArrayList<>();
//                        Map<String, Map<String, String>> followingTaskID;
                            docRef.get().addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        Map<String, Object> map = document.getData();
                                        if(map != null){
                                            String nickName = map.get("nickName").toString();
                                            prefs.edit().putString("nickName", nickName).apply();
                                            List<String> followPersonEmail = new ArrayList<>();
                                            String bio = map.get("bio").toString();
                                            prefs.edit().putString("bio", bio).apply();

                                        }
                                    } else {
                                        Log.w(TAG, "使用者資料不存在");
                                    }
                                } else {
                                    Log.e(TAG, "Firesstore 查詢失敗", task.getException());
                                }
                            });

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "登入失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }
        });

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
