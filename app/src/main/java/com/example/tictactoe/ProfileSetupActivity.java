package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.tictactoe.databinding.ActivityProfileSetupBinding;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.Manifest;
import android.content.pm.PackageManager;

public class ProfileSetupActivity extends AppCompatActivity {
    private ActivityProfileSetupBinding binding;
    private SoundPool soundPool;
    private int clickSoundId;
    private EditText playerNameInput;
    private EditText playerDescriptionInput;
    private ImageView profileImageView;
    private boolean isEditMode = false;
    private Bitmap selectedPhotoBitmap = null;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

        // Get input fields
        playerNameInput = binding.playerNameInput;
        playerDescriptionInput = binding.playerDescriptionInput;
        profileImageView = findViewById(R.id.profileImageView);

        // Check if this is edit mode
        isEditMode = getIntent().getBooleanExtra("editMode", false);
        
        if (isEditMode) {
            loadExistingProfile();
        }

        // Initialize gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        loadPhotoFromUri(imageUri);
                    }
                }
        );

        // Initialize permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchGallery();
                    } else {
                        Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Gallery Button
        binding.uploadPhotoButton.setOnClickListener(v -> {
            playClickSound();
            String name = playerNameInput.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your player name", Toast.LENGTH_SHORT).show();
                return;
            }
            requestGalleryPermission();
        });

        // Camera Button
        binding.cameraButton.setOnClickListener(v -> {
            playClickSound();
            String name = playerNameInput.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your player name", Toast.LENGTH_SHORT).show();
                return;
            }
            navigateToPhotoUpload("camera", name);
        });

        // Save Profile Button
        binding.saveProfileButton.setOnClickListener(v -> {
            playClickSound();
            String name = playerNameInput.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your player name", Toast.LENGTH_SHORT).show();
                return;
            }
            String description = playerDescriptionInput.getText().toString().trim();
            
            // Save photo if selected
            String photoPath = null;
            if (selectedPhotoBitmap != null) {
                photoPath = savePhotoToInternalStorage(selectedPhotoBitmap);
            }
            
            savePlayerProfile(name, description, photoPath);
        });

        // Skip for Now Button
        binding.skipButton.setOnClickListener(v -> {
            playClickSound();
            if (isEditMode) {
                finish(); // Just close if editing
            } else {
                markProfileSetupComplete();
                startMainApp();
            }
        });
    }

    private void navigateToPhotoUpload(String source, String playerName) {
        Intent intent = new Intent(ProfileSetupActivity.this, PhotoUploadActivity.class);
        intent.putExtra("playerName", playerName);
        intent.putExtra("playerDescription", playerDescriptionInput.getText().toString().trim());
        intent.putExtra("photoSource", source);
        startActivity(intent);
        finish();
    }

    private void savePlayerProfile(String name, String description, String photoPath) {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("profileSetupComplete", true);
        editor.putString("playerName", name);
        editor.putString("playerDescription", description);
        if (photoPath != null) {
            editor.putString("playerPhotoPath", photoPath);
        }
        editor.apply();
        
        if (isEditMode) {
            finish(); // Just close if editing
        } else {
            startMainApp(); // Start main app if first time setup
        }
    }

    private void loadExistingProfile() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String name = prefs.getString("playerName", "");
        String description = prefs.getString("playerDescription", "");
        String photoPath = prefs.getString("playerPhotoPath", "");
        
        playerNameInput.setText(name);
        playerDescriptionInput.setText(description);
        
        // Load profile photo if exists
        if (!photoPath.isEmpty()) {
            try {
                File file = new File(getFilesDir(), photoPath);
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    selectedPhotoBitmap = BitmapFactory.decodeStream(fis);
                    if (profileImageView != null && selectedPhotoBitmap != null) {
                        profileImageView.setImageBitmap(selectedPhotoBitmap);
                        profileImageView.setVisibility(android.view.View.VISIBLE);
                        binding.uploadPhotoButton.setText("Change Photo");
                    }
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Clear corrupted photo path
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("playerPhotoPath");
                editor.apply();
            }
        }
    }

    private void markProfileSetupComplete() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        prefs.edit().putBoolean("profileSetupComplete", true).apply();
    }

    private void startMainApp() {
        Intent intent = new Intent(ProfileSetupActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestGalleryPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // Older versions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void loadPhotoFromUri(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            selectedPhotoBitmap = BitmapFactory.decodeStream(inputStream);
            if (profileImageView != null) {
                profileImageView.setImageBitmap(selectedPhotoBitmap);
                profileImageView.setVisibility(android.view.View.VISIBLE);
            }
            if (inputStream != null) inputStream.close();
            
            // Update button text to show photo selected
            binding.uploadPhotoButton.setText("Change Photo");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading photo", Toast.LENGTH_SHORT).show();
        }
    }

    private String savePhotoToInternalStorage(Bitmap bitmap) {
        try {
            String fileName = "profile_photo_" + System.currentTimeMillis() + ".png";
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playClickSound() {
        if (soundPool != null) {
            soundPool.play(clickSoundId, 0.5f, 0.5f, 1, 0, 1.0f);
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
