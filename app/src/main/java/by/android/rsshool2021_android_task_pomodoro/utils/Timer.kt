package by.android.rsshool2021_android_task_pomodoro.utils

import android.os.CountDownTimer
import by.android.rsshool2021_android_task_pomodoro.INTERVAL
import by.android.rsshool2021_android_task_pomodoro.Stopwatch

fun getCountDownTimer(startTime:Long, tick: (Long) -> Unit, finish: () -> Unit): CountDownTimer {
    return object : CountDownTimer(startTime, INTERVAL) {

        override fun onTick(millisUntilFinished: Long) {
            tick(millisUntilFinished)
        }

        override fun onFinish() {
            finish()
        }
    }
}