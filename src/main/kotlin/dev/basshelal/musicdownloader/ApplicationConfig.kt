package dev.basshelal.musicdownloader

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

    override var outputDir: String by LateInit()
        private set
    override var inputDir: String by LateInit()
        private set
    override var formats: List<String> by LateInit()
        private set
    override var cookies: Map<String, String> by LateInit()
        private set
    override var archivesDir: String by LateInit()
        private set
    override var rateLimit: Int by LateInit()
        private set
    override var rescanPeriod: Int by LateInit()
        private set
    override var executable: String by LateInit()
        private set

    fun initialize(configFile: File = this.configFile,
                   yamlConfig: YamlConfig = this.yamlConfig,
                   commandLineConfig: CommandLineConfig = this.commandLineConfig) {
        this.configFile = configFile
        this.yamlConfig = yamlConfig
        this.commandLineConfig = commandLineConfig

        this.outputDir = commandLineConfig.outputDir ?: yamlConfig.outputDir
        this.inputDir = commandLineConfig.inputDir ?: yamlConfig.inputDir
        this.formats = commandLineConfig.formats ?: yamlConfig.formats
        this.cookies = commandLineConfig.cookies ?: yamlConfig.cookies
        this.archivesDir = commandLineConfig.archivesDir ?: yamlConfig.archivesDir
        this.rateLimit = commandLineConfig.rateLimit ?: yamlConfig.rateLimit
        this.rescanPeriod = commandLineConfig.rescanPeriod ?: yamlConfig.rescanPeriod
        this.executable = commandLineConfig.executable ?: yamlConfig.executable
    }
}