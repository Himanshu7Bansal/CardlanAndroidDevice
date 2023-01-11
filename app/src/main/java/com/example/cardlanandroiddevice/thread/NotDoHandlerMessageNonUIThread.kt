package com.example.cardlanandroiddevice.thread

import android.os.Message

/**
 * There is no need to process the thread of the handler message.
 * Only the doRun method needs to be rewritten.
 */
open class NotDoHandlerMessageNonUIThread : DefaultBaseNonUIThread<Any?>() {

    override fun doHandlerMsg(handlerEntity: Any?) {}
    override fun doHandlerMessage(handlerMessage: Message?) {}
    override fun doRun() {}
}