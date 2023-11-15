package com.example.sinupsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SpotAdapter extends ArrayAdapter<Spot> {

    public SpotAdapter(Context context, List<Spot> spots) {
        super(context, 0, spots);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Spot spot = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        TextView spotNameTextView = convertView.findViewById(R.id.spotNameTextView);
        TextView addressTextView = convertView.findViewById(R.id.addressTextView);
        TextView detailsTextView = convertView.findViewById(R.id.detailsTextView);

        if (spot != null) {
            spotNameTextView.setText("スポット名: " + spot.getSpotName());
            addressTextView.setText("住所: " + spot.getAddress());
            detailsTextView.setText("詳細: " + spot.getDetails());
        }

        return convertView;
    }
}
