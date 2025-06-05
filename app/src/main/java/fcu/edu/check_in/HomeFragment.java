package fcu.edu.check_in;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.edu.check_in.adapter.MyTaskAdapter;
import fcu.edu.check_in.callBack.FirestoreCallback;
import fcu.edu.check_in.model.MyTask;
import fcu.edu.check_in.model.TaskDataManager;

public class HomeFragment extends Fragment {

    private Button btnAddCheck;
    private List<MyTask> taskList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView rvMyTask = view.findViewById(R.id.rv_My_Task);
        TaskDataManager taskDataManager = new TaskDataManager();


        taskList.add(new MyTask("伏地挺身*10", "54PPE"));
        taskList.add(new MyTask("跑10KM", "Toast"));

        MyTaskAdapter adapter = new MyTaskAdapter(taskList);
        rvMyTask.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyTask.setAdapter(adapter);
        taskDataManager.getTotalCounter(new FirestoreCallback() {
            @Override
            public void onCallback(int counter) {
                taskListFactory(counter, adapter);
            }
        });

        btnAddCheck = view.findViewById(R.id.btn_addcheck);

        btnAddCheck.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), newcheck.class);
            startActivity(intent);
        });

        return view;
    }

    private void taskListFactory(int count, MyTaskAdapter adapter){
        /*
        從firebase中抓取打卡單
        目前打卡單擁有者先以email代替
         */
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for(int i = 1; i <= count; i++){
            DocumentReference docRef = db.collection("task").document(String.format("%04d", i));

            docRef.get().addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   DocumentSnapshot documentSnapshot = task.getResult();
                   if(documentSnapshot.exists()){
                       Map<String, Object> map = documentSnapshot.getData();
                       this.taskList.add(new MyTask((String) map.get("title"), (String) map.get("ownerEmail")));
                       adapter.notifyItemInserted(taskList.size() - 1);
                   }else{
                       Log.w("Firestore", "資料不存在");
                   }
               }else{
                   Log.e("Firestore", "查詢失敗", task.getException());
               }
            });
        }
    }

}
