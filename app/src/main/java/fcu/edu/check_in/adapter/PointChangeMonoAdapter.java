package fcu.edu.check_in.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.edu.check_in.R;
import fcu.edu.check_in.model.PointChangeMono;

public class PointChangeMonoAdapter extends RecyclerView.Adapter<PointChangeMonoAdapter.ViewHolder> {

    private final List<PointChangeMono> pointList;

    public PointChangeMonoAdapter(List<PointChangeMono> pointList) {
        this.pointList = pointList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pointo_change_mono, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PointChangeMono point = pointList.get(position);

        holder.itemText.setText(point.getItem());
        holder.statusText.setText(getStatusString(point.getStatus()));

        if (point.getStatus() == 0) {
            holder.pointCostText.setText("無法兌換");
        } else {
            holder.pointCostText.setText("P：" + point.getCostPoint());
        }
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;
        TextView statusText;
        TextView pointCostText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.tv_pointo_Items);
            statusText = itemView.findViewById(R.id.tv_pointo_status);
            pointCostText = itemView.findViewById(R.id.tv_pointo_pointCost);
        }
    }

    private String getStatusString(int status) {
        if (status == 0) {
            return "兌換完畢";
        } else if (status == -1) {
            return "剩餘：∞ 個";
        } else {
            return "剩餘：" + status + " 個";
        }
    }
}
