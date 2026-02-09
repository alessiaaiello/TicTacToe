package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.media.SoundPool;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityMainBinding;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button[] buttons;
    private int[] board = new int[9];
    private boolean xTurn = true;
    private boolean gameOver = false;
    private String gameMode = "friend"; // "friend" or "computer"
    private String difficulty = "hard"; // "easy", "medium", or "hard"
    private Random random = new Random();
    private Handler handler = new Handler();
    private SoundPool soundPool;
    private int clickSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply saved background theme
        applyBackgroundTheme();

        gameMode = getIntent().getStringExtra("gameMode");
        if (gameMode == null) {
            gameMode = "friend";
        }

        difficulty = getIntent().getStringExtra("difficulty");
        if (difficulty == null) {
            difficulty = "hard";
        }

        // Initialize sound pool for button clicks
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        clickSoundId = soundPool.load(this, R.raw.button_click, 1);

        buttons = new Button[]{
            binding.btn0, binding.btn1, binding.btn2,
            binding.btn3, binding.btn4, binding.btn5,
            binding.btn6, binding.btn7, binding.btn8
        };

        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(v -> onCellClicked(finalI));
        }

        binding.resetButton.setOnClickListener(v -> {
            playClickSound();
            resetGame();
        });
        binding.resetButtonOverlay.setOnClickListener(v -> {
            playClickSound();
            resetGame();
        });
        binding.backButton.setOnClickListener(v -> {
            playClickSound();
            finish();
        });

        // Settings Button
        binding.settingsButton.setOnClickListener(v -> {
            playClickSound();
            Intent intent = new Intent(MainActivity.this, ProfileSetupActivity.class);
            intent.putExtra("editMode", true);
            startActivity(intent);
        });

        updateStatus();
    }

    private void onCellClicked(int index) {
        if (gameOver) return;
        if (board[index] != 0) return;

        String[] symbols = getCurrentSymbols();
        if (gameMode.equals("friend")) {
            // Friend mode: alternate between players
            board[index] = xTurn ? 1 : 2;
            buttons[index].setText(xTurn ? symbols[0] : symbols[1]);
        } else {
            // Computer mode: player is always first symbol
            board[index] = 1;
            buttons[index].setText(symbols[0]);
        }

        int winner = checkWinner();
        if (winner != 0) {
            String winnerMessage = winner == 1 ? symbols[0] + " Wins!" : symbols[1] + " Wins!";
            endGame(winnerMessage);
            return;
        }

        if (isBoardFull()) {
            endGame("Draw!");
            return;
        }

        if (gameMode.equals("computer")) {
            // Computer's turn
            handler.postDelayed(this::computerMove, 500);
        } else {
            // Friend's turn
            xTurn = !xTurn;
            updateStatus();
        }
    }

    private void computerMove() {
        if (difficulty.equals("easy")) {
            easyMove();
        } else if (difficulty.equals("medium")) {
            mediumMove();
        } else {
            hardMove();
        }
    }

    private void easyMove() {
        // Random move
        int[] availableMoves = new int[9];
        int count = 0;
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                availableMoves[count] = i;
                count++;
            }
        }

        if (count > 0) {
            int randomIndex = random.nextInt(count);
            int move = availableMoves[randomIndex];
            board[move] = 2;
            String[] symbols = getCurrentSymbols();
            buttons[move].setText(symbols[1]);

            int winner = checkWinner();
            if (winner != 0) {
                endGame("✿ Wins!");
                return;
            }

            if (isBoardFull()) {
                endGame("Draw!");
                return;
            }
        }
        updateStatus();
    }

    private void mediumMove() {
        // Simple strategy: take center, corners, then edges
        // First, try to win
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                board[i] = 2;
                if (checkWinner() == 2) {
                    String[] symbols = getCurrentSymbols();
                    buttons[i].setText(symbols[1]);
                    endGame(symbols[1] + " Wins!");
                    return;
                }
                board[i] = 0;
            }
        }

        // Block player from winning
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                board[i] = 1;
                if (checkWinner() == 1) {
                    board[i] = 2;
                    buttons[i].setText(getCurrentSymbols()[1]);
                    board[i] = 2;
                    updateStatus();
                    return;
                }
                board[i] = 0;
            }
        }

        String[] symbols = getCurrentSymbols();
        // Take center
        if (board[4] == 0) {
            board[4] = 2;
            buttons[4].setText(symbols[1]);
        }
        // Take corners
        else if (board[0] == 0) {
            board[0] = 2;
            buttons[0].setText(symbols[1]);
        }
        // Random available move
        else {
            int[] availableMoves = new int[9];
            int count = 0;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 0) {
                    availableMoves[count] = i;
                    count++;
                }
            }
            if (count > 0) {
                int move = availableMoves[random.nextInt(count)];
                board[move] = 2;
                buttons[move].setText(symbols[1]);
            }
        }

        int winner = checkWinner();
        if (winner != 0) {
            endGame(symbols[1] + " Wins!");
            return;
        }

        if (isBoardFull()) {
            endGame("Draw!");
            return;
        }
        updateStatus();
    }

    private void hardMove() {
        // Minimax algorithm
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                board[i] = 2;
                int score = minimax(0, false);
                board[i] = 0;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        if (bestMove != -1) {
            board[bestMove] = 2;
            String[] symbols = getCurrentSymbols();
            buttons[bestMove].setText(symbols[1]);

            int winner = checkWinner();
            if (winner != 0) {
                endGame(symbols[1] + " Wins!");
                return;
            }

            if (isBoardFull()) {
                endGame("Draw!");
                return;
            }
        }
        updateStatus();
    }

    private int minimax(int depth, boolean isMaximizing) {
        int winner = checkWinner();
        if (winner == 2) return 10 - depth; // Computer wins
        if (winner == 1) return depth - 10; // Player wins
        if (isBoardFull()) return 0; // Draw

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 0) {
                    board[i] = 2;
                    int score = minimax(depth + 1, false);
                    board[i] = 0;
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 0) {
                    board[i] = 1;
                    int score = minimax(depth + 1, true);
                    board[i] = 0;
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private void endGame(String message) {
        gameOver = true;
        binding.statusText.setText(message);
        binding.winnerText.setText(message);
        binding.winnerText.setVisibility(android.view.View.VISIBLE);
        binding.confettiView.setVisibility(android.view.View.VISIBLE);
        binding.confettiView.startConfetti();
        binding.resetButtonContainer.setVisibility(android.view.View.VISIBLE);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                return false;
            }
        }
        return true;
    }

    private void updateStatus() {
        String[] symbols = getCurrentSymbols();
        if (gameMode.equals("computer")) {
            binding.statusText.setText(xTurn ? "Your turn (" + symbols[0] + ")" : "Computer (" + symbols[1] + ")");
        } else {
            binding.statusText.setText(xTurn ? symbols[0] + "'s turn" : symbols[1] + "'s turn");
        }
    }

    private void playClickSound() {
        if (soundPool != null) {
            soundPool.play(clickSoundId, 0.5f, 0.5f, 1, 0, 1.0f);
        }
    }

    private void resetGame() {
        binding.resetButtonContainer.setVisibility(android.view.View.GONE);
        for (int i = 0; i < board.length; i++) {
            board[i] = 0;
            buttons[i].setText("");
        }
        xTurn = true;
        gameOver = false;
        binding.winnerText.setVisibility(android.view.View.GONE);
        binding.confettiView.stopConfetti();
        binding.confettiView.setVisibility(android.view.View.GONE);
        updateStatus();
    }

    private String[] getCurrentSymbols() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String theme = prefs.getString("backgroundTheme", "seascape");
        
        switch (theme) {
            case "forest":
                return new String[]{"🐱", "🐶"}; // Animals for forest
            case "desert":
                return new String[]{"⚽", "🏀"}; // Sports for desert
            case "space":
                return new String[]{"X", "O"}; // Letters for space
            case "sunset":
                return new String[]{"♥", "♠"}; // Hearts for sunset
            case "ocean":
                return new String[]{"1", "2"}; // Numbers for ocean
            default:
                return new String[]{"★", "✿"}; // Classic for seascape
        }
    }

    private void applyBackgroundTheme() {
        SharedPreferences prefs = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        String background = prefs.getString("backgroundTheme", "seascape");
        
        int backgroundRes = getBackgroundResource(background);
        if (backgroundRes != 0) {
            binding.getRoot().setBackgroundResource(backgroundRes);
        }
        
        // Apply themed button colors
        applyThemedButtonColors(background);
    }

    private void applyThemedButtonColors(String theme) {
        int buttonColor = getThemeButtonColor(theme);
        
        if (binding.resetButton != null) {
            binding.resetButton.setBackgroundColor(buttonColor);
        }
        if (binding.resetButtonOverlay != null) {
            binding.resetButtonOverlay.setBackgroundColor(buttonColor);
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

    private int checkWinner() {
        int[][] lines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };

        for (int[] line : lines) {
            int a = line[0], b = line[1], c = line[2];
            if (board[a] != 0 && board[a] == board[b] && board[a] == board[c]) {
                return board[a];
            }
        }
        return 0;
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
