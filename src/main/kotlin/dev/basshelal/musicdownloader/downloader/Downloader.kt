package dev.basshelal.musicdownloader.downloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.threads.LoopingThread
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.log.Log
import java.io.File

object Downloader {

    private var youtubeDL: YoutubeDL? = null

    private val thread = LoopingThread {
        val ytdlExec: String = ApplicationConfig.executable

        Log.i("Starting $ytdlExec")

        youtubeDL = YoutubeDL.builder()
                .exec(ytdlExec)
                .addArg("--version")
                .build().start().blockUntilCompletion()

        Thread.sleep(2000)

    }

    fun initialize() {
        addShutdownHook { stop() }
    }

    fun start() {
        thread.start()
    }

    fun stop() {
        youtubeDL?.stop()
        thread.stop()
    }
}