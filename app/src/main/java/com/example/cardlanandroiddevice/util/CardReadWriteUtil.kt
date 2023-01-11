package com.example.cardlanandroiddevice.util

import com.cardlan.twoshowinonescreen.CardLanStandardBus
import com.cardlan.utils.ByteUtil
import com.example.cardlanandroiddevice.util.CardlanLog.debugOnConsole

/**
 * card operation util
 */
class CardReadWriteUtil {

    private val mCardLanDevCtrl: CardLanStandardBus
    private var mHasInitDev = false
    private var mHasGetResetBytes = false
    private var mInitStatus = -1

    /**
     * get the card sn
     * @return return the card sn byte array, if not search card, it return null
     */
    val cardResetBytes: ByteArray?
        get() {
            initDev()
            val resetByte = ByteArray(S_Reset_buffer_size)
            val cardResult = mCardLanDevCtrl.callCardReset(resetByte)
            if (cardResult == 1) {
                mHasGetResetBytes = false
                return null
            }
            mHasGetResetBytes = true
            return resetByte
        }

    init {
        mCardLanDevCtrl = CardLanStandardBus()
    }

    /**
     * Reads data from a sector or block
     *
     * @param readSectorStr     Sector index, default 0
     * @param readIndexStr      Domain index, default 0
     * @param readkeyHexStr     The check Key for the read operation, default"0xFFFFFFFFFFFF"
     * @param readKeyAreaHexStr read block of Key,default:0x0b,It is only can operation to "0x0a" or "0x0b",if not，default:"0x0b"
     * @return byte[]
     */
    fun callReadJNI(
        readSectorStr: String, readIndexStr: String, readkeyHexStr: String?,
        readKeyAreaHexStr: String?
    ): ByteArray? {
        var mReadKeyAreaHexStr = readKeyAreaHexStr
        val readSector = stringToChar(readSectorStr)
        val readIndex = stringToChar(readIndexStr)
        val readkeyHex = readkeyHexStr?.let { hexToByteArray(it) }
        if (!ByteUtil.notNull(mReadKeyAreaHexStr)) {
            mReadKeyAreaHexStr = "0b"
        }
        if (mReadKeyAreaHexStr != "0a" && mReadKeyAreaHexStr != "0b") {
            mReadKeyAreaHexStr = "0b"
        }
        val readKeyArea = ByteUtil.hexStringToChar(mReadKeyAreaHexStr)
        return callReadJNI(readSector, readIndex, readkeyHex, readKeyArea)
    }

    /**
     * Call the[CardLanDevCtrl.callReadOneSectorDataFromCard],
     * Read data from a sector or block
     *
     * @param readSector Sector index
     * @param readindex  block index
     * @param readkey The check Key for the read operation
     * @param readKeyArea  read block of Key,default:0x0b,It is only can operation to "0x0a" or "0x0b"
     * @return byte[]
     */
    fun callReadJNI(
        readSector: Char,
        readindex: Char,
        readkey: ByteArray?,
        readKeyArea: Char
    ): ByteArray? {

        //Initialization machine
        initDev()
        val one = 1.toChar()
        if (!mHasGetResetBytes) {
            val resetByte = ByteArray(S_Reset_buffer_size)
            val cardResult = mCardLanDevCtrl.callCardReset(resetByte)
        }
        val readMsg = mCardLanDevCtrl.callReadOneSectorDataFromCard(
            readSector,
            readindex, one,
            readkey, readKeyArea
        )
        if (ByteUtil.notNull(readMsg)) {
            val realStr = ByteUtil.byteArrayToHexString(readMsg)
            debugOnConsole(CardReadWriteUtil::class.java, "The information read is：$realStr")
            return readMsg
        }
        return null
    }

