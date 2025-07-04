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
import fcu.edu.check_in.edit_check;
import fcu.edu.check_in.model.MyTask;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> {

    private final List<MyTask> taskList;

    public MyTaskAdapter(List<MyTask> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyTask task = taskList.get(position);
        holder.titleText.setText(task.getTitle());
        holder.initiatorText.setText(task.getInitiator());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), edit_check.class);
            intent.putExtra("taskID", task.getTaskID()); // 傳入 taskID
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView initiatorText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tv_my_task_title);
            initiatorText = itemView.findViewById(R.id.tv_my_task_initiator);
        }
    }
}