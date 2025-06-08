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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.edu.check_in.adapter.FollowTaskAdapter;
import fcu.edu.check_in.adapter.OtherTaskAdapter;
import fcu.edu.check_in.model.MyTask;
import fcu.edu.check_in.model.Person;
import fcu.edu.check_in.model.PersonManager;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences prefs;

    private TextView tvName, tvBio;
    private Button btnLogout, btnSetting;
    private RecyclerView recyclerView;
    private OtherTaskAdapter taskAdapter;
    private List<MyTask> taskList = new ArrayList<>();

    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        boolean updated = result.getData().getBooleanExtra("profile_updated", false);
                        if (updated) {
                            showUserInfo();
                            loadFollowedTasks();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.text_nickname);
        tvBio = view.findViewById(R.id.tv_bio);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnSetting = view.findViewById(R.id.btnsetting);
        recyclerView = view.findViewById(R.id.recycler_checklists);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 使用成員變數，不要重新宣告
        taskAdapter = new OtherTaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        btnLogout.setOnClickListener(v -> {
            prefs.edit().putBoolean("is_logged_in", false).apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), edit_Profile.class);
            editProfileLauncher.launch(intent);
        });

        showUserInfo();
        loadFollowedTasks();

        return view;
    }


    private void showUserInfo() {
        String email = prefs.getString("email", null);
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "Email 為 null 或空字串");
            tvName.setText("尚未登入使用者");
            tvBio.setText("");
            recyclerView.setAdapter(null);
            return;
        }

        Person person = PersonManager.getInstance().getCurrentPerson();
        if (person != null) {
            tvName.setText(person.getNickName() != null ? person.getNickName() : "無暱稱");
            tvBio.setText(person.getBio() != null ? person.getBio() : "尚未填寫簡介");
        } else {
            tvName.setText("資料尚未載入");
            tvBio.setText("");
        }
    }

    private void loadFollowedTasks() {
        String email = prefs.getString("email", null);
        if (email == null) return;

        taskList.clear();
        taskAdapter.notifyDataSetChanged();

        db.collection("users").document(email).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                Map<String, Object> data = userDoc.getData();
                if (data != null && data.containsKey("followingTaskID")) {
                    Map<String, Object> followingMap = (Map<String, Object>) data.get("followingTaskID");
                    for (String taskID : followingMap.keySet()) {
                        db.collection("task").document(taskID).get().addOnSuccessListener(taskDoc -> {
                            if (taskDoc.exists()) {
                                Map<String, Object> taskData = taskDoc.getData();
                                if (taskData != null) {
                                    String title = (String) taskData.get("title");
                                    String ownerEmail = (String) taskData.get("ownerEmail");
                                    taskList.add(new MyTask(title, ownerEmail, taskID));
                                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
