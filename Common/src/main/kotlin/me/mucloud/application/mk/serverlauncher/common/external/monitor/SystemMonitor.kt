package me.mucloud.application.mk.serverlauncher.common.external.monitor

import com.sun.management.OperatingSystemMXBean
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool
import java.lang.management.ManagementFactory
import java.util.*
import kotlin.concurrent.schedule

object SystemMonitor {

    private var Interval: Int = 3 // Seconds
    private lateinit var Tsk: TimerTask
    private lateinit var MonitorFlow: MutableSharedFlow<StatusPacket>

    fun initMonitor(interval: Int){ Interval = interval
        MonitorFlow = MutableSharedFlow()
        runBlocking {
            Tsk = async { Timer().schedule(0L, Interval * 1000L){
                val os = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
                val cpuUsage = os.systemCpuLoad * 100
                val memoryUsage = ((os.totalPhysicalMemorySize - os.freePhysicalMemorySize).toDouble() / os.totalPhysicalMemorySize) *100

                runBlocking {
                    MonitorFlow.emit(StatusPacket(
                        SystemStatus(cpuUsage, memoryUsage),
                        ServerStatus(ServerPool.getTotalServer(), ServerPool.getOnlineServerCount(), ServerPool.getOfflineServerCount()),
                        AppInfoStatus("MuCore HPE DEV Mini", "LovePoem V1 DEV.1", 0, 2),
                    ))
                }
            }}.await()
        }
    }

    fun getStatus(): SharedFlow<StatusPacket> {
        return MonitorFlow.asSharedFlow()
    }

    fun close(){
        Tsk.cancel()
    }
}

@Serializable
data class StatusPacket(
    private val systemStatus: SystemStatus,
    private val serverStatus: ServerStatus,
    private val appInfoStatus: AppInfoStatus
)

@Serializable
data class SystemStatus(
    private val CpuUsage: Double,
    private val MemUsage: Double,
)

@Serializable
data class ServerStatus(
    private val totalServer: Int,
    private val onlineServer: Int,
    private val offlineServer: Int,
)

@Serializable
data class AppInfoStatus(
    private val core: String,
    private val ver: String,
    private val pluginCount: Int,
    private val templatePackCount: Int,
)
