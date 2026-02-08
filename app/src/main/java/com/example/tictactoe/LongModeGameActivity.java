package com.example.tictactoe;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityLongModeGameBinding;
import java.util.Random;

public class LongModeGameActivity extends AppCompatActivity {

    private ActivityLongModeGameBinding binding;
    private Button[] buttons;
    private int[] board = new int[9];
    private boolean xTurn = true;
    private boolean gameOver = false;
    private String gameMode;
    private String difficulty = "hard";
    private int totalRounds;
    private int currentRound = 1;
    private int player1Wins = 0;
    private int player2Wins = 0;
    private Random random = new Random();
    private Handler handler = new Handler();
    private SoundPool soundPool;
    private int clickSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLongModeGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gameMode = getIntent().getStringExtra("gameMode");
        totalRounds = getIntent().getIntExtra("totalRounds", 3);

        // Initialize sound pool
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
            startNewRound();
        });

        binding.backButton.setOnClickListener(v -> {
            playClickSound();
            finish();
        });

        updateScore();
    }

    private void onCellClicked(int index) {
        if (gameOver) return;
        if (board[index] != 0) return;

        if (gameMode.equals("friend")) {
            board[index] = xTurn ? 1 : 2;
            buttons[index].setText(xTurn ? "★" : "✿");
        } else {
            board[index] = 1;
            buttons[index].setText("★");
        }

        int winner = checkWinner();
        if (winner != 0) {
            endRound(winner);
            return;
        }

        if (isBoardFull()) {
            endRound(0); // Draw
            return;
        }

        if (gameMode.equals("computer")) {
            handler.postDelayed(this::computerMove, 500);
        } else {
            xTurn = !xTurn;
            updateStatus();
        }
    }

    private void computerMove() {
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
            buttons[bestMove].setText("✿");

            int winner = checkWinner();
            if (winner != 0) {
                endRound(winner);
                return;
            }

            if (isBoardFull()) {
                endRound(0);
                return;
            }
        }
        updateStatus();
    }

    private int minimax(int depth, boolean isMaximizing) {
        int winner = checkWinner();
        if (winner == 2) return 10 - depth;
        if (winner == 1) return depth - 10;
        if (isBoardFull()) return 0;

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

    private void endRound(int winner) {
        gameOver = true;
        if (winner == 1) {
            player1Wins++;
            binding.statusText.setText("★ Wins Round " + currentRound + "!");
        } else if (winner == 2) {
            player2Wins++;
            binding.statusText.setText("✿ Wins Round " + currentRound + "!");
        } else {
            binding.statusText.setText("Draw!");
        }

        // Check if series is complete
        if (currentRound >= totalRounds) {
            handler.postDelayed(this::showSeriesWinner, 2000);
        } else {
            binding.resetButton.setText("Next Round (" + (currentRound + 1) + "/" + totalRounds + ")");
        }
    }

    private void showSeriesWinner() {
        String winner;
        if (player1Wins > player2Wins) {
            winner = "★ Wins the Series!\n" + player1Wins + " - " + player2Wins;
        } else if (player2Wins > player1Wins) {
            winner = "✿ Wins the Series!\n" + player2Wins + " - " + player1Wins;
        } else {
            winner = "Series Tied!\n" + player1Wins + " - " + player2Wins;
        }

        Toast.makeText(this, winner, Toast.LENGTH_LONG).show();
        
        // Go back to home after showing result
        handler.postDelayed(() -> {
            Intent intent = new Intent(LongModeGameActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }

    private void startNewRound() {
        currentRound++;
        for (int i = 0; i < board.length; i++) {
            board[i] = 0;
            buttons[i].setText("");
        }
        xTurn = true;
        gameOver = false;
        binding.resetButton.setText("Next Round (" + (currentRound + 1) + "/" + totalRounds + ")");
        updateScore();
    }

    private void updateScore() {
        String scoreText = "Round " + currentRound + "/" + totalRounds + " | ★: " + player1Wins + " - ✿: " + player2Wins;
        binding.scoreText.setText(scoreText);
        updateStatus();
    }

    private void updateStatus() {
        binding.statusText.setText(xTurn ? "★'s turn" : "✿'s turn");
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

    private boolean isBoardFull() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                return false;
            }
        }
        return true;
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
