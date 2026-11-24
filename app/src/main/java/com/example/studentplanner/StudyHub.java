


package com.example.studentplanner;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudyHub extends AppCompatActivity {

    TextView tvTotalSubjects, tvTotalTasks;
    TextView tvOverallProgress, tvWeeklyHours;
    TextView tvStudyHours;
    TextView tvTimer, tvTimerMode;

    Button btnTimer, btnResetTimer;

    LinearLayout subjectsProgressContainer;
    LinearLayout upcomingTasksContainer;

    CountDownTimer countDownTimer;

    long timeLeft = 25 * 60 * 1000;
    boolean timerRunning = false;
    boolean breakMode = false;

    final long FOCUS_TIME = 25 * 60 * 1000;
    final long BREAK_TIME = 5 * 60 * 1000;

    SharedPreferences studyStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_hub2);

        tvTotalSubjects = findViewById(R.id.tvTotalSubjects);
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvOverallProgress = findViewById(R.id.tvOverallProgress);
        tvWeeklyHours = findViewById(R.id.tvWeeklyHours);
        tvStudyHours = findViewById(R.id.tvStudyHours);

        tvTimer = findViewById(R.id.tvTimer);
        tvTimerMode = findViewById(R.id.tvTimerMode);

        btnTimer = findViewById(R.id.btnTimer);
        btnResetTimer = findViewById(R.id.btnResetTimer);

        subjectsProgressContainer =
                findViewById(R.id.subjectsProgressContainer);

        upcomingTasksContainer =
                findViewById(R.id.upcomingTasksContainer);

        studyStats = getSharedPreferences("StudyStats", MODE_PRIVATE);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Open Study Statistics
        findViewById(R.id.studyActivityCard).setOnClickListener(v -> {
            Intent intent = new Intent(StudyHub.this, StudyStatistics.class);
            startActivity(intent);
        });

        btnTimer.setOnClickListener(v -> startPauseTimer());
        btnResetTimer.setOnClickListener(v -> resetTimer());

        loadDashboard();
        updateTimerDisplay();
        updateStudyHours();
    }

    private void loadDashboard() {
        loadSubjects();
        loadTasks();
    }

    private void loadSubjects() {

        SharedPreferences prefs =
                getSharedPreferences("MySubjects", MODE_PRIVATE);

        String subjects = prefs.getString("subjects", "[]");

        try {
            JSONArray subjectsArray = new JSONArray(subjects);

            tvTotalSubjects.setText(
                    String.valueOf(subjectsArray.length())
            );

            subjectsProgressContainer.removeAllViews();

            int totalProgress = 0;
            int subjectsCount = subjectsArray.length();

            for (int i = 0; i < subjectsArray.length(); i++) {

                JSONObject subject = subjectsArray.getJSONObject(i);

                String subjectName =
                        subject.optString("subjectName", "Subject");

                String doctorName =
                        subject.optString("doctorName", "");

                int lecturesDone = studyStats.getInt(
                        subjectName + "_lecturesDone", 0);

                int lecturesTotal = studyStats.getInt(
                        subjectName + "_lecturesTotal", 0);

                int assignmentsDone = studyStats.getInt(
                        subjectName + "_assignmentsDone", 0);

                int assignmentsTotal = studyStats.getInt(
                        subjectName + "_assignmentsTotal", 0);

                int total = lecturesTotal + assignmentsTotal;
                int completed = lecturesDone + assignmentsDone;

                int progress = 0;

                if (total > 0) {
                    progress = (completed * 100) / total;
                }

                totalProgress += progress;

                addSubjectCard(
                        subjectName,
                        doctorName,
                        lecturesDone,
                        lecturesTotal,
                        assignmentsDone,
                        assignmentsTotal,
                        progress
                );
            }

            int overall = 0;

            if (subjectsCount > 0) {
                overall = totalProgress / subjectsCount;
            }

            tvOverallProgress.setText(overall + "%");

            ProgressBar progressBar =
                    findViewById(R.id.progressOverall);

            progressBar.setProgress(overall);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSubjectCard(
            String subjectName,
            String doctorName,
            int lecturesDone,
            int lecturesTotal,
            int assignmentsDone,
            int assignmentsTotal,
            int progress) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 14, 16, 14);
        card.setBackgroundResource(R.drawable.bg_study_card);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cardParams);

        TextView name = new TextView(this);
        name.setText(subjectName);
        name.setTextColor(Color.parseColor("#17365D"));
        name.setTextSize(17);
        name.setTypeface(null, Typeface.BOLD);

        card.addView(name);

        if (!doctorName.isEmpty()) {
            TextView doctor = new TextView(this);
            doctor.setText("Doctor: " + doctorName);
            doctor.setTextColor(Color.GRAY);
            doctor.setTextSize(13);
            card.addView(doctor);
        }

        TextView details = new TextView(this);

        details.setText(
                "Lectures: " + lecturesDone + " / " + lecturesTotal +
                        "\nAssignments: " + assignmentsDone + " / " + assignmentsTotal
        );

        details.setTextColor(Color.parseColor("#667085"));
        details.setTextSize(13);
        details.setPadding(0, 8, 0, 8);

        card.addView(details);

        ProgressBar progressBar = new ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
        );

        progressBar.setMax(100);
        progressBar.setProgress(progress);

        progressBar.setProgressTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#2675CC")
                )
        );

        card.addView(progressBar);

        TextView percentage = new TextView(this);
        percentage.setText(progress + "% completed");
        percentage.setTextColor(Color.parseColor("#2675CC"));
        percentage.setTextSize(13);
        percentage.setPadding(0, 6, 0, 0);

        card.addView(percentage);

        TextView edit = new TextView(this);
        edit.setText("Tap to update progress →");
        edit.setTextColor(Color.parseColor("#2675CC"));
        edit.setTextSize(13);
        edit.setGravity(Gravity.END);

        card.addView(edit);

        card.setOnClickListener(v ->
                showProgressDialog(subjectName)
        );

        subjectsProgressContainer.addView(card);
    }

    private void showProgressDialog(String subjectName) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 10, 24, 10);

        EditText lecturesDone = createNumberInput("Completed lectures");
        EditText lecturesTotal = createNumberInput("Total lectures");

        EditText assignmentsDone =
                createNumberInput("Completed assignments");

        EditText assignmentsTotal =
                createNumberInput("Total assignments");

        layout.addView(lecturesDone);
        layout.addView(lecturesTotal);
        layout.addView(assignmentsDone);
        layout.addView(assignmentsTotal);

        lecturesDone.setText(String.valueOf(
                studyStats.getInt(subjectName + "_lecturesDone", 0)));

        lecturesTotal.setText(String.valueOf(
                studyStats.getInt(subjectName + "_lecturesTotal", 0)));

        assignmentsDone.setText(String.valueOf(
                studyStats.getInt(subjectName + "_assignmentsDone", 0)));

        assignmentsTotal.setText(String.valueOf(
                studyStats.getInt(subjectName + "_assignmentsTotal", 0)));

        new AlertDialog.Builder(this)
                .setTitle(subjectName + " Progress")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {

                    try {
                        int ld = Integer.parseInt(
                                lecturesDone.getText().toString());

                        int lt = Integer.parseInt(
                                lecturesTotal.getText().toString());

                        int ad = Integer.parseInt(
                                assignmentsDone.getText().toString());

                        int at = Integer.parseInt(
                                assignmentsTotal.getText().toString());

                        if (ld < 0 || lt < 0 || ad < 0 || at < 0
                                || ld > lt || ad > at) {

                            Toast.makeText(
                                    this,
                                    "Please enter valid numbers",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        studyStats.edit()
                                .putInt(subjectName + "_lecturesDone", ld)
                                .putInt(subjectName + "_lecturesTotal", lt)
                                .putInt(subjectName + "_assignmentsDone", ad)
                                .putInt(subjectName + "_assignmentsTotal", at)
                                .apply();

                        loadSubjects();

                        Toast.makeText(
                                this,
                                "Progress Updated Successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Please enter valid numbers",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private EditText createNumberInput(String hint) {

        EditText input = new EditText(this);
        input.setHint(hint);
        input.setInputType(2);

        return input;
    }

    private void loadTasks() {

        SharedPreferences prefs =
                getSharedPreferences("MyTasks", MODE_PRIVATE);

        String tasks = prefs.getString("tasks", "[]");

        try {
            JSONArray tasksArray = new JSONArray(tasks);

            tvTotalTasks.setText(
                    String.valueOf(tasksArray.length())
            );

            upcomingTasksContainer.removeAllViews();

            if (tasksArray.length() == 0) {

                TextView empty = new TextView(this);
                empty.setText("No upcoming tasks");
                empty.setTextColor(Color.GRAY);
                empty.setPadding(10, 15, 10, 15);

                upcomingTasksContainer.addView(empty);
            }

            for (int i = 0; i < tasksArray.length(); i++) {

                JSONObject task = tasksArray.getJSONObject(i);

                String taskName = task.optString("name", "Task");
                String date = task.optString("date", "No date");
                String priority = task.optString("priority", "Normal");
                String duration = task.optString("duration", "");
                String startTime = task.optString("startTime", "");
                String endTime = task.optString("endTime", "");

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(16, 14, 16, 14);
                card.setBackgroundResource(R.drawable.bg_study_card);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                params.setMargins(0, 0, 0, 10);
                card.setLayoutParams(params);

                TextView name = new TextView(this);
                name.setText(taskName);
                name.setTextColor(Color.parseColor("#17365D"));
                name.setTextSize(15);
                name.setTypeface(null, Typeface.BOLD);

                card.addView(name);

                TextView taskDate = new TextView(this);

                taskDate.setText(
                        date + "   •   " + priority +
                                "\n" + duration + " hours" +
                                "\n" + startTime + " - " + endTime
                );

                taskDate.setTextColor(Color.GRAY);
                taskDate.setTextSize(13);

                card.addView(taskDate);

                upcomingTasksContainer.addView(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPauseTimer() {

        if (timerRunning) {

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            timerRunning = false;
            btnTimer.setText("Start");

        } else {

            countDownTimer = new CountDownTimer(timeLeft, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                    timeLeft = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {

                    timerRunning = false;
                    btnTimer.setText("Start");

                    if (!breakMode) {

                        saveCompletedFocusSession();

                        breakMode = true;
                        timeLeft = BREAK_TIME;

                        tvTimerMode.setText("Break Time");

                        Toast.makeText(
                                StudyHub.this,
                                "Focus session completed! Take a break.",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        breakMode = false;
                        timeLeft = FOCUS_TIME;

                        tvTimerMode.setText("Focus Session");

                        Toast.makeText(
                                StudyHub.this,
                                "Break completed! Ready to study?",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    updateTimerDisplay();
                    updateStudyHours();
                }

            }.start();

            timerRunning = true;
            btnTimer.setText("Pause");
        }
    }

    private void saveCompletedFocusSession() {

        int totalMinutes = studyStats.getInt(
                "totalStudyMinutes", 0);

        int todayMinutes = studyStats.getInt(
                getTodayKey(), 0);

        studyStats.edit()
                .putInt("totalStudyMinutes", totalMinutes + 25)
                .putInt(getTodayKey(), todayMinutes + 25)
                .apply();
    }

    private String getTodayKey() {

        SimpleDateFormat format =
                new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        return "studyMinutes_" + format.format(new Date());
    }

    private void resetTimer() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timerRunning = false;
        breakMode = false;
        timeLeft = FOCUS_TIME;

        btnTimer.setText("Start");
        tvTimerMode.setText("Focus Session");

        updateTimerDisplay();
    }

    private void updateTimerDisplay() {

        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        tvTimer.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                )
        );
    }

    private void updateStudyHours() {

        int totalMinutes = studyStats.getInt(
                "totalStudyMinutes", 0);

        int weeklyMinutes = getWeeklyStudyMinutes();

        int hours = weeklyMinutes / 60;
        int minutes = weeklyMinutes % 60;

        tvWeeklyHours.setText(
                hours + "h " + minutes + "m"
        );

        int totalHours = totalMinutes / 60;
        int totalRemainingMinutes = totalMinutes % 60;

        tvStudyHours.setText(
                "Total Focus Time: " +
                        totalHours + "h " + totalRemainingMinutes + "m"
        );
    }

    private int getWeeklyStudyMinutes() {

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (day + 5) % 7;

        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int weeklyMinutes = 0;

        SimpleDateFormat format =
                new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {

            String key = "studyMinutes_" +
                    format.format(calendar.getTime());

            weeklyMinutes += studyStats.getInt(key, 0);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return weeklyMinutes;
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadDashboard();
        updateStudyHours();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
