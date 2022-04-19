package dev.basshelal.musicdownloader

object Downloader {

    private val thread = object : Thread() {

        override fun run() {
            while (true) {

                val ytdlExec: String = ApplicationConfig.executable

                println("Starting $ytdlExec")

                ProcessBuilder(ytdlExec, "--version")
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectInput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start().waitFor()

                sleep(5_000)
            }
        }
    }

    fun start() {
        if (!thread.isAlive) thread.start()
    }

    fun stop() {
        if (thread.isAlive) thread.stop()
    }
}