package com.csgsky.cscommon

class MessageMonitorBuilder private constructor() {
    private var saveCount = 200
    private var duration = 5000L
    private var lagTime = 500L
    private var messageCost = 100L
    private var idleClear = false

    companion object{

        @JvmStatic
        fun startBuilder(): MessageMonitorBuilder{
            return MessageMonitorBuilder()
        }
    }

    fun setSaveCount(saveCount: Int): MessageMonitorBuilder{
        this.saveCount = saveCount
        return this
    }

    fun setCatDuration(duration: Long): MessageMonitorBuilder{
        this.duration = duration
        return this
    }

    fun setMinLagTime(lagTime: Long): MessageMonitorBuilder{
        this.lagTime = lagTime
        return this
    }

    fun setMinMessageCost(messageCost: Long): MessageMonitorBuilder{
        this.messageCost = messageCost
        return this
    }

    fun setIdleClear(idleClear: Boolean): MessageMonitorBuilder{
        this.idleClear = idleClear
        return this
    }

    fun build(): MonitorConfig {
        return  MonitorConfig(
            duration = if (duration <= 0) 5000L else duration,
            saveCount = if (saveCount <= 0) 20 else saveCount,
            lagTime = if (lagTime <= 0) 500L else lagTime,
            messageCost = if (messageCost <= 0) 100L else messageCost,
            clearAtIdle = idleClear
        )
    }

//    fun startMonitor() {
//        MessageMonitor.startMonitor(MonitorConfig(
//            duration = if (duration <= 0) 5000L else duration,
//            saveCount = if (saveCount <= 0) 20 else saveCount,
//            lagTime = if (lagTime <= 0) 500L else lagTime,
//            messageCost = if (messageCost <= 0) 100L else messageCost,
//            clearAtIdle = idleClear
//        ))
//    }
}