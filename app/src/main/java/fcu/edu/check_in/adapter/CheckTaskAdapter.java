package fcu.edu.check_in.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fcu.edu.check_in.R;
import fcu.edu.check_in.model.CheckTask;

public class CheckTaskAdapter extends RecyclerView.Adapter<CheckTaskAdapter.CheckTaskViewHolder> {

    private Context context;
    private List<CheckTask> taskList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FragmentUpdateListener listener;

    public CheckTaskAdapter(Context context, List<CheckTask> taskList, FragmentUpdateListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CheckTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_check_task, parent, false);
        return new CheckTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckTaskViewHolder holder, int position) {
        CheckTask task = taskList.get(position);
        String taskId = task.getTaskId();
        String startime = task.getStartTime();
        String week = task.getWeek();
        db.collection("task").document(taskId)
                .get()
                .addOnSuccessListener(taskDoc -> {
                    if (taskDoc.exists()) {
                        String title = taskDoc.getString("title");

                        holder.tvTitle.setText("任務:"+title);
                    }
                });
        holder.tvStartime.setText("打卡時間：" + startime);
        holder.tvWeek.setText("打卡星期:"+week);
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);
        if (email == null) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.collection("users")
                .document(email)
                .collection("checkRecord")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    boolean hasChecked = documentSnapshot.exists() &&
                            documentSnapshot.contains(today) &&
                            Boolean.TRUE.equals(documentSnapshot.getBoolean(today));

                    if (hasChecked) {
                        holder.btnCheck.setText("已打卡");
                        holder.btnCheck.setEnabled(false);
                    } else {
                        holder.btnCheck.setText("打卡");
                        holder.btnCheck.setEnabled(true);
                        holder.btnCheck.setOnClickListener(v -> {
                            Map<String, Object> data = new HashMap<>();
                            data.put(today, true);

                            db.collection("users")
                                    .document(email)
                                    .collection("checkRecord")
                                    .document(taskId)
                                    .set(data, SetOptions.merge())
                                    .addOnSuccessListener(unused -> {
                                        // 加分
                                        SharedPreferences pointPrefs = context.getSharedPreferences("point", Context.MODE_PRIVATE);
                                        int point = pointPrefs.getInt("point", 0);
                                        pointPrefs.edit().putInt("point", point + 1).apply();

                                        holder.btnCheck.setText("已打卡");
                                        holder.btnCheck.setEnabled(false);
                                        Toast.makeText(context, "打卡成功，+1 積分", Toast.LENGTH_SHORT).show();


                                        // 如果 Fragment 要更新積分顯示，可呼叫 interface 或重新載入
                                        if (listener != null) {
                                            listener.onPointUpdated();
                                        }

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "打卡失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class CheckTaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartime,tvTitle,tvWeek;
        Button btnCheck;

        public CheckTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartime = itemView.findViewById(R.id.tv_start_time);
            btnCheck = itemView.findViewById(R.id.btn_check_in);
            tvTitle = itemView.findViewById(R.id.tv_task_id);
            tvWeek = itemView.findViewById(R.id.tv_week);
        }
    }

    public interface FragmentUpdateListener {
        void onPointUpdated(); // 讓 Fragment 知道積分更新了
    }
}
