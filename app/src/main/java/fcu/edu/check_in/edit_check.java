package fcu.edu.check_in;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class edit_check extends AppCompatActivity {

    EditText editTime1, editTime2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcheck);

        editTime1 = findViewById(R.id.editTime1);
        editTime2 = findViewById(R.id.editTime2);

        setupTimePicker(editTime1);
        setupTimePicker(editTime2);
    }

    private void setupTimePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        // 預設時間顯示為現在時間
        editText.setText(String.format("%02d:%02d", hour, minute));

        editText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(edit_check.this,
                    (TimePicker view, int hourOfDay, int minute1) -> {
                        String time = String.format("%02d:%02d", hourOfDay, minute1);
                        editText.setText(time);
                    }, hour, minute, true); // true 表示 24 小時制

            timePickerDialog.show();
        });
    }
}
