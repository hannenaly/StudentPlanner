package com.example.studentplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class newtask extends AppCompatActivity {

    private EditText etTaskName;
    private EditText etDuration;
    private Button btnSelectDate;
    private Button btnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_newtask);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTaskName = findViewById(R.id.etTaskName);
        etDuration = findViewById(R.id.etDuration);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnAddTask = findViewById(R.id.btnAddTask);

        // Reset text field so user types a new task every time
        etTaskName.setText("");

        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(newtask.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateStr = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        btnSelectDate.setText(dateStr);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnAddTask.setOnClickListener(v -> {
            String taskName = etTaskName.getText().toString().trim();
            if (taskName.isEmpty()) {
                Toast.makeText(newtask.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("task_name", taskName);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
