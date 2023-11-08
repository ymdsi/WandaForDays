package com.example.sinupsample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private String[] imagePaths;

    public ImageAdapter(Context context, String[] imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.length;
    }

    @Override
    public Object getItem(int position) {
        return imagePaths[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(425, 425)); // グリッド内の各セルのサイズを設定
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        String imagePath = imagePaths[position];

        // Firebase Storageから画像をダウンロードし、ImageViewに表示
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context)
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // エラーハンドリング
        });

        return imageView;
    }
}

