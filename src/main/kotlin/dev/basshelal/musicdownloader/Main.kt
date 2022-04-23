package dev.basshelal.musicdownloader

fun main(args: Array<String>) {

    ApplicationConfig.initialize(args)

    Downloader.initialize()

    Downloader.start()

}