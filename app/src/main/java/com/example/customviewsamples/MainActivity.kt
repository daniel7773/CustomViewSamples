package com.example.customviewsamples

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.customviewsamples.chart.ChartView
import com.example.customviewsamples.chart.fakedata.DataProvider
import com.example.customviewsamples.customscroll.CustomScrollView
import com.example.customviewsamples.imagetext.DrawTextView
import com.example.customviewsamples.imagetext.ProjectResources
import com.example.customviewsamples.imagetext.projectResources
import com.example.customviewsamples.moving_square.MyView
import com.example.customviewsamples.mygame.GameActivity
import com.example.customviewsamples.mygame.GameView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job() // a lifecycle of a SessionManager coroutine

    override val coroutineContext: CoroutineContext // guaranties that when it's lifecycle ends everything gonna be cancelled
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // for DrawTextView
        projectResources = ProjectResources(resources)

        setContentView(R.layout.activity_main)

        // chart logic
        val chartView = findViewById<ChartView>(R.id.chartView)
        val myView = findViewById<MyView>(R.id.myView)
        val gameView = findViewById<GameView>(R.id.gameView)
        gameView.setOnClickListener {
            val myIntent = Intent(this@MainActivity, GameActivity::class.java)
            this@MainActivity.startActivity(myIntent)
        }
        val scrollView = findViewById<CustomScrollView>(R.id.scrollView)

        launch(coroutineContext) {
            withContext(Dispatchers.IO) {
                DataProvider.mainAsFlow().collect { point ->
                    withContext(Dispatchers.Main) {
                        chartView.addPath(point)
                    }
                }
            }
        }

        // draw text logic


        val drawTextView = findViewById<DrawTextView>(R.id.drawTextView)
        var bool = false
        drawTextView.setOnClickListener {
            if (bool) {
                val typeFace = Typeface.createFromAsset(assets, "fonts/sample_font.ttf")
                drawTextView.typeFace = typeFace
                drawTextView.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.cicada))
            } else {
                drawTextView.typeFace = Typeface.DEFAULT
                drawTextView.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.avatar))
            }
            bool = !bool
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}