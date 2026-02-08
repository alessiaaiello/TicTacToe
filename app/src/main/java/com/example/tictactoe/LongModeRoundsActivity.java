package com.example.tictactoe;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityLongModeRoundsBinding;

public class LongModeRoundsActivity extends AppCompatActivity {
    private ActivityLongModeRoundsBinding binding;
    private SoundPool soundPool;
    private int clickSoundId;
    private String gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLongModeRoundsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gameMode = getIntent().getStringExtra("gameMode");

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

        // 3 Rounds Button
        binding.roundsThreeButton.setOnClickListener(v -> {
            playClickSound();
            startLongMode(3);
        });

        // 5 Rounds Button
        binding.roundsFiveButton.setOnClickListener(v -> {
            playClickSound();
            startLongMode(5);
        });

        // 10 Rounds Button
        binding.roundsTenButton.setOnClickListener(v -> {
            playClickSound();
            startLongMode(10);
        });

        // Back Button
        binding.backButton.setOnClickListener(v -> {
            playClickSound();
            finish();
        });
    }

    private void startLongMode(int rounds) {
        Intent intent = new Intent(LongModeRoundsActivity.this, LongModeGameActivity.class);
        intent.putExtra("gameMode", gameMode);
        intent.putExtra("totalRounds", rounds);
        startActivity(intent);
        finish();
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
