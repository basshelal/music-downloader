package dev.basshelal.musicdownloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.formattedLocalDateTime
import dev.basshelal.musicdownloader.core.path
import dev.basshelal.musicdownloader.downloader.Downloader
import dev.basshelal.musicdownloader.log.Log
import dev.basshelal.musicdownloader.log.logI

fun main(args: Array<String>) {

    Log.addLogFile(filePath = path("./.log/$formattedLocalDateTime.log"))

    Log.level = Log.Level.VERBOSE

    ApplicationConfig.initialize(args)

    YoutubeDL.update(ApplicationConfig.downloaderExec)

    Downloader.initialize()

    Downloader.start()

    // TODO: 16-Jun-2022 @basshelal: Song data editor including tags such as Title Artist Album in addition to custom
    //  tags

}