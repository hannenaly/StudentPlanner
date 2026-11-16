package com.example.studentplanner;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class MyTasks extends AppCompatActivity {

    EditText etTaskName, etDuration;
    Button btnSelectDate, btnSaveTask, btnBackHome;
    Button btnStartTime, btnEndTime;
    RadioGroup rgPriority;
    LinearLayout tasksContainer;
    SharedPreferences Preferences;

    Spinner spReminder;

    String selectedDate = "";
    String selectedStartTime = "";
    String selectedEndTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        NotificationHelper.createChannel(this);


        etTaskName = findViewById(R.id.etTaskName);
        etDuration = findViewById(R.id.etDuration);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnBackHome = findViewById(R.id.btnBackHome);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        rgPriority = findViewById(R.id.rgPriority);
        tasksContainer = findViewById(R.id.tasksContainer);
        spReminder = findViewById(R.id.spReminder);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.reminder_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReminder.setAdapter(adapter);


        Preferences = getSharedPreferences("MyTasks", MODE_PRIVATE);

        loadTasks();

        btnSelectDate.setOnClickListener(v -> selectDate());

        btnStartTime.setOnClickListener(v -> selectTime(true));

        btnEndTime.setOnClickListener(v -> selectTime(false));

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

    private void selectTime(boolean isStartTime) {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {

                    String time = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                    );

                    if (isStartTime) {
                        selectedStartTime = time;
                        btnStartTime.setText(time);
                    } else {
                        selectedEndTime = time;
                        btnEndTime.setText(time);
                    }
                },
                hour,
                minute,
                true
        );

        timePicker.show();
    }

    private void saveTask() {

        String name = etTaskName.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String date = selectedDate;

        int selectedId = rgPriority.getCheckedRadioButtonId();

        if (name.isEmpty() || duration.isEmpty()
                || date.isEmpty() || selectedId == -1
                || selectedStartTime.isEmpty()
                || selectedEndTime.isEmpty()) {

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
            task.put("startTime", selectedStartTime);
            task.put("endTime", selectedEndTime);
            task.put("reminderOption", spReminder.getSelectedItemPosition());


            tasks.put(task);

            Preferences.edit()
                    .putString("tasks", tasks.toString())
                    .apply();
            long dueDateMillis = convertDateToMillis(date);

            scheduleReminder(
                    name,
                    dueDateMillis,
                    spReminder.getSelectedItemPosition()
            );

            loadTasks();
            scheduleReminder(
                    "Test Notification",
                    System.currentTimeMillis() + 60 * 1000L,
                    0
            );

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        etTaskName.setText("");
        etDuration.setText("");

        selectedDate = "";
        selectedStartTime = "";
        selectedEndTime = "";

        btnSelectDate.setText("Select Date");
        btnStartTime.setText("Select Start Time");
        btnEndTime.setText("Select End Time");

        rgPriority.check(R.id.rbMedium);

        Toast.makeText(this, "Task Added Successfully",
                Toast.LENGTH_SHORT).show();
    }

    private void addTaskCard(String name, String duration,
                             String date, String priority,
                             String startTime, String endTime,
                             int position) {

        // Main Card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        int[] colors = {
                Color.parseColor("#EAF0FC"),
                Color.parseColor("#E8F8F5"),
                Color.parseColor("#FFF6DF"),
                Color.parseColor("#F7EDFC")
        };

        card.setBackground(createBackground(
                colors[position % colors.length], 25));

        // Icon Box
        TextView icon = new TextView(this);
        icon.setText("▤");
        icon.setTextSize(25);
        icon.setTextColor(Color.parseColor("#17365D"));
        icon.setGravity(Gravity.CENTER);

        icon.setBackground(createBackground(Color.WHITE, 18));

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(dp(52), dp(52));

        iconParams.setMargins(0, 0, dp(14), 0);
        card.addView(icon, iconParams);

        // Task Information
        LinearLayout information = new LinearLayout(this);
        information.setOrientation(LinearLayout.VERTICAL);

        TextView taskName = new TextView(this);
        taskName.setText(name);
        taskName.setTextSize(17);
        taskName.setTextColor(Color.parseColor("#17365D"));
        taskName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView taskDetails = new TextView(this);
        taskDetails.setText(
                duration + " hours\n" +
                        startTime + " - " + endTime + "\n" +
                        date + "  |  " + priority
        );

        taskDetails.setTextSize(13);
        taskDetails.setTextColor(Color.parseColor("#667085"));

        information.addView(taskName);
        information.addView(taskDetails);

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        card.addView(information, infoParams);

        // Done Button
        Button doneButton = new Button(this);
        doneButton.setText("Done");
        doneButton.setTextSize(13);
        doneButton.setAllCaps(false);
        doneButton.setTextColor(Color.parseColor("#17365D"));
        doneButton.setPadding(dp(8), 0, dp(8), 0);

        doneButton.setBackground(createBackground(Color.WHITE, 30));

        LinearLayout.LayoutParams doneParams =
                new LinearLayout.LayoutParams(
                        dp(75),
                        dp(45)
                );

        doneParams.setMargins(dp(8), 0, 0, 0);

        card.addView(doneButton, doneParams);

        // Card Margins
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, dp(12));

        card.setLayoutParams(cardParams);

        tasksContainer.addView(card);

        // Remove Task When Done
        doneButton.setOnClickListener(v -> {
            removeTask(position);
        });
    }

    private void removeTask(int position) {

        try {
            JSONArray tasks = new JSONArray(
                    Preferences.getString("tasks", "[]")
            );

            if (position >= 0 && position < tasks.length()) {

                tasks.remove(position);

                Preferences.edit()
                        .putString("tasks", tasks.toString())
                        .apply();

                loadTasks();

                Toast.makeText(this, "Task Completed!",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {

        try {
            String saved = Preferences.getString("tasks", "[]");

            JSONArray tasks = new JSONArray(saved);

            tasksContainer.removeAllViews();

            for (int i = 0; i < tasks.length(); i++) {

                JSONObject task = tasks.getJSONObject(i);

                String name = task.getString("name");
                String duration = task.getString("duration");
                String date = task.getString("date");
                String priority = task.getString("priority");

                String startTime = task.optString(
                        "startTime", "Not set");

                String endTime = task.optString(
                        "endTime", "Not set");

                addTaskCard(
                        name,
                        duration,
                        date,
                        priority,
                        startTime,
                        endTime,
                        i
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private GradientDrawable createBackground(int color, int radius) {

        GradientDrawable background = new GradientDrawable();

        background.setColor(color);
        background.setCornerRadius(dp(radius));

        return background;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }
    private void scheduleReminder(String taskName, long dueDateMillis, int reminderOption) {
        long reminderTime = dueDateMillis;

        switch (reminderOption) {
            case 1:
                reminderTime -= 60 * 60 * 1000L;
                break;
            case 2:
                reminderTime -= 3 * 60 * 60 * 1000L;
                break;
            case 3:
                reminderTime -= 24 * 60 * 60 * 1000L;
                break;
            case 4:
                reminderTime -= 2 * 24 * 60 * 60 * 1000L;
                break;
        }

        if (reminderTime <= System.currentTimeMillis()) {
            return;
        }

        Intent intent = new Intent(this, TaskReminderreceiver.class);
        intent.putExtra("taskName", taskName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) (dueDateMillis % Integer.MAX_VALUE),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
            );
        }
    }
    private long convertDateToMillis(String date) {
        String[] parts = date.split("/");

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int year = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 9, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
