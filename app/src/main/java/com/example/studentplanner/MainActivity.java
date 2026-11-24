



package com.example.studentplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    Button MyTasks, MySubjects, Exams, StudyHub;
    TextView tvTasksCount, tvSubjectsCount;

    DrawerLayout drawerLayout;

    TextView btnOpenDrawer;
    TextView navHome, navSubjects, navTasks;
    TextView navExams, navStudyHub, navAbout, navTimer;
    TextView navStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyTasks = findViewById(R.id.MyTasks);
        MySubjects = findViewById(R.id.MySubjects);
        Exams = findViewById(R.id.Exams);
        StudyHub = findViewById(R.id.StudyHub);

        tvTasksCount = findViewById(R.id.tvTasksCount);
        tvSubjectsCount = findViewById(R.id.tvSubjectsCount);

        drawerLayout = findViewById(R.id.drawerLayout);

        btnOpenDrawer = findViewById(R.id.btnOpenDrawer);

        navHome = findViewById(R.id.navHome);
        navSubjects = findViewById(R.id.navSubjects);
        navTasks = findViewById(R.id.navTasks);
        navExams = findViewById(R.id.navExams);
        navStudyHub = findViewById(R.id.navStudyHub);
        navAbout = findViewById(R.id.navAbout);
        navTimer = findViewById(R.id.navTimer);
        navStatistics = findViewById(R.id.navStatistics);

        btnOpenDrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(findViewById(R.id.navigationDrawer));
        });

        MyTasks.setOnClickListener(v -> openTasks());

        MySubjects.setOnClickListener(v -> openSubjects());

        Exams.setOnClickListener(v ->
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        );

        StudyHub.setOnClickListener(v -> openStudyHub(false));

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

        navStudyHub.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            openStudyHub(false);
        });

        navTimer.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            openStudyHub(true);
        });

        navStatistics.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            openStatistics();
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

    private void openStudyHub(boolean openTimer) {
        Intent intent = new Intent(MainActivity.this, StudyHub.class);
        intent.putExtra("open_timer", openTimer);
        startActivity(intent);
    }

    private void openStatistics() {
        Intent intent = new Intent(MainActivity.this, StudyStatistics.class);
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
