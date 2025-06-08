package fcu.edu.check_in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.edu.check_in.adapter.PointChangeMonoAdapter;
import fcu.edu.check_in.model.PointChangeMono;
import fcu.edu.check_in.model.TaskDataManager;

public class PointActivity extends AppCompatActivity {

    private Button btn_back;
    private Button btn_home;
    private TextView tv_showPoint;
    private RecyclerView rcv_changeMono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_point);

        rcv_changeMono = findViewById(R.id.rcv_changeMono);

        ViewCompat.setOnApplyWindowInsetsListener(rcv_changeMono, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_showPoint = findViewById(R.id.tv_showPoint);
        btn_back = findViewById(R.id.btn_back_point);
        btn_home = findViewById(R.id.btn_to_home_point);

        btn_back.setOnClickListener(v -> finish());

        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(PointActivity.this, MainActivity.class);
            startActivity(intent);
        });

        setupChangeRecyclerView();
    }

    private void setupChangeRecyclerView() {
        rcv_changeMono.setLayoutManager(new LinearLayoutManager(this));

        List<PointChangeMono> mockData = List.of(
                new PointChangeMono("棒棒糖", 10, 50),
                new PointChangeMono("麥香", 0, 200),
                new PointChangeMono("給你一個讚", -1, 20)
        );

        PointChangeMonoAdapter adapter = new PointChangeMonoAdapter(mockData);
        rcv_changeMono.setAdapter(adapter);
    }
}
