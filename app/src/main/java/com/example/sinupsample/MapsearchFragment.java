package com.example.sinupsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class MapsearchFragment extends Fragment {

    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView searchResultTextView;

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
        AutoCompleteTextView searchTextView = view.findViewById(R.id.Search_Text);
        ImageButton searchButton = view.findViewById(R.id.search_button);
        searchResultTextView = view.findViewById(R.id.searchResultTextView);

        // 検索ボタンにクリックリスナーを設定
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 現在位置を取得して近くのカフェを検索
                getCurrentLocationAndSearchCafe();
            }
        });

        return view;
    }

    private void getCurrentLocationAndSearchCafe() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // 現在位置から近くのカフェを検索
                                searchNearbyCafe(location.getLatitude(), location.getLongitude());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // パーミッションが許可された場合、処理を続行
                getCurrentLocationAndSearchCafe();
            } else {
                // パーミッションが拒否された場合、適切な対応を行う
                // 例: ユーザーに説明するダイアログを表示するなど
            }
        }
    }

    private void searchNearbyCafe(double latitude, double longitude) {
        // FindCurrentPlaceリクエストを構築
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG))
                .build();

        // FindCurrentPlaceリクエストを実行
        placesClient.findCurrentPlace(request)
                .addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onSuccess(FindCurrentPlaceResponse response) {
                        // カフェの検索結果を取得
                        List<PlaceLikelihood> likelihoods = response.getPlaceLikelihoods();

                        // 検索結果を表示
                        displaySearchResults(likelihoods);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 検索失敗時の処理
                        searchResultTextView.setText("検索に失敗しました");
                    }
                });
    }

    private void displaySearchResults(List<PlaceLikelihood> likelihoods) {
        // likelihoodsから必要な情報を取り出して表示
        for (PlaceLikelihood likelihood : likelihoods) {
            Place place = likelihood.getPlace();
            String name = place.getName();
            String address = place.getAddress();

            // ここで検索結果を表示する処理を追加

            // 例えば、searchResultTextViewに表示する場合
            searchResultTextView.setText("カフェ名: " + name + "\n住所: " + address);
        }
    }
}
