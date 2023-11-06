package com.example.sinupsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.example.sinupsample.NOTIFICATION_ACTION")) {
            // 通知アクションが実行されたときの処理をここに追加

            // 例: 通知がタップされたときにアクティビティを開く
            Intent openActivityIntent = new Intent(context, LoginActivity.class);
            openActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(openActivityIntent);
        }

    }
}
