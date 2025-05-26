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
import java.util.Objects;

import fcu.edu.check_in.model.Person;
import fcu.edu.check_in.model.PersonManager;

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
                                String nickName = "", bio = "";
                                List<String> followPersonEmail;
                                Map<String, Map<String, String>> followingTaskID;
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        Map<String, Object> map = doc.getData();
                                        if (map != null) {
                                            if (map.containsKey("nickName")) {
                                                nickName = Objects.requireNonNull(map.get("nickName")).toString();
//                                            Log.d(TAG, "nick name is " + nickName);
                                            } else {
                                                Log.d(TAG, "nick name does not exist");
                                            }
                                            if (map.containsKey("bio")) {
                                                bio = Objects.requireNonNull(map.get("bio")).toString();
                                            } else {
                                                Log.d(TAG, "bio does not exist");
                                            }

                                            Person person = new Person(nickName, bio, email, null, null);
                                            PersonManager.getInstance().setCurrentPerson(person);
                                        } else {
                                            Log.d(TAG, "map is null");
                                        }
                                    } else {
                                        Log.w(TAG, "使用者資料不存在");
                                    }
                                } else {
                                    Log.e(TAG, "Firebase 查詢失敗", task.getException());
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
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}