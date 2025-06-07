package fcu.edu.check_in.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.edu.check_in.R;
import fcu.edu.check_in.model.MyTask;
import fcu.edu.check_in.show_check_detail; // ← 改這裡！

public class OtherTaskAdapter extends RecyclerView.Adapter<OtherTaskAdapter.ViewHolder> {

    private final List<MyTask> taskList;

    public OtherTaskAdapter(List<MyTask> taskList) {
        this.taskList = taskList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInitiator;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_my_task_title);
            tvInitiator = view.findViewById(R.id.tv_my_task_initiator);
        }
    }

    @NonNull
    @Override
    public OtherTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_task, parent, false);
        return new OtherTaskAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherTaskAdapter.ViewHolder holder, int position) {
        MyTask task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvInitiator.setText(task.getInitiator());

        // 點擊後開啟 show_check_detail，並傳入 taskID
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), show_check_detail.class);
            intent.putExtra("taskID", task.getTaskID()); // 傳入正確的 taskID
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
