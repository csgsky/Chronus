package com.csgsky.cscommon.storage;

class SuperCache<K, V>(val capacity: Int, val shouldRemoveDelegate: ShouldRemoveDelegate<K, V>?) {

    val innerCache: LinkedHashMap<K, V>
    var lastValue: V? = null
    var lastBefore: V? = null
    var needReport: Boolean = false

    constructor(superCache: SuperCache<K, V>) : this(superCache.capacity, null) {
        innerCache.putAll(superCache.innerCache)
    }

    companion object {
        @JvmStatic
        fun <K, V> copy(superCache: SuperCache<K, V>): SuperCache<K, V> {
            return SuperCache(superCache)
        }
    }

    init {
        innerCache = object : LinkedHashMap<K, V>(capacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
                return shouldRemoveDelegate?.removeEldestEntry(this@SuperCache, eldest) ?: false
            }
        }
    }

    interface ShouldRemoveDelegate<K, V> {
        fun removeEldestEntry(
            cache: SuperCache<K, V>,
            entry: MutableMap.MutableEntry<K, V>?
        ): Boolean
    }

    fun put(key: K, value: V) {
        lastBefore = lastValue
        lastValue = value
        innerCache[key] = value
    }

    fun clear() {
        needReport = false
        innerCache.clear()
    }
}