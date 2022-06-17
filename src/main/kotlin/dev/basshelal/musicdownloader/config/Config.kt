package dev.basshelal.musicdownloader.config

interface Config {
    val strictMode: Boolean?
    val outputDir: String?
    val inputDir: String?
    val formats: List<String>?
    val cookies: String?
    val archivesDir: String?
    val rateLimit: Int?
    val rescanPeriod: Int?
    val executable: String?
    val isFileWatching: Boolean?
    val isBackupEnabled: Boolean?
    val backupDirs: List<String>?
    val backupPeriod: Int?
}