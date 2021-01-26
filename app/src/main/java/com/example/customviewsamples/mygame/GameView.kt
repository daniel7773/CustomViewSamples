package com.example.customviewsamples.mygame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.example.customviewsamples.R

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var xDir = 1
    var yDir = 1

    var cherryBitmap: Bitmap? = null
    var unscaledCherryBitmap: Bitmap = BitmapFactory.decodeResource(
        resources,
        R.drawable.cherry
    )

    var cherryX = 10f
    var cherryY = 10f

    var cherryHeight = 120f
    var cherryWidth = 120f


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        cherryBitmap?.let { cherryBitmap ->
            canvas?.run {
                if (cherryX >= width - cherryWidth) {
                    xDir = -1
                }

                if (cherryX <= 0) {
                    xDir = 1
                }

                if (cherryY >= height - cherryHeight) {
                    yDir = -1
                }

                if (cherryY <= 0) {
                    yDir = 1
                }
            }
        }


        cherryX += xDir
        cherryY += yDir

        cherryBitmap?.run {
            canvas?.drawBitmap(this, cherryX, cherryY, null)
        }

        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        cherryBitmap = Bitmap.createScaledBitmap(
            unscaledCherryBitmap,
            cherryWidth.toInt(),
            cherryHeight.toInt(),
            false
        )
    }
}