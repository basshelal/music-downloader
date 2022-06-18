package dev.basshelal.musicdownloader

import dev.basshelal.musicdownloader.config.ApplicationConfig
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.addShutdownHook
import dev.basshelal.musicdownloader.core.formattedLocalDateTime
import dev.basshelal.musicdownloader.downloader.Downloader
import dev.basshelal.musicdownloader.log.Log

fun main(args: Array<String>) {

    Log.addLogFile(null, "$formattedLocalDateTime.log")

    addShutdownHook { Log.e("Exiting!") }

    ApplicationConfig.initialize(args)

    YoutubeDL.update(ApplicationConfig.downloaderExec)

    Log.v("initialized!")

    Downloader.initialize()

    Log.v("starting!")

    Downloader.start()

    Log.v("started!")

    // TODO: 16-Jun-2022 @basshelal: Song data editor including tags such as Title Artist Album in addition to custom
    //  tags

}