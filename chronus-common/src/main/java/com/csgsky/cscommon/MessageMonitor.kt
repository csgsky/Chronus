package com.csgsky.cscommon;

import android.os.*
import android.util.Log
import com.csgsky.cscommon.storage.DefaultShouldRemoveDelegate
import com.csgsky.cscommon.storage.SuperCache
import com.csgsky.cscommon.utils.AppExecutors
import java.lang.StringBuilder
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap

object MessageMonitor {
    private var callback: MonitorCallback? = null
    private var innerData: InnerMessageMonitorData? = null
    private var messageSeq: Long = 0L //消息序号
    private var currentThreadName = Thread.currentThread().name;
    private var currentInnerMessage = InnerMessage(currentThreadName)
    private val odd = AtomicBoolean(false)

    @JvmStatic
    fun setMonitorCallBack(callback: MonitorCallback?) {
        this.callback = callback
    }

    internal fun getCallBack(): MonitorCallback? {
        return callback
    }

    /**
     * run after looper prepare
     */
    @JvmStatic
    fun startMonitor(monitorConfig: MonitorConfig) {
        if (innerData != null) return
        createCurrentThreadMonitorData(monitorConfig)
        startMonitorInner()
    }

    private fun report(reason: String, type: String) {
        innerReport(innerData, reason, type)
    }

    private fun startMonitorInner() {
        Looper.myLooper()?.setMessageLogging {
            if (it.startsWith("<<<<< Finished to ") == true and !odd.get()) return@setMessageLogging
            if (!odd.get()) {
                msgStart(it)
            } else {
                msgEnd(it)
            }
            odd.set(!odd.get())
        }
        Looper.myQueue().addIdleHandler {
            if (innerData?.lagCache?.needReport == true) {
                report(
                    "System get idle at ${SystemClock.uptimeMillis()} and has lag or cost too much time",
                    MonitorCallback.MESSAGE_SLOW
                )
            }
            true
        }
    }

    private fun msgStart(msg: String) {
        val dispatchStartTime = SystemClock.elapsedRealtime()
        Log.d(
            "MessageMonitorLog",
            "--single--message-----------------------start-----------------------\n-"
        )
        Log.d("MessageMonitorLog", msg)
        Log.d("MessageMonitorLog", "dispatchStartTime: $dispatchStartTime")
        // todo 增加一个 idle 消息
        currentInnerMessage = InnerMessage(currentThreadName)
        currentInnerMessage.messageSeq = messageSeq
        val text = msg.substring(">>>>> Dispatching to ".length, msg.length)
        val subs = text.split(" ")
        currentInnerMessage.handler = try {
            subs.getOrNull(1) ?: ""
        } catch (e: Throwable) {
            ""
        }
        currentInnerMessage.callback = try {
            subs.getOrNull(3) ?: ""
        } catch (e: Throwable) {
            ""
        }
        currentInnerMessage.what = try {
            subs.getOrNull(4)?.toInt() ?: -1
        } catch (e: Throwable) {
            0
        }
        currentInnerMessage.start(dispatchStartTime)
    }

    @Synchronized
    private fun msgEnd(msg: String) {
        val dispatchEndTime = SystemClock.elapsedRealtime()
        Log.d("MessageMonitorLog", msg)
        Log.d("MessageMonitorLog", "dispatchEndTime: $dispatchEndTime")
        currentInnerMessage.finish(dispatchEndTime)
        with(innerData) {
            if (this == null) {
                callback?.monitorError(
                    "GetMonitorDataError",
                    "MonitorError: innerProcess get monitorCache == null"
                )
                return
            }
            if (currentInnerMessage.cost > monitorConfig.messageCost) {
                lagCache?.needReport = true
                currentInnerMessage.isSlow = true
            }
            lagCache.put(
                currentInnerMessage.messageSeq.toString(),
                currentInnerMessage
            )
        }
        messageSeq++
    }

    private fun innerReport(innerData: InnerMessageMonitorData?, reason: String, type: String) {
        val safeInnerData = innerData ?: return
        val copyData = InnerMessageMonitorData(
            safeInnerData.monitorConfig,
            SuperCache.copy(safeInnerData.lagCache)
        )
        AppExecutors.getInstance().networkIO().execute {
            val reportData = formatReportData(copyData, type)
            if (reportData.isNotBlank()) {
                Log.d("MessageMonitorLog", "------------inner report----------------")
                callback?.reportLagMessages(
                    type,
                    "{\"reason\": \"$reason\",\"messageData\": $reportData}"
                )
            }
            safeInnerData.lagCache.needReport = false
        }
    }

    fun getMainReportMessageData(): String {
        if (innerData == null) {
            callback?.monitorError(
                "GetMonitorDataError",
                "MonitorError: getMainReportMessageData get monitorCache == null"
            )
            return ""
        }
        return formatReportData(innerData, MonitorCallback.MESSAGE_SLOW)
    }

    private fun formatReportData(
        innerData: InnerMessageMonitorData?,
        type: String
    ): String {
        val reportData = StringBuilder("[")
        val iterator = innerData?.lagCache?.innerCache?.iterator()
        if (type == MonitorCallback.MESSAGE_SLOW) {
            while (iterator?.hasNext() == true) {
                val entry = iterator.next()
                reportData.append(entry.value.toString())
                reportData.append(",")
            }
        }

        if (reportData.length > 1 && reportData.lastIndexOf(",") > 0) {
            reportData.deleteCharAt(reportData.lastIndexOf(","))
        }
        reportData.append("]")
        return reportData.toString()
    }

    private fun createCurrentThreadMonitorData(config: MonitorConfig): InnerMessageMonitorData {
        val innerData = InnerMessageMonitorData(
            config,
            SuperCache(config.saveCount, DefaultShouldRemoveDelegate())
        )
        this.innerData = innerData
        return innerData
    }

    fun getMainInnerData(): InnerMessageMonitorData? {
        return innerData
    }
}