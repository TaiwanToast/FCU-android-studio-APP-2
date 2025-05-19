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
import fcu.edu.check_in.model.FollowPeople;
import fcu.edu.check_in.show_following_people;

public class FollowPeopleAdapter extends RecyclerView.Adapter<FollowPeopleAdapter.ViewHolder> {
    private final List<FollowPeople> peopleList;
    public FollowPeopleAdapter(List<FollowPeople> peopleList) {
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public FollowPeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_people_follow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowPeopleAdapter.ViewHolder holder, int position) {
        FollowPeople people = peopleList.get(position);
        holder.nameText.setText(people.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), show_following_people.class);
            intent.putExtra("name", people.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.tv_name_followPeople);
        }
    }
}
