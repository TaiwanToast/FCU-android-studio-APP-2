package fcu.edu.check_in;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.edu.check_in.adapter.MyTaskAdapter;
import fcu.edu.check_in.adapter.OtherTaskAdapter;
import fcu.edu.check_in.callBack.FirestoreCallback;
import fcu.edu.check_in.model.MyTask;
import fcu.edu.check_in.model.TaskDataManager;

public class HomeFragment extends Fragment {

    private Button btnAddCheck;
    private Button btnPointChange;
    private List<MyTask> mineTaskList = new ArrayList<>();
    private List<MyTask> otherTaskList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupMineRecyclerView(view);
        setupOtherRecyclerView(view);

        btnAddCheck = view.findViewById(R.id.btn_addcheck);
        btnPointChange = view.findViewById(R.id.btn_toPoint_home);

        btnAddCheck.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), newcheck.class);
            startActivity(intent);
        });

        btnPointChange.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PointActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // ✅ 設定自己的任務列表
    private void setupMineRecyclerView(View view) {
        RecyclerView rvMineTask = view.findViewById(R.id.rv_My_Task);
        TaskDataManager taskDataManager = new TaskDataManager();
        MyTaskAdapter adapter = new MyTaskAdapter(mineTaskList);

        rvMineTask.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMineTask.setAdapter(adapter);

        taskDataManager.getTotalCounter(new FirestoreCallback() {
            @Override
            public void onCallback(int counter) {
                initMineTaskList(adapter);
            }
        });
    }

    // ✅ 設定其他人的任務列表
    private void setupOtherRecyclerView(View view) {
        RecyclerView rvOtherTask = view.findViewById(R.id.rcv_other);
        OtherTaskAdapter adapter = new OtherTaskAdapter(otherTaskList);


        rvOtherTask.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOtherTask.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .whereNotEqualTo("ownerEmail", "目前登入者的email") // 你可以用 FirebaseAuth.getInstance().getCurrentUser().getEmail()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> map = document.getData();
                            otherTaskList.clear();
                            adapter.notifyDataSetChanged();
                            otherTaskList.add(new MyTask((String) map.get("title"), (String) map.get("ownerEmail"),document.getId()));
                            adapter.notifyItemInserted(otherTaskList.size() - 1);
                        }
                    } else {
                        Log.e(TAG, "取得 other task 失敗", task.getException());
                    }
                });
    }

    // ✅ 初始化自己的任務資料
    private void initMineTaskList(MyTaskAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 取得 SharedPreferences 裡的 email
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);
        if (email == null) {
            Log.e("initMineTaskList", "email not found in SharedPreferences");
            return;

        }

        // 清空舊資料
        mineTaskList.clear();
        adapter.notifyDataSetChanged();

        // 取得使用者文件
        db.collection("users").document(email).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                Map<String, Object> data = userDoc.getData();
                if (data != null && data.containsKey("followingTaskID")) {
                    Map<String, Object> followingMap = (Map<String, Object>) data.get("followingTaskID");

                    for (String taskID : followingMap.keySet()) {
                        Map<String, Object> followInfo = (Map<String, Object>) followingMap.get(taskID);
                        String startDate = followInfo != null && followInfo.containsKey("startDate") ?
                                (String) followInfo.get("startDate") : "";
                        String week = followInfo != null && followInfo.containsKey("week") ?
                                (String) followInfo.get("week") : "";

                        db.collection("task").document(taskID).get().addOnSuccessListener(taskDoc -> {
                            if (taskDoc.exists()) {
                                Map<String, Object> taskData = taskDoc.getData();
                                if (taskData != null) {
                                    String title = (String) taskData.get("title");
                                    String ownerEmail = (String) taskData.get("ownerEmail");
                                    MyTask myTask = new MyTask(title, ownerEmail,taskID);// startDate, week
                                    mineTaskList.add(myTask);
                                    adapter.notifyItemInserted(mineTaskList.size() - 1);
                                }
                            }
                        }).addOnFailureListener(e -> Log.e("Firestore", "查詢任務失敗：" + e.getMessage()));
                    }
                } else {
                    Log.w("initMineTaskList", "使用者沒有 followingTaskID 欄位");
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "查詢使用者失敗：" + e.getMessage()));
    }


}
