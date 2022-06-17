@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.musicdownloader.core

import dev.basshelal.musicdownloader.config.ConfigDefaults
import dev.basshelal.musicdownloader.log.logV

/**
 * Wrapper around ytdl, only implements the required API
 */
public class YoutubeDL
private constructor(private val processBuilder: ProcessBuilder) {

    private var process: Process? = null

    public class Builder {

        private var exec: String = ConfigDefaults.Config.executable
        private var url: String = ""
        private val flags = LinkedHashSet<String>()
        private val args = LinkedHashMap<String, String>()

        public fun exec(exec: String): Builder = apply { this.exec = exec }

        public fun url(url: String): Builder = apply { this.url = url }

        public fun extractAudio(): Builder = addFlag("--extract-audio")

        public fun format(format: AudioFormat): Builder = addArg("--audio-format", format.toString())

        public fun quality(quality: QualityFormat): Builder = addArg("--audio-quality", quality.value.toString())

        public fun embedThumbnail(): Builder = addFlag("--embed-thumbnail")

        public fun thumbnailFormat(format: ThumbnailFormat): Builder = addArg("--convert-thumbnails", format.toString())

        public fun embedMetadata(): Builder = addFlag("--embed-metadata")
                .addFlag("--no-embed-chapters").addFlag("--no-embed-info-json")

        public fun sleepIntervalMin(min: Int): Builder = addArg("--sleep-interval", min.toString())

        public fun quiet(): Builder = addFlag("--quiet")

        public fun noWarnings(): Builder = addFlag("--no-warnings")

        public fun progress(): Builder = addFlag("--progress")

        public fun printTraffic(): Builder = addFlag("--print-traffic")

        public fun verbose(): Builder = addFlag("--verbose")

        public fun simulate(): Builder = addFlag("--simulate")

        public fun cookies(cookiesFilePath: String): Builder = addArg("--cookies", cookiesFilePath)

        public fun output(outputTemplate: String): Builder = addArg("--output", outputTemplate)

        public fun batchFile(filePath: String): Builder = addArg("--batch-file", filePath)

        public fun retries(count: Int): Builder = addArg("--retries", count.toString())

        public fun limitRate(bytesPerSecond: String): Builder = addArg("--limit-rate", bytesPerSecond)

        public fun downloadArchive(archivePath: String): Builder = addArg("--download-archive", archivePath)

        public fun ignoreErrors(): Builder = addFlag("--ignore-errors")

        public fun addArg(argName: String, argValue: String = ""): Builder = apply { args[argName] = argValue }

        public fun removeArg(argName: String): Builder = apply { args.remove(argName) }

        public fun addFlag(flagName: String): Builder = apply { flags.add(flagName) }

        public fun removeFlag(flagName: String): Builder = apply { flags.remove(flagName) }

        public fun build(): YoutubeDL {
            return YoutubeDL(processBuilder = ProcessBuilder()
                    .command(mutableListOf(exec).also {
                        it.addAll(flags)
                        it.addAll(args.flatMap { listOf(it.key, it.value) })
                        it.add(url)
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
        public fun builder(): Builder = Builder()
    }

    public fun start(): YoutubeDL {
        if (process == null) {
            process = processBuilder.start()
            infoString?.also { "Starting process:\n\t$it".logV() }
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
        return this
    }

    public val infoString: String?
        get() = process?.let { "[${it.pid()}] ${it.info()?.commandLine()?.get()}" }
}