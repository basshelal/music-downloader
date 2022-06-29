package dev.basshelal.musicdownloader.filesystem.updater

import com.google.gson.Gson
import dev.basshelal.musicdownloader.core.exit
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ApplicationUpdater {
    /** Exit code that application will use to signal to bash wrapper that it needs to update */
    const val UPDATE_EXIT_CODE = 69

    private val latestGitHubCommit: String
        get() {
            val connection = URL("https://api.github.com/repos/basshelal/music-downloader/commits?per_page=1")
                    .openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000

            connection.connect()

            val content = StringBuffer()
            BufferedReader(InputStreamReader(connection.inputStream)).also { reader ->
                reader.forEachLine { content.append(it) }
            }.close()

            data class JSON(@JvmField var sha: String = "")

            val result: Array<JSON> = Gson().fromJson(content.toString(), Array<JSON>::class.java)

            return result.firstOrNull()?.sha ?: ""
        }

    private val latestLocalCommit: String
        get() {
            val process = ProcessBuilder("git", "rev-parse", "HEAD").start()
            process.waitFor()
            val result: String? = process.inputStream.bufferedReader().readLine()
            return result ?: ""
        }

    fun isUpdateAvailable(): Boolean = latestGitHubCommit != latestLocalCommit

    fun exitToUpdate(): Nothing {
        exit(UPDATE_EXIT_CODE)
    }
}