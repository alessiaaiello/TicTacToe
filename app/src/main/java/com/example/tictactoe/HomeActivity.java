package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityHomeBinding;
import java.io.File;
import java.io.FileInputStream;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private SoundPool soundPool;
    private int clickSoundId;
    private TextView welcomeText;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

        // Load and display player profile
        loadPlayerProfile();

        // Play with Friend Button
        binding.friendButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "friend");
            startActivity(intent);
        });

        // Play with Computer Button
        binding.computerButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(HomeActivity.this, DifficultyActivity.class);
            startActivity(intent);
        });

        // Long Mode Button
        binding.longModeButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(HomeActivity.this, LongModeOpponentActivity.class);
            startActivity(intent);
        });

        // Settings Button
        binding.settingsButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(HomeActivity.this, ProfileSetupActivity.class);
            intent.putExtra("editMode", true);
            startActivity(intent);
        });
    }

    private void loadPlayerProfile() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String playerName = prefs.getString("playerName", "");
        String photoPath = prefs.getString("playerPhotoPath", "");
        
        // Debug: log the player name
        android.util.Log.d("HomeActivity", "Player name: '" + playerName + "'");
        android.util.Log.d("HomeActivity", "Photo path: '" + photoPath + "'");
        
        // Add welcome text with player name
        if (!playerName.isEmpty()) {
            welcomeText = new TextView(this);
            welcomeText.setText("Welcome, " + playerName + "!");
            welcomeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            welcomeText.setTextColor(0xFFFFFFFF);
            welcomeText.setShadowLayer(4, 2, 2, 0xFF000000);
            
            // Add welcome text to the main layout
            LinearLayout mainContent = (LinearLayout) ((FrameLayout) binding.getRoot()).getChildAt(1);
            mainContent.addView(welcomeText, 0);
            
            // Add some margin below welcome text
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) welcomeText.getLayoutParams();
            params.setMargins(0, 0, 0, 20);
            welcomeText.setLayoutParams(params);
        }
        
        // Load and display profile image if exists
        if (!photoPath.isEmpty()) {
            try {
                File file = new File(getFilesDir(), photoPath);
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    
                    profileImage = new ImageView(this);
                    profileImage.setImageBitmap(bitmap);
                    profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    
                    // Set size and style for profile image
                    int size = (int) (80 * getResources().getDisplayMetrics().density);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
                    params.setMargins(16, 16, 16, 0);
                    params.gravity = android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL;
                    profileImage.setLayoutParams(params);
                    profileImage.setElevation(8);
                    profileImage.setBackgroundResource(android.R.drawable.dialog_frame);
                    
                    ((FrameLayout) binding.getRoot()).addView(profileImage);
                    
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
