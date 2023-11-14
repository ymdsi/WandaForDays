package com.example.sinupsample;

        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AutoCompleteTextView;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import androidx.annotation.NonNull;
        import androidx.core.app.ActivityCompat;
        import androidx.fragment.app.Fragment;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.android.libraries.places.api.Places;
        import com.google.android.libraries.places.api.model.AutocompletePrediction;
        import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
        import com.google.android.libraries.places.api.model.RectangularBounds;
        import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
        import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
        import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
        import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
        import com.google.android.libraries.places.api.net.PlacesClient;

        import java.util.Arrays;
        import java.util.List;

public class MapsearchFragment extends Fragment {

    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView searchResultTextView;
    private AutoCompleteTextView searchTextView;
    private ImageButton searchButton;
    private ProgressBar progressBar;
    private TextView[] searchResultTextViewArray;
    //    MapsFragmentに渡す緯度経度
    private double ido;
    private double keido;

    public MapsearchFragment() {
        // 必要な空の公開コンストラクタ
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapsearch, container, false);

        // Places APIと位置情報を初期化
        Places.initialize(requireContext(), "AIzaSyCNGQofTU2TC4DZidAnGAZ26CsZPE3UBTA");
        placesClient = Places.createClient(requireContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // UI要素を見つける
        searchTextView = view.findViewById(R.id.Search_Text);
        searchButton = view.findViewById(R.id.search_button);
        searchResultTextView = view.findViewById(R.id.searchResultTextView);
        progressBar = view.findViewById(R.id.progressBar);

        searchResultTextViewArray = new TextView[5];
        searchResultTextViewArray[0] = searchResultTextView;
        searchResultTextViewArray[1] = view.findViewById(R.id.searchResultTextView1);
        searchResultTextViewArray[2] = view.findViewById(R.id.searchResultTextView2);
        searchResultTextViewArray[3] = view.findViewById(R.id.searchResultTextView3);
        searchResultTextViewArray[4] = view.findViewById(R.id.searchResultTextView4);

        // 検索ボタンにクリックリスナーを設定
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 検索バーのテキストを取得
                String searchTerm = searchTextView.getText().toString();

                showProgressBar();
                // 現在位置を取得して近くの場所を検索
                getCurrentLocationAndSearchPlaces(searchTerm);
            }
        });

        return view;
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getCurrentLocationAndSearchPlaces(String searchTerm) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()

                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // 現在位置から近くの場所を検索
                                searchNearbyPlaces(searchTerm, location.getLatitude(), location.getLongitude());
                            }
                        }
                    });
        } else {
            // 位置情報のパーミッションがない場合は要求
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private void searchNearbyPlaces(String searchTerm, double latitude, double longitude) {
        // 検索範囲を指定（任意）
        double okayamaLatitude = 34.6551;
        double okayamaLongitude = 133.9195;

        // 岡山県全体を含む範囲を指定
        LatLngBounds okayamaBounds = new LatLngBounds(
                new LatLng(34.6550, 133.9194),
                new LatLng(34.6552, 133.9196)
        );

        RectangularBounds okayamaRectangularBounds = RectangularBounds.newInstance(okayamaBounds);

        // キーワードを含む検索クエリの作成
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(searchTerm) // 任意のキーワード
                .setLocationBias(okayamaRectangularBounds)
                .build();
        // Places APIにリクエストを送信
        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        autocompletePredictions.addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                // 検索結果を表示
                displayAutocompleteResults(response.getAutocompletePredictions());

                hideProgressBar();
            }
        });
    }

    private void displayAutocompleteResults(List<AutocompletePrediction> predictions) {
        StringBuilder resultText = new StringBuilder();

        // 最大で10件の検索結果を表示

        for (int i = 0; i < Math.min(predictions.size(), searchResultTextViewArray.length); i++) {
            AutocompletePrediction prediction = predictions.get(i);
            String placeName = prediction.getPrimaryText(null).toString();
            String placeAddress = prediction.getSecondaryText(null).toString();

            if (placeAddress.contains("日本、岡山")) {
                searchResultTextViewArray[i].setText("建物名: " + placeName + "\n住所: " + placeAddress + "\n\n");
            }
        }

        // 例えば、searchResultTextViewに表示する場合
        searchResultTextView.setText(resultText.toString());


    }
}
