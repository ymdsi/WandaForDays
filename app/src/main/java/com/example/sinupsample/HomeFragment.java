package com.example.sinupsample;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

    public HomeFragment() {
        // Required empty public constructor
    }


    // onCreateView メソッド内で view を宣言して初期化
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        temperatureTextView = view.findViewById(R.id.temperatureTextView);
        soilTemperatureTextView = view.findViewById(R.id.soilTemperatureTextView);
        imageView = view.findViewById(R.id.imageView); // ImageView を初期化

        // ネットワーク通信を非同期で実行
        new GetDataTask().execute();

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
//                    double soilTemperature = ;
                    int imageResource;

                    if (soilTemperature <= 25.0) {
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

}
