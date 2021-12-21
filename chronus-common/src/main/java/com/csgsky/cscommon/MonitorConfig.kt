package com.csgsky.cscommon

/**
 *  created by chenshaogang on 2021/11/30
 *  description:
 */
class MonitorConfig(
    val duration: Long = 5000L,
    val saveCount: Int = 20,
    val lagTime: Long = 500L,
    val messageCost: Long = 500L,
    val clearAtIdle: Boolean = false
)