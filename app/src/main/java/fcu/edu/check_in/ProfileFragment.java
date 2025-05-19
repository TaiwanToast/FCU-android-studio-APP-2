package fcu.edu.check_in;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

import fcu.edu.check_in.model.Person;

public class ProfileFragment extends Fragment {
    private Person person;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences prefs;
    private TextView tvName;
    private Button btnLogout;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context != null) {
            prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = view.findViewById(R.id.btn_logout);
        tvName = view.findViewById(R.id.text_nickname);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            prefs.edit().putBoolean("is_logged_in", false).apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

//        db.collection("users").whereEqualTo("email", prefs.getString("email", "")).get().addOnSuccessListener(
//                querySnapshot  -> {
//                    for (QueryDocumentSnapshot doc : querySnapshot) {
//                        tvName.setText(doc.getData().toString());
//                    }
//                }
//        );
//        this.tvName.setText(prefs.getString(""));
        FirebaseAuth auth = FirebaseAuth.getInstance();
//        String uid = auth.getCurrentUser().getUid();
        String email = prefs.getString("email", "");
        DocumentReference docRef = db.collection("users").document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        tvName.setText(document.getData().toString());
                        Map<String, Object> map = document.getData();
                        String nickName = map.get("nickname").toString();
                        tvName.setText(nickName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


//        return inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }



}
