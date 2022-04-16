package dev.basshelal.musicdownloader

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Config(
        @SerialName("output-dir") val outputDir: String,
        @SerialName("input-dir") val inputDir: String,
        @SerialName("formats") val formats: List<String>,
        @SerialName("cookies") val cookies: Map<String, String>,
        @SerialName("archives-dir") val archivesDir: String,
        @SerialName("rate-limit") val rateLimit: Int,
        @SerialName("rescan-period") val rescanPeriod: Int,
        @SerialName("file-watching-enabled") val isFileWatching: Boolean,
        @SerialName("backup-dirs") val backupDirs: List<String>,
        @SerialName("backup-period") val backupPeriod: Int,
)