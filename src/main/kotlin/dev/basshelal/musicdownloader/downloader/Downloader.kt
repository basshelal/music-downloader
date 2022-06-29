package dev.basshelal.musicdownloader.downloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.AudioFormat
import dev.basshelal.musicdownloader.core.LateInit
import dev.basshelal.musicdownloader.core.QualityFormat
import dev.basshelal.musicdownloader.core.ThumbnailFormat
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.core.format
import dev.basshelal.musicdownloader.core.isFile
import dev.basshelal.musicdownloader.core.mkfl
import dev.basshelal.musicdownloader.core.now
import dev.basshelal.musicdownloader.core.readDir
import dev.basshelal.musicdownloader.core.threads.LoopingThread
import dev.basshelal.musicdownloader.filesystem.updater.ApplicationUpdater
import dev.basshelal.musicdownloader.log.logE
import dev.basshelal.musicdownloader.log.logI
import dev.basshelal.musicdownloader.log.logV
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object Downloader {

    private var builder: YoutubeDL.Builder by LateInit()
    private var youtubeDL: YoutubeDL by LateInit()

    private val thread = LoopingThread {
        "Checking for music-downloader updates".logV()

        if (ApplicationUpdater.isUpdateAvailable()) {
            "An update is available, updating and restarting".logI()
            ApplicationUpdater.exitToUpdate()
        }

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

        "Formats to download: ${ApplicationConfig.formats.joinToString(",")}".logV()
        "Input files: ${readDir(ApplicationConfig.inputDir).joinToString(",")}".logV()

        ApplicationConfig.formats.forEach { format: String ->
            "Starting download for format: $format".logV()
            readDir(ApplicationConfig.inputDir).forEach { inputFilePath: String ->
                "Starting download for input file: $inputFilePath".logV()
                builder.format(AudioFormat.fromString(format))
                        .batchFile(inputFilePath)

                ApplicationConfig.cookies.also { cookiesPath: String ->
                    if (isFile(cookiesPath)) {
                        "Using cookies file: $cookiesPath".logV()
                        builder.cookies(cookiesPath)
                    } else {
                        logE("Cookies file: $cookiesPath is invalid")
                    }
                }

                val archiveFileName = ApplicationConfig.archivesDir + File.separator + File(inputFilePath).name
                if (!isFile(archiveFileName) && !mkfl(archiveFileName)) {
                    logE("Could not create archive file: ${archiveFileName}, " +
                            "it may already exist as a directory or its parent directory may have restricted write " +
                            "permissions for this user")
                } else {
                    "Using archive file: $archiveFileName".logV()
                    builder.archive(archiveFileName)
                }

                val outputTemplate = "${ApplicationConfig.outputDir}/%(uploader)s/%(title)s-%(uploader)s-%(id)s.%(ext)s"
                builder.output(outputTemplate)

                "Downloader starting YoutubeDL".logV()
                youtubeDL = builder
                        .build().start().blockUntilCompletion()
                "Downloader finished YoutubeDL, restarting".logV()
            }
        }
        val timeToWake: LocalDateTime = now.plus(ApplicationConfig.rescanPeriod.toLong(), ChronoUnit.MINUTES)
        "Downloading finished, sleeping until: ${timeToWake.format()}".logI()
        Thread.sleep(ApplicationConfig.rescanPeriod * (60L * 1000L))
    }

    fun initialize() {
        "Initializing Downloader".logV()
        addShutdownHook { stop() }
    }

    fun start() {
        "Starting Downloader".logV()
        thread.start()
    }

    fun stop() {
        "Stopping Downloader".logV()
        youtubeDL.stop()
        thread.stop()
    }
}