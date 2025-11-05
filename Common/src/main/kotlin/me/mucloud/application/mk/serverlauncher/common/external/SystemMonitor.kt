package me.mucloud.application.mk.serverlauncher.common.external

import com.sun.management.OperatingSystemMXBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.mucloud.application.mk.serverlauncher.common.MuCoreMini
import java.lang.management.ManagementFactory
import java.util.*
import kotlin.concurrent.schedule

class SystemMonitor(
    private val core: MuCoreMini
) {

    private var Interval: Int = core.getMuCoreConfig().getSystemMonitorInterval() // Seconds
    private lateinit var Tsk: TimerTask
    private var MonitorFlow: MutableStateFlow<StatusPacket> = MutableStateFlow(getCurrentStatus())

    private fun getCurrentStatus(): StatusPacket {
        val os = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        val cpuUsage = os.cpuLoad * 100
        val memoryUsage = ((os.totalMemorySize - os.freeMemorySize).toDouble() / os.totalMemorySize) *100
        val serverPool = core.getServerPool()
        val coreInfo = core.getMuCoreInfo()

        return StatusPacket(
            SystemStatus(cpuUsage, memoryUsage),
            ServerStatus(serverPool.getTotalServer(), serverPool.getOnlineServerCount(), serverPool.getOfflineServerCount()),
            coreInfo
        )
    }

    fun initMonitor(){
        CoroutineScope(Dispatchers.IO).launch {
            Tsk = async {
                Timer().schedule(0L, Interval * 1000L) {
                    launch { MonitorFlow.emit(getCurrentStatus()) }
                }
            }.await()
        }
    }

    fun getStatus(): StateFlow<StatusPacket> = MonitorFlow.asStateFlow()

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
)
