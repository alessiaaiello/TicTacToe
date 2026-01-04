package com.example.tictactoe

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttons: Array<Button>

    // 0 = empty, 1 = X, 2 = O
    private val board = IntArray(9) { 0 }
    private var xTurn = true
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttons = arrayOf(
            binding.btn0, binding.btn1, binding.btn2,
            binding.btn3, binding.btn4, binding.btn5,
            binding.btn6, binding.btn7, binding.btn8
        )

        for (i in buttons.indices) {
            buttons[i].setOnClickListener { onCellClicked(i) }
        }

        binding.resetButton.setOnClickListener { resetGame() }
        updateStatus()
    }

    private fun onCellClicked(index: Int) {
        if (gameOver) return
        if (board[index] != 0) return

        board[index] = if (xTurn) 1 else 2
        buttons[index].text = if (xTurn) "X" else "O"

        val winner = checkWinner()
        if (winner != 0) {
            gameOver = true
            binding.statusText.text = if (winner == 1) "X wins!" else "O wins!"
            return
        }

        if (board.all { it != 0 }) {
            gameOver = true
            binding.statusText.text = "Draw"
            return
        }

        xTurn = !xTurn
        updateStatus()
    }

    private fun updateStatus() {
        binding.statusText.text = if (xTurn) "X's turn" else "O's turn"
    }

    private fun resetGame() {
        for (i in board.indices) {
            board[i] = 0
            buttons[i].text = ""
        }
        xTurn = true
        gameOver = false
        updateStatus()
    }

    private fun checkWinner(): Int {
        val lines = arrayOf(
            intArrayOf(0,1,2), intArrayOf(3,4,5), intArrayOf(6,7,8),
            intArrayOf(0,3,6), intArrayOf(1,4,7), intArrayOf(2,5,8),
            intArrayOf(0,4,8), intArrayOf(2,4,6)
        )

        for (line in lines) {
            val (a, b, c) = line
            if (board[a] != 0 && board[a] == board[b] && board[a] == board[c]) {
                return board[a]
            }
        }
        return 0
    }
}
