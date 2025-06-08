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
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PointChangeMono item);
    }

    public PointChangeMonoAdapter(List<PointChangeMono> pointList) {
        this.pointList = pointList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
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
        holder.pointCostText.setText(String.valueOf(point.getCostPoint()));

        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(point);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;
        TextView pointCostText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.tv_pointo_Items);
            pointCostText = itemView.findViewById(R.id.tv_pointo_pointCost);
        }
    }
}
