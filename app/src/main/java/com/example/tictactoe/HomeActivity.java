package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Play with Friend Button
        binding.friendButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("gameMode", "friend");
            startActivity(intent);
        });

        // Play with Computer Button
        binding.computerButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DifficultyActivity.class);
            startActivity(intent);
        });
    }
}
