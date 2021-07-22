package by.android.rsshool2021_android_task_pomodoro

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {
    private lateinit var binding: ActivityMainBinding
    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var min = 0L
    private var sec = 0L
    private var nextId = 0
    private var startTime = 0L
    var text:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTime = System.currentTimeMillis()

        lifecycleScope.launch(Dispatchers.Main){
            while (true){
                text = (System.currentTimeMillis() - startTime).displayTime()
                delay(INTERVAL)
            }
        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.editTextMin.addTextChangedListener {
            min = binding.editTextMin.text.toString().toLong().times(60000L)
        }

        binding.editTextSec.addTextChangedListener {
            sec = binding.editTextSec.text.toString().toLong().times(1000L)
        }

        binding.addNewStopwatchButton.setOnClickListener {
                val time = min + sec

                stopwatches.add(Stopwatch(nextId++, time, true))
                stopwatchAdapter.submitList(stopwatches.toList())
        }
    }




    override fun start(id: Int) {
        changeStopwatch(id, null, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, currentMs ?: it.currentMs, isStarted))
            } else {
                newTimers.add(it)

            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded(){
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startService(startIntent)
        Log.d("TAG", "onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded(){
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}