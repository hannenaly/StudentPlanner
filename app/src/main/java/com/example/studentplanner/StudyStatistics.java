
package com.example.studentplanner;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;

import java.util.Calendar;
import java.util.Locale;

public class StudyStatistics extends AppCompatActivity {

    TextView tvTotalTime, tvWeeklyTime;
    TextView tvDailyAverage, tvActiveDays;

    LinearLayout weeklyChartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_statistics);

        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvWeeklyTime = findViewById(R.id.tvWeeklyTime);
        tvDailyAverage = findViewById(R.id.tvDailyAverage);
        tvActiveDays = findViewById(R.id.tvActiveDays);

        weeklyChartContainer =
                findViewById(R.id.weeklyChartContainer);

        findViewById(R.id.btnBackStats).setOnClickListener(v -> finish());

        loadStatistics();
    }

    private void loadStatistics() {

        SharedPreferences stats =
                getSharedPreferences("StudyStats", MODE_PRIVATE);

        int totalMinutes =
                stats.getInt("totalStudyMinutes", 0);

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (day + 5) % 7;

        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);

        int[] minutes = new int[7];

        int weeklyMinutes = 0;
        int activeDays = 0;

        for (int i = 0; i < 7; i++) {

            String dateKey = String.format(
                    Locale.US,
                    "studyMinutes_%04d%02d%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            minutes[i] = stats.getInt(dateKey, 0);

            weeklyMinutes += minutes[i];

            if (minutes[i] > 0) {
                activeDays++;
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        int averageMinutes = weeklyMinutes / 7;

        tvTotalTime.setText(formatTime(totalMinutes));
        tvWeeklyTime.setText(formatTime(weeklyMinutes));
        tvDailyAverage.setText(formatTime(averageMinutes));
        tvActiveDays.setText(activeDays + " / 7");

        showWeeklyChart(minutes);
    }

    private String formatTime(int minutes) {

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        if (hours > 0) {
            return hours + "h " + remainingMinutes + "m";
        }

        return remainingMinutes + "m";
    }

    private void showWeeklyChart(int[] minutes) {

        weeklyChartContainer.removeAllViews();

        String[] days = {
                "Mon", "Tue", "Wed",
                "Thu", "Fri", "Sat", "Sun"
        };

        int maxMinutes = 0;

        for (int value : minutes) {
            if (value > maxMinutes) {
                maxMinutes = value;
            }
        }

        for (int i = 0; i < 7; i++) {

            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams columnParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                    );

            columnParams.setMargins(dp(2), 0, dp(2), 0);
            column.setLayoutParams(columnParams);

            TextView valueText = new TextView(this);
            valueText.setText(minutes[i] == 0 ? "" : minutes[i] + "m");
            valueText.setTextColor(0xFF667085);
            valueText.setTextSize(9);
            valueText.setGravity(Gravity.CENTER);

            column.addView(valueText);

            View bar = new View(this);

            int barHeight = 4;

            if (maxMinutes > 0 && minutes[i] > 0) {
                barHeight = Math.max(
                        8,
                        (int) (100.0 * minutes[i] / maxMinutes)
                );
            }

            LinearLayout.LayoutParams barParams =
                    new LinearLayout.LayoutParams(dp(18), dp(barHeight));

            barParams.setMargins(0, dp(5), 0, dp(7));
            bar.setLayoutParams(barParams);

            GradientDrawable shape = new GradientDrawable();
            shape.setColor(0xFF2675CC);
            shape.setCornerRadius(dp(8));

            bar.setBackground(shape);

            column.addView(bar);

            TextView dayText = new TextView(this);
            dayText.setText(days[i]);
            dayText.setTextColor(0xFF667085);
            dayText.setTextSize(10);
            dayText.setGravity(Gravity.CENTER);

            column.addView(dayText);

            weeklyChartContainer.addView(column);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }
}
