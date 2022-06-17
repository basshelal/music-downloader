@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.musicdownloader.core.threads

import dev.basshelal.musicdownloader.core.AtomicValueDelegate
import dev.basshelal.musicdownloader.core.println
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

class ProcessOutputRedirectThread(private val sourceStream: InputStream) : BaseThread() {

    public override var state: State by AtomicValueDelegate(State.STOPPED)
        protected set

    public override var pollMillis: Long = 10L

    private val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(sourceStream))

    private val bufferedWriters: MutableList<BufferedWriter> = mutableListOf()

    protected override var thread: Thread = Thread {
        while (state != State.STOPPED) {
            if (pollMillis > 0) Thread.sleep(pollMillis)
            if (state != State.PAUSED) {
                bufferedReader.readLine()?.also { line: String ->
                    bufferedWriters.forEach { it.println(line) }
                }
            }
        }
    }

    public fun add(writer: BufferedWriter) {
        bufferedWriters.add(writer)
    }

    public fun add(file: File) = this.add(file.bufferedWriter())

    public fun add(stream: OutputStream) = this.add(BufferedWriter(stream.bufferedWriter()))

    public fun addAll(files: List<File>) = files.forEach { this.add(it) }

    public override fun start() {
        if (state != State.STARTED) {
            thread.start()
            state = State.STARTED
        }
    }

    public override fun stop() {
        if (state == State.STARTED) {
            blockUntilDeath(millis = 10L)
            bufferedWriters.forEach { it.close() }
            bufferedReader.close()
            state = State.STOPPED
        }
    }
}