package com.example.cardlanandroiddevice.thread

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.cardlanandroiddevice.util.CardlanLog

/**
 * Non-UI thread, does not block the main thread.
 * There is a bug here. If the handler is not initialized, other sub-threads based on this class
 * will not receive the message if they call it. The reason is because a looper can only have one
 * handler.
 *
 * If another thread is referenced, the looper obtained is not the looper of the referenced thread
 * but the looper of the current thread.
 */

abstract class BaseNonUIThread<T> : Thread() {

    private val lock = Object()

    private var baseLooper: Looper? = null
    private var mBaseHandler: Handler? = null
    private var needInitHandler = true

    override fun run() {
        Looper.prepare()
        synchronized(lock) {
            baseLooper = Looper.myLooper()
            lock.notifyAll()
            // notifyAll()
        }
        if (needInitHandler) {
            getThreadHandler(this)
        }
        doRun()
        CardlanLog.debugOnConsole(this.javaClass, this.name + " thread exits")
    }
    // If the thread has been started, wait until the looper has been created.

    /**
     * Get the looper of the current thread.
     * @return Looper
     */
    val looper: Looper?
        get() {
            if (!isAlive) {
                return null
            }
            // If the thread has been started, wait until the looper has been created.
            synchronized(lock) {
                while (isAlive && baseLooper == null) {
                    try {
                        lock.wait()
                        // wait()
                    } catch (_: InterruptedException) {

                    }
                }
            }
            return baseLooper
        }

    /**
     * Get the handler of the current thread
     * @param baseNonUIThread
     * @return Handler
     */
    fun getThreadHandler(baseNonUIThread: BaseNonUIThread<T>): Handler {
        if (mBaseHandler == null) {
            mBaseHandler = BaseHandler(baseNonUIThread.looper)
        }
        return mBaseHandler as Handler
    }

    /**
     * send messages
     *
     * @param consumeEntity
     * @param baseNonUIThread
     * @param msgWhat
     */
    fun addMessage(consumeEntity: T, baseNonUIThread: BaseNonUIThread<T>, msgWhat: Int) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = consumeEntity
        baseNonUIThread.getThreadHandler(baseNonUIThread).sendMessage(message)
    }

    private inner class BaseHandler(looper: Looper?) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            doHandlerMsg(msg)
        }
    }

    /**
     * quit
     * @return boolean
     */
    fun quit(): Boolean {
        val looper: Looper? = looper
        if (looper != null) {
            looper.quit()
            return true
        }
        return false
    }

    /***
     * Do you need to initialize handler
     * @param needInitHandler
     */
    fun setNeedInitHandler(needInitHandler: Boolean) {
        this.needInitHandler = needInitHandler
    }

    /**
     * What to do in the run method. The child thread does not need to consider the looper,
     * just write what needs to be done directly.
     */
    abstract fun doRun()

    /***
     * Handle the handler's message
     * @param msg
     */
    abstract fun doHandlerMsg(msg: Message?)
}