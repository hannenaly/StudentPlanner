
package com.example.studentplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SubjectDetails extends AppCompatActivity {

    TextView tvSubjectName, tvDoctorName;
    TextView tvCreditHours, tvSection, tvRoom;

    Button  btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subject_details);

        tvSubjectName = findViewById(R.id.tvSubjectName);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvCreditHours = findViewById(R.id.tvCreditHours);
        tvSection = findViewById(R.id.tvSection);
        tvRoom = findViewById(R.id.tvRoom);

        btnBack = findViewById(R.id.btnBack);

        String subjectName = getIntent().getStringExtra("subjectName");
        String doctorName = getIntent().getStringExtra("doctorName");
        String creditHours = getIntent().getStringExtra("creditHours");
        String section = getIntent().getStringExtra("section");
        String room = getIntent().getStringExtra("room");

        tvSubjectName.setText(subjectName);
        tvDoctorName.setText("Doctor: " + doctorName);
        tvCreditHours.setText("Credit Hours: " + creditHours);
        tvSection.setText("Section: " + section);
        tvRoom.setText("Room: " + room);

        btnBack.setOnClickListener(v -> finish());
    }
}
