package com.example.customviewsamples.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.customviewsamples.R
import com.example.customviewsamples.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs


// TODO: now it can't use more then 600 values, handle

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext // guaranties that when it's lifecycle ends everything gonna be cancelled
        get() = job + Dispatchers.Main

    /**
     * Scrolling stuff
     */
    private var gestureDetector: GestureDetector? = null
    private var scroller: Scroller? = null

    /**
     * Drawing stuff
     */
    private val backgroundPaint = Paint()
    private val chartPaintStrokeWidth = 3.px.toFloat()
    private val chartLineColor =
        ResourcesCompat.getColor(
            context.resources,
            R.color.chart_line_color,
            context.theme
        )
    private val chartLineColorLight =
        ResourcesCompat.getColor(
            context.resources,
            R.color.chart_line_color_light,
            context.theme
        )

    private val chartPaint = Paint().apply {
        color = chartLineColor
        strokeWidth = chartPaintStrokeWidth
        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(50.0f)
    }

    private val chartCirclePaint = Paint().apply {
//        xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR) // TODO: чтобы цвета не наслаивались
        color = chartLineColorLight
    }

    private val chartPath = Path()

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 2f
    }

    private val borderRect = Rect(0, 0, width, height)

    /**
     * Filling chart logic
     */
    private val maxDotsPerScreen = 150
    private val pathFromSource = ArrayList<Float>()
    private var isCalledAfterValueAdded = false
    private val maxLengthMultiplier = 4
    private var lastXPosition = 0f


    var backgroundDrawable: BackgroundDrawable? = BackgroundDrawable(context)
    var bitmap: Bitmap? = null

    init {
        gestureDetector = GestureDetector(context, MyGestureListener())
        scroller = Scroller(context)
        /**
         * was

        isHorizontalScrollBarEnabled = true

        val a = context.obtainStyledAttributes(R.styleable.ChartView)
        a.recycle()

         */
    }

    private fun getStartX(): Float {
        return if (pathFromSource.size <= maxDotsPerScreen) {
            10f
        } else ((width / maxDotsPerScreen) * pathFromSource.size * getLengthMultiplier() * -1.0).toFloat()
    }

    private fun getStepByX(): Int {
        return if (pathFromSource.size < 1) {
            width / 2
        } else {
            if (pathFromSource.size < maxDotsPerScreen) {
                width / pathFromSource.size
            } else {
                (width * getLengthMultiplier()).toInt() / maxDotsPerScreen
            }
        }
    }


    // TODO: rename, cause it's not only maximizing but minimizing too (усредняет крч)
    private fun getMaximizeByY(): Float { // if value will be below 0 it won't work properly
        var maximizeByY = 1f
        var maxValue = Float.MIN_VALUE
        var minValue = Float.MAX_VALUE
        pathFromSource.forEach {
            if (it > maxValue) {
                maxValue = it
            }
            if (minValue > it) {
                minValue = it
            }
        }
        Log.d("Dadasrwqw", "measuredHeight * 0.8f = ${measuredHeight * 0.8f}")
        Log.d("Dadasrwqw", "maxValue - minValue = ${maxValue - minValue}")
        if (pathFromSource.isNotEmpty()) {
            var volatile = 1f
            val differ = maxValue - minValue
            if (differ > 0 && differ <= 1) {
                volatile = abs(maxValue)
            } else if (differ < 0) {
                if (differ < 0 && differ >= -1) {
                    volatile = abs(minValue)
                } else if (differ < -1) {
                    volatile = abs(differ)
                }
            } else if (differ > 1) {
                volatile = differ
            } else { // divide by zero
                volatile = abs(maxValue)
            }
            maximizeByY = (measuredHeight * 0.8f) / volatile
        }

        return maximizeByY
    }


    private fun getMinValue(): Float {
        var minValue: Float? = null
        pathFromSource.forEach {
            if (minValue == null || abs(it) < minValue!!) {
                minValue = abs(it)
            }
        }
        return minValue ?: 0f
    }

    fun addPath(float: Float) {
        pathFromSource.add(float)
        isCalledAfterValueAdded = true
        invalidate()
    }

    private fun getLengthMultiplier(): Double {
        if (pathFromSource.size < 151) return 1.0
        if ((pathFromSource.size / maxDotsPerScreen) > maxLengthMultiplier) {
            return maxLengthMultiplier.toDouble()
        } else return pathFromSource.size.toDouble() / maxDotsPerScreen
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // check for tap and cancel fling
        Log.d(
            "asdadqw",
            "coordinate x: ${event.x}"
        ) // x здесь координата на экране на не на графике (т.е. всегда от 0 до ширины экрана)
        if (event.pointerCount == 1 || event.pointerCount == 2) {
            super.onTouchEvent(event)
        }

        if (event.action == MotionEvent.ACTION_UP) {
            performClick()
        }

        if (event.action and MotionEvent.ACTION_MASK === MotionEvent.ACTION_DOWN) {
            if (!scroller!!.isFinished) scroller!!.abortAnimation()
        }

        if (gestureDetector!!.onTouchEvent(event)) return true // allows us to scroll with gesture logic

        return true
    }

    override fun computeScroll() {
        if (scroller!!.computeScrollOffset()) {
            val oldX = scrollX
            val oldY = scrollY
            val x: Int = scroller!!.currX
            val y: Int = scroller!!.currY
            scrollTo(x, y)
            if (oldX != scrollX || oldY != scrollY) {
                onScrollChanged(scrollX, scrollY, oldX, oldY)
            }
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {

        if (width > 0 && bitmap == null) {
            bitmap = backgroundDrawable?.toBitmap( // TODO: added hardcoded 200
                (width * (maxLengthMultiplier + 2) + 200).px, // max length for not recreate bitmap a lot of times TODO: handle, fix
                height
            )
        }

        val bitmapStart = if(getStartX() > 0) -100f else getStartX()
        bitmap?.run { // not working with width, but working with measuredWidth TODO: find out why
            canvas?.drawBitmap(this, bitmapStart, 0f, backgroundPaint)
        }

        var nextX = getStartX()
        var lastY = 0f

        val stepByX = getStepByX()

        chartPath.reset()

        if (pathFromSource.size > 0) {

            val maximizeByY = getMaximizeByY()

            Log.d("Asdasdqwwrf", "maximizeByY: ${maximizeByY}")

            var minValue = 0f

            if (maximizeByY > 1) {
                minValue = getMinValue()
            }

            lastY = (pathFromSource[0] - minValue + 20f) * maximizeByY

            chartPath.moveTo(
                nextX,
                lastY
            )

            pathFromSource.forEach { value ->
                nextX += stepByX
                lastY = (value - minValue) * maximizeByY
                chartPath.lineTo(nextX, lastY)
            }

            canvas?.drawPath(chartPath, chartPaint)
        }
        lastXPosition = nextX // keeping max scrolling

        chartCirclePaint.apply {
            isAntiAlias = true
            strokeWidth = 6f
            style = Paint.Style.FILL
        }

        val bitmap = Bitmap.createBitmap(
            40, 40,
            Bitmap.Config.ARGB_8888
        )

        for(i in 0..39) {
            for (j in 0..39) {
                bitmap.setPixel(
                    j, i, Color.rgb(
                        chartLineColor.red,
                        chartLineColor.green + (i),
                        chartLineColor.blue + (i * 3).toInt()
                    )
                )
            }
        }



        canvas?.drawBitmap(getCroppedBitmap(bitmap)!!, lastXPosition - 16f, lastY - 16f, Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        })

        canvas?.drawRect(borderRect, borderPaint)

        // Scrolling to end of chart for prevent user see not actual info first
        if (isCalledAfterValueAdded) {
            scroller?.finalX = lastXPosition.toInt() - width
            isCalledAfterValueAdded = false
        }
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    private val colors = intArrayOf(
        ContextCompat.getColor(context, R.color.chart_line_color),
        Color.WHITE
    )
    private var positions = floatArrayOf(0.0f, 1.0f)
    private var gradient: SweepGradient? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // TODO: move everything with sizes here
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun computeHorizontalScrollRange(): Int {
        return width
    }

    override fun computeVerticalScrollRange(): Int {
        return height
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
        pathFromSource.clear()
        backgroundDrawable = null
        bitmap = null
    }

    inner class MyGestureListener : SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            val minX = getStartX().toInt()

            Log.d("asdadqw", "min x while scrolling: ${minX}")
            Log.d("asdadqw", "scrollX: ${scrollX}")

            scroller!!.fling(
                scrollX,
                0,
                (-velocityX).toInt(),
                0,
                minX,
                lastXPosition.toInt() - width + 200, // TODO: added hardcoded 200
                0,
                0
            )
            awakenScrollBars(scroller!!.duration)
            invalidate()
            return true
        }
    }

}