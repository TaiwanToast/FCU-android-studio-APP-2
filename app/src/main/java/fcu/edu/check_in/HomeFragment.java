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

import fcu.edu.check_in.adapter.MyTaskAdapter;
import fcu.edu.check_in.model.MyTask;

public class HomeFragment extends Fragment {

    private Button btnAddCheck;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView rvMyTask = view.findViewById(R.id.rv_My_Task);

        List<MyTask> taskList = new ArrayList<>();
        taskList.add(new MyTask("伏地挺身*10", "54PPE"));
        taskList.add(new MyTask("跑10KM", "Toast"));

        MyTaskAdapter adapter = new MyTaskAdapter(taskList);
        rvMyTask.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyTask.setAdapter(adapter);

        btnAddCheck = view.findViewById(R.id.btn_addcheck);

        btnAddCheck.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), newcheck.class);
            startActivity(intent);
        });

        return view;
    }
}
