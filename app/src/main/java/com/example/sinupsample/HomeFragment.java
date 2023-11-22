package com.example.sinupsample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private TextView temperatureTextView;
    private TextView soilTemperatureTextView;
    private ImageView imageView; // ImageViewを宣言
//    ミッション
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int stepCount = 0;
    private boolean isWalking = false;
    private TextView stepCountTextView;
    private Button startCountButton; // ボタンをフィールドとして宣言
    private boolean isCounting = false; // 歩数計測中かどうかのフラグ

    private Button Get_Point_Button1;
    private Button Get_Point_Button2;
    private TextView mission1_TextView;
    private TextView mission2_TextView;
    private Integer mission1_judgment=0;
    private Integer mission2_judgment=0;
    private DatabaseReference userRef;
    private MotionLayout motion;

    public HomeFragment() {
        // Required empty public constructor
        userRef = FirebaseDatabase.getInstance().getReference("users");
    }


    // onCreateView メソッド内で view を宣言して初期化
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        temperatureTextView = view.findViewById(R.id.temperatureTextView);
        soilTemperatureTextView = view.findViewById(R.id.soilTemperatureTextView);
        imageView = view.findViewById(R.id.imageView); // ImageView を初期化
        startCountButton = view.findViewById(R.id.start_count_button);
        stepCountTextView = view.findViewById(R.id.step_count_text);
        Get_Point_Button1 = view.findViewById(R.id.get_point1);
        Get_Point_Button2 = view.findViewById(R.id.get_point2);
        mission1_TextView = view.findViewById(R.id.mission_1);
        mission2_TextView = view.findViewById(R.id.mission_2);
        motion = view.findViewById(R.id.motion2);



        //ボタン初期化
        Get_Point_Button2.setEnabled(false);
        Get_Point_Button1.setEnabled(false);
        // センサーマネージャーを初期化
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        // 加速度センサーを取得
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // ネットワーク通信を非同期で実行
        new GetDataTask().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                motion.transitionToEnd();
            }
        }, 900); //秒数


        startCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCount(); // ボタンがクリックされたときに歩数カウントを開始
            }
        });

        return view;


    }

    private class GetDataTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            // APIからデータを取得するロジックを実装
            JSONObject data = fetchDataFromAPI();
            return data;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            super.onPostExecute(data);

            if (data != null) {
                try {
                    // JSONデータから気温と地温の配列を取得
                    JSONObject hourlyData = data.getJSONObject("hourly");

                    JSONArray timeArray = hourlyData.getJSONArray("time");
                    JSONArray temperatureArray = hourlyData.getJSONArray("temperature_2m");
                    JSONArray soilTemperatureArray = hourlyData.getJSONArray("soil_temperature_0cm");

                    // 現在の時刻を取得
                    Date currentTime = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                    String currentTimestamp = sdf.format(currentTime);

                    // 最も近い時間を検索
                    int closestIndex = findClosestTimeIndex(timeArray, currentTimestamp);

                    // 最も近い時間に対応する気温と地温を取得
                    double temperature = temperatureArray.getDouble(closestIndex);
                    double soilTemperature = soilTemperatureArray.getDouble(closestIndex);
//                    double soilTemperature = 50;
                    int imageResource;

                    if (soilTemperature <= 0.0) {
                        imageResource = R.drawable.image4;
                    } else if (soilTemperature <= 10.0) {
                        imageResource = R.drawable.image3;
                    } else if (soilTemperature > 10.0 && soilTemperature <= 25.0) {
                        imageResource = R.drawable.image1;
                    } else if (soilTemperature > 25.0 && soilTemperature <= 35.0) {
                        imageResource = R.drawable.image2;
                    } else if (soilTemperature > 35.0 && soilTemperature <= 43.0) {
                        imageResource = R.drawable.image3;
                    } else {
                        imageResource = R.drawable.image4;
                    }

                    // 画像ビューにデータを表示
                    imageView.setImageResource(imageResource);

                    // テキストビューにデータを表示
                    temperatureTextView.setText("気温: " + temperature + "°C");
                    soilTemperatureTextView.setText("路面温度: " + soilTemperature + "°C");

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "データの解析に失敗しました", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "データの取得に失敗しました", Toast.LENGTH_SHORT).show();
            }
        }

        private int findClosestTimeIndex(JSONArray timeArray, String currentTime) {
            // 最も近い時間を見つけるロジック
            int closestIndex = 0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                Date current = sdf.parse(currentTime);
                long currentTimestamp = current.getTime();

                long closestDifference = Long.MAX_VALUE;

                for (int i = 0; i < timeArray.length(); i++) {
                    String timeString = timeArray.getString(i);
                    Date time = sdf.parse(timeString);
                    long timeTimestamp = time.getTime();

                    long difference = Math.abs(currentTimestamp - timeTimestamp);

                    if (difference < closestDifference) {
                        closestDifference = difference;
                        closestIndex = i;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return closestIndex;
        }
    }

    // APIからデータを取得するメソッド
    private JSONObject fetchDataFromAPI() {
        try {
            String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=35.7&longitude=139.6875&hourly=temperature_2m,soil_temperature_0cm&timezone=Asia%2FTokyo&forecast_days=1";
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // JSONデータを解析してJSONObjectに変換
                return new JSONObject(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onResume() {
        super.onResume();
        // ここでセンサーリスナーを登録する必要はありません
        // ボタンがクリックされたときにセンサーリスナーを登録します
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

            double magnitude = Math.sqrt(x * x + y * y);

            // 歩数の単純なカウントを行う例
            if (magnitude > 13 && isCounting && !isWalking) {
                stepCount++;
                isWalking = true;
            } else if (magnitude < 10) {
                isWalking = false;
            }

            // 歩数をUIに表示
            stepCountTextView.setText("現在: " + stepCount + "歩");
            if (stepCount >= 20 && mission1_judgment < 1) {
                    Get_Point_Button1.setEnabled(true);

            }if (stepCount >= 40 && mission2_judgment < 1) {
                Get_Point_Button2.setEnabled(true);
            }
            Get_Point_Button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mission1_TextView.setText("　　30ポイント 獲得　");
                    mission1_judgment = mission1_judgment + 1;

                    DatabaseReference userPointsRef = userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points");
                    userPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // データがHashMap型であるか確認
                                if (dataSnapshot.getValue() instanceof Long) {
                                    // データをLong型に変換
                                    Long userPoints = dataSnapshot.getValue(Long.class);

                                    // +30して更新
                                    userPointsRef.setValue(userPoints + 30);
                                    Toast.makeText(requireContext(), "30ポイント獲得しました！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // エラー処理
                        }
                    });
                    Get_Point_Button1.setText("獲得済");
                    Get_Point_Button1.setEnabled(false);
                }
            });

            Get_Point_Button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mission2_TextView.setText("　　50ポイント 獲得　");
                    mission2_judgment = mission2_judgment + 1;

                    DatabaseReference userPointsRef = userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points");
                    userPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // データがHashMap型であるか確認
                                if (dataSnapshot.getValue() instanceof Long) {
                                    // データをLong型に変換
                                    Long userPoints = dataSnapshot.getValue(Long.class);

                                    // +50して更新
                                    userPointsRef.setValue(userPoints + 50);
                                    Toast.makeText(requireContext(), "50ポイント獲得しました！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // エラー処理
                        }
                    });
                    Get_Point_Button2.setText("獲得済");
                    Get_Point_Button2.setEnabled(false);
                }
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // 加速度センサーの精度変更の処理
        }
    };

    public void startCount() {
        if (!isCounting) {
            isCounting = true;
            // 歩数カウントを開始するためのコードをここに追加
            // クリックされたときにセンサーリスナーを登録
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}

