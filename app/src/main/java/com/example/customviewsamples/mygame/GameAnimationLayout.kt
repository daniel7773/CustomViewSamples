package com.example.customviewsamples.mygame

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.example.customviewsamples.R

class GameAnimationLayout @JvmOverloads constructor(
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

    var screenWidth = 1000
    var screenHeight = 600

    var bitmapCherry: Bitmap? = null

    var bitmap2Player: Bitmap? = null

    // 2, управляемый футболист
    // переменные для перетаскивания
    var drag = false
    var dragX = 0f
    var dragY = 0f

    var p: Paint = Paint().apply {
        color = Color.YELLOW
    }

    // координаты для рисования квадрата
    var myX = 100f
    var myY = 100f
    var player2Size = 300



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

        bitmap2Player = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.messi_no_background),
            player2Size,
            player2Size,
            false
        )

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w
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

            drawSquare(screenWidth / 4f, 130f, screenWidth * 0.75f, 650f)

//            // Рисуем управляемый квадрат
//            canvas?.drawRect(myX, myY, myX + player2Size, myY + player2Size, p)

            canvas?.drawBitmap(
                bitmapCherry!!,
                cherryX - (bitmapCherry!!.width / 2f),
                (cherryY - bitmapCherry!!.height / 2f),
                null
            )

            canvas?.drawBitmap(
                bitmap2Player!!,
                myX - (bitmap2Player!!.width / 2f),
                (myY - bitmap2Player!!.height / 2f),
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
        }

        square?.let { this@GameAnimationLayout.canvas?.drawPath(it, redPaintbrushStroke) }
    }

    private var reachedRight = false

    private fun motionCherry(speed: Int) {

        if (reachedRight && cherryX >= screenWidth / 4f) {
            cherryX -= speed
        } else if (cherryX < screenWidth * 0.75f && !reachedRight) {
            cherryX += speed
        }

        if (cherryX >= screenWidth * 0.75f) {
            reachedRight = true
        }

        if (cherryX <= screenWidth / 4f) {
            reachedRight = false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // координаты Touch-события
        val evX = event.x
        val evY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN ->                 // если касание было начато в пределах квадрата
                if (evX >= myX && evX <= myX + player2Size && evY >= myY && evY <= myY + player2Size) {
                    // включаем режим перетаскивания
                    drag = true
                    // разница между левым верхним углом квадрата и точкой касания
                    dragX = evX - myX
                    dragY = evY - myY
                }
            MotionEvent.ACTION_MOVE ->                 // если режим перетаскивания включен
                if (drag) {
                    // определеяем новые координаты для рисования
                    val newMyX = evX - dragX
                    val newMyY = evY - dragY
                    // перерисовываем экран
                    if(newMyY + player2Size < height && newMyX + player2Size < width && newMyY >= -1 && newMyX >= -1) { // condition чтобы не выезжало за края вью
                        myX = newMyX
                        myY = newMyY
                        invalidate()
                    }
                }
            MotionEvent.ACTION_UP ->                 // выключаем режим перетаскивания
                drag = false
        }
        return true
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