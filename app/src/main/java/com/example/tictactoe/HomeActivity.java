package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityHomeBinding;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private SoundPool soundPool;
    private int clickSoundId;
    private TextView welcomeText;
    private ImageView profileImage;
    private MediaPlayer backgroundMusicPlayer;
    private boolean isMusicPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

        // Apply saved background theme and load player profile
        applySavedBackground();
        loadPlayerProfile();
        
        // Start background music if enabled
        startBackgroundMusic();

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
            showSettingsMenu();
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

    private void showSettingsMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_settings_menu, null);
        builder.setView(dialogView);

        Button editProfileBtn = dialogView.findViewById(R.id.editProfileBtn);
        Button changeLayoutBtn = dialogView.findViewById(R.id.changeLayoutBtn);
        Button playerIdBtn = dialogView.findViewById(R.id.playerIdBtn);
        Button backgroundMusicBtn = dialogView.findViewById(R.id.backgroundMusicBtn);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        AlertDialog dialog = builder.create();

        editProfileBtn.setOnClickListener(v -> {
            playClickSound();
            dialog.dismiss();
            Intent intent = new Intent(HomeActivity.this, ProfileSetupActivity.class);
            intent.putExtra("editMode", true);
            startActivity(intent);
        });

        changeLayoutBtn.setOnClickListener(v -> {
            playClickSound();
            dialog.dismiss();
            showLayoutOptions();
        });

        playerIdBtn.setOnClickListener(v -> {
            playClickSound();
            dialog.dismiss();
            showPlayerId();
        });

        backgroundMusicBtn.setOnClickListener(v -> {
            playClickSound();
            dialog.dismiss();
            toggleBackgroundMusic();
        });

        cancelBtn.setOnClickListener(v -> {
            playClickSound();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showLayoutOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Complete Theme");

        String[] themes = {
            "Seascape - 🌊 Ocean Adventure",
            "Forest - 🌲 Nature Escape", 
            "Desert - 🏜️ Sand Dunes",
            "Space - 🌌 Cosmic Journey",
            "Sunset - 🌅 Golden Hour",
            "Ocean - 🐠 Deep Blue"
        };

        builder.setSingleChoiceItems(themes, -1, null);
        builder.setPositiveButton("Apply Theme", (dialog, which) -> {
            int selectedPosition = ((android.widget.ListView) ((android.app.AlertDialog) dialog).getListView()).getCheckedItemPosition();
            String selectedTheme = getThemeValue(selectedPosition);
            applyCompleteTheme(selectedTheme);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private String getThemeValue(int position) {
        switch (position) {
            case 0: return "seascape";
            case 1: return "forest";
            case 2: return "desert";
            case 3: return "space";
            case 4: return "sunset";
            case 5: return "ocean";
            default: return "seascape";
        }
    }

    private void applyCompleteTheme(String theme) {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Apply complete theme package
        switch (theme) {
            case "seascape":
                editor.putString("backgroundTheme", "seascape");
                editor.putString("symbolSet", "classic");
                editor.putString("themeColors", "ocean");
                break;
            case "forest":
                editor.putString("backgroundTheme", "forest");
                editor.putString("symbolSet", "animals");
                editor.putString("themeColors", "forest");
                break;
            case "desert":
                editor.putString("backgroundTheme", "desert");
                editor.putString("symbolSet", "sports");
                editor.putString("themeColors", "desert");
                break;
            case "space":
                editor.putString("backgroundTheme", "space");
                editor.putString("symbolSet", "letters");
                editor.putString("themeColors", "space");
                break;
            case "sunset":
                editor.putString("backgroundTheme", "sunset");
                editor.putString("symbolSet", "hearts");
                editor.putString("themeColors", "sunset");
                break;
            case "ocean":
                editor.putString("backgroundTheme", "ocean");
                editor.putString("symbolSet", "numbers");
                editor.putString("themeColors", "ocean");
                break;
        }
        
        editor.putString("gameLayout", theme); // Keep backward compatibility
        editor.apply();
        
        // Apply immediately
        updateHomeBackground(theme);
        updateHomeThemeColors(theme);
        
        // Show confirmation
        showThemeConfirmation(theme);
    }

    private void showThemeConfirmation(String theme) {
        String themeName = getThemeDisplayName(theme);
        String[] symbols = getCurrentThemeSymbols(theme);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Theme Applied!");
        builder.setMessage("Your " + themeName + " theme has been applied!\n\n" +
                "Background: " + themeName + "\n" +
                "Symbols: " + symbols[0] + " " + symbols[1] + "\n\n" +
                "Changes will take effect in your next game.");
        builder.setPositiveButton("Awesome!", null);
        builder.show();
    }

    private String getThemeDisplayName(String theme) {
        switch (theme) {
            case "seascape": return "Seascape";
            case "forest": return "Forest";
            case "desert": return "Desert";
            case "space": return "Space";
            case "sunset": return "Sunset";
            case "ocean": return "Ocean";
            default: return theme;
        }
    }

    private String[] getCurrentThemeSymbols(String theme) {
        switch (theme) {
            case "seascape": return new String[]{"★", "✿"};
            case "forest": return new String[]{"🐱", "🐶"};
            case "desert": return new String[]{"⚽", "🏀"};
            case "space": return new String[]{"X", "O"};
            case "sunset": return new String[]{"♥", "♠"};
            case "ocean": return new String[]{"1", "2"};
            default: return new String[]{"★", "✿"};
        }
    }

    private void updateHomeThemeColors(String theme) {
        // Update button colors based on theme
        int buttonColor = getThemeButtonColor(theme);
        
        if (binding.friendButton != null) {
            binding.friendButton.setBackgroundColor(buttonColor);
        }
        if (binding.computerButton != null) {
            binding.computerButton.setBackgroundColor(buttonColor);
        }
        if (binding.longModeButton != null) {
            binding.longModeButton.setBackgroundColor(getThemeSecondaryButtonColor(theme));
        }
    }

    private int getThemeButtonColor(String theme) {
        switch (theme) {
            case "seascape": return 0xFFFF8C42; // Orange
            case "forest": return 0xFF4CAF50; // Green
            case "desert": return 0xFFF4A460; // Sandy
            case "space": return 0xFF9370DB; // Purple
            case "sunset": return 0xFFFF6B35; // Sunset orange
            case "ocean": return 0xFF0099CC; // Ocean blue
            default: return 0xFFFF8C42;
        }
    }

    private int getThemeSecondaryButtonColor(String theme) {
        switch (theme) {
            case "seascape": return 0xFF9370DB; // Purple
            case "forest": return 0xFF8B4513; // Brown
            case "desert": return 0xFFDEB887; // Burlywood
            case "space": return 0xFF191970; // Midnight blue
            case "sunset": return 0xFFC73E1D; // Deep orange
            case "ocean": return 0xFF006994; // Dark ocean blue
            default: return 0xFF9370DB;
        }
    }

    private void showSymbolOptions() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String currentSymbols = prefs.getString("symbolSet", "classic");

        String[] symbolSets = {"Classic (★ ✿)", "Hearts (♥ ♠)", "Numbers (1 2)", "Letters (X O)", "Animals (🐱 🐶)", "Sports (⚽ 🏀)"};
        String[] symbolValues = {"classic", "hearts", "numbers", "letters", "animals", "sports"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Symbol Set");
        builder.setSingleChoiceItems(symbolSets, 
            java.util.Arrays.asList(symbolValues).indexOf(currentSymbols), null);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            int selectedPosition = ((android.widget.ListView) ((android.app.AlertDialog) dialog).getListView()).getCheckedItemPosition();
            String selectedSymbols = symbolValues[selectedPosition];
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("symbolSet", selectedSymbols);
            editor.apply();
            
            // The change will apply automatically in the next game
            showSymbolChangeConfirmation(selectedSymbols);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showBackgroundOptions() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String currentBackground = prefs.getString("backgroundTheme", "seascape");

        String[] backgrounds = {"Seascape", "Forest", "Desert", "Space", "Sunset", "Ocean"};
        String[] backgroundValues = {"seascape", "forest", "desert", "space", "sunset", "ocean"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Background Theme");
        builder.setSingleChoiceItems(backgrounds, 
            java.util.Arrays.asList(backgroundValues).indexOf(currentBackground), null);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            int selectedPosition = ((android.widget.ListView) ((android.app.AlertDialog) dialog).getListView()).getCheckedItemPosition();
            String selectedBackground = backgroundValues[selectedPosition];
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("backgroundTheme", selectedBackground);
            editor.putString("gameLayout", selectedBackground); // Keep backward compatibility
            editor.apply();
            
            // Update home screen background immediately
            updateHomeBackground(selectedBackground);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showSymbolChangeConfirmation(String symbolSet) {
        String displayName = getSymbolDisplayName(symbolSet);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbols Changed!");
        builder.setMessage("Your symbols have been updated to: " + displayName + "\n\nThese will appear in your next game.");
        builder.setPositiveButton("Great!", null);
        builder.show();
    }

    private String getSymbolDisplayName(String symbolSet) {
        switch (symbolSet) {
            case "classic": return "Classic (★ ✿)";
            case "hearts": return "Hearts (♥ ♠)";
            case "numbers": return "Numbers (1 2)";
            case "letters": return "Letters (X O)";
            case "animals": return "Animals (🐱 🐶)";
            case "sports": return "Sports (⚽ 🏀)";
            default: return symbolSet;
        }
    }

    private void updateHomeBackground(String background) {
        // Update home screen background
        int backgroundRes = getBackgroundResource(background);
        if (backgroundRes != 0) {
            binding.getRoot().setBackgroundResource(backgroundRes);
        }
    }

    private void applySavedBackground() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String background = prefs.getString("backgroundTheme", "seascape");
        updateHomeBackground(background);
    }

    private int getBackgroundResource(String background) {
        switch (background) {
            case "seascape": return R.drawable.main_seascape_background;
            case "forest": return R.drawable.main_forest_background;
            case "desert": return R.drawable.main_desert_background;
            case "space": return R.drawable.main_space_background;
            case "sunset": return R.drawable.main_sunset_background;
            case "ocean": return R.drawable.main_ocean_background;
            default: return R.drawable.main_seascape_background;
        }
    }

    private void showPlayerId() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String playerId = prefs.getString("playerId", "");
        
        // Generate player ID if it doesn't exist
        if (playerId.isEmpty()) {
            playerId = "PLAYER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("playerId", playerId);
            editor.apply();
        }

        String playerName = prefs.getString("playerName", "Player");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Player Information");
        
        String message = "Player Name: " + playerName + "\n\nPlayer ID: " + playerId + "\n\nThis ID uniquely identifies your profile in the game.";
        builder.setMessage(message);
        
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void startBackgroundMusic() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        boolean musicEnabled = prefs.getBoolean("backgroundMusicEnabled", false);
        
        if (musicEnabled && !isMusicPlaying) {
            try {
                backgroundMusicPlayer = MediaPlayer.create(this, R.raw.background_music);
                if (backgroundMusicPlayer != null) {
                    backgroundMusicPlayer.setLooping(true);
                    backgroundMusicPlayer.setVolume(0.3f, 0.3f);
                    backgroundMusicPlayer.start();
                    isMusicPlaying = true;
                } else {
                    android.widget.Toast.makeText(this, "Unable to load background music", android.widget.Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                android.widget.Toast.makeText(this, "Unable to start background music", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null && isMusicPlaying) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
            isMusicPlaying = false;
        }
    }

    private void toggleBackgroundMusic() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        boolean musicEnabled = prefs.getBoolean("backgroundMusicEnabled", false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Background Music");
        
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_background_music, null);
        Switch musicSwitch = dialogView.findViewById(R.id.musicSwitch);
        
        musicSwitch.setChecked(musicEnabled);
        
        builder.setView(dialogView);
        
        builder.setPositiveButton("Save", (dialog, which) -> {
            boolean newState = musicSwitch.isChecked();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("backgroundMusicEnabled", newState);
            editor.apply();
            
            if (newState && !isMusicPlaying) {
                startBackgroundMusic();
            } else if (!newState && isMusicPlaying) {
                stopBackgroundMusic();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
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
        stopBackgroundMusic();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        boolean musicEnabled = prefs.getBoolean("backgroundMusicEnabled", false);
        if (musicEnabled && backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        boolean musicEnabled = prefs.getBoolean("backgroundMusicEnabled", false);
        if (musicEnabled && backgroundMusicPlayer != null && !backgroundMusicPlayer.isPlaying()) {
            try {
                backgroundMusicPlayer.start();
                isMusicPlaying = true;
            } catch (Exception e) {
                // Handle case where media player was released
                startBackgroundMusic();
            }
        }
    }
}
