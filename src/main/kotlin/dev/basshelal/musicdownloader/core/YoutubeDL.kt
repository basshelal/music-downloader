@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.musicdownloader.core

import dev.basshelal.musicdownloader.config.ConfigDefaults
import dev.basshelal.musicdownloader.core.threads.ProcessOutputRedirectThread
import java.io.File

/**
 * Wrapper around ytdl, only implements the required API
 */
public class YoutubeDL
private constructor(private val processBuilder: ProcessBuilder) {

    private var process: Process? = null
    private val outputFiles: MutableList<File> = mutableListOf()
    private val errorFiles: MutableList<File> = mutableListOf()
    private lateinit var outputRedirectThread: ProcessOutputRedirectThread
    private lateinit var errorRedirectThread: ProcessOutputRedirectThread

    public class Builder {

        private var exec: String = ConfigDefaults.Config.executable
        private val args = LinkedHashMap<String, String>()
        private val outputFiles: MutableList<File> = mutableListOf()
        private val errorFiles: MutableList<File> = mutableListOf()

        public fun exec(exec: String): Builder = apply { this.exec = exec }

        public fun url(url: String): Builder = apply { addArg("url", url) }

        public fun addOutputFile(file: File): Builder = apply { this.outputFiles.add(file) }

        public fun removeOutputFile(file: File): Builder = apply { this.outputFiles.remove(file) }

        public fun addErrorFile(file: File): Builder = apply { this.errorFiles.add(file) }

        public fun removeErrorFile(file: File): Builder = apply { this.errorFiles.remove(file) }

        public fun addArg(argName: String, argValue: String = ""): Builder = apply { args[argName] = argValue }

        public fun removeArg(argName: String): Builder = apply { args.remove(argName) }

        public fun build(): YoutubeDL {
            return YoutubeDL(processBuilder = ProcessBuilder()
                    .command(mutableListOf(exec)
                            .also { it.addAll(args.flatMap { listOf(it.key, it.value) }) })
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
            ).also {
                it.outputFiles.setAll(outputFiles)
                it.errorFiles.setAll(errorFiles)
            }
        }
    }

    companion object {
        public fun builder(): Builder = Builder()
    }

    public fun start(): YoutubeDL {
        if (process == null) {
            process = processBuilder.start()
            process?.also {
                outputRedirectThread = ProcessOutputRedirectThread(it.inputStream)
                errorRedirectThread = ProcessOutputRedirectThread(it.errorStream)

                outputRedirectThread.addAll(outputFiles)
                errorRedirectThread.addAll(errorFiles)

                outputRedirectThread.start()
                errorRedirectThread.start()

                it.onExit().whenCompleteAsync { process: Process, throwable: Throwable ->
                    this.stop()
                }
            }
        }
        return this
    }

    public fun blockUntilCompletion(): YoutubeDL {
        process?.waitFor()
        return this
    }

    public fun stop(): YoutubeDL {
        process?.also {
            it.destroy()
            while (it.isAlive) Thread.sleep(10)
        }
        outputRedirectThread.stop()
        errorRedirectThread.stop()
        return this
    }
}