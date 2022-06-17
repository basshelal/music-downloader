@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.musicdownloader.core.threads

import dev.basshelal.musicdownloader.core.AtomicValueDelegate

public class LoopingThread(runnable: Runnable? = null) : BaseThread() {

    protected override var thread: Thread? = null

    public override var state: State by AtomicValueDelegate(State.STOPPED)
        protected set

    public override var pollMillis: Long = 0L

    private val actualRunnable: Runnable = Runnable {
        while (state != State.STOPPED) {
            if (pollMillis > 0) Thread.sleep(pollMillis)
            if (state != State.PAUSED) runnable?.run()
        }
    }

    public override fun start() {
        if (state == State.STOPPED) {
            blockUntilDeath(millis = 10L)
            thread = Thread(actualRunnable)
            state = State.PAUSED
        }
        if (state != State.STARTED) {
            thread?.start()
            state = State.STARTED
        }
    }

    public fun pause() {
        if (state == State.STARTED) state = State.PAUSED
    }

    public override fun stop() {
        state = State.STOPPED
    }

}