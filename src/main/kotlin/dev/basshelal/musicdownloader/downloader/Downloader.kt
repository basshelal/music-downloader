package dev.basshelal.musicdownloader.downloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.AudioFormat
import dev.basshelal.musicdownloader.core.LateInit
import dev.basshelal.musicdownloader.core.QualityFormat
import dev.basshelal.musicdownloader.core.ThumbnailFormat
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.core.isFile
import dev.basshelal.musicdownloader.core.mkfl
import dev.basshelal.musicdownloader.core.readDir
import dev.basshelal.musicdownloader.core.threads.LoopingThread
import dev.basshelal.musicdownloader.log.logE
import java.io.File

object Downloader {

    private var builder: YoutubeDL.Builder by LateInit()
    private var youtubeDL: YoutubeDL by LateInit()

    private val thread = LoopingThread {
        // Add default common options first
        builder = YoutubeDL.builder()
                .exec(ApplicationConfig.downloaderExec)
                .progress()
                .embedThumbnail()
                .embedMetadata()
                .thumbnailFormat(ThumbnailFormat.PNG)
                .extractAudio()
                .quality(QualityFormat.BEST)
                .ignoreErrors()
                .noWarnings()
                .retries(25)
                .sleepInterval(5)

        ApplicationConfig.formats.forEach { format: String ->
            readDir(ApplicationConfig.inputDir).forEach { inputFilePath: String ->
                builder.format(AudioFormat.fromString(format))
                        .batchFile(inputFilePath)

                ApplicationConfig.cookies.also { cookiesPath: String ->
                    if (isFile(cookiesPath)) {
                        builder.cookies(cookiesPath)
                    } else {
                        logE("Cookies file: $cookiesPath is invalid")
                    }
                }

                val archiveFileName = ApplicationConfig.archivesDir + File.pathSeparator + inputFilePath
                if (!isFile(archiveFileName) && !mkfl(archiveFileName)) {
                    logE("Could not create archive file: ${archiveFileName}, " +
                            "it may already exist as a directory or its parent directory may have restricted write " +
                            "permissions for this user")
                }
                builder.archive(archiveFileName)

                val outputTemplate = "${ApplicationConfig.outputDir}/%(uploader)s/%(title)s-%(uploader)s-%(id)s.%(ext)s"
                builder.output(outputTemplate)

                youtubeDL = builder
                        .build().start().blockUntilCompletion()
            }
        }
    }

    fun initialize() {
        addShutdownHook { stop() }
    }

    fun start() {
        thread.start()
    }

    fun stop() {
        youtubeDL.stop()
        thread.stop()
    }
}