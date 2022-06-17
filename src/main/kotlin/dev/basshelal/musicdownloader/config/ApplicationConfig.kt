@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.musicdownloader.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlException
import com.charleskorn.kaml.decodeFromStream
import com.github.ajalt.clikt.core.PrintHelpMessage
import dev.basshelal.musicdownloader.core.LateInit
import dev.basshelal.musicdownloader.core.exit
import dev.basshelal.musicdownloader.core.printErr
import java.io.File
import java.io.IOException

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
    override var executable: String by LateInit()
        private set
    override var isFileWatching: Boolean by LateInit()
        private set // TODO implement!
    override var isBackupEnabled: Boolean by LateInit()
        private set // TODO implement!
    override var backupDirs: List<String> by LateInit()
        private set // TODO implement!
    override var backupPeriod: Int by LateInit()
        private set // TODO implement!

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
                printErr("File ${commandLineConfig.configFilePath} does not exist")
                exit(1)
            }
        }

        yamlConfig = try {
            Yaml(configuration = YamlConfiguration(strictMode = false))
                    .decodeFromStream(configFile.inputStream())
        } catch (e: YamlException) {
            e.printStackTrace()
            printErr("An error occurred trying to read the config file located at ${configFile.absolutePath}, exiting")
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
        executable = commandLineConfig.executable ?: yamlConfig.executable

        verifyConfig()
    }

    private inline fun verifyConfig() {
        var errors = 0
        if (!File(outputDir).isDirectory) {
            if (!strictMode) {
                println("Output directory: $outputDir not found, creating $outputDir")
                File(outputDir).mkdirs()
            } else {
                printErr("Output directory: $outputDir not found!")
                errors++
            }
        }
        if (!File(inputDir).isDirectory) {
            if (!strictMode) {
                println("Input directory: $inputDir not found, creating $inputDir")
                File(inputDir).mkdirs()
            } else {
                printErr("Input directory: $inputDir not found!")
                errors++
            }
        }
        if (!File(archivesDir).isDirectory) {
            if (!strictMode) {
                println("Archives directory: $archivesDir not found, creating $archivesDir")
                File(archivesDir).mkdirs()
            } else {
                printErr("Archives directory: $archivesDir not found!")
                errors++
            }
        }
        if (!File(cookies).isFile) {
            if (!strictMode) {
                println("Cookies file: $cookies not found, creating $cookies")
                File(cookies).also {
                    it.parentFile.mkdirs()
                    it.createNewFile()
                }
            } else {
                printErr("Cookies file: $cookies not found")
                errors++
            }
        }
        if (formats.any { it !in ConfigDefaults.supportedFormats }) {
            if (!strictMode) {
                println("Formats contains unknown format(s):\n${formats.filter { it !in ConfigDefaults.supportedFormats }}" +
                        "\n\tSupported formats are m4a,wav,mp3,flac\n\tRemoving unknown formats")
                formats = formats.filter { it in listOf("m4a", "wav", "mp3", "flac") }.distinct()
            } else {
                printErr("Formats contains unknown format(s):\n${formats.filter { it !in ConfigDefaults.supportedFormats }}" +
                        "\n\tSupported formats are m4a,wav,mp3,flac")
                errors++
            }
        }
        if (rateLimit < 0) {
            if (!strictMode) {
                println("Rate limit: $rateLimit is less than 0, setting to default: 0")
                rateLimit = 0
            } else {
                printErr("Rate limit: $rateLimit is less than 0!")
                errors++
            }
        }
        if (rescanPeriod < 0) {
            if (!strictMode) {
                printErr("Rescan period: $rescanPeriod is less than 0, setting to default: 360")
                rescanPeriod = 360
            } else {
                printErr("Rescan period: $rescanPeriod is less than 0!")
                errors++
            }
        }

        try {
            ProcessBuilder(executable)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start().waitFor()
        } catch (e: IOException) {
            printErr("FATAL ERROR:\n\tCould not find executable $executable, " +
                    "ensure that it exists and is accessible from PATH")
            errors++
        }

        if (errors > 0) {
            printErr("$errors errors occurred")
            exit(1)
        }
    }
}