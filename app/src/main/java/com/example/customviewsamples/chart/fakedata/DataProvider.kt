package com.example.customviewsamples.chart.fakedata

import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

object DataProvider {

    fun mainAsFlow() = flow<Float> { // flow builder
        var lastValue = 250f



        for (i in 1..299) {
//            delay(1000)
            lastValue += (-2..2).random()
            emit(lastValue)
        }
    }
}