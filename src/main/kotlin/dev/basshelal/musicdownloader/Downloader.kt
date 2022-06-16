package dev.basshelal.musicdownloader

object Downloader {

    private val thread = LoopingThread {
        val ytdlExec: String = ApplicationConfig.executable

        println("Starting $ytdlExec")

        ProcessBuilder(ytdlExec, "--version")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start().waitFor()

    }

    fun initialize() {
        addShutdownHook { Downloader.stop() }
    }

    fun start() {
        thread.start()
    }

    fun stop() {
        thread.stop()
    }
}