package com.example.cardlanandroiddevice.util

import com.cardlan.twoshowinonescreen.CardLanStandardBus
import com.cardlan.utils.ByteUtil
import com.example.cardlanandroiddevice.util.CardlanLog.debugOnConsole

/**
 * cpu card of Key helper class
 * Created by cardlan on 18-7-16.
 */
class CpuCardSecretKeyHelper {

    //The key related
    private val assembleBytes = byteArrayOf(0x26, 0x91.toByte(), 0x13, 0x00)
    private val hexSrcKey = "1122334455667788"
    private var mErrorMsg: String? = null
    private val mCardLanDes = CardLanStandardBus()

    /**
     * Access the encrypted key of the CPU card through sn.
     * @param cardSn
     * @return
     */
    fun getCpuCardKey(cardSn: ByteArray): ByteArray? {
        debugOnConsole(this.javaClass, ByteUtil.byteArrayToHexString(cardSn))
        mErrorMsg = null
        var returnBytes: ByteArray? = null
        if (!ByteUtil.notNull(cardSn)) {
            mErrorMsg = "the card sn byte array is null"
            return returnBytes
        }
        var tempOne: ByteArray? = null
        val srcKeyBytes = ByteUtil.hexStringToByteArray(hexSrcKey)
        if (!ByteUtil.notNull(srcKeyBytes)) {
            mErrorMsg = "the src Key Bytes is null"
            return returnBytes
        }
        if (cardSn.size < 4) {
            mErrorMsg = "the card sn length is small than 4"
            return returnBytes
        }
        var valibCardSN: ByteArray? = null
        valibCardSN = if (cardSn.size == 4) {
            cardSn
        } else {
            ByteUtil.copyBytes(cardSn, 0, 4)
        }
        tempOne = ByteUtil.addBytes(srcKeyBytes, ByteUtil.naBytes(srcKeyBytes))
        debugOnConsole(
            this.javaClass, "the temp one array is " + ByteUtil
                .byteArrayToHexArray(tempOne)
        )
        var desSrc: ByteArray? = null
        desSrc = ByteUtil.addBytes(valibCardSN, assembleBytes)
        val tempTwo = ByteUtil.addBytes(desSrc, ByteUtil.naBytes(desSrc))
        debugOnConsole(
            this.javaClass, "the temp two array is " + ByteUtil
                .byteArrayToHexArray(tempTwo)
        )
        val headOutBytes = ByteArray(8)
        val headTempBytes = ByteUtil.copyBytes(tempTwo, 0, 8)
        debugOnConsole(
            this.javaClass, "the headTempBytes is " + ByteUtil
                .byteArrayToHexArray(headTempBytes)
        )
        debugOnConsole(
            this.javaClass,
            " headTempBytes length" + headTempBytes.size + ", tempOne length " + tempOne.size
        )
        val resultHead = mCardLanDes.callRunDes(
            0.toChar(), 0.toChar(), headTempBytes,
            headOutBytes, tempOne
        )
        debugOnConsole(
            this.javaClass,
            "the RunDes result is " + ByteUtil.byteToHex(resultHead.code.toByte())
        )
        debugOnConsole(
            this.javaClass, "the headOutBytes is " + ByteUtil
                .byteArrayToHexArray(headOutBytes)
        )
        val tailOutBytes = ByteArray(8)
        val tailTempBytes = ByteUtil.copyBytes(tempTwo, tempTwo.size - 8, 8)
        debugOnConsole(
            this.javaClass, "the tailTempBytes is " + ByteUtil
                .byteArrayToHexArray(tailTempBytes)
        )
        debugOnConsole(
            this.javaClass,
            " tailTempBytes length" + tailTempBytes.size + ", tempOne length " + tempOne.size
        )
        val resultTail = mCardLanDes.callRunDes(
            0.toChar(), 0.toChar(), tailTempBytes,
            tailOutBytes, tempOne
        )
        debugOnConsole(
            this.javaClass,
            "the RunDes result is " + ByteUtil.byteToHex(resultTail.code.toByte())
        )
        debugOnConsole(
            this.javaClass, "the tailOutBytes is " + ByteUtil
                .byteArrayToHexArray(tailOutBytes)
        )
        if (ByteUtil.notNull(headOutBytes) && ByteUtil.notNull(tailOutBytes)) {
            returnBytes = ByteUtil.addBytes(headOutBytes, tailOutBytes)
        } else {
            mErrorMsg = "get Cpu Card Key failed"
        }
        debugOnConsole(
            this.javaClass, "the returnBytes array is " + ByteUtil
                .byteArrayToHexArray(returnBytes)
        )
        return returnBytes
    }

    fun getmErrorMsg(): String? {
        return mErrorMsg
    }

    //File selection
    private val cmd_file_select = "00A40000023F0100"

    //Initialize consumption _ headers
    private val cmd_init_consume = "805001020B"

    //Initialize the consumption _ key index
    private val cmd_init_consume_key_index = "01"

    //Initial consumption _ transaction amount (cent)
    private val cmd_init_consume_fee = "00000001"

    //Initialize the consumer transaction terminal number
    private val cmd_init_consume_terminal_no = "100000000321"

    //Initializes the consumption terminator
    private val cmd_init_consume_end = "0F"

    /**
     * 　Gets the key for the consumption argument
     * @param consumeInitBytes Consumes the bytes returned after initialization
     * @return byte[]
     */
    fun getConsumeInitKey(consumeInitBytes: ByteArray?): ByteArray? {
        return null as ByteArray?
    }

    /**
     * Call the[CardLanDes.callRunDes]. Gets the encrypted byte array
     *
     * @param srcBytes The encrypted source array
     * @param keyBytes　Encrypted key
     * @return
     */
    fun callRunDes(srcBytes: ByteArray, keyBytes: ByteArray?): ByteArray? {
        mErrorMsg = null
        var returnBytes: ByteArray? = null
        if (!ByteUtil.notNull(srcBytes)) {
            mErrorMsg = "the srcBytes length is null"
            return returnBytes
        }
        if (!ByteUtil.notNull(keyBytes)) {
            mErrorMsg = "the keyBytes length is null"
            return returnBytes
        }
        returnBytes = ByteArray(srcBytes.size)
        val resultHead = mCardLanDes.callRunDes(
            0.toChar(), 0.toChar(), srcBytes,
            returnBytes, keyBytes
        )
        if (resultHead.code == 0) {
            //TODO
        }
        return returnBytes
    }

    /**
     * Call back any length of MAC,
     * [CardLanDes.MacAnyLength].
     * @param srcBytes
     * @param keyBytes Encrypted key converted array
     * @return
     */
    fun callMacAnyLength(srcBytes: ByteArray, keyBytes: ByteArray?): ByteArray? {
        mErrorMsg = null
        var returnBytes: ByteArray? = null
        if (!ByteUtil.notNull(srcBytes)) {
            mErrorMsg = "the srcBytes length is null"
            return returnBytes
        }
        if (!ByteUtil.notNull(keyBytes)) {
            mErrorMsg = "the keyBytes length is null"
            return returnBytes
        }
        val initInBytes = ByteArray(srcBytes.size)
        returnBytes = ByteArray(srcBytes.size)
        val macResult = mCardLanDes.MacAnyLength(initInBytes, srcBytes, returnBytes, keyBytes)
        if (macResult.code == 0) {
            //TODO
        }
        return returnBytes
    }
}