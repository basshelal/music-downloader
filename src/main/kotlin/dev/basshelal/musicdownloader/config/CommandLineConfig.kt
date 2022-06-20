package dev.basshelal.musicdownloader.config

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
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
            names = arrayOf("--rescan-period"),
            help = "Minutes between checking (and downloading) new music"
    ).int()

    override val downloaderExec: String? by option(
            names = arrayOf("--exec", "--ytdl", "--executable", "--exec"),
            help = "Path to youtube-dl executable"
    )

    override val isBackupEnabled: Boolean? by option(
            names = arrayOf("--backup-enabled")
    ).boolean()

    override val backupDirs: List<String>? by option(
            names = arrayOf("--backup-dirs")
    ).split(",")

    override val backupPeriod: Int? by option(
            names = arrayOf("--backup-period")
    ).int()

    // TODO: 18-Jun-2022 @basshelal: Implement and test everything below
    override val downloaderArgs: String? by option(
            names = arrayOf("--downloader-args")
    )

    val sleepSeconds: Int? by option(
            names = arrayOf("--sleep-seconds"),
            help = "Seconds to sleep between individual song downloads (and checks), defaults to 0"
    ).int()

    val update: Boolean? by option(
            names = arrayOf("--update", "-U"),
            help = "Update program and exit, needs manual restart"
    ).flag()

    val logDir: String? by option(
            names = arrayOf("--log-dir"),
            help = "Directory containing log files, null for no log files"
    )

    val logLevel: String? by option(
            names = arrayOf("--log-level"),
            help = "Log verbosity, possible values are (case insensitive), " +
                    "NONE (N, Q), ERROR (E), WARN (W), DEBUG (D), INFO (I), VERBOSE (V)"
    ).choice(choices = arrayOf("NONE", "N", "Q", "ERROR", "E", "WARN", "W", "DEBUG", "D", "INFO", "I", "VERBOSE", "V"),
            ignoreCase = true)

    val quiet: Boolean? by option(
            names = arrayOf("--quiet", "-q"),
            help = "Alias for --log-level Q"
    ).flag(secondaryNames = arrayOf("--no-quiet"))

    val verbose: Boolean? by option(
            names = arrayOf("--verbose", "-v"),
            help = "Alias for --log-level V"
    ).flag(secondaryNames = arrayOf("--no-verbose"))

    override fun run() = Unit
}