package by.android.rsshool2021_android_task_pomodoro

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import by.android.rsshool2021_android_task_pomodoro.adapter.StopwatchAdapter
import by.android.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import by.android.rsshool2021_android_task_pomodoro.interfaces.StopwatchListener
import by.android.rsshool2021_android_task_pomodoro.service.ForegroundService
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {
    private lateinit var binding: ActivityMainBinding
    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var min = 0L
    private var sec = 0L
    private var nextId = 0
    private var startedTimerId = -1
    private var startTime = 0L
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.editTextMin.addTextChangedListener() {
            min = binding.editTextMin.text.toString().toLongOrNull()?.times(60000L) ?: 0L
        }

        binding.editTextSec.addTextChangedListener {
            sec = binding.editTextSec.text.toString().toLongOrNull()?.times(1000L) ?: 0L
        }
        binding.addNewStopwatchButton.setOnClickListener {
            if (checkValues()) {
                startTime = min + sec
                stopwatches.add(
                    Stopwatch(
                        nextId++,
                        startTime,
                        startTime,
                        isStarted = false,
                        isFinished = false
                    )
                )
                stopwatchAdapter.submitList(stopwatches.toList())
            } else {
                checkValues()
            }
        }
    }

    private fun checkValues(): Boolean {
        return if (min > 0 || sec > 0) {
            true
        } else {
            Toast.makeText(this, "Input values", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun start(id: Int) {
        if (startedTimerId != -1) {
            val timer = stopwatches.find { it.id == startedTimerId }
            timer?.let { stop(startedTimerId, timer.currentMs) }
        }

        timer?.cancel()
        timer = stopwatches.find { it.id == id }?.let { getCountDownTimer(it) }
        timer?.start()

        startedTimerId = id
        changeStopwatch(id, null, isStarted = true, isFinished = false)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, isStarted = false, isFinished = true)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(
        id: Int,
        currentMs: Long?,
        isStarted: Boolean,
        isFinished: Boolean
    ) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(
                    Stopwatch(
                        it.id,
                        startTime,
                        currentMs ?: it.currentMs,
                        isStarted,
                        isFinished
                    )
                )
            } else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }


    private fun getCountDownTimer(timer: Stopwatch): CountDownTimer {
        return object : CountDownTimer(startTime, INTERVAL) {

            override fun onTick(millisUntilFinished: Long) {
                changeStopwatch(timer.id, millisUntilFinished, isStarted = true, isFinished = false)
            }

            override fun onFinish() {
                changeStopwatch(timer.id, 0L, isStarted = false, isFinished = true)
            }

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startService(startIntent)
        Log.d("TAG", "onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}