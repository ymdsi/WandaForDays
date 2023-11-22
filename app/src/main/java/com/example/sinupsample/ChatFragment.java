package com.example.sinupsample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {

    private EditText editTextMessage;
    private Button sendBT;
    private TextView textView;
    private ProgressBar progressBar;
    private static final String GPT_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        sendBT = view.findViewById(R.id.button);
        editTextMessage = view.findViewById(R.id.edit_text);
        editTextMessage.setMovementMethod(new ScrollingMovementMethod());
        textView = view.findViewById(R.id.text_view);
        textView.setMovementMethod(new ScrollingMovementMethod());
        progressBar = view.findViewById(R.id.bar);
        progressBar.setVisibility(View.GONE);

        editTextMessage.getEditableText().clear();

        sendBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("回答を考えています...");
                sendBT.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                String queryText = editTextMessage.getText().toString() + "際の対処法を1個50字以内で5個";
                new HttpAsyncTask().execute(queryText);
            }
        });

        return view;
    }

    private class HttpAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS) // 接続のタイムアウトを15秒に設定
                    .readTimeout(120, TimeUnit.SECONDS) // 読み取りのタイムアウトを15秒に設定
                    .writeTimeout(10, TimeUnit.SECONDS) // 書き込みのタイムアウトを15秒に設定
                    .build();
            String answer = null;

            try {
                String queryText = params[0];
                String jsonInputString = "{\"model\": \"gpt-3.5-turbo\",\"messages\": [{\"role\": \"user\", \"content\": \"" + queryText + "\"}]}";

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(jsonInputString, JSON);

                Request request = new Request.Builder()
                        .url(GPT_ENDPOINT)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject jsonObj = new JSONObject(responseData);
                    JSONArray choices = jsonObj.getJSONArray("choices");
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    answer = message.getString("content");
                } else {
                    // エラー処理
                    answer = "エラーが発生しました。";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            // 回答を表示
            textView.setText(result);
            sendBT.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}