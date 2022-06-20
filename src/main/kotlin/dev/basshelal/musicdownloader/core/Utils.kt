@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.musicdownloader.core

import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.convert
import java.io.BufferedWriter
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
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

internal class AtomicValueDelegate<E>(initialVal: E) : ReadWriteProperty<Any?, E> {
    private val value: AtomicReference<E> = AtomicReference(initialVal)

    override fun getValue(thisRef: Any?, property: KProperty<*>): E = this.value.get()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: E): Unit = this.value.set(value)
}

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

internal inline val now: LocalDateTime
    get() = LocalDateTime.now()

internal inline fun LocalDateTime.format(): String = this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS"))

internal inline val formattedLocalDateTime: String
    get() = LocalDateTime.now().format()

internal inline fun <T> MutableCollection<T>.setAll(collection: Collection<T>) {
    this.clear()
    this.addAll(collection)
}

internal fun BufferedWriter.println(message: String) {
    synchronized(this) {
        this.write(message)
        this.newLine()
        this.flush()
    }
}

internal fun isDir(path: String): Boolean = File(path).isDirectory

internal fun mkdirs(path: String): Boolean = File(path).mkdirs()

internal fun isFile(path: String): Boolean = File(path).isFile

internal fun mkfl(path: String): Boolean = File(path).let {
    it.parentFile.mkdirs()
    it.createNewFile()
}

internal fun readDir(path: String): List<String> = File(path).let {
    it.listFiles()?.map { it.path }?.sorted() ?: emptyList()
}

internal fun path(path: String): String {
    var result: String = path
    if (result.startsWith("~")) {
        System.getProperty("user.home")?.also { home ->
            result = result.replaceFirst("~", home)
        }
    }
    if (result.startsWith("./")) {
        result = result.replaceFirst("./", "")
    }
    return result
}