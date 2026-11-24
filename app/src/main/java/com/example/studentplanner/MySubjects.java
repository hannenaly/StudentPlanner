
package com.example.studentplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MySubjects extends AppCompatActivity {

    EditText etSubjectName, etDoctorName;
    EditText etCreditHours, etSection, etRoom;

    Button btnAddSubject, btnBackHome;
    SharedPreferences Preferences;
    LinearLayout subjectsContainer;

    int[] cardColors = {
            0xFFEAF0FC,
            0xFFE8F8F5,
            0xFFFFF6DF,
            0xFFF7EDFC
    };

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

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        card.setBackground(createBackground(
                cardColors[subjectsContainer.getChildCount() % cardColors.length],
                20
        ));

        card.setElevation(dp(3));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, dp(14));
        card.setLayoutParams(cardParams);


        TextView icon = new TextView(this);
        icon.setText("📚");
        icon.setTextSize(25);
        icon.setGravity(Gravity.CENTER);

        icon.setBackground(createBackground(
                Color.WHITE, 14
        ));

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(55), dp(55)
                );

        iconParams.setMargins(0, 0, dp(14), 0);
        icon.setLayoutParams(iconParams);

        card.addView(icon);



        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        details.setLayoutParams(detailsParams);

        TextView name = new TextView(this);
        name.setText(subjectName);
        name.setTextSize(18);
        name.setTextColor(Color.rgb(23, 54, 93));
        name.setTypeface(null, android.graphics.Typeface.BOLD);

        details.addView(name);

        TextView doctor = new TextView(this);
        doctor.setText("Doctor: " + doctorName);
        doctor.setTextSize(14);
        doctor.setTextColor(Color.rgb(90, 90, 90));

        LinearLayout.LayoutParams doctorParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        doctorParams.topMargin = dp(5);
        doctor.setLayoutParams(doctorParams);

        details.addView(doctor);

        TextView hours = new TextView(this);
        hours.setText("Credit Hours: " + creditHours);
        hours.setTextSize(14);
        hours.setTextColor(Color.rgb(90, 90, 90));

        details.addView(hours);

        TextView sectionRoom = new TextView(this);
        sectionRoom.setText(
                "Section: " + section + "   |   Room: " + room
        );
        sectionRoom.setTextSize(13);
        sectionRoom.setTextColor(Color.rgb(100, 100, 100));

        details.addView(sectionRoom);

        TextView tap = new TextView(this);
        tap.setText("Tap to view details  →");
        tap.setTextSize(12);
        tap.setTextColor(Color.rgb(23, 54, 93));

        LinearLayout.LayoutParams tapParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        tapParams.topMargin = dp(7);
        tap.setLayoutParams(tapParams);

        details.addView(tap);

        card.addView(details);



        card.setOnClickListener(v -> {

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

        subjectsContainer.addView(card);
    }

    private void loadSubjects() {

        subjectsContainer.removeAllViews();

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

    private GradientDrawable createBackground(int color, int radius) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }
}
