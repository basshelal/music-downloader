package dev.basshelal.musicdownloader

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import java.io.File

fun main(args: Array<String>) {
    println("""Args:
        |${args.joinToString()}""".trimMargin()
    )

    val config: Config = Yaml.default.decodeFromStream(File("config.yaml").inputStream())

    println("config: ${config.toString().replace(',', '\n')}")

}