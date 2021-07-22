package by.android.rsshool2021_android_task_pomodoro.viewHolder

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import by.android.rsshool2021_android_task_pomodoro.*
import by.android.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import by.android.rsshool2021_android_task_pomodoro.interfaces.StopwatchListener
import by.android.rsshool2021_android_task_pomodoro.utils.getCountDownTimer

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {
    private var timer: CountDownTimer? = null

    private var startTime = 0L

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
            } else {
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
        timer = getTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()


        //TODO add custom view
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

    private fun getTimer(stopwatch: Stopwatch) =
        getCountDownTimer(
            stopwatch.currentMs,
            tick = {
                binding.stopwatchTimer.text = it.displayTime()
                stopwatch.currentMs=it
                binding.customCircle.setCurrent(it)
            },
            finish = {
                binding.stopwatchTimer.text = START_TIME

                binding.customCircle.setCurrent(0)
                stopwatch.isStarted = false

                binding.root.setCardBackgroundColor(resources.getColor(R.color.purple_500, resources.newTheme()))
            }
        )
}

