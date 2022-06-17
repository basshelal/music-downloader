@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.musicdownloader.core

import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.convert
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean
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

internal class AtomicBooleanDelegate(initialVal: Boolean = false) : ReadWriteProperty<Any?, Boolean> {
    private val value: AtomicBoolean = AtomicBoolean(initialVal)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = this.value.get()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean): Unit = this.value.set(value)
}

internal inline fun printErr(message: Any?) = System.err.println(message)

internal inline fun exit(code: Int = 0): Nothing = exitProcess(code)

internal inline fun addShutdownHook(func: Runnable) {
    Runtime.getRuntime().addShutdownHook(Thread(func))
}

internal inline fun Thread.blockUntil(
        millis: Long = 10,
        condition: (Thread) -> Boolean) {
    while (!condition(this)) Thread.sleep(millis)
}

internal inline fun Int.hoursToMinutes(): Int = this * 60

internal inline fun RawOption.boolean() = convert { java.lang.Boolean.valueOf(it) }

internal inline val formattedLocalDateTime: String
    get() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS E dd-MM-yyyy"))