@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.musicdownloader.log

import dev.basshelal.musicdownloader.core.AtomicValueDelegate
import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.core.formattedLocalDateTime
import dev.basshelal.musicdownloader.core.println
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

object Log {

    enum class Level { NONE, ERROR, WARN, DEBUG, INFO, VERBOSE }

    public var level: Level by AtomicValueDelegate(Level.INFO)

    private val streams: MutableMap<Level, MutableList<BufferedWriter>> = mutableMapOf()

    init {
        streams[Level.ERROR] = mutableListOf(BufferedWriter(OutputStreamWriter(System.err)))
        streams[Level.WARN] = mutableListOf(BufferedWriter(OutputStreamWriter(System.out)))
        streams[Level.DEBUG] = mutableListOf(BufferedWriter(OutputStreamWriter(System.out)))
        streams[Level.INFO] = mutableListOf(BufferedWriter(OutputStreamWriter(System.out)))
        streams[Level.VERBOSE] = mutableListOf(BufferedWriter(OutputStreamWriter(System.out)))

        addShutdownHook { Level.values().forEach { streams[it]?.forEach { it.close() } } }
    }

    public fun addLogFile(level: Level?, filePath: String): Boolean {
        val file = File(filePath)
        if (!file.createNewFile()) return false
        if (level == null) {
            val writer = FileOutputStream(file).bufferedWriter()
            Level.values().forEach { streams[it]?.also { it.add(writer) } ?: return false }
        } else {
            streams[level]?.also { it.add(FileOutputStream(file).bufferedWriter()) } ?: return false
        }
        return true
    }

    public fun e(message: String) = log(Level.ERROR, message)

    public fun w(message: String) = log(Level.WARN, message)

    public fun d(message: String) = log(Level.DEBUG, message)

    public fun i(message: String) = log(Level.INFO, message)

    public fun v(message: String) = log(Level.VERBOSE, message)

    public fun log(level: Level, message: String): Unit {
        // TODO: 17-Jun-2022 @basshelal: Add colors
        val time: String = formattedLocalDateTime
        streams[level]?.forEach {
            if (level <= this.level)
                it.println("$time [${level.name}] : " + message)
        }
    }
}

public inline fun logE(message: String) = Log.e(message)
public inline fun logW(message: String) = Log.w(message)
public inline fun logD(message: String) = Log.d(message)
public inline fun logI(message: String) = Log.i(message)
public inline fun logV(message: String) = Log.v(message)

@JvmName("logEExt")
public inline fun String.logE() = Log.e(this)

@JvmName("logWExt")
public inline fun String.logW() = Log.w(this)

@JvmName("logDExt")
public inline fun String.logD() = Log.d(this)

@JvmName("logIExt")
public inline fun String.logI() = Log.i(this)

@JvmName("logVExt")
public inline fun String.logV() = Log.v(this)