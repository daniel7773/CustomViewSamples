package com.example.customviewsamples.chart

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.example.customviewsamples.R
import com.example.customviewsamples.px

class BackgroundDrawable(context: Context): Drawable() {

    private val bubbleColor =
        ResourcesCompat.getColor(context.resources, R.color.bubble_color, context.theme)
    private val backgroundColor =
        ResourcesCompat.getColor(context.resources, R.color.background_color, context.theme)
    private val simplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    val skyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 20
        color = bubbleColor
        strokeCap = Paint.Cap.ROUND
    }


    private val backgroundRect = Rect()
    private val rect = Rect()
    private val rectRound = RectF(rect)

    private val padding = 8.px

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        val rowHeight = height / NUM_OF_CIRCLE_ROWS
        val rowWidth = width / NUM_OF_CIRCLE_COLUMNS

        val maxRadius = if (rowHeight > rowWidth) rowWidth / 3 else rowHeight / 3
        val minRadius = maxRadius / 2

        canvas.apply {
            backgroundRect.set(0, 0, width, height)
            simplePaint.color = backgroundColor
            drawRect(backgroundRect, simplePaint)

            for (i in 1..NUM_OF_CIRCLE_ROWS) {
                for (j in 1..NUM_OF_CIRCLE_COLUMNS) {
                    val top = (((i - 1) * rowHeight)..(i * rowHeight-maxRadius)).random().toFloat()

                    val left = (((j - 1)  * rowWidth)..(j * rowWidth-maxRadius)).random().toFloat()

                    val diameter = (minRadius..maxRadius).random().toFloat()

                    rectRound.set(left, top, left + diameter, top + diameter)

                    drawRoundRect(rectRound, 90f, 90f, skyPaint)
                }
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        // no-op
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // no-op
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

}

private const val NUM_OF_CIRCLE_ROWS = 7
private const val NUM_OF_CIRCLE_COLUMNS = 7