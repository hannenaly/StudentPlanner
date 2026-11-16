

package com.example.studentplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button MyTasks, MySubjects, Exams, DailyPlanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyTasks = findViewById(R.id.MyTasks);
        MySubjects = findViewById(R.id.MySubjects);
        Exams = findViewById(R.id.Exams);
        DailyPlanner = findViewById(R.id.DailyPlanner);

        MyTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyTasks.class);
            startActivity(intent);
        });

        MySubjects.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MySubjects.class);
            startActivity(intent);
        });
    }
}
