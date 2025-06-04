package fcu.edu.check_in.model;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fcu.edu.check_in.callBack.FirestoreCallback;

public class TaskDataManager {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getTotalCounter(FirestoreCallback callback){
        DocumentReference docRef = db.collection("task").document("totalcounter");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Long number = document.getLong("count");
                    if (number != null) {
                        callback.onCallback(number.intValue());
                    }
                } else {
                    Log.w("Firestore", "資料不存在");
                }
            } else {
                Log.e("Firestore", "查詢失敗", task.getException());
            }
        });
    }
}
