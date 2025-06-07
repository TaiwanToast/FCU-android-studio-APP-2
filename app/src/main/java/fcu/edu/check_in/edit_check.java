package fcu.edu.check_in;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class edit_check extends AppCompatActivity {

    TextView checkTitle, checkBio;
    EditText editTime1, editTime2;
    Button btnBack, btnSave;
    CheckBox ckMon, ckTue, ckWed, ckThu, ckFri, ckSat, ckSun;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences prefs;
    String email, taskID;
    String currentTaskTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcheck);

        // UI 初始化
        checkTitle = findViewById(R.id.checkTitle);
        checkBio = findViewById(R.id.checkBio);
        editTime1 = findViewById(R.id.editTime1);
        editTime2 = findViewById(R.id.editTime2);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSubmit);

        ckMon = findViewById(R.id.ckMon);
        ckTue = findViewById(R.id.ckTue);
        ckWed = findViewById(R.id.ckWed);
        ckThu = findViewById(R.id.ckThu);
        ckFri = findViewById(R.id.ckFri);
        ckSat = findViewById(R.id.ckSat);
        ckSun = findViewById(R.id.ckSun);

        setupTimePicker(editTime1);
        setupTimePicker(editTime2);

        // 取得登入 email 和 taskID
        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        email = prefs.getString("email", null);
        taskID = getIntent().getStringExtra("taskID");

        // Log 確認
        Log.d("edit_check", "email: " + email + ", taskID: " + taskID);

        initFromFirebase(); // ⭐ 初始化畫面
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            String start = editTime1.getText().toString() + "~" + editTime2.getText().toString();
            StringBuilder weekBuilder = new StringBuilder();
            if (ckMon.isChecked()) weekBuilder.append("1");
            if (ckTue.isChecked()) weekBuilder.append("2");
            if (ckWed.isChecked()) weekBuilder.append("3");
            if (ckThu.isChecked()) weekBuilder.append("4");
            if (ckFri.isChecked()) weekBuilder.append("5");
            if (ckSat.isChecked()) weekBuilder.append("6");
            if (ckSun.isChecked()) weekBuilder.append("7");

            db.collection("users").document(email)
                    .update("followingTaskID." + taskID + ".startime", start,
                            "followingTaskID." + taskID + ".week", weekBuilder.toString())
                    .addOnSuccessListener(unused -> {
                        scheduleAlarms(weekBuilder.toString(), editTime1.getText().toString());
                        Toast.makeText(this, "儲存成功", Toast.LENGTH_SHORT).show();
                        finish(); // 儲存後關閉頁面
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "儲存失敗", Toast.LENGTH_SHORT).show());
        });
    }

    private void setupTimePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        editText.setText(String.format("%02d:%02d", hour, minute));

        editText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(edit_check.this,
                    (TimePicker view, int hourOfDay, int minute1) -> {
                        String time = String.format("%02d:%02d", hourOfDay, minute1);
                        editText.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });
    }

    // ⭐ 從 Firebase 初始化時間與星期選項
    private void initFromFirebase() {
        if (email == null || taskID == null) return;

        // 先從 users 文件讀取跟蹤任務的基本資訊
        db.collection("users").document(email).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                Map<String, Object> data = userDoc.getData();
                if (data != null && data.containsKey("followingTaskID")) {
                    Map<String, Object> followingMap = (Map<String, Object>) data.get("followingTaskID");
                    if (followingMap.containsKey(taskID)) {
                        Map<String, Object> userTaskData = (Map<String, Object>) followingMap.get(taskID);

                        // 讀 startime, week
                        String startTimeStr = userTaskData.containsKey("startime") ?
                                (String) userTaskData.get("startime") : "";
                        String weekStr = userTaskData.containsKey("week") ?
                                (String) userTaskData.get("week") : "";

                        // 更新時間與 checkbox
                        if (startTimeStr == null || startTimeStr.isEmpty()) {
                            Calendar now = Calendar.getInstance();
                            String nowTime = String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
                            editTime1.setText(nowTime);
                            editTime2.setText(nowTime);
                        } else {
                            String[] times = startTimeStr.split("~");
                            if (times.length == 2) {
                                editTime1.setText(times[0]);
                                editTime2.setText(times[1]);
                            }
                        }
                        setWeekCheckboxes(weekStr);

                        // 接著從 task 集合讀取該 taskID 詳細資料 (bio, title)
                        db.collection("task").document(taskID).get().addOnSuccessListener(taskDoc -> {
                            if (taskDoc.exists()) {
                                Map<String, Object> taskData = taskDoc.getData();
                                String bio = "";
                                String title = "";
                                if (taskData != null) {
                                    bio = taskData.containsKey("bio") ? (String) taskData.get("bio") : "";
                                    title = taskData.containsKey("title") ? (String) taskData.get("title") : "";
                                }

                                // 更新畫面文字
                                checkTitle.setText(title);
                                checkBio.setText(bio);

                                // 儲存成員變數，供後續鬧鐘使用
                                currentTaskTitle = title;
                            }
                        }).addOnFailureListener(e -> Log.e("Firebase", "讀取任務資料失敗：" + e.getMessage()));
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e("Firebase", "讀取使用者資料失敗：" + e.getMessage()));
    }


    private void scheduleAlarms(String week, String timeText) {
        String[] timeParts = timeText.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        for (char c : week.toCharArray()) {
            int dayOfWeek = Character.getNumericValue(c);  // 1~7, 1=週一, 7=週日

            Calendar calendar = Calendar.getInstance();
            int calendarDayOfWeek = (dayOfWeek == 7) ? Calendar.SUNDAY : dayOfWeek + 1;
            calendar.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("title", "打卡提醒");
            intent.putExtra("content", currentTaskTitle + ": 該打卡囉！");

            // ✅ 使用 uniqueRequestCode 避免鬧鐘重複
            int uniqueRequestCode = taskID.hashCode() * 10 + dayOfWeek;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    uniqueRequestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
            );
        }
    }



    // ⭐ 根據字串 "12367" 勾選對應的 checkbox
    private void setWeekCheckboxes(String weekStr) {
        Map<Character, CheckBox> weekMap = new HashMap<>();
        weekMap.put('1', ckMon);
        weekMap.put('2', ckTue);
        weekMap.put('3', ckWed);
        weekMap.put('4', ckThu);
        weekMap.put('5', ckFri);
        weekMap.put('6', ckSat);
        weekMap.put('7', ckSun);

        for (char c : weekStr.toCharArray()) {
            if (weekMap.containsKey(c)) {
                weekMap.get(c).setChecked(true);
            }
        }
    }
    private void saveToFirebase() {
        if (email == null || taskID == null) {
            Toast.makeText(this, "資料錯誤，無法儲存", Toast.LENGTH_SHORT).show();
            return;
        }

        // 讀取時間
        String time1 = editTime1.getText().toString();
        String time2 = editTime2.getText().toString();
        String startTimeStr = time1 + "~" + time2;

        // 讀取勾選的星期
        StringBuilder weekBuilder = new StringBuilder();
        if (ckMon.isChecked()) weekBuilder.append("1");
        if (ckTue.isChecked()) weekBuilder.append("2");
        if (ckWed.isChecked()) weekBuilder.append("3");
        if (ckThu.isChecked()) weekBuilder.append("4");
        if (ckFri.isChecked()) weekBuilder.append("5");
        if (ckSat.isChecked()) weekBuilder.append("6");
        if (ckSun.isChecked()) weekBuilder.append("7");
        String weekStr = weekBuilder.toString();

        Map<String, Object> taskData = new HashMap<>();
        taskData.put("startime", startTimeStr);
        taskData.put("week", weekStr);

        db.collection("users").document(email)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> userData = doc.getData();
                        if (userData != null) {
                            Map<String, Object> followingMap = (Map<String, Object>) userData.get("followingTaskID");
                            if (followingMap == null) followingMap = new HashMap<>();

                            followingMap.put(taskID, taskData);

                            db.collection("users").document(email)
                                    .update("followingTaskID", followingMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "儲存成功", Toast.LENGTH_SHORT).show();
                                        finish(); // 返回上一頁
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "儲存失敗：" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("Firebase", "儲存失敗", e);
                                    });
                        }
                    } else {
                        Toast.makeText(this, "使用者資料不存在", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "讀取使用者資料失敗：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Firebase", "讀取失敗", e);
                });
    }

}
