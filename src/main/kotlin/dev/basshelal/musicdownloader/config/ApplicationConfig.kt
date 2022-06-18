@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.musicdownloader.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlException
import com.charleskorn.kaml.decodeFromStream
import com.github.ajalt.clikt.core.PrintHelpMessage
import dev.basshelal.musicdownloader.core.AudioFormat
import dev.basshelal.musicdownloader.core.LateInit
import dev.basshelal.musicdownloader.core.YoutubeDL
import dev.basshelal.musicdownloader.core.exit
import dev.basshelal.musicdownloader.core.isDir
import dev.basshelal.musicdownloader.core.isFile
import dev.basshelal.musicdownloader.core.mkdirs
import dev.basshelal.musicdownloader.core.mkfl
import dev.basshelal.musicdownloader.log.logE
import dev.basshelal.musicdownloader.log.logW
import java.io.File

/** The current config across the entire application */
object ApplicationConfig : Config {
    var configFile: File by LateInit {
        NullPointerException("Config File was not set in ApplicationConfig")
    }

    var yamlConfig: YamlConfig by LateInit {
        NullPointerException("yamlConfig was not set in ApplicationConfig")
    }

    var commandLineConfig: CommandLineConfig by LateInit {
        NullPointerException("commandLineConfig was not set in ApplicationConfig")
    }

    override var strictMode: Boolean by LateInit()
        private set
    override var outputDir: String by LateInit()
        private set
    override var inputDir: String by LateInit()
        private set
    override var formats: List<String> by LateInit()
        private set
    override var cookies: String by LateInit()
        private set
    override var archivesDir: String by LateInit()
        private set
    override var rateLimit: Int by LateInit()
        private set
    override var rescanPeriod: Int by LateInit()
        private set
    override var downloaderExec: String by LateInit()
        private set
    override var isBackupEnabled: Boolean by LateInit()
        private set
    override var backupDirs: List<String> by LateInit()
        private set
    override var backupPeriod: Int by LateInit()
        private set
    override var downloaderArgs: String by LateInit()
        private set

    fun initialize(args: Array<String>) {
        commandLineConfig = CommandLineConfig().also {
            try {
                it.parse(args)
            } catch (e: PrintHelpMessage) {
                println(it.getFormattedHelp())
                exit()
            }
        }

        configFile = File(commandLineConfig.configFilePath).also {
            if (!it.exists() || !it.isFile) {
                logE("File ${commandLineConfig.configFilePath} does not exist, cannot proceed, exiting")
                exit(1)
            }
        }

        yamlConfig = try {
            Yaml(configuration = YamlConfiguration(strictMode = false /*ignore unknown keys*/))
                    .decodeFromStream(configFile.inputStream())
        } catch (e: YamlException) {
            e.printStackTrace()
            logE("An error occurred trying to read the config file located at ${configFile.absolutePath}, exiting")
            exit(1)
        }

        strictMode = commandLineConfig.strictMode ?: yamlConfig.strictMode
        outputDir = commandLineConfig.outputDir ?: yamlConfig.outputDir
        inputDir = commandLineConfig.inputDir ?: yamlConfig.inputDir
        formats = commandLineConfig.formats ?: yamlConfig.formats
        cookies = commandLineConfig.cookies ?: yamlConfig.cookies
        archivesDir = commandLineConfig.archivesDir ?: yamlConfig.archivesDir
        rateLimit = commandLineConfig.rateLimit ?: yamlConfig.rateLimit
        rescanPeriod = commandLineConfig.rescanPeriod ?: yamlConfig.rescanPeriod
        downloaderExec = commandLineConfig.downloaderExec ?: yamlConfig.downloaderExec
        isBackupEnabled = commandLineConfig.isBackupEnabled ?: yamlConfig.isBackupEnabled
        backupDirs = commandLineConfig.backupDirs ?: yamlConfig.backupDirs
        backupPeriod = commandLineConfig.backupPeriod ?: yamlConfig.backupPeriod

        verifyConfig()
    }

    private var errors = 0
    private inline fun error(message: String) {
        logE(message)
        errors++
    }