    /**
     * Writes data to the card
     *
     * @param writeSectorStr  Sector index
     * @param writeindexStr  block index
     * @param writeHexStr  Write hexadecimal
     * @param hexWriteKey     default:"0xFFFFFFFFFFFF"
     * @param writeKeyAreaStr default:"0b"
     * @return  Returns the status of the write operation was successful
     */
    fun callWriteJNI(
        writeSectorStr: String,
        writeindexStr: String,
        writeHexStr: String,
        hexWriteKey: String,
        writeKeyAreaStr: String
    ): Int {
        var writeKeyAreaStr = writeKeyAreaStr
        val writeSector = stringToChar(writeSectorStr)
        val writeindex = stringToChar(writeindexStr)
        val writeKey = hexToByteArray(hexWriteKey)
        if (!ByteUtil.notNull(writeKeyAreaStr)) {
            writeKeyAreaStr = "0b"
        }
        if (writeKeyAreaStr != "0a" && writeKeyAreaStr != "0b") {
            writeKeyAreaStr = "0b"
        }
        val writeKeyArea = ByteUtil.hexStringToChar(writeKeyAreaStr)
        return callWriteJNI(writeSector, writeindex, writeHexStr, writeKey, writeKeyArea)
    }

    /**
     * Call the[CardLanDevCtrl.callWriteOneSertorDataToCard],
     * Writes data to the card
     *
     * @param writeSector Sector index
     * @param writeindex  block index
     * @param writeHexStr Write hexadecimal
     * @param writeKey the check Key for the read operation
     * @param readKeyArea The area to read the key
     * @return int
     */
    fun callWriteJNI(
        writeSector: Char,
        writeindex: Char,
        writeHexStr: String,
        writeKey: ByteArray?,
        readKeyArea: Char
    ): Int {
        initDev()
        //reset card
        if (!mHasGetResetBytes) {
            val resetByte =
                ByteArray(S_Reset_buffer_size)
            val cardResult = mCardLanDevCtrl.callCardReset(resetByte)
        }
        val writeBytes = hexToByteArray(writeHexStr)
        val one = 1.toChar()
        return mCardLanDevCtrl.callWriteOneSertorDataToCard(
            writeBytes,
            writeSector,
            writeindex, one,
            writeKey, readKeyArea
        )
    }

    /**
     * Call the[CardLanDevCtrl.callInitDev],
     * Initialize the device. If it has been initialized, it will not be initialized again
     */
    fun initDev() {
        if (!ismHasInitDev()) {
            mInitStatus = mCardLanDevCtrl.callInitDev()
            debugOnConsole(this.javaClass, "initStatus:$mInitStatus")
            mHasInitDev = mInitStatus == 0 || mInitStatus == -3 || mInitStatus == -4
        }
    }

    private fun stringToChar(string: String): Char {
        var string: String? = string
        if (!ByteUtil.notNull(string)) {
            string = "0"
        }
        return ByteUtil.intStringToChar(string)
    }

    private fun hexToByteArray(hex: String): ByteArray {
        var hex: String? = hex
        if (!ByteUtil.notNull(hex)) {
            hex = "FFFFFFFFFFFF"
        }
        return ByteUtil.hexStringToByteArray(hex)
    }

    fun setmHasInitDev(mHasInitDev: Boolean) {
        this.mHasInitDev = mHasInitDev
    }

    fun ismHasInitDev(): Boolean {
        return mHasInitDev
    }

    fun setmHasGetResetBytes(mHasGetResetBytes: Boolean) {
        this.mHasGetResetBytes = mHasGetResetBytes
    }

    /**
     * Call the[CardLanDevCtrl.callCpuSendCmd],
     * CPU card sends CMD command to communicate with hardware;
     * @param cmdArray cmd array
     * @param receiveArray receive array
     * @return
     */
    fun callSendCpuCmd(cmdArray: ByteArray?, receiveArray: ByteArray?): Int {
        return mCardLanDevCtrl.callCpuSendCmd(cmdArray, receiveArray)
    }

    companion object {

        private const val S_Reset_buffer_size = 32
    }
}