package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityDifficultyBinding;

public class DifficultyActivity extends AppCompatActivity {
    private ActivityDifficultyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDifficultyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Easy Button
        binding.easyButton.setOnClickListener(v -> {
            Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "computer");
            intent.putExtra("difficulty", "easy");
            startActivity(intent);
            finish();
        });

        // Medium Button
        binding.mediumButton.setOnClickListener(v -> {
            Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "computer");
            intent.putExtra("difficulty", "medium");
            startActivity(intent);
            finish();
        });

        // Hard Button
        binding.hardButton.setOnClickListener(v -> {
            Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "computer");
            intent.putExtra("difficulty", "hard");
            startActivity(intent);
            finish();
        });

        // Back Button
        binding.backButton.setOnClickListener(v -> finish());
    }
}
