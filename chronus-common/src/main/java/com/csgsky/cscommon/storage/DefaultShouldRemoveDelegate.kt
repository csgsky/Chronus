package com.csgsky.cscommon.storage;

import android.os.SystemClock
import android.util.Log
import com.csgsky.cscommon.InnerMessage
import com.csgsky.cscommon.MessageMonitor

/**
 * removeEldestEntry 逻辑，不要有其他任何无关的逻辑放进去
 */
class DefaultShouldRemoveDelegate : SuperCache.ShouldRemoveDelegate<String, InnerMessage> {

    override fun removeEldestEntry(
        cache: SuperCache<String, InnerMessage>,
        entry: MutableMap.MutableEntry<String, InnerMessage>?
    ): Boolean {
        // 消息数小于最大数量
        if (cache.innerCache.size <= MessageMonitor.getMainInnerData()?.monitorConfig?.saveCount ?: 100) {
            Log.d("MessageMonitorLog", "--single--message---------------------end-------------------------\n-")
            return false
        }
        Log.d("MessageMonitorLog", "--single--message----------------------------------------------\n-")
        // 消息中存在卡顿
        return true
    }
}