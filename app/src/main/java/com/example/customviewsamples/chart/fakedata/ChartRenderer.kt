package com.example.customviewsamples.chart.fakedata

import android.graphics.Canvas

interface ChartRenderer {
    fun setValues(values: List<Double>)

    fun addValue(value: Double)

    fun onFrame(frameTimeMillis: Long, speed: Int)

    fun draw(canvas: Canvas, clipWidth: Int, clipHeight: Int)

    fun getChartItemByScreenX(x: Float): ChartItem
}

data class ChartItem(val y: Float, val value: Double)