package com.example.cardlanandroiddevice.thread

/**
 * card reader thread
 */
class ReadCardNonUIThread(private val mTarget: Runnable) : NotDoHandlerMessageNonUIThread() {

    override fun doRun() {
        mTarget.run()
    }
}