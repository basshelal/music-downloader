package dev.basshelal.musicdownloader.config

import dev.basshelal.musicdownloader.core.hoursToMinutes

object ConfigDefaults {

    val configFilePath: String = "./config.yaml"

    object Config : dev.basshelal.musicdownloader.config.Config {
        override val strictMode: Boolean = false
        override val outputDir: String = "./music/"
        override val inputDir: String = "./urls/"
        override val formats: List<String> = listOf("m4a")
        override val cookies: String = "./cookies.txt"
        override val archivesDir: String = "./archives/"
        override val rateLimit: Int = 0
        override val rescanPeriod: Int = 360
        override val downloaderExec: String = "./bin/yt-dlp"
        override val isBackupEnabled: Boolean = false
        override val backupDirs: List<String> = emptyList()
        override val backupPeriod: Int = 3.hoursToMinutes()
        override val downloaderArgs: String = ""
    }
}