    private inline fun verifyConfig() {
        if (!isDir(outputDir)) {
            if (!strictMode) {
                logW("Output directory: $outputDir not found, creating $outputDir")
                if (!mkdirs(outputDir))
                    error("Failed to create dir $outputDir")
            } else {
                error("Output directory: $outputDir not found!")
            }
        }
        if (!isDir(inputDir)) {
            if (!strictMode) {
                logW("Input directory: $inputDir not found, creating $inputDir")
                if (!mkdirs(inputDir))
                    error("Failed to create dir $inputDir")
            } else {
                error("Input directory: $inputDir not found!")
            }
        }
        if (!isDir(archivesDir)) {
            if (!strictMode) {
                logW("Archives directory: $archivesDir not found, creating $archivesDir")
                if (!mkdirs(archivesDir))
                    error("Failed to create dir $outputDir")
            } else {
                error("Archives directory: $archivesDir not found!")
            }
        }
        if (!isFile(cookies)) {
            if (!strictMode) {
                logW("Cookies file: $cookies not found, creating $cookies")
                if (!mkfl(cookies))
                    error("Failed to create file $cookies")
            } else {
                error("Cookies file: $cookies not found")
            }
        }
        if (formats.any { it !in AudioFormat.stringList }) {
            if (!strictMode) {
                logW("Formats contains unknown format(s):\n${formats.filter { it !in AudioFormat.stringList }}" +
                        "\n\tSupported formats are ${AudioFormat.stringList.joinToString(",")}" +
                        "\n\tRemoving unknown formats")
                formats = formats.filter { it in AudioFormat.stringList }.distinct()
                if (formats.isEmpty()) {
                    formats = ConfigDefaults.Config.formats
                    logW("Formats is empty, using defaults: ${formats.joinToString(",")}")
                }
            } else {
                error("Formats contains unknown format(s):\n${formats.filter { it !in AudioFormat.stringList }}" +
                        "\n\tSupported formats are ${AudioFormat.stringList.joinToString(",")}")
            }
        }
        if (rateLimit < 0) {
            if (!strictMode) {
                logW("Rate limit: $rateLimit is less than 0, setting to default: ${ConfigDefaults.Config.rateLimit}")
                rateLimit = ConfigDefaults.Config.rateLimit
            } else {
                error("Rate limit: $rateLimit is less than 0!")
            }
        }
        if (rescanPeriod < 0) {
            if (!strictMode) {
                logW("Rescan period: $rescanPeriod is less than 0, setting to default: ${ConfigDefaults.Config.rescanPeriod}")
                rescanPeriod = ConfigDefaults.Config.rescanPeriod
            } else {
                error("Rescan period: $rescanPeriod is less than 0!")
            }
        }

        if (!YoutubeDL.verifyExec(downloaderExec)) {
            if (!strictMode) {
                logW("Executable: $downloaderExec could not be found, using default: ${ConfigDefaults.Config.downloaderExec}")
                downloaderExec = ConfigDefaults.Config.downloaderExec
            } else {
                error("Executable: $downloaderExec could not be found, " +
                        "ensure that it exists and is accessible from PATH")
            }
        }

        // TODO: 18-Jun-2022 @basshelal: Verify backup options
        //  for the backup system how it should work is NEVER at the same time as the download because we will likely
        //  be using rsync and this could cause inconsistencies while downloading, so instead we always give priority
        //  to one (still unsure which one) and when it's time for the other to run, we queue it. For example,
        //  if we are currently downloading and it's time to run a backup, we queue it and as soon as the download is
        //  finished, we run the backup, similarly, if that backup is running and it's time to download, we wait for
        //  the backup to finish and start downloading, queueing is not technically necessary since we won't download
        //  multiple times if the backup took too long, just once, so it's more of a flag than a queue

        // TODO: 18-Jun-2022 @basshelal: Downloader args

        if (errors > 0) {
            logE("$errors unrecoverable errors occurred (some may be due to strict mode being enabled)" +
                    "\nFix the errors and retry, exiting")
            exit(1)
        }
    }
}