package com.example.studentplanner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MyTasks extends AppCompatActivity {

    EditText etTaskName, etDuration;
    Button btnSelectDate, btnSaveTask, btnBackHome;
    RadioGroup rgPriority;
    LinearLayout tasksContainer;
    SharedPreferences Preferences;

    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        etTaskName = findViewById(R.id.etTaskName);
        etDuration = findViewById(R.id.etDuration);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnBackHome = findViewById(R.id.btnBackHome);
        rgPriority = findViewById(R.id.rgPriority);
        tasksContainer = findViewById(R.id.tasksContainer);
        Preferences = getSharedPreferences("MyTasks", MODE_PRIVATE);
        loadTasks();


        btnSelectDate.setOnClickListener(v -> selectDate());

        btnSaveTask.setOnClickListener(v -> saveTask());
        etTaskName.requestFocus();



        etTaskName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(etTaskName,
                            InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        etDuration.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(etDuration,
                            InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        etTaskName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                etDuration.requestFocus();
                return true;
            }
            return false;
        });

        etDuration.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveTask();
                return true;
            }
            return false;
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MyTasks.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }



    private void selectDate() {
;
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    selectedDate = selectedDay + "/"
                            + (selectedMonth + 1) + "/"
                            + selectedYear;

                    btnSelectDate.setText(selectedDate);
                },
                year, month, day
        );

        datePicker.show();
    }

    private void saveTask() {

        String name = etTaskName.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String date = selectedDate;

        int selectedId = rgPriority.getCheckedRadioButtonId();

        if (name.isEmpty() || duration.isEmpty()
                || date.isEmpty() || selectedId == -1) {

            Toast.makeText(this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = findViewById(selectedId);
        String priority = selectedRadio.getText().toString();

        try {
            JSONArray tasks = new JSONArray(
                    Preferences.getString("tasks", "[]")
            );

            JSONObject task = new JSONObject();

            task.put("name", name);
            task.put("duration", duration);
            task.put("date", date);
            task.put("priority", priority);

            tasks.put(task);

            Preferences.edit()
                    .putString("tasks", tasks.toString())
                    .apply();

            addTaskCard(name, duration, date, priority);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        etTaskName.setText("");
        etDuration.setText("");
        selectedDate = "";
        btnSelectDate.setText("Select Date");
        rgPriority.check(R.id.rbMedium);

        Toast.makeText(this, "Task Added Successfully",
                Toast.LENGTH_SHORT).show();
    }

    private void addTaskCard(String name, String duration,
                             String date, String priority) {

        TextView taskCard = new TextView(this);

        taskCard.setText(
                "Task: " + name +
                        "\nDuration: " + duration + " hours" +
                        "\nDue Date: " + date +
                        "\nPriority: " + priority
        );

        taskCard.setTextSize(16);
        taskCard.setTextColor(0xFFFFFFFF);
        taskCard.setPadding(24, 24, 24, 24);

        taskCard.setBackgroundResource(R.drawable.subjectcard);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 16);

        taskCard.setLayoutParams(params);

        tasksContainer.addView(taskCard);
    }

    private void loadTasks() {

        try {
            String saved = Preferences.getString("tasks", "[]");

            JSONArray tasks = new JSONArray(saved);

            for (int i = 0; i < tasks.length(); i++) {

                JSONObject task = tasks.getJSONObject(i);

                String name = task.getString("name");
                String duration = task.getString("duration");
                String date = task.getString("date");
                String priority = task.getString("priority");

                addTaskCard(name, duration, date, priority);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
