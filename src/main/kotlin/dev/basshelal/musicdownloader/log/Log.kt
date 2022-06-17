package dev.basshelal.musicdownloader.log

import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.core.formattedLocalDateTime
import dev.basshelal.musicdownloader.core.println
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

object Log {

    enum class Level { ERROR, WARN, DEBUG, INFO, VERBOSE }

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
            it.println("$time [${level.name}] : " + message)
        }
    }
}