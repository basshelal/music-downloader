package dev.basshelal.musicdownloader.core

enum class AudioFormat {
    M4A, FLAC, MP3, WAV;

    override fun toString(): String = this.name.lowercase()

    companion object {
        val list: List<AudioFormat> = AudioFormat.values().toList()
        val stringList: List<String> = AudioFormat.values().toList().map { it.toString() }
    }
}

enum class ThumbnailFormat {
    JPG, PNG, WEBP;

    override fun toString(): String = this.name.lowercase()
}

enum class QualityFormat(val value: Int) {
    BEST(0), GOOD(2), MEDIUM(5), LOW(7), WORST(10);
}