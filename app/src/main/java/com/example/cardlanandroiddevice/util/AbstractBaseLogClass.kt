package com.example.cardlanandroiddevice.util

import com.cardlan.utils.ByteUtil

abstract class AbstractBaseLogClass {

    /**
     * 　Print log information of type string
     * @param logMsg
     */
    fun printLog(logMsg: String) {
        CardlanLog.debugOnConsole(this.javaClass, "String message :$logMsg")
    }

    /**
     * Print log information of type int
     * @param intLog
     */
    fun printLog(intLog: Int) {
        CardlanLog.debugOnConsole(this.javaClass, "int message: $intLog")
    }

    /**
     * Print log information of type byte[]
     * @param logMsgBytes
     */
    fun printLog(logMsgBytes: ByteArray?) {
        CardlanLog.debugOnConsole(
            this.javaClass, "byte array message:" + ByteUtil
                .byteArrayToHexString(logMsgBytes)
        )
    }
}