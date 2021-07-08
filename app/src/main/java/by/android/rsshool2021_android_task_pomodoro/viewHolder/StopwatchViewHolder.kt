package by.android.rsshool2021_android_task_pomodoro.viewHolder

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import by.android.rsshool2021_android_task_pomodoro.R
import by.android.rsshool2021_android_task_pomodoro.Stopwatch
import by.android.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import by.android.rsshool2021_android_task_pomodoro.interfaces.StopwatchListener

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
):RecyclerView.ViewHolder(binding.root) {
    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

        if (stopwatch.isStarted) {
            startTime(stopwatch)
        } else {
            stopTime()
        }

        initButtonsListeners(stopwatch)
    } //в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            }else{
                listener.start(stopwatch.id)
            }
        }
        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }



    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n")
    private fun startTime(stopwatch: Stopwatch) {
        binding.startButton.text = "Stop"

        val colorValue = resources.getColor(R.color.purple_200, resources.newTheme())
        binding.startButton.setBackgroundColor(colorValue)

        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n")
    private fun stopTime() {
        binding.startButton.text = "Start"

        val colorValue = resources.getColor(R.color.purple_500, resources.newTheme())
        binding.startButton.setBackgroundColor(colorValue)

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs += interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }
        }
    }



    private fun Long.displayTime(): String {
        if (this <= 0) {
            return START_TIME
        }

        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        val ms = this % 1000 / 10

        return "${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
    } //данный метод расширения для Long конвертирует текущее значение таймера в миллисекундах в формат “HH:MM:SS:MsMs” и возвращает соответствующую строку

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {
        private const val START_TIME = "00:00:00:00"
        private const val UNIT_TEN_MS = 10L
        private const val PERIOD = 1000L * 60L * 60L * 24L // Day
    }
}