package com.example.cardlanandroiddevice.util

/*
 *  @Project：  CardDemoForForeign
 *  @packege：  com.cardlan.twoshowinonescreen.util
 *  @class:     CardlanLog
 *  @author:    cardlan
 *  @Creation-time:  2018/8/10 0010 pm 4:09
 *  @description：  To print the log
 */
object CardlanLog {

    private const val printOnConsole = true
    fun debugOnConsole(clazz: Class<*>, msg: String) {
        if (printOnConsole) {
            println("[" + clazz.simpleName + "] INFO : " + msg)
        }
    }

    fun debugOnConsole(clazz: Class<*>?, e: Exception) {
        if (printOnConsole) {
            e.printStackTrace()
        }
    }
}