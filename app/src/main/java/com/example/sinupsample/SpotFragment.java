package com.example.sinupsample;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpotFragment extends Fragment {

    private ImageButton SpotImage_Button;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private List<Spot> spotList;

    public SpotFragment() {
        // 必要な空の公開コンストラクタ
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot, container, false);
        SpotImage_Button = view.findViewById(R.id.Spotadd_Button);
        listView = view.findViewById(R.id.listView);
        searchView = view.findViewById(R.id.Search_Text);

        // ArrayAdapterの初期化
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);

        // ListViewにAdapterをセット
        listView.setAdapter(adapter);

        SpotImage_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SpotPostFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Firebaseからデータを取得してListViewに表示するメソッド
        loadSpotDataFromFirebase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 対応するSpotオブジェクトを取得
                Spot selectedSpot = spotList.get(position);

                // Spotの住所を取得して緯度経度に変換
                if (selectedSpot != null) {
                    String address = selectedSpot.getAddress();
                    convertAddressToLatLng(address);
                }
            }
        });

        return view;
    }

    private void convertAddressToLatLng(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address resultAddress = addressList.get(0);
                double ido = resultAddress.getLatitude();
                double keido = resultAddress.getLongitude();


                // MapsFragmentに遷移
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, MapsFragment.newInstance(ido, keido));
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(requireContext(), "緯度経度を取得できませんでした", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSpotDataFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("spot");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                spotList = new ArrayList<>();  // spotList を初期化

                for (DataSnapshot spotSnapshot : dataSnapshot.getChildren()) {
                    String spotName = spotSnapshot.child("スポット名").getValue(String.class);
                    String address = spotSnapshot.child("住所").getValue(String.class);
                    String details = spotSnapshot.child("詳細").getValue(String.class);

                    if (spotName != null && address != null && details != null) {
                        Spot spot = new Spot(spotName, address, details);
                        spotList.add(spot);
                    }
                }

                displaySpotList(spotList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラーが発生した場合の処理
            }
        });

        listView.setTextFilterEnabled(true);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    // 入力テキストに変更があったとき
                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (s.equals("")) {
                            listView.clearTextFilter();
                        } else {
                            listView.setFilterText(s);
                        }
                        return false;
                    }

                    // 検索ボタンを押したとき
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }
                });
    }

    private void displaySpotList(List<Spot> spotList) {
        for (Spot spot : spotList) {
            adapter.add("スポット名: " + spot.getSpotName() +
                    "\n住所: " + spot.getAddress() +
                    "\n詳細: " + spot.getDetails());
        }
    }
}
