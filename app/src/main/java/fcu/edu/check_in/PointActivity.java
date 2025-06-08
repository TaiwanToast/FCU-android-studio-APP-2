package fcu.edu.check_in;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fcu.edu.check_in.adapter.PointChangeMonoAdapter;
import fcu.edu.check_in.model.PointChangeMono;
import fcu.edu.check_in.model.TaskDataManager;
import android.app.AlertDialog;

public class PointActivity extends AppCompatActivity {

    private EditText editTextRewardName, editTextPointCost;
    private TextView tvPoint;
    private Button btn_back;
    private Button btn_home;
    private Button btnAddReward;
    private RecyclerView recyclerView;
    private List<PointChangeMono> dataList = new ArrayList<>();
    private PointChangeMonoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        // 你的 findViewById 等初始化
        btn_back = findViewById(R.id.btn_back_point);
        btn_home = findViewById(R.id.btn_to_home_point);
        editTextRewardName = findViewById(R.id.editTextRewardName);
        editTextPointCost = findViewById(R.id.editTextPointCost);
        btnAddReward = findViewById(R.id.btnAddReward);
        recyclerView = findViewById(R.id.rcv_changeMono);
        tvPoint = findViewById(R.id.tv_showPoint);

        setupChangeRecyclerView();

        btnAddReward.setOnClickListener(v -> {
            String rewardName = editTextRewardName.getText().toString().trim();
            String pointStr = editTextPointCost.getText().toString().trim();

            if (rewardName.isEmpty() || pointStr.isEmpty()) {
                Toast.makeText(this, "請輸入完整資料", Toast.LENGTH_SHORT).show();
                return;
            }

            int costPoint;
            try {
                costPoint = Integer.parseInt(pointStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "點數必須是數字", Toast.LENGTH_SHORT).show();
                return;
            }

            saveRewardToPreferences(rewardName, costPoint);
            loadDataFromPreferences();
            adapter.notifyDataSetChanged();
            updatePointDisplay();

            editTextRewardName.setText("");
            editTextPointCost.setText("");
        });

        btn_back.setOnClickListener(v -> finish());

        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(PointActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
    private void REPoint(){
        SharedPreferences prefs = getSharedPreferences("point", MODE_PRIVATE);
        Integer point = prefs.getInt("point", 0);
        tvPoint.setText("積分: "+point.toString());
    }
    private void saveRewardToPreferences(String rewardName, int costPoint) {
        SharedPreferences prefs = getSharedPreferences("point", MODE_PRIVATE);
        String currentData = prefs.getString("point_list", "");
        currentData += rewardName + ":" + costPoint + ";";
        prefs.edit().putString("point_list", currentData).apply();
    }

    private void loadDataFromPreferences() {
        dataList.clear();
        SharedPreferences prefs = getSharedPreferences("point", MODE_PRIVATE);
        String dataStr = prefs.getString("point_list", "");
        if (!dataStr.isEmpty()) {
            String[] items = dataStr.split(";");
            for (String item : items) {
                if (item.trim().isEmpty()) continue;
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    String name = parts[0];
                    int cost;
                    try {
                        cost = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        cost = 0;
                    }
                    dataList.add(new PointChangeMono(name, cost));
                }
            }
        }
    }

    private void setupChangeRecyclerView() {
        updatePointDisplay();
        loadDataFromPreferences();

        adapter = new PointChangeMonoAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> showOptionDialog(item));
    }

    private void showOptionDialog(PointChangeMono item) {
        String[] options = {"兌換", "刪除", "取消"};
        new AlertDialog.Builder(this)
                .setTitle("你要對 " + item.getItem() + " 做什麼？")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:  // 兌換
                            confirmExchange(item);
                            break;
                        case 1:  // 刪除
                            confirmDelete(item);
                            break;
                        case 2:  // 取消
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void confirmExchange(PointChangeMono item) {
        new AlertDialog.Builder(this)
                .setTitle("確定兌換 " + item.getItem() + " 嗎？")
                .setPositiveButton("確認", (dialog, which) -> {
                    int currentPoint = getCurrentPoint();
                    if (currentPoint >= item.getCostPoint()) {
                        int newPoint = currentPoint - item.getCostPoint();
                        saveCurrentPoint(newPoint);
                        Toast.makeText(this, "好好獎勵自己吧！", Toast.LENGTH_SHORT).show();
                        updatePointDisplay();
                    } else {
                        Toast.makeText(this, "積分不足無法兌換", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    private void confirmDelete(PointChangeMono item) {
        new AlertDialog.Builder(this)
                .setTitle("確定刪除 " + item.getItem() + " 嗎？")
                .setPositiveButton("確認", (dialog, which) -> {
                    deleteReward(item);
                    Toast.makeText(this, "已刪除 " + item.getItem(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private int getCurrentPoint() {
        SharedPreferences prefs = getSharedPreferences("point", MODE_PRIVATE);
        return prefs.getInt("point", 0);
    }

    private void saveCurrentPoint(int point) {
        SharedPreferences prefs = getSharedPreferences("point", MODE_PRIVATE);
        prefs.edit().putInt("point", point).apply();
    }

    private void updatePointDisplay() {
        int point = getCurrentPoint();
        tvPoint.setText("積分: " + point);
    }

    private void deleteReward(PointChangeMono item) {
        SharedPreferences prefs = getSharedPreferences("point", MODE_PRIVATE);
        String dataStr = prefs.getString("point_list", "");

        StringBuilder newData = new StringBuilder();
        String[] items = dataStr.split(";");
        for (String entry : items) {
            if (entry.trim().isEmpty()) continue;
            if (!entry.startsWith(item.getItem() + ":")) {
                newData.append(entry).append(";");
            }
        }
        prefs.edit().putString("point_list", newData.toString()).apply();

        loadDataFromPreferences();
        adapter.notifyDataSetChanged();
        updatePointDisplay();
    }

    // 你原本的 saveRewardToPreferences() 和 loadDataFromPreferences() 保留不動

}