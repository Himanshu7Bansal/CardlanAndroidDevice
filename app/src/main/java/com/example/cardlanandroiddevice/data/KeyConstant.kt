package com.example.cardlanandroiddevice.data

import com.cardlan.utils.ByteUtil

object KeyConstant {

    /**
     * The key calculated by the authorization card
     */
    @JvmField
    var mAuthCardKey: ByteArray = ByteUtil.hexStringToByteArray("1122334455667788")
}