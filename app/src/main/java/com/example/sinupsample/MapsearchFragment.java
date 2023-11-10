package com.example.sinupsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class MapsearchFragment extends Fragment {

    private static final String TAG = MapsearchFragment.class.getSimpleName();
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public MapsearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mapsearch, container, false);

        // ユーザーの許可を確認し、位置情報を取得する
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        return view;
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng currentLatLng = new LatLng(latitude, longitude);

                        // 取得した位置情報を使ってPlaces APIを呼び出す
                        searchNearbyPlaces(currentLatLng);
                    }
                });
    }

    private void searchNearbyPlaces(LatLng currentLatLng) {
        Places.initialize(requireContext(), "AIzaSyCNGQofTU2TC4DZidAnGAZ26CsZPE3UBTA");

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        String keyword = "犬が入れる建物";
        String location = String.format("%f,%f", currentLatLng.latitude, currentLatLng.longitude);
        String radius = "500"; // メートル

        FindAutocompletePredictionsRequest request =
                FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(keyword)
                        .setLocationBias(RectangularBounds.newInstance(
                                LatLngBounds.builder().include(currentLatLng).build()
                        ))
                        .build();

        PlacesClient placesClient = Places.createClient(requireContext());

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        Log.i(TAG, "Place Prediction: " + prediction.getPlaceId());
                        // 取得した情報を利用してUIに表示などを行う
                    }
                })
                .addOnFailureListener(exception -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        Toast.makeText(requireContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "位置情報の許可が必要です", Toast.LENGTH_SHORT).show();
            }
        }
    }
}