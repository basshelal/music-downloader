package dev.basshelal.musicdownloader

import java.util.concurrent.atomic.AtomicBoolean

object Downloader {

    private val thread = object : Thread() {

        var isRunning: AtomicBoolean = AtomicBoolean(true)

        override fun run() {
            while (isRunning.get()) {

                val ytdlExec: String = ApplicationConfig.executable

                println("Starting $ytdlExec")

                ProcessBuilder(ytdlExec, "--version")
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectInput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start().waitFor()

                try {
                    sleep(Long.MAX_VALUE)
                } catch (e: InterruptedException) {
                    println("Stopping Downloader thread...")
                }
            }
        }
    }

    fun initialize() {
        Runtime.getRuntime().addShutdownHook(Thread {
            Downloader.stop()
        })
    }

    fun start() {
        if (!thread.isAlive) thread.start()
    }

    fun stop() {
        if (thread.isAlive) {
            thread.isRunning.set(false)
            thread.interrupt()
        }
    }
}