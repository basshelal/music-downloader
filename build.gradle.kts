import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    application
    java
    kotlin("plugin.serialization") version "1.7.0"
}
group = "dev.basshelal"
version = "1.0-SNAPSHOT"

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

tasks.register<JavaExec>(name = "music-downloader") {
    mainClass.set("dev.basshelal.musicdownloader.MainKt")
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.register("classpath") {
    println(sourceSets.main.get().runtimeClasspath.asPath)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.charleskorn.kaml:kaml:0.43.0")
    implementation("com.github.ajalt.clikt:clikt:3.4.1")
    implementation("com.diogonunes:JColor:5.3.1")
    implementation("com.google.code.gson:gson:2.9.0")
}