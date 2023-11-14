package com.example.sinupsample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends Fragment {

    private WebView webView;

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        webView = view.findViewById(R.id.webview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // WebViewの設定
        webView.getSettings().setJavaScriptEnabled(true); // JavaScriptを有効にする（必要に応じて）
        webView.setWebViewClient(new WebViewClient()); // WebView内でリンクがクリックされたときに新しいウィンドウを開かずにWebView内で表示

        // WebViewに表示するURLを指定
        String url = "http://54.174.192.202/index.php";
//        // 表示したいウェブサイトのURLを設定
        webView.loadUrl(url);
    }
}
