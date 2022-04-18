package dev.basshelal.musicdownloader

object Downloader {

    fun start() {
        val ytdlExec: String = ApplicationConfig.executable

        println("Starting $ytdlExec")

        ProcessBuilder(ytdlExec, "--version")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start().waitFor()
    }
}