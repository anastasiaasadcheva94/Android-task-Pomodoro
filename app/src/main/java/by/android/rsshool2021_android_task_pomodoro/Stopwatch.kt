package by.android.rsshool2021_android_task_pomodoro

data class Stopwatch(
    val id: Int,
    val startTime: Long,
    var currentMs: Long,
    var isStarted: Boolean,
    var isFinished: Boolean
)
