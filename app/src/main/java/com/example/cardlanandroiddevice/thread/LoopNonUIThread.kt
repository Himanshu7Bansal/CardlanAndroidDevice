package com.example.cardlanandroiddevice.thread

import android.os.Looper
import android.os.Message

/**
 * The loop thread. run method only does loop.
 */
abstract class LoopNonUIThread<T> : DefaultBaseNonUIThread<T>() {

    override fun doRun() {
        Looper.loop()
    }

    override fun doHandlerMessage(handlerMessage: Message?) {}
}