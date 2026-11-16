package com.example.studentplanner;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class TaskReminderreceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String taskName =
                intent.getStringExtra("taskName");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context, "task_channel"
                )
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Task Reminder")
                        .setContentText(taskName + " is due soon!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        manager.notify(
                intent.getIntExtra("notificationId", 1),
                builder.build()
        );
    }
}