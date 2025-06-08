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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import fcu.edu.check_in.adapter.CheckTaskAdapter;
import fcu.edu.check_in.model.CheckTask;
import fcu.edu.check_in.model.MyTask;

public class CheckInFragment extends Fragment implements CheckTaskAdapter.FragmentUpdateListener {
    private RecyclerView recyclerView;
    private List<CheckTask> taskList = new ArrayList<>();
    private CheckTaskAdapter adapter;
    private TextView tvPoints;
    private Button btnRedeem;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences prefs;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        tvPoints = view.findViewById(R.id.tv_points);
        btnRedeem = view.findViewById(R.id.btn_redeem);
        recyclerView = view.findViewById(R.id.recycler_check_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CheckTaskAdapter(getContext(), taskList, this); // 需建立 adapter
        recyclerView.setAdapter(adapter);

        btnRedeem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PointActivity.class);
            startActivity(intent);
        });

        loadCheckTasks();

        return view;
    }
    public void onResume() {
        super.onResume();
        updatePointsDisplay(); // 每次畫面重載都更新積分
    }
    public void onPointUpdated() {
        updatePointsDisplay(); // 刷新積分顯示
    }
    private void updatePointsDisplay() {
        SharedPreferences pointPrefs = requireContext().getSharedPreferences("point", Context.MODE_PRIVATE);
        int point = pointPrefs.getInt("point", 0);
        tvPoints.setText("積分：" + point);
    }

    private void loadCheckTasks() {
        String email = prefs.getString("email", null);
        if (email == null) return;

        taskList.clear();
        adapter.notifyDataSetChanged();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(email).get().addOnSuccessListener(userDoc -> {
            if (!userDoc.exists()) return;

            Map<String, Object> userData = userDoc.getData();
            if (userData == null || !userData.containsKey("followingTaskID")) return;

            Map<String, Object> taskMap = (Map<String, Object>) userData.get("followingTaskID");
            int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); // Sunday = 1, Monday = 2, ...
            String todayStr = String.valueOf((today == 1) ? 7 : today - 1); // 轉換為 Monday = 1, Sunday = 7

            LocalTime now = LocalTime.now();

            if (taskMap != null && !taskMap.isEmpty()) {
                for (String taskId : taskMap.keySet()) {
                    Map<String, Object> taskInfo = (Map<String, Object>) taskMap.get(taskId);
                    String startime = (String) taskInfo.get("startime");
                    String week = (String) taskInfo.get("week");

                    // 1. 週幾檢查
                    if (!week.contains(todayStr)) continue;

                    // 2. 時間比較
                    String[] times = startime.split("~");
                    if (times.length != 2) continue;

                    try {
                        LocalTime endTime = LocalTime.parse(times[1]);

                        if (now.isAfter(endTime)) continue; // 如果已過結束時間就不顯示

                        // 3. TODO: 檢查是否已打卡 (這部分視你打卡記錄儲存在哪裡)

                        // 加入任務列表
                        CheckTask task = new CheckTask(taskId, startime, week);
                        taskList.add(task);
                        adapter.notifyItemInserted(taskList.size() - 1);

                    } catch (DateTimeParseException e) {
                        Log.e("CheckInFragment", "時間格式錯誤: " + startime);
                    }
                }
            }
        });
    }

}
