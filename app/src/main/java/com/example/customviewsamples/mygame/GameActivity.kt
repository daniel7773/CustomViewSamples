package com.example.customviewsamples.mygame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var gameLayout: GameAnimationLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameLayout = GameAnimationLayout(this)
        setContentView(gameLayout)
    }

    override fun onPause() {
        super.onPause()
        gameLayout.pause()
    }

    override fun onResume() {
        super.onResume()
        gameLayout.resume()
    }

}