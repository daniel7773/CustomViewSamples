package com.example.customviewsamples.chart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.customviewsamples.databinding.BackgroundBubblesViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class BackgroundBubblesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): CardView(context, attrs, defStyleAttr), CoroutineScope {

    private val binding = BackgroundBubblesViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    init {
        binding.bubblesBackground.background = BackgroundDrawable(context)
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext // guaranties that when it's lifecycle ends everything gonna be cancelled
        get() = job + Dispatchers.Main

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        chartRenderer.init(dataProvider.getHistoryData(15))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
    }

}