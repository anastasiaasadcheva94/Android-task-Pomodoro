package by.android.rsshool2021_android_task_pomodoro

data class Stopwatch(
    val id:Int, //чтобы отличать айтемы друг от друга
    var currentMs: Long, // количество миллисекунд прошедших со старта
    var isStarted: Boolean //работает ли секундомер или остановлен
)
