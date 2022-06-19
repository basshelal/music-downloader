package dev.basshelal.musicdownloader.config

// TODO: 17-Jun-2022 @basshelal: Add log level which should include ytdl verbosity
interface Config {
    val strictMode: Boolean?
    val outputDir: String?
    val inputDir: String?
    val formats: List<String>?
    val cookies: String?
    val archivesDir: String?
    val rateLimit: Int?
    val rescanPeriod: Int?
    val downloaderExec: String?
    val isBackupEnabled: Boolean?
    val backupDirs: List<String>?
    val backupPeriod: Int?
    val downloaderArgs: String?

    public fun jsonify(): String =
            """|{
               |   strictMode: ${strictMode}
               |   outputDir: ${outputDir}
               |   inputDir: ${inputDir}
               |   formats: ${formats?.joinToString(",")}
               |   cookies: ${cookies}
               |   archivesDir: ${archivesDir}
               |   rateLimit: ${rateLimit}
               |   rescanPeriod: ${rescanPeriod}
               |   downloaderExec: ${downloaderExec}
               |   isBackupEnabled: ${isBackupEnabled}
               |   backupDirs: ${backupDirs?.joinToString(",")}
               |   backupPeriod: ${backupPeriod}
               |   downloaderArgs: ${downloaderArgs}
               |}
        """.trimMargin()

}