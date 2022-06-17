@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.musicdownloader.core

import dev.basshelal.musicdownloader.config.ConfigDefaults

/**
 * Wrapper around ytdl, only implements the required API
 */
public class YoutubeDL
private constructor(private val processBuilder: ProcessBuilder) {

    private var process: Process? = null

    public class Builder {

        private var exec: String = ConfigDefaults.Config.executable
        private val args = LinkedHashMap<String, String>()

        public fun exec(exec: String): Builder = apply { this.exec = exec }

        public fun url(url: String): Builder = apply { addArg("url", url) }

        public fun addArg(argName: String, argValue: String = ""): Builder = apply { args[argName] = argValue }

        public fun removeArg(argName: String): Builder = apply { args.remove(argName) }

        public fun build(): YoutubeDL {
            return YoutubeDL(processBuilder = ProcessBuilder()
                    .command(mutableListOf(exec)
                            .also { it.addAll(args.flatMap { listOf(it.key, it.value) }) })
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
            )
        }
    }

    companion object {
        public fun builder(): Builder = Builder()
    }

    public fun start() {
        if (process == null) {
            process = processBuilder.start()
        }
    }

    public fun blockUntilCompletion() {
        process?.waitFor()
    }

    public fun stop() {
        process?.also {
            it.destroy()
            while (it.isAlive) Thread.sleep(10)
        }
    }
}