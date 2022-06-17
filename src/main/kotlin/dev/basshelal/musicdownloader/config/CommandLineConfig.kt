package dev.basshelal.musicdownloader.config

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.int
import dev.basshelal.musicdownloader.core.boolean

// Read docs here https://ajalt.github.io/clikt/parameters/#parameter-names
// TODO: 17-Jun-2022 @basshelal: Add help texts!
class CommandLineConfig : Config, CliktCommand(
        name = "music downloader",
        help = """Bassam Helal's Personal Music Downloader
               |https://github.com/basshelal/music-downloader/
               """.trimMargin(),
) {

    val configFilePath: String by option(
            names = arrayOf("--config", "-c"),
            help = "Location of the yaml config file, defaults to ./config.yaml",
    ).default(ConfigDefaults.configFilePath)

    override val strictMode: Boolean? by option(
            names = arrayOf("--strict-mode"),
            help = """Strict mode enabled means that all provided options must be valid and will 
                   |not be fixed by the program to the default, for example a negative value where it is
                   |not allowed will always be turned to the default value if strict mode is off.
                   |Defaults to false"""
                    .trimMargin()
    ).flag(secondaryNames = arrayOf("--no-strict-mode"), default = false)

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

    override val cookies: String? by option(
            names = arrayOf("--cookies", "-C"),
            help = "Cookies text file to use when authentication is required"
    )

    override val archivesDir: String? by option(
            names = arrayOf("--archives-dir", "-a"),
            help = "Archives directory to keep track of downloaded music"
    )

    override val rateLimit: Int? by option(
            names = arrayOf("--rate-limit", "--limit", "-l"),
            help = "Download rate limit in MB/s, 0 for no limit"
    ).int()

    override val rescanPeriod: Int? by option(
            names = arrayOf("--rescan-period")
    ).int()

    override val executable: String? by option(
            names = arrayOf("--exec", "--ytdl", "--executable", "--exec")
    )

    override val isFileWatching: Boolean? by option(
            names = arrayOf("--file-watching")
    ).boolean()

    override val isBackupEnabled: Boolean? by option(
            names = arrayOf("--backup-enabled")
    ).boolean()

    override val backupDirs: List<String>? by option(
            names = arrayOf("--backup-dirs")
    ).split(",")

    override val backupPeriod: Int? by option(
            names = arrayOf("--backup-period")
    ).int()

    override fun run() = Unit
}