package com.example.cardlanandroiddevice.data

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.cardlan.utils.ByteUtil
import com.cardlan.utils.CalendarUtil
import com.example.cardlanandroiddevice.secret.DESTwo
import com.example.cardlanandroiddevice.secret.ICardDataListener
import com.example.cardlanandroiddevice.thread.ReadCardNonUIThread
import com.example.cardlanandroiddevice.util.AbstractBaseLogClass
import com.example.cardlanandroiddevice.util.CardReadWriteUtil
import com.example.cardlanandroiddevice.util.CardlanLog
import com.example.cardlanandroiddevice.util.CpuCardSecretKeyHelper
import java.io.File
import java.io.FileOutputStream
import java.security.InvalidParameterException
import java.util.*

/**
 * Created by cardlan on 18-6-4.
 * Terminal consumption data this class supports
 * reading and writing of unauthorized CARDS and authorization CARDS
 *
 */
class TerminalConsumeDataForSystem : AbstractBaseLogClass() {

    //Consumption amount, unit cent
    private var mConsumeFee = 0

    //Card read threads
    private var mReadThread: Thread? = null

    //    private boolean mNeedStartReadThreadWhenSetConsumeFee = true;
    fun getmConsumeFee(): Int {
        return mConsumeFee
    }

    fun setmConsumeFee(mConsumeFee: Int) {
        this.mConsumeFee = mConsumeFee
    }

    private fun mConsumeFeeIsLegal(): Boolean {
        return mConsumeFee > 0
    }

    private var mReadOrWriteKeyHexStr: String? = null
    fun getmReadOrWriteKeyHexStr(): String? {
        return mReadOrWriteKeyHexStr
    }

    /**
     * Total number of sectors read, default 4
     */
    private var mSectorReadNumber = 4

    /**
     * Sector reads index, default 0
     */
    private var mSectorReadIndex = 1
    private val mCardUtil: CardReadWriteUtil = CardReadWriteUtil()

