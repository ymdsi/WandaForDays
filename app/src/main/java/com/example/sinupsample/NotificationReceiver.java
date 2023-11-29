package com.example.sinupsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 通知を作成・表示するコード

        createNotification(context, "Walktime Notification", "It's time to walk!");
    }

    private void createNotification(Context context, String title, String message) {
        Log.d("WalktimeFragment", "Creating notification: " + title + " - " + message);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "わんだforでいず散歩時間";
            NotificationChannel channel = new NotificationChannel(channelId, "わんだforでいず散歩時間", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("わんだforでいず")
                .setContentText("散歩時間のお知らせです！")
                .setSmallIcon(R.drawable.app_logo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("わんだforでいず散歩時間");
        }

        notificationManager.notify(1, builder.build());
    }
}