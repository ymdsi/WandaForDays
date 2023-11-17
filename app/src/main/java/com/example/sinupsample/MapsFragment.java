package com.example.sinupsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 14f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // ImageButtonを取得
        ImageButton myImageButton = view.findViewById(R.id.back_button);

        // ImageButtonにクリックリスナーを設定
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SpotFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }


    public static MapsFragment newInstance(double ido, double keido, String spotName) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putDouble("緯度", ido);
        args.putDouble("経度", keido);
        args.putString("スポット名", spotName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // プラスマイナスボタンのクリックリスナーを設定
        ImageButton zoomInButton = view.findViewById(R.id.zoom_in_button);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }
        });

        ImageButton zoomOutButton = view.findViewById(R.id.zoom_out_button);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // チェック: 位置情報アクセスの許可
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 位置情報の取得とマーカーの設定
            mMap.setMyLocationEnabled(true);

            Bundle args = getArguments();
            if (args != null) {
                double latitude = args.getDouble("緯度", 0.0);
                double longitude = args.getDouble("経度", 0.0);
                String SpotName = args.getString("スポット名");

                // ピンを指すマーカーを追加
                LatLng location = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(location).title(SpotName));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
            } else {
                // 初期の位置情報 (Okayama)を設定
                double initialLatitude = 34.6551;
                double initialLongitude = 133.9195;
                LatLng initialLocation = new LatLng(initialLatitude, initialLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, DEFAULT_ZOOM));
            }
        } else {
            // パーミッションが許可されていない場合、パーミッションをリクエスト
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
                                        LatLng currentLocation = new LatLng(latitude, longitude);
                                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("現在位置"));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                                    }
                                }
                            });
                }
                return false;
            }
        });
    }
}
