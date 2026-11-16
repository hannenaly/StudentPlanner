package com.example.studentplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MySubjects extends AppCompatActivity {

    EditText etSubjectName, etDoctorName;
    EditText etCreditHours, etSection, etRoom;

    Button btnAddSubject, btnBackHome;
    SharedPreferences Preferences;
    LinearLayout subjectsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subjects);

        etSubjectName = findViewById(R.id.etSubjectName);
        etDoctorName = findViewById(R.id.etDoctorName);
        etCreditHours = findViewById(R.id.etCreditHours);
        etSection = findViewById(R.id.etSection);
        etRoom = findViewById(R.id.etRoom);

        btnAddSubject = findViewById(R.id.btnAddSubject);
        btnBackHome = findViewById(R.id.btnBackHome);

        subjectsContainer = findViewById(R.id.subjectsContainer);

        Preferences = getSharedPreferences("MySubjects", MODE_PRIVATE);
        loadSubjects();

        btnAddSubject.setOnClickListener(v -> addSubject());

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MySubjects.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    private void addSubject() {

        String subjectName = etSubjectName.getText().toString().trim();
        String doctorName = etDoctorName.getText().toString().trim();
        String creditHours = etCreditHours.getText().toString().trim();
        String section = etSection.getText().toString().trim();
        String room = etRoom.getText().toString().trim();

        if (subjectName.isEmpty() || doctorName.isEmpty()
                || creditHours.isEmpty() || section.isEmpty()
                || room.isEmpty()) {

            Toast.makeText(this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray subjects = new JSONArray(
                    Preferences.getString("subjects", "[]")
            );

            JSONObject subject = new JSONObject();

            subject.put("subjectName", subjectName);
            subject.put("doctorName", doctorName);
            subject.put("creditHours", creditHours);
            subject.put("section", section);
            subject.put("room", room);

            subjects.put(subject);

            Preferences.edit()
                    .putString("subjects", subjects.toString())
                    .apply();

            addSubjectCard(subjectName, doctorName,
                    creditHours, section, room);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        etSubjectName.setText("");
        etDoctorName.setText("");
        etCreditHours.setText("");
        etSection.setText("");
        etRoom.setText("");

        Toast.makeText(this, "Subject Added Successfully",
                Toast.LENGTH_SHORT).show();
    }
    private void addSubjectCard(String subjectName,
                                String doctorName,
                                String creditHours,
                                String section,
                                String room) {

        TextView subjectCard = new TextView(this);

        subjectCard.setText(
                subjectName +
                        "\nDoctor: " + doctorName +
                        "\nCredit Hours: " + creditHours +
                        "\nTap to view details"
        );

        subjectCard.setTextSize(16);
        subjectCard.setTextColor(0xFFFFFFFF);
        subjectCard.setPadding(24, 24, 24, 24);

        subjectCard.setBackgroundResource(R.drawable.subjectcard);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 16);

        subjectCard.setLayoutParams(params);

        subjectCard.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MySubjects.this,
                    SubjectDetails.class
            );

            intent.putExtra("subjectName", subjectName);
            intent.putExtra("doctorName", doctorName);
            intent.putExtra("creditHours", creditHours);
            intent.putExtra("section", section);
            intent.putExtra("room", room);

            startActivity(intent);
        });

        subjectsContainer.addView(subjectCard);
    }
    private void loadSubjects() {

        try {
            String saved = Preferences.getString("subjects", "[]");

            JSONArray subjects = new JSONArray(saved);

            for (int i = 0; i < subjects.length(); i++) {

                JSONObject subject = subjects.getJSONObject(i);

                String subjectName = subject.getString("subjectName");
                String doctorName = subject.getString("doctorName");
                String creditHours = subject.getString("creditHours");
                String section = subject.getString("section");
                String room = subject.getString("room");

                addSubjectCard(subjectName, doctorName,
                        creditHours, section, room);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
