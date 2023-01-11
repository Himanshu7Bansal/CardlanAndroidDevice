package com.example.cardlanandroiddevice.thread

import android.os.Message

/**
 * Default abstract thread, added default message [.SF_ADD_MSG]
 */
abstract class DefaultBaseNonUIThread<T> : BaseNonUIThread<T>() {

    /**
     * Send the default message, the default message is [.SF_ADD_MSG]
     *
     * @param consumeEntity
     * @param baseThread
     */
    fun addMessage(consumeEntity: T, baseThread: DefaultBaseNonUIThread<T>?) {
        addMessage(consumeEntity, baseThread!!, SF_ADD_MSG)
    }

    /**
     * send messages
     * Does not support calling in multiple threads, because the thread that calls this method
     * sends this information, not this thread.
     *
     * @param consumeEntity
     * @param msgWhat
     */
    fun addMessage(consumeEntity: T, msgWhat: Int) {
        addMessage(consumeEntity, this, msgWhat)
    }

    override fun doHandlerMsg(msg: Message?) {
        when (msg!!.what) {
            SF_ADD_MSG -> {
                //
                val handlerEntity: T? = msg.obj as T
                handlerEntity?.let { doHandlerMsg(it) }
            }
            else -> doHandlerMessage(msg)
        }
    }

    /**
     * Process the message sent from the handler, the message is of generic type,
     * and the message is not null.
     * @param handlerEntity
     */
    abstract fun doHandlerMsg(handlerEntity: T)

    /**
     * Process the message sent from the handler, the message is of generic type,
     * and the message is not null.
     * @param handlerMessage
     */
    abstract fun doHandlerMessage(handlerMessage: Message?)

    companion object {

        private const val SF_ADD_MSG = 101
    }
}