    //Is it an authorization card
    private var isAuthCard = false
    /**
     * Card data list
     * key ：  sector-index（ sector + index）
     * value：byte array
     */
    //    private HashMap<String, byte[]> mCardBytes = new HashMap<>();
    /**
     * This method will start a thread to read the card information continuously.
     * This method will determine whether the thread exists.
     * If it exists, the thread will not be recreated.
     * So you can stop the thread by [.setmConsumeFee];
     *
     * @param iCardDataListener Card information callback interface，
     */
    fun startRead(iCardDataListener: ICardDataListener?) {
        mReadThread = null
        if (mReadThread != null) {
            if ((mReadThread as Thread).isAlive) {
                return
            }
        } else {
            mReadThread = ReadCardNonUIThread(Runnable {
                while (mConsumeFeeIsLegal()) {
                    // long threadID = Thread.currentThread().getId();
                    // CardlanLog.debugOnConsole(this.getClass(),"thread id :" + threadID);
                    mReadOrWriteKeyHexStr = null
                    isAuthCard = false
                    mCardUtil.initDev()
                    var resetBytes: ByteArray? = null
                    if (iCardDataListener != null) {
                        resetBytes = mCardUtil.cardResetBytes
                        if (!ByteUtil.notNull(resetBytes)) {
                            CardlanLog.debugOnConsole(
                                TerminalConsumeDataForSystem::class.java,
                                "Did not find the card"
                            )
                            continue
                        }
                        try {
                            mReadOrWriteKeyHexStr =
                                ByteUtil.byteArrayToHexString(calculateNormalCardKey(resetBytes))
                        } catch (e: KeyErrorException) {
                            e.printStackTrace()
                        }
                        iCardDataListener.cardResetMsg(resetBytes)
                    }
                    //                        mCardBytes.clear();
                    for (i in mSectorReadIndex..mSectorReadNumber) {
                        //Each sector has four modules, but the last one holds the check code,
                        // so no reading is required
                        if (i == 2 || i == 3) {
                            continue
                        }
                        val sector: Byte = ByteUtil.intToByteTwo(i)
                        var readTemp: ByteArray? = null
                        var j = 0
                        while (j < 3) {
                            if (i == 0 && (j == 1 || j == 2)) {
                                j++
                                continue
                            }
                            val index: Byte = ByteUtil.intToByteTwo(j)
                            if (i > 0) {
                                //Starting from the first sector,
                                // the subsequent reads and writes need to be written using the computed read key
                                readTemp = mCardUtil.callReadJNI(
                                    ByteUtil.byteToHex(sector),
                                    ByteUtil.byteToHex(index),
                                    mReadOrWriteKeyHexStr,
                                    null
                                )
                                if (!ByteUtil.notNull(readTemp)) {
                                    //
                                }
                            } else {
                                readTemp = mCardUtil.callReadJNI(
                                    ByteUtil.byteToHex(sector),
                                    ByteUtil.byteToHex(index), null, null
                                )
                            }
                            //If the data is not read,
                            // you do not need to continue reading because there are no CARDS.
                            if (i == 0 && j == 0) {
                                if (!ByteUtil.notNull(readTemp)) {
                                    //Determine if it is an authorization card
                                    isAuthCard(resetBytes, iCardDataListener)
                                    break
                                } else {
                                    //Ordinary card, calculate the key of ordinary card
                                    val sn = resetBytes
                                    try {
                                        mReadOrWriteKeyHexStr =
                                            ByteUtil.byteArrayToHexString(calculateNormalCardKey(sn))
                                    } catch (e: KeyErrorException) {
                                        e.printStackTrace()
                                        CardlanLog.debugOnConsole(
                                            TerminalConsumeDataForSystem::class.java,
                                            e
                                        )
                                    }
                                    if (iCardDataListener != null) {
                                        iCardDataListener.readMsg(i, j, readTemp, false)
                                    }
                                    //                                        continue;
                                }
                            } else {
                                if (isAuthCard) {
                                    break
                                }
                                if (iCardDataListener != null) {
                                    if (i == mSectorReadNumber && j == 2) {
                                        try {
                                        } catch (e: Exception) {
                                            CardlanLog.debugOnConsole(
                                                TerminalConsumeDataForSystem::class.java,
                                                "Read the card to complete!"
                                            )
                                            CardlanLog.debugOnConsole(
                                                TerminalConsumeDataForSystem::class.java,
                                                ByteUtil.byteArrayToHex(readTemp)
                                            )
                                            CardlanLog.debugOnConsole(
                                                TerminalConsumeDataForSystem::class.java,
                                                "==============="
                                            )
                                        }
                                        mCardUtil.setmHasGetResetBytes(false)
                                        iCardDataListener.readMsg(i, j, readTemp, true)
                                    } else {
                                        iCardDataListener.readMsg(i, j, readTemp, false)
                                    }
                                }
                                //                                    continue;
                            }
                            j++
                        }
                        if (isAuthCard) {
                            break
                        }
                        if (i == 0 && j == 0) {
                            //If the data is not read,
                            // you do not need to continue reading because there are no CARDS
                            if (!ByteUtil.notNull(readTemp)) {
                                break
                            }
                        }
                    }
                }
                CardlanLog.debugOnConsole(
                    TerminalConsumeDataForSystem::class.java,
                    "Exit the card reader thread!"
                )
            })
            mReadThread!!.start()
        }
    }

    /**
     * Whether the write card process was written successfully
     *
     * @param sector    sector index of hex string
     * @param index     block index of hex string
     * @param hexString hex string
     * @param keys      hex string
     * @param keyArea   hex string
     * @return boolean if write success, it return true, else return false
     */
    fun writeData(
        sector: String,
        index: String,
        hexString: String,
        keys: String,
        keyArea: String
    ): Boolean {
        val writeStatus: Int = mCardUtil.callWriteJNI(sector, index, hexString, keys, keyArea)
        CardlanLog.debugOnConsole(this.javaClass, "writeStatus : $writeStatus")
        return writeStatus == 5
    }

