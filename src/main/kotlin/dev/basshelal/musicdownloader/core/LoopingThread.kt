@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.musicdownloader.core

internal class LoopingThread(runnable: Runnable? = null) {

    private var thread: Thread

    private var hasStopped: Boolean = false

    private val actualRunnable: Runnable = Runnable {
        while (isStarted) {
            if (!isPaused) runnable?.run()
        }
    }

    public var isStarted: Boolean by AtomicBooleanDelegate(initialVal = false)
        private set

    public var isPaused: Boolean by AtomicBooleanDelegate(initialVal = false)

    init {
        thread = Thread(actualRunnable)
    }

    public fun start() {
        if (hasStopped) {
            thread.blockUntil(millis = 10) { !it.isAlive }
            thread = Thread(actualRunnable)
            isStarted = false
        }
        if (!isStarted) {
            thread.start()
            isStarted = true
        }
    }

    public fun stop() {
        isStarted = false
        hasStopped = true
    }

}