package dev.basshelal.musicdownloader

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.SerialName

interface Config {
    val outputDir: String?
    val inputDir: String?
    val formats: List<String>?
    val cookies: Map<String, String>?
    val archivesDir: String?
    val rateLimit: Int?
    val rescanPeriod: Int?
    val executable: String?
}

@kotlinx.serialization.Serializable
data class YamlConfig(
        @SerialName("output-dir")
        override val outputDir: String,
        @SerialName("input-dir")
        override val inputDir: String,
        @SerialName("formats")
        override val formats: List<String>,
        @SerialName("cookies")
        override val cookies: Map<String, String>,
        @SerialName("archives-dir")
        override val archivesDir: String,
        @SerialName("rate-limit")
        override val rateLimit: Int,
        @SerialName("rescan-period")
        override val rescanPeriod: Int,
        @SerialName("executable")
        override val executable: String
) : Config

// Read docs here https://ajalt.github.io/clikt/parameters/#parameter-names

class CommandLineConfig : Config,
        CliktCommand(
                name = "downloader",
                help = """Bassam Helal's Personal Music Downloader
                    |https://github.com/basshelal/music-downloader/
                """.trimMargin(),
        ) {

    val configFile: String by option(
            names = arrayOf("--config"),
            help = "Location of yaml config file, defaults to ./config.yaml",
    ).default("./config.yaml")

    override val outputDir: String? by option(
            names = arrayOf("--output-dir", "-o"),
            help = "Directory where music will be downloaded",
    )

    override val inputDir: String? by option(
            names = arrayOf("--input-dir", "-i"),
            help = "Directory where files with urls containing music is located",
    )

    override val formats: List<String>? by option(
            names = arrayOf("--formats", "-f"),
            help = "Formats separated by commas (,), supported formats are m4a,wav,mp3,flac",
    ).split(",")

    override val cookies: Map<String, String>? by option(names = arrayOf("--cookies", "-c"))
            .convert { it.split(",").associate { "" to it } }

    override val archivesDir: String? by option(names = arrayOf("--archives-dir", "-a"))

    override val rateLimit: Int? by option(names = arrayOf("--rate-limit")).int()

    override val rescanPeriod: Int? by option(names = arrayOf("--rescan-period")).int()

    override val executable: String? by option(names = arrayOf("--exec", "--ytdl", "--executable", "--exec"))

    override fun run() = Unit
}