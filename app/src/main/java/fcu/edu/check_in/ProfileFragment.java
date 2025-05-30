package fcu.edu.check_in;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import fcu.edu.check_in.model.Person;
import fcu.edu.check_in.model.PersonManager;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences prefs;
    private TextView tvName;
    private Button btnLogout;
    private Button btnSetting;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = view.findViewById(R.id.btn_logout);
        tvName = view.findViewById(R.id.text_nickname);
        btnSetting = view.findViewById(R.id.btnsetting);
        btnLogout.setOnClickListener(v -> {
            prefs.edit().putBoolean("is_logged_in", false).apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        btnSetting.setOnClickListener(v -> {
            Intent intenttosetting = new Intent(getActivity(), edit_Profile.class);
            startActivity(intenttosetting);
        });

        // 取得 email，並做 null/空字串檢查
        String email = prefs.getString("email", null);
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "Email 為 null 或空字串");
            tvName.setText("尚未登入使用者");
            return view;
        }

        Person person = PersonManager.getInstance().getCurrentPerson();

        tvName.setText(person.getNickName());

//        DocumentReference docRef = db.collection("users").document(email);
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    Map<String, Object> map = document.getData();
//                    if (map != null && map.containsKey("nickName")) {
//                        Object nicknameObj = map.get("nickName");
//                        tvName.setText(nicknameObj != null ? nicknameObj.toString() : "無暱稱");
//                    } else {
//                        tvName.setText("暱稱未設定");
//                    }
//                } else {
//                    Log.w(TAG, "使用者資料不存在");
//                    tvName.setText("查無使用者資料");
//                }
//            } else {
//                Log.e(TAG, "Firestore 查詢失敗", task.getException());
//                tvName.setText("載入使用者資料失敗");
//            }
//        });

        return view;
    }
}
