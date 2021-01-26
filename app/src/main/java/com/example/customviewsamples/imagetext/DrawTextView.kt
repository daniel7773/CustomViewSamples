package com.example.customviewsamples.imagetext

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.customviewsamples.R
import com.example.customviewsamples.px
import java.lang.Math.abs


class DrawTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TEXT = "Sample Text"
    }

    val borderPaint: Paint
    val borderRect: Rect

    var imageSize = 56.px

    private var bitmap: Bitmap? = null

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = (Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, false))
    }

    private val drawPaint = Paint().apply {
        color = resources.getColor(R.color.teal_200)
    }

    private var drawText = TEXT
    private val drawTextCoordinate = Coordinate()

    var customText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    // This is to set to ensure the coordinate Y position is fix to the
    // Sample Text height, so that it doesn't move despite of the drawText height change
    // This make it prettier when perform drawing, so the text stay on the baseline regardless
    // of the drawText change height.
    var fixHeightCoordinate: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var customCenter: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var customAlign: Paint.Align = Paint.Align.CENTER
        set(value) {
            field = value
            projectResources.paintText.textAlign = field
            invalidate()
        }

    var typeFace: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            projectResources.paintText.typeface = field
            invalidate()
        }

    private val sampleTextBound by lazy {
        val textBound = Rect()
        projectResources.paintText.getTextBounds(TEXT, 0, TEXT.length, textBound)
        textBound
    }

    private var originTextBound = calculateOriginTextBound()

    init {
        borderPaint = Paint()
        borderRect = Rect(0, 0, measuredWidth, measuredHeight)
    }

    private fun calculateOriginTextBound(): Rect {
        val textBound = Rect()
        projectResources.paintText.getTextBounds(drawText, 0, drawText.length, textBound)
        return textBound
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.avatar), imageSize, imageSize, false
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (measuredWidth == 0 || height == 0) return

        drawText = if (customText.isBlank()) TEXT else customText
        originTextBound = calculateOriginTextBound()

        drawTextCoordinate.x = measuredWidth / 2f

        if (fixHeightCoordinate) {
            if (customCenter) {
                drawTextCoordinate.y = measuredHeight / 2f - sampleTextBound.exactCenterY()
            } else {
                drawTextCoordinate.y = measuredHeight / 2f + sampleTextBound.calculateCenterY() + 32.px // added bc before it image was too hight
            }
        } else {
            if (customCenter) {
                drawTextCoordinate.y = measuredHeight / 2f - originTextBound.exactCenterY()
            } else {
                drawTextCoordinate.y = measuredHeight / 2f + originTextBound.calculateCenterY() + 32.px // added bc before it image was too hight
            }

        }

        bitmap?.run {
            canvas.drawBitmap(
                this,
                measuredWidth / 2f - this.width / 2f,
                drawTextCoordinate.y - (projectResources.paintText.textSize + this.height + 4.px),
                drawPaint
            )
        }

        canvas.drawText(
            drawText,
            drawTextCoordinate.x,
            drawTextCoordinate.y,
            projectResources.paintText
        )

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.strokeWidth = 2f
        canvas.drawRect(borderRect, borderPaint);
    }

    // https://developer.android.com/reference/android/view/View.MeasureSpec.html
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY) {
            /**
             * The parent has determined an exact size for the child.
             * The child is going to be given those bounds regardless of how big it wants to be.
             */
        } else if (widthMode == MeasureSpec.AT_MOST) {
            /**
             * The child can be as large as it wants up to the specified size.
             * https://developer.android.com/reference/android/view/View.MeasureSpec.html
             */
            width = originTextBound.width() + 32.px
        } else {
            /** UNSPECIFIED
             * The child can be as large as it wants up to the specified size.
             */
            width = originTextBound.width() + 32.px
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = originTextBound.height() + 24.px + imageSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = originTextBound.height() + 24.px + imageSize
        } else {
            // UNSPECIFIED
            height = originTextBound.height() + 24.px + imageSize
        }
        setMeasuredDimension(width, height)
    }

    private fun Rect.calculateCenterY(): Float {
        return abs((bottom - top) / 2f)
    }

    private fun Rect.calculateCenterX(): Float {
        return abs((right - left) / 2f)
    }

    class Coordinate(var x: Float = 0f, var y: Float = 0f)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bitmap = null
    }
}
