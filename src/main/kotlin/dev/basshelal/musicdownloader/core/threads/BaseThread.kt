package dev.basshelal.musicdownloader.core.threads

import dev.basshelal.musicdownloader.core.blockUntil

abstract class BaseThread {

    enum class State { STOPPED, STARTED, PAUSED }

    protected abstract val thread: Thread?

    public abstract var state: State
        protected set

    public abstract var pollMillis: Long

    public abstract fun start()

    public abstract fun stop()

    protected fun blockUntilDeath(millis: Long) = thread?.blockUntil(millis) { !it.isAlive }

}