package com.example.sinupsample;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class WalktimeFragment extends Fragment {

    private Button setTimeButton;
    private TextView notificationTextView;
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;

    private static final int PERMISSION_REQUEST_CODE = 123;

    public WalktimeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通知許可が必要かどうかを確認
        checkNotificationPermission();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // アクセス許可が許可された場合の処理
            } else {
                // アクセス許可が拒否された場合の処理
            }
        }
    }

    private void checkNotificationPermission() {
        if (!isNotificationPermissionEnabled()) {
            // 通知許可が拒否されている場合、ダイアログを表示して許可を求める
            showNotificationPermissionDialog();
        }
    }

    private boolean isNotificationPermissionEnabled() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();
        return areNotificationsEnabled;
    }

    private void showNotificationPermissionDialog() {
        // 通知許可ダイアログを表示するためのコードを追加
        // ダイアログ内でユーザーに通知許可を求めるメッセージを表示し、設定画面に誘導するオプションを提供
        // 例えば、以下のように設定画面に誘導するための Intent を作成して使用できます：

        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", requireContext().getPackageName());
        intent.putExtra("app_uid", requireContext().getApplicationInfo().uid);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walktime, container, false);

        // back_buttonのクリック処理を追加
        AppCompatImageButton backButton = view.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // OtherFragmentに遷移
                OtherFragment otherFragment = new OtherFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, otherFragment);
                transaction.addToBackStack(null); // 戻るボタンで戻れるようにする
                transaction.commit();
            }
        });

        setTimeButton = view.findViewById(R.id.setTimeButton);
        notificationTextView = view.findViewById(R.id.notificationTextView); // TextView を初期化
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        return view;
    }



    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 選択した時間を処理
                        // hourOfDay が選択した時（0-23）
                        // minute が選択した分（0-59）

                        // 現在の時間を取得
                        Calendar currentCalendar = Calendar.getInstance();
                        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                        int currentMinute = currentCalendar.get(Calendar.MINUTE);

                        // 選択した時間と現在の時間を比較
                        if (hourOfDay > currentHour || (hourOfDay == currentHour && minute > currentMinute)) {
                            // 通知を設定
                            setNotification(requireContext(), hourOfDay, minute);
                        } else {
                            // 時間が過去の場合、エラーメッセージを表示
                            notificationTextView.setText("選択した時間は過去の時間です。");
                        }
                    }
                },
                currentHour, currentMinute, true // true で24時間形式、false で12時間形式
        );

        timePickerDialog.show();
    }


    private void setNotification(Context context, int hour, int minute) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel
        createNotificationChannel(context);

        // Create an intent to launch the desired activity
        Intent activityIntent = new Intent(context, LoginActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure it starts a new task

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("わんだforでいず")
                .setContentText("お散歩時間のお知らせです！")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent); // Set the activity to launch

        // Set the alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        alarmIntent.setAction("com.example.sinupsample.ALARM_ACTION"); // Set the action
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long timeInMillis = calendar.getTimeInMillis();



        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Update the TextView with a notification message
        String notificationMessage = "通知が" + hour + "時" + minute + "分に設定されました";
        notificationTextView.setText(notificationMessage);
    }




    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "わんだforでいず";
            String description = "お散歩時間のお知らせです！";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
