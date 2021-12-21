package com.csgsky.cscommon

import android.os.Message
import android.os.SystemClock
import android.util.Log
import kotlin.math.cos

/**
 *  created by chenshaogang on 2021/11/30
 *  description: 消息实体
 */
data class InnerMessage(val currentThread: String) {
    var messageSeq: Long = 0L // 消息的唯一性和顺序性
    var dispatchStartTime = 0L
    var dispatchEndTime = 0L
    var isSlow = false
    var cost = 0L
    var handler: String = ""
    var callback: String = ""
    var what: Int = 0


    fun start(dispatchStartTime: Long) {
        this.dispatchStartTime = dispatchStartTime
    }

    fun finish(dispatchEndTime: Long) {
        this.dispatchEndTime = dispatchEndTime
        cost = dispatchEndTime - dispatchStartTime
        Log.d("MessageMonitorLog", "messageSeq: $messageSeq ,cost: $cost")
    }

    fun clear() {
        dispatchStartTime = 0L
        dispatchEndTime = 0L
        isSlow = false
        cost = 0L
        handler = ""
        callback = ""
        what = 0
    }

    override fun toString(): String {
        return "{\"seq\":\"$messageSeq\"," +
                "\"currentThread\":\"$currentThread\"," +
                "\"dispatchStartTime\":\"$dispatchStartTime\"," +
                "\"dispatchEndTime\":\"$dispatchEndTime\"," +
                "\"isSlow\":\"$isSlow\"," +
                "\"cost\":\"$cost\"," +
                "\"handler\":\"$handler\"," +
                "\"callback\":\"$callback\"," +
                "\"what\":\"$what\"}"
    }
}