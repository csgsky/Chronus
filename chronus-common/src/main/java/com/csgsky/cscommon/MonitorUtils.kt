package com.csgsky.cscommon

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 *  created by chenshaogang on 2021/11/30
 *  description:
 */
object MonitorUtils {
    fun getClassField(className: String, fieldName: String): Field? {
        return try {
            Class.forName(className).getDeclaredField(fieldName)
        } catch (e: Throwable) {
            null
        }
    }
}