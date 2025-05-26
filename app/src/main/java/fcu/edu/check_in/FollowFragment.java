package fcu.edu.check_in;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fcu.edu.check_in.adapter.FollowPeopleAdapter;
import fcu.edu.check_in.model.FollowPeople;

public class FollowFragment extends Fragment {

    public FollowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        RecyclerView rvPeopleFollow = view.findViewById(R.id.rv_poeple_follow);
        Button btn_searchFriend = view.findViewById(R.id.btn_searchFriend);

        List<FollowPeople> peopleList = new ArrayList<>();
        peopleList.add(new FollowPeople("54PPE"));
        peopleList.add(new FollowPeople("Toast"));

        FollowPeopleAdapter adapter = new FollowPeopleAdapter(peopleList);
        rvPeopleFollow.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPeopleFollow.setAdapter(adapter);

        btn_searchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchFriendActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
