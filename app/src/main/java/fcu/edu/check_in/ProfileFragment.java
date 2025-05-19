package fcu.edu.check_in;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import fcu.edu.check_in.model.Person;

public class ProfileFragment extends Fragment {
    private Person person;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences prefs;
    private TextView tvName;
    public ProfileFragment(SharedPreferences prefs) {
        // Required empty public constructor
        this.prefs = prefs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db.collection("users").whereEqualTo("email", prefs.getString("email", "")).get().addOnSuccessListener(
                querySnapshot  -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        tvName.setText(doc.getData().toString());
                    }
                }
        );
//        this.tvName.setText(prefs.getString(""));
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }



}
