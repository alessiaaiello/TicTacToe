package com.example.tictactoe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.tictactoe.databinding.ActivityPhotoUploadBinding;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PhotoUploadActivity extends AppCompatActivity {
    private ActivityPhotoUploadBinding binding;
    private SoundPool soundPool;
    private int clickSoundId;
    private Bitmap selectedPhotoBitmap = null;
    private String playerName = "";
    private String playerDescription = "";
    private String photoSource = "";

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get player info from intent
        Intent intent = getIntent();
        playerName = intent.getStringExtra("playerName");
        playerDescription = intent.getStringExtra("playerDescription");
        photoSource = intent.getStringExtra("photoSource");

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        loadPhotoFromUri(imageUri);
                    }
                }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            selectedPhotoBitmap = (Bitmap) extras.get("data");
                            binding.photoPreview.setImageBitmap(selectedPhotoBitmap);
                            binding.savePhotoButton.setEnabled(true);
                        }
                    }
                }
        );

        // Permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    }
                }
        );

        // Gallery button
        binding.galleryButton.setOnClickListener(v -> {
            playClickSound();
            launchGallery();
        });

        // Camera button
        binding.cameraButton.setOnClickListener(v -> {
            playClickSound();
            requestCameraPermission();
        });

        // Save button
        binding.savePhotoButton.setOnClickListener(v -> {
            playClickSound();
            savePhoto();
        });

        // Back button
        binding.backButton.setOnClickListener(v -> {
            playClickSound();
            finish();
        });

        // Auto-launch camera or gallery based on source
        if ("camera".equals(photoSource)) {
            requestCameraPermission();
        } else if ("gallery".equals(photoSource)) {
            launchGallery();
        }
    }

    private void playClickSound() {
        soundPool.play(clickSoundId, 0.8f, 0.8f, 1, 0, 1.0f);
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void loadPhotoFromUri(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            selectedPhotoBitmap = BitmapFactory.decodeStream(inputStream);
            binding.photoPreview.setImageBitmap(selectedPhotoBitmap);
            binding.savePhotoButton.setEnabled(true);
            if (inputStream != null) inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePhoto() {
        if (selectedPhotoBitmap == null) {
            Toast.makeText(this, "Please select a photo first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Save to internal storage with unique filename
            String fileName = "profile_photo_" + System.currentTimeMillis() + ".png";
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            selectedPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

            // Save player profile data
            SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("profileSetupComplete", true);
            editor.putString("playerName", playerName);
            editor.putString("playerDescription", playerDescription);
            editor.putString("playerPhotoPath", fileName);
            editor.apply();

            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            
            // Start home activity
            Intent homeIntent = new Intent(PhotoUploadActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        super.onDestroy();
    }
}
