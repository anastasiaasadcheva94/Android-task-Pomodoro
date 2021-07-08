package by.android.rsshool2021_android_task_pomodoro.interfaces

interface StopwatchListener {
    fun start(id: Int)

    fun stop(id: Int, currentMs: Long)

    fun delete(id: Int)
}