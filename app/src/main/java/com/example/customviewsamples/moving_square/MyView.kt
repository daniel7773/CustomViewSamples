package com.example.customviewsamples.moving_square

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    var p: Paint

    // координаты для рисования квадрата
    var myX = 100f
    var myY = 100f
    var side = 100

    // переменные для перетаскивания
    var drag = false
    var dragX = 0f
    var dragY = 0f
    override fun onDraw(canvas: Canvas) {
        // рисуем квадрат
        canvas.drawRect(myX, myY, myX + side, myY + side, p)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // координаты Touch-события
        val evX = event.x
        val evY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN ->                 // если касание было начато в пределах квадрата
                if (evX >= myX && evX <= myX + side && evY >= myY && evY <= myY + side) {
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
                    if(newMyY + side < height && newMyX + side < width && newMyY >= -1 && newMyX >= -1) { // condition чтобы не выезжало за края вью
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

    init {
        p = Paint()
        p.color = Color.GREEN
    }
}
