package com.csgsky.cscommon

import android.os.Message
import com.csgsky.cscommon.storage.SuperCache

/**
 *  created by chenshaogang on 2021/11/30
 *  description:
 */
data class InnerMessageMonitorData(
    val monitorConfig: MonitorConfig,
    val lagCache: SuperCache<String, InnerMessage>
)