package dev.basshelal.musicdownloader

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlException
import com.charleskorn.kaml.decodeFromStream
import com.github.ajalt.clikt.core.PrintHelpMessage
import java.io.File

fun main(args: Array<String>) {

    val commandLineConfig: CommandLineConfig = CommandLineConfig().also {
        try {
            it.parse(args)
        } catch (e: PrintHelpMessage) {
            println(it.getFormattedHelp())
            exit()
        }
    }

    val configFile = File(commandLineConfig.configFile).also {
        if (!it.exists() || !it.isFile) {
            printErr("File ${commandLineConfig.configFile} does not exist")
            exit(1)
        }
    }

    val yamlConfig: YamlConfig = try {
        Yaml(configuration = YamlConfiguration(strictMode = false))
                .decodeFromStream(configFile.inputStream())
    } catch (e: YamlException) {
        e.printStackTrace()
        printErr("An error occurred trying to read the config file located at ${configFile.absolutePath}, exiting")
        exit(1)
    }

    ApplicationConfig.initialize(
            configFile = configFile,
            yamlConfig = yamlConfig,
            commandLineConfig = commandLineConfig,
    )

    Downloader.start()

}