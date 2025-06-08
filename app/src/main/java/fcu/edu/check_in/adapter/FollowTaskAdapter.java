package fcu.edu.check_in.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; // ✅ 加這行
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore; // ✅ 加這行

import java.util.ArrayList;
import java.util.List;

import fcu.edu.check_in.R;
import fcu.edu.check_in.model.MyTask;

public class FollowTaskAdapter extends RecyclerView.Adapter<FollowTaskAdapter.FollowTaskViewHolder> {

    private final List<MyTask> taskList = new ArrayList<>();
    private final Context context;
    private final String userEmail;

    // ⭐ 1. 點擊事件的 listener 介面
    public interface OnItemClickListener {
        void onItemClick(MyTask task);
    }

    // ⭐ 2. listener 欄位
    private OnItemClickListener listener;

    // ⭐ 3. setter 方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FollowTaskAdapter(Context context, String userEmail) {
        this.context = context;
        this.userEmail = userEmail;
    }

    public void updateData(List<MyTask> newTasks) {
        taskList.clear();
        taskList.addAll(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FollowTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_task, parent, false);
        return new FollowTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowTaskViewHolder holder, int position) {
        MyTask task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.initiator.setText("由 " + task.getInitiator() + " 發起");

        holder.btnCancel.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userEmail)
                    .update("followingTaskID." + task.getTaskID(), null)
                    .addOnSuccessListener(aVoid -> {
                        taskList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "已取消追蹤", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "取消失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // ⭐ 4. 點擊整個項目時觸發 listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class FollowTaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, initiator;
        Button btnCancel;

        public FollowTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_task_title);
            initiator = itemView.findViewById(R.id.tv_initiator);
            btnCancel = itemView.findViewById(R.id.btn_unfollow);
        }
    }
}
