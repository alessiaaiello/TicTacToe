package com.example.tictactoe;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoe.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button[] buttons;
    private int[] board = new int[9];
    private boolean xTurn = true;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        board[index] = xTurn ? 1 : 2;
        buttons[index].setText(xTurn ? "★" : "✿");

        int winner = checkWinner();
        if (winner != 0) {
            gameOver = true;
            String winnerMessage = winner == 1 ? "★ Wins!" : "✿ Wins!";
            binding.statusText.setText(winnerMessage);
            binding.winnerText.setText(winnerMessage);
            binding.winnerText.setVisibility(android.view.View.VISIBLE);
            binding.confettiView.setVisibility(android.view.View.VISIBLE);
            binding.confettiView.startConfetti();
            binding.resetButtonContainer.setVisibility(android.view.View.VISIBLE);
            return;
        }

        boolean isFull = true;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                isFull = false;
                break;
            }
        }

        if (isFull) {
            gameOver = true;
            binding.statusText.setText("Draw");
            binding.winnerText.setText("Draw!");
            binding.winnerText.setVisibility(android.view.View.VISIBLE);
            binding.confettiView.setVisibility(android.view.View.VISIBLE);
            binding.confettiView.startConfetti();
            binding.resetButtonContainer.setVisibility(android.view.View.VISIBLE);
            return;
        }

        xTurn = !xTurn;
        updateStatus();
    }

    private void updateStatus() {
        binding.statusText.setText(xTurn ? "★'s turn" : "✿'s turn");
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
