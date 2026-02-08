package com.example.tictactoe;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityDifficultyBinding;

public class DifficultyActivity extends AppCompatActivity {
    private ActivityDifficultyBinding binding;
    private SoundPool soundPool;
    private int clickSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDifficultyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

        // Easy Button
        binding.easyButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "computer");
            intent.putExtra("difficulty", "easy");
            startActivity(intent);
            finish();
        });

        // Medium Button
        binding.mediumButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "computer");
            intent.putExtra("difficulty", "medium");
            startActivity(intent);
            finish();
        });

        // Hard Button
        binding.hardButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "computer");
            intent.putExtra("difficulty", "hard");
            startActivity(intent);
            finish();
        });

        // Back Button
        binding.backButton.setOnClickListener(v -> {
            playClickSound();
            finish();
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
