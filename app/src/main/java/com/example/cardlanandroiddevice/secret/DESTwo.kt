package com.example.cardlanandroiddevice.secret

import com.cardlan.twoshowinonescreen.CardLanStandardBus
import com.example.cardlanandroiddevice.util.CardlanLog

object DESTwo {

    /**
     * This method is used to encrypt the card[CardLanDes.callDesCard],
     * Leave it to the underlying hardware to process and get the results.
     * @param data card sn.
     * @param keyBytes encrypted card sn.
     * @return byte[]
     */
    fun encrypt(data: ByteArray?, keyBytes: ByteArray?): ByteArray? {
        try {
            return CardLanStandardBus().callDesCard(keyBytes, data)
        } catch (e: Throwable) {
            CardlanLog.debugOnConsole(DESTwo::class.java, Exception(e))
            e.printStackTrace()
        }
        return null
    }
}