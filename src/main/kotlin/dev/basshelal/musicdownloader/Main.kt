package dev.basshelal.musicdownloader

fun main(args: Array<String>) {

    ApplicationConfig.initialize(args)

    Downloader.initialize()

    Downloader.start()

    // TODO: 16-Jun-2022 @basshelal: File watcher system

    // TODO: 16-Jun-2022 @basshelal: Bulk song data editor including tags

    // TODO: 16-Jun-2022 @basshelal: Database (mongo) for Youtube video IDs? This may not be useful

}