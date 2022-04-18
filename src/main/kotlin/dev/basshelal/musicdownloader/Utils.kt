@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.musicdownloader

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.system.exitProcess

typealias OnNullCallback = (property: KProperty<*>) -> Throwable

/**
 * Almost identical to kotlin keyword `lateinit` except allows for `val` and lets you throw custom exceptions and
 * handling code for when the value is not yet initialized
 */
internal class LateInit<T : Any>(initialVal: T? = null,
                                 val onNull: OnNullCallback? = null) : ReadWriteProperty<Any?, T> {
    private var value: T? = initialVal

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            this.value ?: this.onNull?.let { throw it(property) } ?: throw NullPointerException()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

internal inline fun printErr(message: Any?)  = System.err.println(message)

internal inline fun exit(code: Int = 0): Nothing = exitProcess(code)