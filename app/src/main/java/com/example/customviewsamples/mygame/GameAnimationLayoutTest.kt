package com.example.customviewsamples.mygame

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.example.customviewsamples.R


/**
 * Old layout with logic I don't want to delete
 */
class GameAnimationLayoutTest @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), Runnable {

    var thread: Thread? = null
    var canDraw: Boolean = false

    val redPaintbrushFill = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    val bluePaintbrushFill = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    val greenPaintbrushFill = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }
    val redPaintbrushStroke = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    val bluePaintbrushStroke = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    val greenPaintbrushStroke = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    var square: Path? = null

    var cherryX = 130 // same as start of square
    var cherryY = 130
    var cherryHeight = 60f
    var cherryWidth = 60f

    var bitmapCherry: Bitmap? = null

    var background: Bitmap? = null
    var canvas: Canvas? = null
    var surfaceHolder: SurfaceHolder? = null

    val NUM_OF_GRASS_ROWS = 8
    private val rect = Rect()
    private val grassColor1 =
        ResourcesCompat.getColor(context.resources, R.color.grass1, context.theme)
    private val grassColor2 =
        ResourcesCompat.getColor(context.resources, R.color.grass2, context.theme)
    private val grassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        surfaceHolder = holder
//        background = BitmapFactory.decodeResource(resources, R.drawable.avatar) // TODO: if not set background it will be strange effect btw...
        bitmapCherry = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.cherry),
            cherryWidth.toInt(),
            cherryHeight.toInt(),
            false
        )

    }

    override fun run() {
        while (canDraw) {
            // TODO: carry out some drawing

            if (surfaceHolder?.surface?.isValid != true) {
                continue
            }

            canvas = surfaceHolder?.lockCanvas()
            motionCherry(10)

            val width = canvas?.width
            val height = canvas?.height
            val rowHeight = height!! / NUM_OF_GRASS_ROWS
            canvas?.apply {
                for (i in 0..NUM_OF_GRASS_ROWS) {
                    val top = i * rowHeight
                    rect.set(0, top, width!!, top + rowHeight)
                    grassPaint.color = if (i % 2 == 0) grassColor1 else grassColor2
                    drawRect(rect, grassPaint)
                }
            }

            drawSquare(130f, 130f, 650f, 650f)

            canvas?.drawBitmap(
                bitmapCherry!!,
                cherryX - (bitmapCherry!!.width / 2f),
                (cherryY - bitmapCherry!!.height / 2f),
                null
            )
            surfaceHolder?.unlockCanvasAndPost(canvas)

        }
    }

    private fun drawSquare(x1: Float, y1: Float, x2: Float, y2: Float) {

        square = Path()
        square?.apply {
            moveTo(x1, y1)
            lineTo(x2, y1)
            moveTo(x2, y1)
            lineTo(x2, y2)
            moveTo(x2, y2)
            lineTo(x1, y2)
            moveTo(x1, y2)
            lineTo(x1, y1)
        }

        square?.let { this@GameAnimationLayoutTest.canvas?.drawPath(it, redPaintbrushStroke) }
    }

    private fun motionCherry(speed: Int) {

        if ((cherryY == 130) && cherryX < 650) {
            cherryX += speed
        }

        if ((cherryY < 650) && cherryX == 650) {
            cherryY += speed
        }

        if ((cherryY == 650) && cherryX > 130) {
            cherryX -= speed
        }

        if ((cherryY > 130) && cherryX == 130) {
            cherryY -= speed
        }
    }

    fun pause() {
        canDraw = false

        while (true) {
            try {
                thread?.join() // join block the current thread until the receiver finishes its execution and dies
                break
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        thread = null
    }

    fun resume() {
        canDraw = true
        thread = Thread(this)
        thread?.start()
    }
}