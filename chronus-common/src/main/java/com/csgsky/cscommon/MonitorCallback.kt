package com.csgsky.cscommon

interface MonitorCallback {
    companion object{
        const val MESSAGE_SLOW = "MESSAGE_SLOW"
    }

    fun monitorError(errorType: String, errorLog: String)

    fun reportLagMessages(errorType:String, messages: String)
}