package dev.basshelal.musicdownloader.core

import dev.basshelal.musicdownloader.config.ConfigDefaults
import dev.basshelal.musicdownloader.log.logV
import java.io.IOException

/**
 * Wrapper around ytdl, only implements the required API
 */
class YoutubeDL
private constructor(private val processBuilder: ProcessBuilder) {

    private var process: Process? = null

    class Builder {

        private var exec: String = ConfigDefaults.Config.downloaderExec
        private var url: String? = null
        private val flags = LinkedHashSet<String>()
        private val args = LinkedHashMap<String, String>()

        fun exec(exec: String): Builder = apply { this.exec = exec }

        fun url(url: String): Builder = apply { this.url = url }

        fun update(): Builder = addFlag("--update")

        fun extractAudio(): Builder = addFlag("--extract-audio")

        fun format(format: AudioFormat): Builder = addArg("--audio-format", format.toString())

        fun quality(quality: QualityFormat): Builder = addArg("--audio-quality", quality.value.toString())

        fun embedThumbnail(): Builder = addFlag("--embed-thumbnail")

        fun thumbnailFormat(format: ThumbnailFormat): Builder = addArg("--convert-thumbnails", format.toString())

        fun embedMetadata(): Builder = addFlag("--embed-metadata")
                .addFlag("--no-embed-chapters").addFlag("--no-embed-info-json")

        fun sleepIntervalMin(min: Int): Builder = addArg("--sleep-interval", min.toString())

        fun quiet(): Builder = addFlag("--quiet")

        fun noWarnings(): Builder = addFlag("--no-warnings")

        fun progress(): Builder = addFlag("--progress")

        fun printTraffic(): Builder = addFlag("--print-traffic")

        fun verbose(): Builder = addFlag("--verbose")

        fun simulate(): Builder = addFlag("--simulate")

        fun cookies(cookiesFilePath: String): Builder = addArg("--cookies", cookiesFilePath)

        fun output(outputTemplate: String): Builder = addArg("--output", outputTemplate)

        fun batchFile(filePath: String): Builder = addArg("--batch-file", filePath)

        fun retries(count: Int): Builder = addArg("--retries", count.toString())

        fun limitRate(bytesPerSecond: String): Builder = addArg("--limit-rate", bytesPerSecond)

        fun downloadArchive(archivePath: String): Builder = addArg("--download-archive", archivePath)

        fun ignoreErrors(): Builder = addFlag("--ignore-errors")

        fun addArg(argName: String, argValue: String = ""): Builder = apply { args[argName] = argValue }

        fun removeArg(argName: String): Builder = apply { args.remove(argName) }

        fun addFlag(flagName: String): Builder = apply { flags.add(flagName) }

        fun removeFlag(flagName: String): Builder = apply { flags.remove(flagName) }

        fun build(): YoutubeDL {
            return YoutubeDL(processBuilder = ProcessBuilder()
                    .command(mutableListOf(exec).also { list: MutableList<String> ->
                        list.addAll(flags)
                        list.addAll(args.flatMap { listOf(it.key, it.value) })
                        url?.also { url -> list.add(url) }
                    })
                    // Output from ytdl doesn't pipe to output files yet, this was implemented at one point but was
                    // limited to one file per ytdl run, after every run the file would be re-written to
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
            )
        }
    }

    companion object {
        fun builder(): Builder = Builder()

        fun verifyExec(exec: String): Boolean {
            try {
                ProcessBuilder(exec)
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start().waitFor()
            } catch (e: IOException) {
                return false
            }
            return true
        }

        fun update(exec: String) {
            YoutubeDL.builder().exec(exec).update().build().start().blockUntilCompletion()
        }
    }

    fun start(): YoutubeDL {
        if (process == null) {
            "Starting process".logV()
            process = processBuilder.start()
            infoString?.also { "Started process:\n\t$it".logV() }
        }
        return this
    }

    fun blockUntilCompletion(): YoutubeDL {
        infoString?.also { "Waiting on process:\n\t$it".logV() }
        process?.waitFor()
        return this
    }

    /** Stops this YoutubeDL process, blocking until it is stopped, you cannot restart it after this is called */
    fun stop(): YoutubeDL {
        process?.also {
            infoString?.also { "Stopping process:\n\t$it".logV() }
            it.destroy()
            while (it.isAlive) Thread.sleep(10)
            "Process stopped".logV()
            process = null
        }
        return this
    }

    val infoString: String?
        get() = process?.let { "[${it.pid()}] ${it.info()?.commandLine()?.get()}" }
}