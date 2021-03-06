package dev.basshelal.musicdownloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.formattedLocalDateTime
import dev.basshelal.musicdownloader.core.path
import dev.basshelal.musicdownloader.downloader.Downloader
import dev.basshelal.musicdownloader.log.Log

fun main(args: Array<String>) {

    Log.addLogFile(filePath = path("./.log/$formattedLocalDateTime.log"))

    Log.level = Log.Level.VERBOSE

    ApplicationConfig.initialize(args)

    YoutubeDL.update(ApplicationConfig.downloaderExec)

    Downloader.initialize()

    Downloader.start()

}