    /**
     * Sets the total number of sectors to read, The default is 7.
     *
     * @param mSectorReadNumber
     */
    fun setmSectorReadNumber(mSectorReadNumber: Int) {
        this.mSectorReadNumber = mSectorReadNumber
    }

    /**
     * Sets the index to start reading sectors, with 0 by default
     *
     * @param mSectorReadIndex
     */
    fun setmSectorReadIndex(mSectorReadIndex: Int) {
        this.mSectorReadIndex = mSectorReadIndex
    }

    private fun writeProc(path: String, buffer: ByteArray): String {
        try {
            val file = File(path)
            val fos = FileOutputStream(file)
            fos.write(buffer)
            fos.close()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return "write error!"
        }
        return buffer.toString()
    }

    /**
     * Corresponding buzzer
     */
    fun callProc() {
        writeProc(procpath, open_bee_voice.toByteArray())
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        writeProc(procpath, close_bee_voice.toByteArray())
    }

    fun hideSoftKeyboard(mEditxt: EditText, mAct: Activity) {
        val imm = mAct.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(mEditxt.windowToken, 0)
    }

    fun showSoftKeyboard(mEditxt: EditText, mAct: Activity) {
        val imm = mAct.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null) {
            mEditxt.requestFocus()
            imm.showSoftInput(mEditxt, 0)
        }
    }

    //The key related
    private val assembleBytes = byteArrayOf(0x26, 0x91.toByte(), 0x13, 0x00)

    //The system authorization card is relevant 0x82,0x26,0x00,0x36,0x82,0x42,0x27,0x79
    private val desKeyBytes = byteArrayOf(
        0x82.toByte(), 0x26, 0x00, 0x36, 0x82.toByte(), 0x42,
        0x27, 0x79
    )
    private val checkBytes = byteArrayOf(0x55, 0xa0.toByte(), 0xa1.toByte(), 0xa2.toByte())

    /**
     * The key calculation method of the system authorization card is inconsistent,
     * so it is not suitable for the system authorization card
     * @param cardSNBytes
     * @return the result of NormalCardReadKey.
     * @throws KeyErrorException
     */
    @Throws(KeyErrorException::class)
    fun calculateNormalCardKey(cardSNBytes: ByteArray?): ByteArray {
        return if (ByteUtil.notNull(cardSNBytes)) {
            var srcBytes: ByteArray? = null
            srcBytes = if (cardSNBytes!!.size == 4) {
                cardSNBytes
            } else if (cardSNBytes.size > 4) {
                ByteUtil.copyBytes(cardSNBytes, 0, 4)
            } else {
                val ipe = InvalidParameterException(
                    TerminalConsumeDataForSystem::class.java.simpleName +
                            "Argument is invalid!"
                )
                CardlanLog.debugOnConsole(TerminalConsumeDataForSystem::class.java, ipe)
                throw ipe
            }
            srcBytes = ByteUtil.addBytes(srcBytes, assembleBytes)
            val desKey: ByteArray? = DESTwo.encrypt(srcBytes, KeyConstant.mAuthCardKey)
            if (desKey != null && ByteUtil.notNull(desKey) && desKey.size >= 6) {
                ByteUtil.copyBytes(desKey, 0, 6)
            } else {
                throw KeyErrorException()
            }
        } else {
            val ipe = InvalidParameterException(
                TerminalConsumeDataForSystem::class.java.simpleName +
                        "Argument is invalid!"
            )
            CardlanLog.debugOnConsole(TerminalConsumeDataForSystem::class.java, ipe)
            throw ipe
        }
    }

    /**
     * calculate to the key to read the authorization card,
     * which is used to read the contents of the system authorization card
     * @param cardSNBytes sn of card to array;
     * @return the result of AuthCardReadKey.
     * @throws KeyErrorException
     */
    @Throws(KeyErrorException::class)
    private fun calculateAuthCardReadKey(cardSNBytes: ByteArray?): ByteArray {
        return if (ByteUtil.notNull(cardSNBytes)) {
            var srcBytes: ByteArray? = null
            srcBytes = if (cardSNBytes!!.size == 4) {
                cardSNBytes
            } else if (cardSNBytes.size > 4) {
                ByteUtil.copyBytes(cardSNBytes, 0, 4)
            } else {
                val ipe = InvalidParameterException(
                    TerminalConsumeDataForSystem::class.java.simpleName +
                            "Argument is invalid!"
                )
                CardlanLog.debugOnConsole(TerminalConsumeDataForSystem::class.java, ipe)
                throw ipe
            }
            srcBytes = ByteUtil.addBytes(srcBytes, assembleBytes)
            //            byte[] desKey = DESTwo.encrypt(desKeyBytes, srcBytes);
            val desKey: ByteArray? = DESTwo.encrypt(srcBytes, desKeyBytes)
            if (desKey != null && ByteUtil.notNull(desKey) && desKey.size >= 6) {
                CardlanLog.debugOnConsole(
                    TerminalConsumeDataForSystem::class.java, ByteUtil
                        .byteArrayToHex(desKey)
                )
                ByteUtil.copyBytes(desKey, 0, 6)
            } else {
                throw KeyErrorException()
            }
        } else {
            val ipe = InvalidParameterException(
                TerminalConsumeDataForSystem::class.java.simpleName +
                        "Argument is invalid!"
            )
            CardlanLog.debugOnConsole(TerminalConsumeDataForSystem::class.java, ipe)
            throw ipe
        }
    }

    /**
     * Read the key to the authorization card,
     * which is used to decrypt the key for ordinary CARDS.
     * @param sectorOneIndexZerobytes The first sector 0 blocks of bytes
     * @return
     */
    private fun readAuthCardKey(sectorOneIndexZerobytes: ByteArray): ByteArray? {
        var returnBytes: ByteArray? = null
        if (ByteUtil.notNull(sectorOneIndexZerobytes)) {
            if (sectorOneIndexZerobytes.size >= checkBytes.size + 8) {
                val checkSrc: ByteArray = ByteUtil.copyBytes(sectorOneIndexZerobytes, 0, 4)
                CardlanLog.debugOnConsole(
                    TerminalConsumeDataForSystem::class.java, "checkSrc=" + ByteUtil
                        .byteArrayToHex(checkSrc)
                )
                CardlanLog.debugOnConsole(
                    TerminalConsumeDataForSystem::class.java, "checkBytes=" +
                            ByteUtil
                                .byteArrayToHex(checkBytes)
                )
                if (Arrays.equals(checkSrc, checkBytes)) {
                    returnBytes = ByteUtil.copyBytes(sectorOneIndexZerobytes, 4, 8)
                    KeyConstant.mAuthCardKey = returnBytes
                    CardlanLog.debugOnConsole(
                        TerminalConsumeDataForSystem::class.java, "KeyConstant" +
                                ".mAuthCardKey=" +
                                ByteUtil.byteArrayToHex(KeyConstant.mAuthCardKey)
                    )
                    isAuthCard = true
                }
            }
        }
        return returnBytes
    }

    /**
     * Reads the bytes of the first sector, the 0th field
     * @param readKeys
     * @return byte[]
     */
    private fun readSectorIndexZeroBytes(readKeys: ByteArray): ByteArray? {
        val readSector = 1.toChar()
        val readindex = 0.toChar()
        val bb = 0x0b.toChar()
        return mCardUtil.callReadJNI(readSector, readindex, readKeys, bb)
    }

    private fun isAuthCard(
        cardSNBytes: ByteArray?,
        iCardDataListener: ICardDataListener?
    ): Boolean {
        try {
            CardlanLog.debugOnConsole(
                TerminalConsumeDataForSystem::class.java, "the card sn is :" +
                        ByteUtil.byteArrayToHex(cardSNBytes)
            )
            val readKeys = calculateAuthCardReadKey(cardSNBytes)
            CardlanLog.debugOnConsole(
                TerminalConsumeDataForSystem::class.java, "the auth card " +
                        "readkeys is :" + ByteUtil.byteArrayToHex(readKeys)
            )
            val sectorOneIndexZerobytes = readSectorIndexZeroBytes(readKeys)
            if (sectorOneIndexZerobytes != null) {
                readAuthCardKey(sectorOneIndexZerobytes)
            }
            if (isAuthCard) {
                iCardDataListener?.readMsg(1, 0, null, true)
            }
        } catch (e: KeyErrorException) {
            e.printStackTrace()
        }
        return isAuthCard
    }

    fun readThreadIsAlive(): Boolean {
        return if (mReadThread != null) {
            mReadThread!!.isAlive && !mReadThread!!.isInterrupted
        } else false
    }

    //===================================Cpu卡===========================================
    private val binNameArray = byteArrayOf(0x3f, 0x01)
    private val selfileDffciArray = byteArrayOf(0x00, 0xa4.toByte(), 0x00, 0x00)
    private val ppse = "2PAY.SYS.DDF01"
    private val getBalance = "GET BALANCE"
    private var receiveBytes: ByteArray? = null
    private val readSize = 128
    private var mReadCpuThread: Thread? = null
    private var mReadFlag = true
    fun startReadCpuCardThread() {
        mReadCpuThread = ReadCardNonUIThread(object : Runnable {
            override fun run() {
                while (mReadFlag) {
                    receiveBytes = ByteArray(readSize)
                    mCardUtil.initDev()
                    val binlenth: Byte = ByteUtil.intToByteTwo(binNameArray.size)
                    val tempBytes: ByteArray = ByteUtil.addBytes(selfileDffciArray, binlenth)
                    val sendCmd: ByteArray = ByteUtil.addBytes(tempBytes, binNameArray)
                    printLog(
                        "sendCmd byte array is " + ByteUtil
                            .byteArrayToHexArray(sendCmd)
                    )
                    val resetBytes: ByteArray? = mCardUtil.cardResetBytes
                    if (!ByteUtil.notNull(resetBytes)) {
                        CardlanLog.debugOnConsole(this.javaClass, "while continue")
                        //                        continue;
                    } else {
                        CardlanLog.debugOnConsole(
                            this.javaClass, "resetBytes : " + ByteUtil
                                .byteArrayToHexString(resetBytes)
                        )
                    }
                    val binresult: Int = mCardUtil.callSendCpuCmd(sendCmd, receiveBytes)
                    CardlanLog.debugOnConsole(this.javaClass, "binresult : $binresult")
                    CardlanLog.debugOnConsole(this.javaClass, ByteUtil.byteArrayToHex(receiveBytes))
                    CardlanLog.debugOnConsole(this.javaClass, "==============================")
                    val receivePpseBytes = ByteArray(readSize)
                    val ppselenth: Byte = ByteUtil.intToByteTwo(ppse.length)
                    val tempppseBytes: ByteArray = ByteUtil.addBytes(selfileDffciArray, ppselenth)
                    val sendCmdppse: ByteArray =
                        ByteUtil.addBytes(tempppseBytes, ppse.toByteArray())
                    CardlanLog.debugOnConsole(
                        this.javaClass, "sendCmdppse byte array is " +
                                ByteUtil
                                    .byteArrayToHexArray(sendCmdppse)
                    )
                    CardlanLog.debugOnConsole(this.javaClass, "==============================")
                    val ppseresult: Int = mCardUtil.callSendCpuCmd(sendCmdppse, receivePpseBytes)
                    CardlanLog.debugOnConsole(this.javaClass, "ppseresult : $ppseresult")
                    CardlanLog.debugOnConsole(
                        this.javaClass,
                        ByteUtil.byteArrayToHex(receivePpseBytes)
                    )
                    CardlanLog.debugOnConsole(this.javaClass, "==============================")

                    //Get the balance
                    val receiveBalanceBytes = ByteArray(readSize)
                    val balanceResult: Int = mCardUtil.callSendCpuCmd(
                        getBalance.toByteArray(),
                        receivePpseBytes
                    )
                    CardlanLog.debugOnConsole(
                        this.javaClass,
                        ByteUtil.byteArrayToHex(receiveBalanceBytes)
                    )
                    CardlanLog.debugOnConsole(
                        this.javaClass, "================balanceResult" +
                                "(" + balanceResult + ")=============="
                    )
                    try {
                        Thread.sleep(5 * 1000L)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                CardlanLog.debugOnConsole(this.javaClass, " exit read cpu card")
            }
        })
        //
        mReadCpuThread!!.start()
    }

    fun stopRead() {
        setmConsumeFee(0)
        mReadFlag = false
        CardlanLog.debugOnConsole(this.javaClass, " stop read card")
    }

    fun sendCpuCmd(sendCmd: ByteArray?, needCheckStatus: Boolean): ByteArray? {
        if (!ByteUtil.notNull(sendCmd)) {
            CardlanLog.debugOnConsole(this.javaClass, "cpu cmd is null")
            return null
        }
        CardlanLog.debugOnConsole(
            this.javaClass,
            "sendCmd:" + ByteUtil.byteArrayToHexString(sendCmd)
        )
        val receiveResultBytes = ByteArray(readSize)
        val result: Int = mCardUtil.callSendCpuCmd(sendCmd, receiveResultBytes)
        CardlanLog.debugOnConsole(this.javaClass, "send cpu cmd result status is $result")
        val returnBytes = checkCpuTradeSuccess(receiveResultBytes, needCheckStatus)
        if (!ByteUtil.notNull(returnBytes)) {
            CardlanLog.debugOnConsole(
                this.javaClass, "send cpu cmd result byte array is " +
                        0
            )
            return null
        }
        return returnBytes
    }

    /**
     * Verify that the CPU card transaction is successful,
     * and determine if the data at the end of the data is 9000.
     * @param receiveResultBytes Operate on the CPU card return results
     * @return boolean return real value, if check success
     */
    private fun checkCpuTradeSuccess(
        receiveResultBytes: ByteArray,
        needCheckStatus: Boolean
    ): ByteArray? {
        var returnBytes: ByteArray? = null
        if (!ByteUtil.notNull(receiveResultBytes)) {
            return null
        }
        returnBytes = ByteUtil.removeNULLByte(receiveResultBytes)
        CardlanLog.debugOnConsole(
            this.javaClass, "send cpu cmd return bytes is " +
                    ByteUtil.byteArrayToHexArray(returnBytes)
        )
        if (!ByteUtil.notNull(returnBytes)) {
            return null
        }
        val lastByte = returnBytes[returnBytes.size - 1]
        val compareByte = 0x90.toByte()
        if (needCheckStatus) {
            if (lastByte != compareByte) {
                return null
            }
        }
        return returnBytes
    }

    val cardResetBytes: ByteArray?
        get() = mCardUtil.cardResetBytes

    //File selection
    private val cmd_file_select = "00A40000023F0100"

    //Initialize the consumption header
    private val cmd_init_consume = "805001020B"

    //Initialize the consumer key index
    private val cmd_init_consume_key_index = "01"

    //Initial consumption transaction amount (cent)
    private val cmd_init_consume_fee = "00000001"

    //Initialize the consumer transaction terminal number
    private val cmd_init_consume_terminal_no = "100000000321"

    //Initializes the consumption terminator
    private val cmd_init_consume_end = "0F"

    //debit　The command header
    private val cmd_debit = "805401000F"

    //Command the tail
    private val cmd_consume_end = "08"
    private var fileBytes: ByteArray? = null

    //count of consumption
    private var consumeCount = 8
    private val mCpuCardSecretKeyHelper: CpuCardSecretKeyHelper = CpuCardSecretKeyHelper()

    //Initializes the resulting array of consumption
    var initCpuConsumeBytes: ByteArray? = null
        private set

    /**
     * Gets the key for the consumption parameter.
     * @return byte[]
     */
    val consumeInitKey: ByteArray?
        get() = null as ByteArray?

    /**
     * Select the file
     */
    fun selectCpuFile(): ByteArray? {
        fileBytes = sendCpuCmd(ByteUtil.hexStringToByteArray(cmd_file_select), true)
        CardlanLog.debugOnConsole(
            this.javaClass,
            "fileBytes :" + ByteUtil.byteArrayToHexArray(fileBytes)
        )
        return fileBytes
    }

    /**
     * Initialize the consumption command of CPU card.
     *
     * @return
     */
    fun sendInitCpuConsumeCmd(consumeFee: Int): ByteArray? {
        selectCpuFile()
        var returnBytes: ByteArray? = null
        val cmdSb = StringBuilder()
        cmdSb.append(cmd_init_consume)
        cmdSb.append(cmd_init_consume_key_index)
        if (consumeFee <= 0) {
            cmdSb.append(cmd_init_consume_fee)
        } else {
            cmdSb.append(ByteUtil.intToHexString(consumeFee))
        }
        cmdSb.append(cmd_init_consume_terminal_no)
        cmdSb.append(cmd_init_consume_end)
        printLog("init Command is:$cmdSb")
        returnBytes = sendCpuCmd(ByteUtil.hexStringToByteArray(cmdSb.toString()), true)
        printLog("init The result of the command is:" + ByteUtil.byteArrayToHexArray(returnBytes))
        return returnBytes
    }

    /**
     * The second des calculation,
     * associated with the first result, is based on the initialization consumption.
     *
     * @return byte[] it not null ,it work success
     */
    fun calculateSecondDes(mConsumeFee: Int): ByteArray? {
        var returnBytes: ByteArray? = null
        val mkeys = calculateFirstDes()
        if (!ByteUtil.notNull(mkeys)) {
            return returnBytes
        }
        initCpuConsumeBytes = sendInitCpuConsumeCmd(mConsumeFee)
        if (!ByteUtil.notNull(initCpuConsumeBytes)) {
            printLog("init failure:")
            return returnBytes
        }
        printLog("keys:" + ByteUtil.byteArrayToHexArray(mkeys))
        printLog("initCpuConsumeBytes : " + ByteUtil.byteArrayToHexArray(initCpuConsumeBytes))
        var sendCmdBytes: ByteArray? = null
        sendCmdBytes = ByteUtil.copyBytes(initCpuConsumeBytes, 11, 4)
        sendCmdBytes = ByteUtil.addBytes(
            sendCmdBytes, ByteUtil.copyBytes(
                initCpuConsumeBytes, 4,
                2
            )
        )
        val consumeConuts: ByteArray = ByteUtil.intToByteArray(consumeCount)
        sendCmdBytes = ByteUtil.addBytes(sendCmdBytes, ByteUtil.copyBytes(consumeConuts, 2, 2))
        printLog(sendCmdBytes)
        if (!ByteUtil.notNull(sendCmdBytes)) {
            return returnBytes
        }
        printLog("sendCmdBytes:" + ByteUtil.byteArrayToHexArray(sendCmdBytes))
        returnBytes = mCpuCardSecretKeyHelper.callRunDes(sendCmdBytes, mkeys)
        printLog(returnBytes)
        return returnBytes
    }

    /**
     * Calculate the MAC
     *
     * @param consumeFee consumeFee
     * @return byte[] if not null, it work success.
     */
    fun calculateMac(consumeFee: Int): ByteArray? {
        var returnBytes: ByteArray? = null
        var consumeFeeBytes: ByteArray? = null
        consumeFeeBytes = if (consumeFee <= 0) {
            ByteUtil.hexStringToByteArray(cmd_init_consume_fee)
        } else {
            ByteUtil.intToByteArray(consumeFee)
        }
        printLog("Transaction amount：" + ByteUtil.byteArrayToIntHighToLow(consumeFeeBytes))
        var sendCmdBytes: ByteArray? = null
        sendCmdBytes = consumeFeeBytes
        sendCmdBytes = ByteUtil.addBytes(sendCmdBytes, 0x06.toByte())
        //Add the terminal number
        sendCmdBytes = ByteUtil.addBytes(
            sendCmdBytes,
            ByteUtil.hexStringToByteArray(cmd_init_consume_terminal_no)
        )
        //Trading hours
        val hexTradeTime: String = CalendarUtil.getDefaultYYYYMMddHHmmss()
        printLog("Trading hours$hexTradeTime")
        sendCmdBytes = ByteUtil.addBytes(sendCmdBytes, ByteUtil.hexStringToByteArray(hexTradeTime))
        //Calculate the MAC
        val keys = calculateSecondDes(consumeFee)
        printLog("Mac sendCmdBytes：" + ByteUtil.byteArrayToHexArray(sendCmdBytes))
        printLog("Mac keys：" + ByteUtil.byteArrayToHexArray(keys))
        returnBytes = mCpuCardSecretKeyHelper.callMacAnyLength(sendCmdBytes, keys)
        return returnBytes
    }

    /**
     * Calculate the first of　Des.
     *
     * @return byte[] Return des results
     */
    fun calculateFirstDes(): ByteArray? {
        return cardResetBytes?.let { mCpuCardSecretKeyHelper.getCpuCardKey(it) }
    }

    /**
     * According to the input amount of CPU card consumption process,and the consumption result is returned
     * @param consumeFee
     * @return byte[] Return consumption results
     */
    fun cpuConsume(consumeFee: Int): ByteArray? {
        mCardUtil.initDev()
        initCpuConsumeBytes = null
        var returnBytes: ByteArray? = null
        var sendCmdBytes: ByteArray? = null
        sendCmdBytes = ByteUtil.hexStringToByteArray(cmd_debit)
        //Transaction number
        val consumeConutBytes: ByteArray = ByteUtil.intToByteArray(consumeCount)
        sendCmdBytes = ByteUtil.addBytes(sendCmdBytes, consumeConutBytes)
        //Trading hours
        val hexTradeTime: String = CalendarUtil.getDefaultYYYYMMddHHmmss()
        sendCmdBytes = ByteUtil.addBytes(sendCmdBytes, ByteUtil.hexStringToByteArray(hexTradeTime))
        //mac
        val macBytes = calculateMac(consumeFee)
        sendCmdBytes = ByteUtil.addBytes(sendCmdBytes, ByteUtil.copyBytes(macBytes, 0, 4))
        sendCmdBytes = ByteUtil.addBytes(sendCmdBytes, 0x08.toByte())
        //Send consumption command
        returnBytes = sendCpuCmd(sendCmdBytes, true)
        if (ByteUtil.notNull(returnBytes)) {
            //Consumption is successful.
            consumeCount++
        }
        return returnBytes
    }

    fun clear() {
        fileBytes = null
    }

    companion object {

        //Buzzer parameters
        private const val procpath = "/proc/gpio_set/rp_gpio_set"
        private const val open_bee_voice = "c_24_1_1"
        private const val close_bee_voice = "c_24_1_0"
    }
}