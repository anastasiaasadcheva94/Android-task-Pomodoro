package by.android.rsshool2021_android_task_pomodoro.viewHolder

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.Log
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import by.android.rsshool2021_android_task_pomodoro.R
import by.android.rsshool2021_android_task_pomodoro.Stopwatch
import by.android.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import by.android.rsshool2021_android_task_pomodoro.displayTime
import by.android.rsshool2021_android_task_pomodoro.interfaces.StopwatchListener

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {
    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        binding.progressCircle.setPeriod(stopwatch.startTime)

        if (stopwatch.isStarted) {
            startTime(stopwatch)
        } else {
            stopTime()
        }

        if (stopwatch.isFinished) {
            val colorValue = resources.getColor(R.color.purple_500, resources.newTheme())
            binding.startButton.setBackgroundColor(colorValue)

            binding.root.setCardBackgroundColor(
                resources.getColor(
                    R.color.purple_500,
                    resources.newTheme()
                )
            )
            binding.progressCircle.setCurrent(0L)
        } else {
            val colorValue = resources.getColor(R.color.purple_200, resources.newTheme())
            binding.startButton.setBackgroundColor(colorValue)

            binding.root.setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    resources.newTheme()
                )
            )
            binding.progressCircle.setCurrent(stopwatch.currentMs)
        }

        initButtonsListeners(stopwatch)
    } //в метод bind передаем экземпляр Stopwatch, он приходит к нам из метода onBindViewHolder адаптера и содержит актуальные параметры для данного элемента списка.

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                Log.d("TAG", "id = ${stopwatch.id}")
                listener.start(stopwatch.id)
            }
        }
        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n")
    private fun startTime(stopwatch: Stopwatch) {
        binding.startButton.text = itemView.context.getString(R.string.stop)

        timer?.cancel()
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }


    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n")
    private fun stopTime() {
        binding.startButton.text = itemView.context.getString(R.string.start)

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }
}

