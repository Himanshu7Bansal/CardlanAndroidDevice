package com.example.cardlanandroiddevice.secret

interface ICardDataListener {

    /**
     * Read the card's data
     * @param sector
     * @param index  the index for each block in the sector
     * @param readBytes byte[] data read from the sector
     * @param readFinished data reading is finished
     */
    fun readMsg(sector: Int, index: Int, readBytes: ByteArray?, readFinished: Boolean)

    /**
     * The data returned when the card is reset
     * @param bytes
     */
    fun cardResetMsg(bytes: ByteArray?)
}