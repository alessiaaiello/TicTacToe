package com.example.tictactoe;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private SoundPool soundPool;
    private int startupSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sound pool
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        startupSoundId = soundPool.load(this, R.raw.startup_chime, 1);

        // Wait a bit for sound to load, then play it
        new Handler().postDelayed(() -> {
            soundPool.play(startupSoundId, 0.8f, 0.8f, 1, 0, 1.0f);
        }, 200);

        // Start animation
        binding.loadingAnimation.setAlpha(0f);
        binding.loadingAnimation.animate()
                .alpha(1f)
                .setDuration(800)
                .start();

        // Navigate to home after 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 3000);
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
