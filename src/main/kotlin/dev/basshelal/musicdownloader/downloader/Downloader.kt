package dev.basshelal.musicdownloader.downloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.LoopingThread
import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.log.Log

object Downloader {

    private val thread = LoopingThread {
        val ytdlExec: String = ApplicationConfig.executable

        Log.i("Starting $ytdlExec")

        ProcessBuilder(ytdlExec, "--version")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start().waitFor()

    }

    fun initialize() {
        addShutdownHook { stop() }
    }

    fun start() {
        thread.start()
    }

    fun stop() {
        thread.stop()
    }
}