package com.example.sinupsample;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeFragment extends Fragment {

    private TextView temperatureTextView;
    private TextView soilTemperatureTextView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        temperatureTextView = view.findViewById(R.id.temperatureTextView);
        soilTemperatureTextView = view.findViewById(R.id.soilTemperatureTextView);

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

                    // 最新のデータを取得
                    double temperature = temperatureArray.getDouble(temperatureArray.length() - 1);
                    double soilTemperature = soilTemperatureArray.getDouble(soilTemperatureArray.length() - 1);

                    // テキストビューにデータを表示
                    temperatureTextView.setText("気温: " + temperature + "°C");
                    soilTemperatureTextView.setText("地温: " + soilTemperature + "°C");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "データの解析に失敗しました", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "データの取得に失敗しました", Toast.LENGTH_SHORT).show();
            }
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