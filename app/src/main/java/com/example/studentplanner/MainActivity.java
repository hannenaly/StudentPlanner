
package com.example.studentplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {

    Button MyTasks, MySubjects, Exams, DailyPlanner;
    TextView tvTasksCount, tvSubjectsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyTasks = findViewById(R.id.MyTasks);
        MySubjects = findViewById(R.id.MySubjects);
        Exams = findViewById(R.id.Exams);
        DailyPlanner = findViewById(R.id.DailyPlanner);
        tvTasksCount = findViewById(R.id.tvTasksCount);
        tvSubjectsCount = findViewById(R.id.tvSubjectsCount);

        MyTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyTasks.class);
            startActivity(intent);
        });

        MySubjects.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MySubjects.class);
            startActivity(intent);
        });


}
private void updateTaskCount() {
    SharedPreferences prefs = getSharedPreferences("MyTasks", MODE_PRIVATE);
    String tasks = prefs.getString("tasks", "[]");
    try {
        JSONArray tasksArray = new JSONArray(tasks);
        tvTasksCount.setText(String.valueOf(tasksArray.length()));
    } catch (JSONException e) {
        e.printStackTrace();
    }
}
private void updateSubjectCount() {
    SharedPreferences prefs = getSharedPreferences("MySubjects", MODE_PRIVATE);
    String subjects = prefs.getString("subjects", "[]");
    try {
        JSONArray subjectsArray = new JSONArray(subjects);
        tvSubjectsCount.setText(String.valueOf(subjectsArray.length()));
    } catch (JSONException e) {
        e.printStackTrace();
    }
}
protected void onResume() {
    super.onResume();
    updateTaskCount();
    updateSubjectCount();
}}
