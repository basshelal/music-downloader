import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    application
    java
    kotlin("plugin.serialization") version "1.6.20"
}
group = "dev.basshelal"
version = "1.0-SNAPSHOT"

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<JavaExec>(name="downloader") {
    mainClass.set("dev.basshelal.musicdownloader.DownloaderKt")
    classpath = sourceSets.main.get().runtimeClasspath
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.charleskorn.kaml:kaml:0.43.0")
}