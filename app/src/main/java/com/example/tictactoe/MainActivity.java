package com.example.tictactoe;

import android.os.Bundle;
import android.os.Handler;
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
    private Random random = new Random();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gameMode = getIntent().getStringExtra("gameMode");
        if (gameMode == null) {
            gameMode = "friend";
        }

        buttons = new Button[]{
            binding.btn0, binding.btn1, binding.btn2,
            binding.btn3, binding.btn4, binding.btn5,
            binding.btn6, binding.btn7, binding.btn8
        };

        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(v -> onCellClicked(finalI));
        }

        binding.resetButton.setOnClickListener(v -> resetGame());
        binding.resetButtonOverlay.setOnClickListener(v -> resetGame());
        updateStatus();
    }

    private void onCellClicked(int index) {
        if (gameOver) return;
        if (board[index] != 0) return;

        // Player move (always ★)
        board[index] = 1;
        buttons[index].setText("★");

        int winner = checkWinner();
        if (winner != 0) {
            endGame("★ Wins!");
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
        // Find best move using minimax algorithm
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
        if (gameMode.equals("computer")) {
            binding.statusText.setText(xTurn ? "Your turn (★)" : "Computer (✿)");
        } else {
            binding.statusText.setText(xTurn ? "★'s turn" : "✿'s turn");
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
}
