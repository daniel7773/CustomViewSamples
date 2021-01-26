package com.example.customviewsamples.customscroll

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.example.customviewsamples.chart.ChartView
import com.example.customviewsamples.moving_square.MyView
import kotlin.math.roundToInt

class CustomScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ScrollView(context, attrs, defStyleAttr) {

    private val TAG = "CustomScrollView"

    private fun isWithinBounds(view: View, ev: MotionEvent): Boolean {
        val xPoint = ev.rawX.roundToInt()
        val yPoint = ev.rawY.roundToInt()
        val l = IntArray(2)
        view.getLocationOnScreen(l)
        val x = l[0]
        val y = l[1]
        val w = view.width
        val h = view.height
        return !(xPoint < x || xPoint > x + w || yPoint < y || yPoint > y + h)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        for (childCounter in 0 until this.childCount) { // for loop for ScrollView layout

            if (this.getChildAt(childCounter) !is ViewGroup) {
                throw Exception("This custom scroll view should contains 1 ViewGroup inside it")
            }

            val child: ViewGroup = this.getChildAt(childCounter) as ViewGroup

            for (innerChildCounter in 0 until child.childCount) { // for loop for ScrollView layout children
                val innerChild = child.getChildAt(innerChildCounter)

                ev?.run {
                    if ((innerChild is ChartView || innerChild is MyView) && isWithinBounds(innerChild, this)) {
                        return false
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}