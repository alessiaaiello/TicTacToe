package com.example.tictactoe;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private SoundPool soundPool;
    private int clickSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

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
