package com.example.sinupsample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;

public class folderFragment extends Fragment {

    private static final int REQUEST_CODE = 1;
    private String folderName = "MyPhotoFolder";

    private ImageView photoImageView;

    public folderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        FloatingActionButton addNewMemoButton = view.findViewById(R.id.add_new_memo);
        photoImageView = view.findViewById(R.id.photoImageView);

        addNewMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }

    private void openGallery() {
        // Create a new folder for photos
        File folder = new File(Environment.getExternalStorageDirectory(), folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Open the gallery to select a photo
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected photo's URI
            Uri selectedImageUri = data.getData();

            // Display the selected photo in the ImageView
            photoImageView.setImageURI(selectedImageUri);

            // You can also save the photo to the created folder here if needed
            try {
                File folder = new File(Environment.getExternalStorageDirectory(), folderName);
                String filename = "photo_" + System.currentTimeMillis() + ".jpg";
                File photoFile = new File(folder, filename);

                InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                OutputStream outputStream = new FileOutputStream(photoFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();

                Toast.makeText(getActivity(), "Photo saved in " + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to save photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
