

package com.example.studentplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    Button MyTasks, MySubjects, Exams, DailyPlanner;
    TextView tvTasksCount, tvSubjectsCount;

    DrawerLayout drawerLayout;

    TextView btnOpenDrawer;
    TextView navHome, navSubjects, navTasks;
    TextView navExams, navDaily, navAbout;

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

        drawerLayout = findViewById(R.id.drawerLayout);

        btnOpenDrawer = findViewById(R.id.btnOpenDrawer);

        navHome = findViewById(R.id.navHome);
        navSubjects = findViewById(R.id.navSubjects);
        navTasks = findViewById(R.id.navTasks);
        navExams = findViewById(R.id.navExams);
        navDaily = findViewById(R.id.navDaily);
        navAbout = findViewById(R.id.navAbout);


        btnOpenDrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(findViewById(R.id.navigationDrawer));
        });


        MyTasks.setOnClickListener(v -> openTasks());


        MySubjects.setOnClickListener(v -> openSubjects());


        Exams.setOnClickListener(v ->
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        );


        DailyPlanner.setOnClickListener(v ->
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        );


        navHome.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
        });


        navSubjects.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            openSubjects();
        });


        navTasks.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            openTasks();
        });


        navExams.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            Toast.makeText(this, "Exams - Coming Soon", Toast.LENGTH_SHORT).show();
        });


        navDaily.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            Toast.makeText(this, "Daily Planner - Coming Soon", Toast.LENGTH_SHORT).show();
        });


        navAbout.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            Toast.makeText(this, "Student Planner App", Toast.LENGTH_SHORT).show();
        });
    }

    private void openTasks() {
        Intent intent = new Intent(MainActivity.this, MyTasks.class);
        startActivity(intent);
    }

    private void openSubjects() {
        Intent intent = new Intent(MainActivity.this, MySubjects.class);
        startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        updateTaskCount();
        updateSubjectCount();
    }
}
