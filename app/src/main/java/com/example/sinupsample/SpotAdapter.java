package com.example.sinupsample;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SpotAdapter extends BaseAdapter implements Filterable {

    private List<Spot> spotList;
    private List<Spot> spotListFull; // 全ての要素を保持するためのリスト
    private LayoutInflater inflater;
    private Context context; // コンテキストを保持するフィールドを追加

    public SpotAdapter(Context context, List<Spot> spotList) {
        this.context = context; // コンストラクタでコンテキストを初期化
        this.spotList = spotList;
        this.spotListFull = new ArrayList<>(spotList); // 全要素をコピー
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return spotList.size();
    }

    @Override
    public Object getItem(int position) {
        return spotList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.custom_list_item, parent, false);

        TextView spotNameTextView = view.findViewById(R.id.spotNameTextView);
        TextView addressTextView = view.findViewById(R.id.addressTextView);
        TextView detailsTextView = view.findViewById(R.id.detailsTextView);
        ImageView photoImageView = view.findViewById(R.id.photoImageView);
        ImageView PRImageView = view.findViewById(R.id.PR_dog);

        Spot spot = spotList.get(position);

        spotNameTextView.setText("スポット名: " + spot.getSpotName());
        addressTextView.setText("住所: " + spot.getAddress());
        detailsTextView.setText("詳細: " + spot.getDetails());

        if (spot.getSponsa()) {
            PRImageView.setVisibility(View.VISIBLE);
        } else {
            PRImageView.setVisibility(View.GONE);
        }

        String photoUrl = spot.getPhotoUrl();

        if (photoUrl != null && !photoUrl.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(photoUrl);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                // ダウンロードURLを使ってGlideで画像を非同期に読み込む
                Glide.with(context)
                        .load(uri)
                        .into(photoImageView);
            }).addOnFailureListener(exception -> {
                Log.e("SpotAdapter", "画像の読み込みエラー", exception);
                // ダウンロードURLの取得に失敗した場合の処理
                exception.printStackTrace();
            });
        }

        return view;
    }


    static class ViewHolder {
        TextView spotNameTextView;
        TextView addressTextView;
        TextView detailsTextView;
        ImageView photoImageView;
        ImageView PRImageView;
    }

    @Override
    public Filter getFilter() {
        return spotFilter;
    }

    private Filter spotFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Spot> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(spotListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Spot spot : spotListFull) {
                    if (spot.getSpotName().toLowerCase().contains(filterPattern)
                            || spot.getAddress().toLowerCase().contains(filterPattern)
                            || spot.getDetails().toLowerCase().contains(filterPattern)) {
                        filteredList.add(spot);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            spotList.clear();
            spotList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}