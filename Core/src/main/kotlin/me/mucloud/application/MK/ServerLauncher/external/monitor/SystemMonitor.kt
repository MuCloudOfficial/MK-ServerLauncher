package me.mucloud.application.MK.ServerLauncher.external.monitor

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

object SystemMonitor {

    private var Interval: Int = 3 // Seconds
    private lateinit var Tsk: TimerTask

    fun initMonitor(interval: Int){ Interval = interval
        Tsk = Timer().schedule(0L, Interval * 1000L){
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val availableMemory = totalMemory - freeMemory
            val availableMemoryPercent = availableMemory.toFloat() / totalMemory
        }
    }
}

data class SystemStatus(
    private val CpuUsage: Float,
    private val MemUsage: Float,
)