package com.example.colortiles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var grid: Array<Array<Int>>
    private val gridSize = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        grid = Array(gridSize) { Array(gridSize) { 0 } }

        createGrid()
    }

    private fun createGrid() {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        gridLayout.columnCount = gridSize
        gridLayout.rowCount = gridSize

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val button = Button(this)
                button.layoutParams = GridLayout.LayoutParams().apply {
                    width = 200
                    height = 200
                    setMargins(4, 4, 4, 4)
                }
                button.setBackgroundColor(getColorForState(grid[i][j]))
                button.setOnClickListener {
                    handleTileClick(i, j)
                }
                gridLayout.addView(button)
            }
        }
    }

    private fun handleTileClick(row: Int, col: Int) {
        grid[row][col] = 1 - grid[row][col]

        for (i in 0 until gridSize) {
            if (i != row) grid[i][col] = 1 - grid[i][col]
        }
        for (j in 0 until gridSize) {
            if (j != col) grid[row][j] = 1 - grid[row][j]
        }

        updateGrid()
        checkForWin()
    }

    private fun updateGrid() {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val button = gridLayout.getChildAt(i * gridSize + j) as Button
                button.setBackgroundColor(getColorForState(grid[i][j]))
            }
        }
    }

    private fun getColorForState(state: Int): Int {
        return if (state == 0) getColor(R.color.white) else getColor(R.color.black)
    }

    private fun checkForWin() {
        val isWin = grid.all { row -> row.all { it == 1 } }

        if (isWin) {
            showVictoryScreen()
        }
    }

    private fun showVictoryScreen() {
        val gameLayout = findViewById<LinearLayout>(R.id.gameLayout)
        val victoryLayout = findViewById<LinearLayout>(R.id.victoryLayout)

        gameLayout.visibility = View.GONE
        victoryLayout.visibility = View.VISIBLE

        val buttonOk = findViewById<Button>(R.id.buttonOk)
        buttonOk.setOnClickListener {
            resetGame()
        }
    }

    private fun resetGame() {
        val gameLayout = findViewById<LinearLayout>(R.id.gameLayout)
        val victoryLayout = findViewById<LinearLayout>(R.id.victoryLayout)

        victoryLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE

        grid = Array(gridSize) { Array(gridSize) { 0 } }
        updateGrid()
    }
}
