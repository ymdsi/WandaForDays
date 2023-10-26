package com.example.sinupsample;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AsyncNetworkTask extends AsyncTask<String, Integer, String> {
    private WeakReference<TextView> txtResult;
    private WeakReference<ProgressBar> progress;
    private WeakReference<ImageView> imageView;
    private int imageResource = R.drawable.image1;

    AsyncNetworkTask(Context context) {
        super();
        HomeActivity activity = (HomeActivity) context;
        txtResult = new WeakReference<>(activity.findViewById(R.id.txtResult));
        progress = new WeakReference<>(activity.findViewById(R.id.progress));
        imageView = new WeakReference<>(activity.findViewById(R.id.imageView));
    }

    @Override
    protected String doInBackground(String... params) {
        publishProgress(30);
        SystemClock.sleep(3000);
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    con.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(100);

        try {
            // JSONデータの解析と最も近い時間の気温と地温の取得
            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONObject hourlyData = jsonObject.getJSONObject("hourly");
            JSONArray timeArray = hourlyData.getJSONArray("time");
            JSONArray temperatureArray = hourlyData.getJSONArray("temperature_2m");
            JSONArray soilTemperatureArray = hourlyData.getJSONArray("soil_temperature_0cm");

            // 現在の時刻を取得
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String currentDateTime = dateFormat.format(new Date());

            // 最も近い時間に対応するデータを見つける
            int closestIndex = -1;
            long minDifference = Long.MAX_VALUE;
            for (int i = 0; i < timeArray.length(); i++) {
                String time = timeArray.getString(i);
                Date dataTime = dateFormat.parse(time);
                Date currentTime = dateFormat.parse(currentDateTime);
                long timeDifference = Math.abs(dataTime.getTime() - currentTime.getTime());
                if (timeDifference < minDifference) {
                    minDifference = timeDifference;
                    closestIndex = i;
                }
            }

            if (closestIndex != -1) {
                // 最も近い時間に対応する気温と路面温度データを取得
                double closestTemperature = temperatureArray.getDouble(closestIndex);
                double closestSoilTemperature = soilTemperatureArray.getDouble(closestIndex);


                // ImageViewを更新
                if (closestSoilTemperature <= 25) {
                    imageResource = R.drawable.image1;
                } else if (closestSoilTemperature <= 35) {
                    imageResource = R.drawable.image2;
                } else if (closestSoilTemperature <= 43) {
                    imageResource = R.drawable.image3;
                } else {
                    imageResource = R.drawable.image4;
                }

                // 取得したデータをTextViewに表示
                String temperatureText = String.format("気温: %.1f°C", closestTemperature);
                String soilTemperatureText = String.format("路面温度: %.1f°C", closestSoilTemperature);
                String displayText = temperatureText + "\n" + soilTemperatureText;

                return displayText;
            } else {
                return "データなし";
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return "データなし";
        }
    }

    @Override
    protected void onPreExecute() {
        progress.get().setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progress.get().setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        txtResult.get().setText(result);
        progress.get().setVisibility(ProgressBar.GONE);
        imageView.get().setImageResource(imageResource);
        imageView.get().setVisibility(View.VISIBLE); // ImageViewを表示する
    }


    @Override
    protected void onCancelled() {
        txtResult.get().setText("キャンセルされました。");
        progress.get().setVisibility(ProgressBar.GONE);
    }
}