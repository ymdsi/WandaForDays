package com.example.sinupsample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MissionFragment extends Fragment {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int stepCount = 0;
    private boolean isWalking = false;
    private TextView stepCountTextView;

    public MissionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        // ImageButtonを取得
        ImageButton myImageButton = view.findViewById(R.id.back_button);
        stepCountTextView = view.findViewById(R.id.step_count_text);

        // センサーマネージャーを初期化
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        // 加速度センサーを取得
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // ImageButtonにクリックリスナーを設定
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new OtherFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 加速度センサーリスナーを登録
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 加速度センサーリスナーを解除
        sensorManager.unregisterListener(sensorEventListener);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x * x + y * y + z * z);

            // ここで歩数の単純なカウントを行う例
            if (magnitude > 10 && !isWalking) {
                stepCount++;
                isWalking = true;
            } else if (magnitude < 10) {
                isWalking = false;
            }

            // 歩数をUIに表示
            stepCountTextView.setText("歩数: " + stepCount);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // 加速度センサーの精度変更の処理
        }
    };
}
