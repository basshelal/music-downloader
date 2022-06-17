package dev.basshelal.musicdownloader.downloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.AudioFormat
import dev.basshelal.musicdownloader.core.QualityFormat
import dev.basshelal.musicdownloader.core.ThumbnailFormat
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.core.threads.LoopingThread
import dev.basshelal.musicdownloader.log.Log

object Downloader {

    private var youtubeDL: YoutubeDL? = null

    private val thread = LoopingThread {
        val ytdlExec: String = ApplicationConfig.executable

        Log.i("Starting $ytdlExec")

        youtubeDL = YoutubeDL.builder()
                .exec(ytdlExec)
                .simulate()
                .quiet()
                .progress()
                .url("https://www.youtube.com/watch?v=51aIQc6E4AI")
                .extractAudio()
                .format(AudioFormat.FLAC)
                .quality(QualityFormat.BEST)
                .embedThumbnail()
                .thumbnailFormat(ThumbnailFormat.PNG)
                .embedMetadata()
                .sleepIntervalMin(5)
                .noWarnings()
                .ignoreErrors()
                .retries(100)
                .build().start().blockUntilCompletion()

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