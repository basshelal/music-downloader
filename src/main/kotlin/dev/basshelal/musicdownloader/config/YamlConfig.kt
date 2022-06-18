package dev.basshelal.musicdownloader.config

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class YamlConfig(
        @SerialName("strict-mode")
        override val strictMode: Boolean,
        @SerialName("output-dir")
        override val outputDir: String,
        @SerialName("input-dir")
        override val inputDir: String,
        @SerialName("formats")
        override val formats: List<String>,
        @SerialName("cookies")
        override val cookies: String,
        @SerialName("archives-dir")
        override val archivesDir: String,
        @SerialName("rate-limit")
        override val rateLimit: Int,
        @SerialName("rescan-period")
        override val rescanPeriod: Int,
        @SerialName("executable")
        override val downloaderExec: String,
        @SerialName("backup-enabled")
        override val isBackupEnabled: Boolean,
        @SerialName("backup-dirs")
        override val backupDirs: List<String>,
        @SerialName("backup-period")
        override val backupPeriod: Int,
        @SerialName("downloader-args")
        override val downloaderArgs: String
) : Config
