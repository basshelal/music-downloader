package dev.basshelal.musicdownloader.core

public enum class AudioFormat {
    M4A, FLAC, MP3, WAV;

    override fun toString(): String = this.name.lowercase()
}

public enum class ThumbnailFormat {
    JPG, PNG, WEBP;

    override fun toString(): String = this.name.lowercase()
}

public enum class QualityFormat(val value: Int) {
    BEST(0), GOOD(2), MEDIUM(5), LOW(7), WORST(10);
}