/*
package com.example.cardlanandroiddevice

import android.app.Activity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.cardlan.twoshowinonescreen.CardLanAdcHelper
import com.cardlan.twoshowinonescreen.CardLanSerialHelper
import com.cardlan.twoshowinonescreen.CardLanSerialHelper.FileCallBack
import com.cardlan.twoshowinonescreen.CardLanSpiHelper
import com.cardlan.twoshowinonescreen.CardLanSpiHelper.SpiCallBackLinstener
import com.cardlan.twoshowinonescreen.CardLanStandardBus
import com.cardlan.utils.ByteUtil
import com.example.cardlanandroiddevice.data.KeyErrorException
import com.example.cardlanandroiddevice.data.TerminalConsumeDataForSystem
import com.example.cardlanandroiddevice.thread.DefaultBaseNonUIThread
import com.example.cardlanandroiddevice.util.CardReadWriteUtil
import com.example.cardlanandroiddevice.util.CardlanLog.debugOnConsole
import com.example.cardlanandroiddevice.util.CpuCardSecretKeyHelper
import com.example.cardlanandroiddevice.util.SharedPreferencesHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.UnsupportedEncodingException

*/
/**
 * Created by cardlan on 18-5-24.
 *//*

class DemoActivity : Activity(), View.OnClickListener, OnLongClickListener, SpiCallBackLinstener {

    var mCardLanDevCtrl = CardLanStandardBus()
    var realData: ByteArray
    var mEditxt_sector_read: EditText? = null
    var mEditxt_read_index: EditText? = null
    var mEditxt_read_key: EditText? = null
    var mEditxt_read_key_type: EditText? = null
    var mEditxt_sector_write: EditText? = null
    var mEditxt_write_index: EditText? = null
    var mEditxt_wirte_key: EditText? = null
    var mEditxt_write_key_type: EditText? = null
    var mReadWriteUtil = CardReadWriteUtil()
    var mRelayout_des: LinearLayout? = null
    var mIsNeedShowCrypt = true
    var terminal: TerminalConsumeDataForSystem? = null
    var mFileInputStream: FileInputStream? = null
    var buffer = ByteArray(32)
    var size = 0
    var adcStr: String? = null

    */
/**
     * Total number of sectors read, default 4
     *//*

    private val mSectorReadNumber = 4

    */
/**
     * Sector reads index, default 0
     *//*

    private val mSectorReadIndex = 1
    private var mReadOrWriteKeyHexStr: String? = null
    private val consumeFee = 1
    var mTxtView_calcu_cpu_mac_value: TextView? = null
    var mTxtView_cp_value: TextView? = null
    var mTxtView_cpu_card_remain_fee: TextView? = null
    var mEditxt_cpu_record: EditText? = null
    var mEditxt_cpu_consume_fee: EditText? = null
    var mRecordSB = StringBuilder()
    var mSharedPreferencesHelper: SharedPreferencesHelper? = null
    var mCmdKey = "cpuCmdRecord"
    private var mInitDev_status_value: TextView? = null
    private var mTxtViewCryptContent: TextView? = null
    private var mCpuCryptContent: TextView? = null
    private var mTvResetCard: TextView? = null
    private var mCpucmdContent: TextView? = null
    private var mTxtView_ADC: TextView? = null
    private var mRead_result: TextView? = null
    private var mWrite_value: TextView? = null
    private var mTv_qc_result: TextView? = null
    var CardSn = ByteArray(16)
    private var mStringBuilder: StringBuilder? = null
    var realQrCode: String? = null
    var QRCodeTailSplit = "\r\n"
    private val QrCodeFlag = mCardLanDevCtrl.callSerialOpen("/dev/ttyAMA4", 115200, 0)
    private val ReadCardFlag = mCardLanDevCtrl.callCardReset(CardSn)
    private var mTTS_editText: EditText? = null
    private var mSpiHelper: CardLanSpiHelper? = null
    private val mRelayReceiver: TextView? = null
    private var mLastValue_str: String? = null
    private var spireceiver: TextView? = null
    private var mReadLength: ByteArray
    private var mReadData: ByteArray
    private var mEditxtCpUwirte: EditText? = null
    private var psamdata_textview: TextView? = null
    private var cardNumber: TextView? = null
    private var mCPUwriteStatusvalue: TextView? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        terminal = TerminalConsumeDataForSystem()
        mStringBuilder = StringBuilder()
        psamdata_textview = findViewById(R.id.psam_tv)
        val mBtn_initDev = findViewById<Button>(R.id.mBtn_initDev)
        val mBtn_reset_card = findViewById<Button>(R.id.mBtn_reset_card)
        val mBtn_read_card = findViewById<Button>(R.id.mBtn_read_card)
        val mBtn_write_card = findViewById<Button>(R.id.mBtn_write_card)
        mBtn_initDev.setOnClickListener(this)
        mBtn_reset_card.setOnClickListener(this)
        mBtn_read_card.setOnClickListener(this)
        mBtn_write_card.setOnClickListener(this)
        if (mSpiHelper == null) {
            mSpiHelper = CardLanSpiHelper()
            mSpiHelper!!.setSpiCallBackLinstener(this)
            mSpiHelper!!.start()
        }
        mSharedPreferencesHelper = SharedPreferencesHelper(this, "cpuCmd")
        mEditxt_sector_read = findViewById(R.id.mEditxt_sector_read)
        mEditxt_read_index = findViewById(R.id.mEditxt_read_index)
        mEditxt_read_key = findViewById(R.id.mEditxt_read_key)
        mEditxt_read_key_type = findViewById(R.id.mEditxt_read_key_type)
        mRead_result = findViewById(R.id.mTxtView_read_result)
        mWrite_value = findViewById(R.id.mTxtView_write_statusvalue)
        mEditxt_sector_write = findViewById(R.id.mEditxt_sector_write)
        mEditxt_write_index = findViewById(R.id.mEditxt_write_index)
        mEditxt_wirte_key = findViewById(R.id.mEditxt_wirte_key)
        mEditxt_write_key_type = findViewById(R.id.mEditxt_write_key_type)
        mTvResetCard = findViewById(R.id.tv_reset_card)
        mRelayout_des = findViewById(R.id.mRelayout_des)
        mTxtViewCryptContent = findViewById(R.id.mTxtView_crypt_content)
        val mBtn_CPUwrite = findViewById<Button>(R.id.mBtn_CPUwrite_card)
        val mBtn_encrypt = findViewById<Button>(R.id.mBtn_encrypt)
        val mBtn_cpuDecrypt = findViewById<Button>(R.id.mBtn_cpu_encrypt)
        mTxtViewCryptContent = findViewById(R.id.mTxtView_crypt_content)
        val mBtn_cpu_consume = findViewById<Button>(R.id.mBtn_cpu_consume)
        mCpuCryptContent = findViewById(R.id.mTxtView_cpucrypt_content)
        val mBtn_cpu_smd = findViewById<Button>(R.id.mBtn_select_cpu)
        mCpucmdContent = findViewById(R.id.mTxtView_cpu_cmd_value)
        mTxtView_ADC = findViewById(R.id.mTxtView_ADC)
        spireceiver = findViewById(R.id.psam_tv)
        cardNumber = findViewById(R.id.psam_card)
        val bt_tts_btn = findViewById<Button>(R.id.bt_tts_btn)
        mBtn_CPUwrite.setOnClickListener(this)
        mBtn_cpu_smd.setOnClickListener(this)
        mBtn_encrypt.setOnClickListener(this)
        mBtn_cpuDecrypt.setOnClickListener(this)
        mBtn_cpu_consume.setOnClickListener(this)
        bt_tts_btn.setOnClickListener(this)
        //mRelayout_des.setVisibility(mIsNeedShowCrypt ? View.VISIBLE : View.GONE);
        mInitDev_status_value = findViewById(R.id.mTxtView_initDev_status_value)
        mEditxt_cpu_consume_fee = findViewById(R.id.mEditxt_cpu_consume_fee)
        mTxtView_calcu_cpu_mac_value = findViewById(R.id.mTxtView_calcu_cpu_mac_value)
        mTxtView_cp_value = findViewById(R.id.mTxtView_cp_value)
        mTxtView_cpu_card_remain_fee = findViewById(R.id.mTxtView_cpu_card_remain_fee)
        mEditxtCpUwirte = findViewById(R.id.mEditxt_CPUwirte_key)
        mCPUwriteStatusvalue = findViewById(R.id.mTxtView_CPUwrite_statusvalue)
        val mBtn_calcu_cpu_mac = findViewById<Button>(R.id.mBtn_calcu_cpu_mac)
        mBtn_calcu_cpu_mac.setOnClickListener(this)
        val mBtn_buzzer = findViewById<Button>(R.id.bt_serial)
        val mBtn_ledRed = findViewById<Button>(R.id.bt_serialledred)
        val mBtn_ledGreen = findViewById<Button>(R.id.bt_serialledgreen)
        val mBtn_Serial_QC = findViewById<Button>(R.id.bt_serial_btn)
        mTv_qc_result = findViewById(R.id.tv_serial_result)
        mTTS_editText = findViewById(R.id.ed_send)
        val btn_Relay = findViewById<Button>(R.id.signal_btn)
        val btn_low = findViewById<Button>(R.id.signal_btn_low)
        val mBtn_psam1 = findViewById<Button>(R.id.bt_psamdata1)
        val mBtn_psam2 = findViewById<Button>(R.id.bt_psamdata2)
        val mBtn_psam3 = findViewById<Button>(R.id.bt_psamdata3)
        val mBtn_psam4 = findViewById<Button>(R.id.bt_psamdata4)
        val mBtn_psam5 = findViewById<Button>(R.id.bt_psamdata5)
        val mBtn_psam6 = findViewById<Button>(R.id.bt_psamdata6)
        val mBtn_clear = findViewById<Button>(R.id.bt_clear)
        mBtn_psam1.setOnClickListener(this)
        mBtn_psam2.setOnClickListener(this)
        mBtn_psam3.setOnClickListener(this)
        mBtn_psam4.setOnClickListener(this)
        mBtn_psam5.setOnClickListener(this)
        mBtn_psam6.setOnClickListener(this)
        mBtn_clear.setOnClickListener(this)

        //mRelayReceiver = findViewById(R.id.signal_receiver);
        //        AdcReadThread adcThread     = new AdcReadThread();
        //        adcThread.start();
        mBtn_buzzer.setOnClickListener(this)
        mBtn_ledRed.setOnClickListener(this)
        mBtn_ledGreen.setOnClickListener(this)
        mBtn_buzzer.setOnLongClickListener(this)
        mBtn_ledRed.setOnLongClickListener(this)
        mBtn_ledGreen.setOnLongClickListener(this)
        mBtn_Serial_QC.setOnClickListener(this)
        btn_Relay.setOnClickListener(this)
        btn_low.setOnClickListener(this)
        val cmds = mSharedPreferencesHelper!!.getSharedPreference(mCmdKey, "") as String?
        if (cmds != null && cmds != "") {
            mEditxt_cpu_record!!.setText(cmds)
            mRecordSB.append(cmds)
        }
        AdcTest()
    }

    private fun AdcTest() {
        val mAdcHelper = CardLanAdcHelper { value ->
            mTxtView_ADC!!.post {
                val key_value = value.toInt()
                val key_str: String
                key_str = if (0 <= key_value && 500 > key_value) {
                    "左"
                } else if (500 <= key_value && 1000 > key_value) {
                    "下"
                } else if (1000 <= key_value && 2000 > key_value) {
                    "右"
                } else if (2000 <= key_value && 3000 > key_value) {
                    "上"
                } else {
                    "unknown"
                }
                if (key_str !== "unknown" && mLastValue_str !== key_str) {
                    terminal!!.callProc()
                }
                mLastValue_str = key_str
                //                        if (value != null) {
                //                            mTerminalConsumeData.callProc();
                //                        }
                mTxtView_ADC!!.text =
                    if ("ADC value : $value" == null) "null" else "$value key:$key_str"
            }
        }
    }

    override fun onClick(v: View) {
        val readSector = stringToChar(mEditxt_sector_read!!.text.toString())
        val readindex = stringToChar(mEditxt_read_index!!.text.toString())
        val readkey = hexToByteArray(mEditxt_read_key!!.text.toString())
        val writeSector = stringToChar(mEditxt_sector_write!!.text.toString())
        val writeindex = stringToChar(mEditxt_write_index!!.text.toString())
        val writekey = hexToByteArray(mEditxt_wirte_key!!.text.toString())
        val aa = 0x0a.toChar()
        val bb = 0x0b.toChar()
        val one = 1.toChar()
        val rkey = mEditxt_read_key_type!!.text.toString()
        var readKeyType = bb
        if (ByteUtil.notNull(rkey)) {
            val readkeys = ByteUtil.hexStringToByteArray(rkey)
            readKeyType = (readkeys[0].toInt() and 0xff).toChar()
        }
        val wkey = mEditxt_write_key_type!!.text.toString()
        var writeKeyType = bb
        if (ByteUtil.notNull(wkey)) {
            val writekeys = ByteUtil.hexStringToByteArray(wkey)
            writeKeyType = (writekeys[0].toInt() and 0xff).toChar()
        }
        when (v.id) {
            R.id.mBtn_initDev -> {
                val initResult = mCardLanDevCtrl.callInitDev()
                if (0 == initResult || -2 == initResult || -3 == initResult || -4 == initResult) {
                    mInitDev_status_value!!.text =
                        resources.getString(R.string.init_dev) + " success!"
                } else {
                    mInitDev_status_value!!.text =
                        resources.getString(R.string.init_dev) + " failure!"
                }
                showToast(mInitDev_status_value!!.text.toString())
            }
            R.id.mBtn_reset_card -> {
                mReadOrWriteKeyHexStr = null
                //                byte[] cardNumber = new byte[S_Reset_buffer_size];
                //                int cardResult = mCardLanDevCtrl.callCardReset(cardNumber);
                val resetbyte = mReadWriteUtil.cardResetBytes
                if (ByteUtil.notNull(resetbyte)) {
                    mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString(resetbyte)
                    mTvResetCard!!.text = mReadOrWriteKeyHexStr
                } else {
                    showToast("reset：failure!")
                    mTvResetCard!!.text = ""
                }
            }
            R.id.mBtn_read_card -> {
                val resetByte = mReadWriteUtil.cardResetBytes
                if (!ByteUtil.notNull(resetByte)) {
                    showToast(resources.getString(R.string.not_find_card))
                    return
                }
                try {
                    mReadOrWriteKeyHexStr =
                        ByteUtil.byteArrayToHexString(terminal!!.calculateNormalCardKey(resetByte))
                } catch (e: KeyErrorException) {
                    e.printStackTrace()
                }
                val sector = ByteUtil.intToByteTwo(readSector.code)
                val index = ByteUtil.intToByteTwo(readindex.code)
                var readTemp: ByteArray? = null
                // the subsequent reads and writes need to be written using the computed read key
                readTemp = mReadWriteUtil.callReadJNI(
                    ByteUtil.byteToHex(sector),
                    ByteUtil.byteToHex(index), mReadOrWriteKeyHexStr, null
                )
                if (ByteUtil.notNull(readTemp)) {
                    mRead_result!!.text = ByteUtil.byteArrayToHexString(readTemp)
                }
            }
            R.id.mBtn_write_card -> {
                mReadOrWriteKeyHexStr = null
                val resetBytes = mReadWriteUtil.cardResetBytes
                if (!ByteUtil.notNull(resetBytes)) {
                    showToast(resources.getString(R.string.not_find_card))
                    return
                }
                try {
                    mReadOrWriteKeyHexStr =
                        ByteUtil.byteArrayToHexString(terminal!!.calculateNormalCardKey(resetBytes))
                } catch (e: KeyErrorException) {
                    e.printStackTrace()
                }

                //int money= 100000;
                val writeResult = mReadWriteUtil.callWriteJNI(
                    "4",
                    "1",
                    ByteUtil.byteArrayToHexString(ByteArray(4)),
                    mReadOrWriteKeyHexStr!!, null
                )
                if (writeResult == 5) {
                    mWrite_value!!.text = resources.getString(R.string.m1_write_value) + writeResult
                    showToast(resources.getString(R.string.writing_successfully))
                }
            }
            R.id.mBtn_encrypt -> {
                mReadOrWriteKeyHexStr = null
                resetBytes = mReadWriteUtil.cardResetBytes
                if (!ByteUtil.notNull(resetBytes)) {
                    showToast(resources.getString(R.string.not_find_card))
                    mTxtViewCryptContent!!.text = ""
                    return
                }
                try {
                    mReadOrWriteKeyHexStr =
                        ByteUtil.byteArrayToHexString(terminal!!.calculateNormalCardKey(resetBytes))
                    mTxtViewCryptContent!!.text = mReadOrWriteKeyHexStr
                } catch (e: KeyErrorException) {
                    e.printStackTrace()
                }
            }
            R.id.mBtn_calcu_cpu_mac -> {
                val macBytes = terminal!!.calculateMac(0)
                if (ByteUtil.notNull(macBytes)) {
                    val showCardKey = resources.getString(R.string.show_card_mac_value)
                    mTxtView_calcu_cpu_mac_value!!.text = String.format(
                        showCardKey,
                        ByteUtil.byteArrayToHexArray(macBytes)
                    )
                } else {
                    debugOnConsole(DemoActivity::class.java, "MAC to calculate failure!")
                }
            }
            R.id.mBtn_CPUwrite_card -> {
                val receivePpseBytes = ByteArray(256)
                var sendCmdppse: ByteArray? = null
                if (mEditxtCpUwirte!!.text != null) {
                    sendCmdppse = ByteUtil.hexStringToByteArray(mEditxtCpUwirte!!.text.toString())
                    if (sendCmdppse == null) {
                        showToast(resources.getString(R.string.write_cpu_empty))
                        return
                    }
                    val ppseresult = mReadWriteUtil.callSendCpuCmd(sendCmdppse, receivePpseBytes)
                    mCPUwriteStatusvalue!!.text = ByteUtil.byteArrayToHexString(receivePpseBytes)
                }
            }
            R.id.mBtn_cpu_consume -> object : DefaultBaseNonUIThread<ByteArray?>() {
                override fun doRun() {
                    val cpuCumsumeBytes = terminal!!.cpuConsume(consumeFee)
                    if (ByteUtil.notNull(cpuCumsumeBytes)) {
                        val showCardKey = resources.getString(R.string.show_card_cpu_consume)
                        runOnUiThread {
                            mTxtView_cp_value!!.text = String.format(
                                showCardKey,
                                ByteUtil.byteArrayToHexArray(cpuCumsumeBytes)
                            )
                        }
                    } else {
                        debugOnConsole(DemoActivity::class.java, "Consumption failed!")
                    }
                }

                override fun doHandlerMsg(handlerEntity: ByteArray) {
                    debugOnConsole(
                        DemoActivity::class.java,
                        "Consumption failed!" + ByteUtil.byteArrayToHexString(handlerEntity)
                    )
                }

                override fun doHandlerMessage(handlerMessage: Message?) {}
            }.start()
            R.id.mBtn_cpu_encrypt -> {
                resetBytes = mReadWriteUtil.cardResetBytes
                mReadOrWriteKeyHexStr = null
                if (!ByteUtil.notNull(resetBytes)) {
                    showToast(resources.getString(R.string.not_find_card))
                    return
                }
                val cpuCardHelper = CpuCardSecretKeyHelper()
                mReadOrWriteKeyHexStr =
                    ByteUtil.byteArrayToHexString(cpuCardHelper.getCpuCardKey(resetBytes))
                if (mReadOrWriteKeyHexStr != null) {
                    mCpuCryptContent!!.text = mReadOrWriteKeyHexStr
                } else {
                    showToast(resources.getString(R.string.encryption_failed))
                }
            }
            R.id.bt_serial -> callProc(procpath, close_bee_voice)
            R.id.bt_serialledred -> callProc(procpath, close_led_red)
            R.id.bt_serialledgreen -> callProc(procpath, close_led_green)
            R.id.mBtn_select_cpu ->                         //File selection
                object : DefaultBaseNonUIThread<ByteArray?>() {
                    override fun doRun() {
                        mReadWriteUtil.initDev()
                        val cardNumber = ByteArray(S_Reset_buffer_size)
                        val cardResult = mCardLanDevCtrl.callCardReset(cardNumber)
                        val initCpuConsumeBytes = terminal!!.selectCpuFile()
                        runOnUiThread(Runnable {
                            if (!ByteUtil.notNull(initCpuConsumeBytes)) {
                                return@Runnable
                            } else {
                                mCpucmdContent!!.text =
                                    resources.getString(R.string.cpu_cmd) + ByteUtil.byteArrayToHexString(
                                        initCpuConsumeBytes
                                    )
                            }
                        })
                    }

                    override fun doHandlerMsg(handlerEntity: ByteArray) {
                        debugOnConsole(
                            DemoActivity::class.java,
                            "Select File is failed!" + ByteUtil.byteArrayToHexString(handlerEntity)
                        )
                    }

                    override fun doHandlerMessage(handlerMessage: Message?) {}
                }.start()
            R.id.bt_serial_btn -> {
                val serialTest = CardLanSerialHelper("/dev/ttyAMA4", 115200, 0, 64)
                serialTest.start()
                serialTest.setCallBack(FileCallBack { buffer, size ->
                    if (buffer == null || buffer.size <= 0) {
                        return@FileCallBack
                    }
                    val one = buffer[0].toInt() != 0
                    val two = buffer[1].toInt() != 5
                    val three = buffer[2].toInt() != 0
                    if (!one || !two || !three) {
                        return@FileCallBack
                    }
                    if (size <= 0) {
                        return@FileCallBack
                    }
                    val tempQrCode = String(buffer, 0, size)
                    Log.d("byte", "buffer= " + ByteUtil.byteArrayToHexString(buffer))
                    mStringBuilder!!.append(tempQrCode)
                    if (mStringBuilder!!.indexOf(QRCodeTailSplit) < 0) {
                        //还没有读完二维码数据
                        return@FileCallBack
                    }
                    if (null == mStringBuilder.toString()) {
                        //没有扫描到二维码，直接返回
                        return@FileCallBack
                    }
                    Log.d("result", "mStringBuilder=$mStringBuilder")
                    realQrCode =
                        mStringBuilder!!.substring(0, mStringBuilder!!.indexOf(QRCodeTailSplit))
                    mStringBuilder!!.replace(0, mStringBuilder!!.length, "")
                    runOnUiThread {
                        if (ByteUtil.notNull(realQrCode.toString())) {
                            //CardLanDevUtil.openProc();
                            mTv_qc_result!!.text = ": " + realQrCode.toString()
                            terminal!!.callProc()
                        }
                    }
                })
            }
            R.id.bt_tts_btn ->                           //send (cmd and Default for voiceText)
                try {
                    if (mTTS_editText!!.text != null && mSpiHelper != null) {
                        val arg = mTTS_editText!!.text.toString().toByteArray(charset("GB2312"))
                        val str_arg_len = byteArrayOf(0x00, 0x00, 0x00, 0x00)
                        str_arg_len[0] = arg.size.toByte()
                        //byte[] str_arg_len = {arg.length, 0x00, 0x00, 0x00};
                        //str_arg_len[1] = (byte) arg2.length;
                        //str_arg_len[2] = (byte) arg3.length;
                        mSpiHelper!!.write(0x30.toByte(), 0x03.toByte(), str_arg_len, arg)
                        //ttsplay(arg1,arg2,arg3);
                    } else {
                        mSpiHelper = CardLanSpiHelper()
                        val arg = mTTS_editText!!.text.toString().toByteArray(charset("GB2312"))
                        val str_arg_len = byteArrayOf(0x00, 0x00, 0x00, 0x00)
                        str_arg_len[0] = arg.size.toByte()
                        mSpiHelper!!.write(0x30.toByte(), 0x03.toByte(), str_arg_len, arg)
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            R.id.signal_btn -> if (mSpiHelper != null) {
                val arg = "B_06".toByteArray()
                //byte[] arg1 = {'B','0','6'};
                //byte[] str_arg_len = new byte[4];
                val str_arg_len = byteArrayOf(0x00, 0x00, 0x00, 0x00)
                str_arg_len[0] = arg.size.toByte()
                mSpiHelper!!.write(0x10.toByte(), 0x01.toByte(), str_arg_len, arg)
            }
            R.id.signal_btn_low -> if (mSpiHelper != null) {
                val arg = "B_06".toByteArray()
                //byte[] arg1 = {'B','0','6'};
                val str_arg_len = byteArrayOf(0x00, 0x00, 0x00, 0x00)
                str_arg_len[0] = arg.size.toByte()
                mSpiHelper!!.write(0x10.toByte(), 0x02.toByte(), str_arg_len, arg)
            }
            R.id.bt_psamdata1 -> mSpiHelper!!.openSc("50")
            R.id.bt_psamdata2 -> mSpiHelper!!.openSc("51")
            R.id.bt_psamdata3 -> mSpiHelper!!.openSc("52")
            R.id.bt_psamdata4 -> mSpiHelper!!.openSc("60")
            R.id.bt_psamdata5 -> mSpiHelper!!.openSc("61")
            R.id.bt_psamdata6 -> mSpiHelper!!.openSc("62")
            R.id.bt_clear -> spireceiver!!.text = ""
            else -> {}
        }
    }

    @Throws(IOException::class)
    private fun readSDCardFile(path: String): String {
        val file = File(path)
        //	        FileInputStream fis = new FileInputStream(file);
        //	        String result = streamRead(fis);
        val fr = FileReader(file)
        val buff = CharArray(1024)
        fr.read(buff)
        return String(buff)
    }

    override fun receiveKeyCode(keyCode: String, keyByte: Byte) {
        if (ByteUtil.notNull(keyCode)) {
            Log.d(
                "keyCode",
                "keyCode = " + keyCode + ",keyBtye = " + ByteUtil.byteToHexString(keyByte)
            )
        }
    }

    override fun receivePasmData(code: String, respondBuff: ByteArray) {
        if (ByteUtil.notNull(respondBuff)) {
            Log.d("respondBuff", "respondBuff = " + ByteUtil.byteArrayToHexString(respondBuff))
            if ("pasm" == code) {
                if (respondBuff.size < 8) {
                    return
                }
                val head = respondBuff[0]
                val dev = respondBuff[1]
                val cmd = respondBuff[2]
                val ret_len = respondBuff[3]
                val end = respondBuff[7]
                val ret = ByteUtil.copyBytes(respondBuff, 8, ret_len.toInt())
                when (cmd) {
                    0x01 -> {
                        //open
                        if (ByteUtil.byteArrayToIntHighToLow(ret) != 0) {
                            return
                        }
                        //设置波特率;
                        mSpiHelper!!.setBaudrate(ByteUtil.byteToHex(dev), "2580")
                    }
                    0x02 -> {}
                    0x03 -> {

                        //读数据(spi),获取可读数据长度;
                        mReadLength = mSpiHelper!!.getCanReadData(ByteUtil.byteToHex(dev))
                    }
                    0x04 -> {
                        runOnUiThread(Runnable {
                            if (respondBuff[3].toInt() == 0x00 && respondBuff[4].toInt() == 0x00 && respondBuff[5].toInt() == 0x00 && respondBuff[6].toInt() == 0x00) {
                                psamdata_textview!!.append(" null")
                                return@Runnable
                            }
                            val s_msg = ByteUtil.byteArrayToHexString(respondBuff)
                            val value = s_msg.substring(16)
                            psamdata_textview!!.append(" $value")
                            cardNumber!!.text = "CardNumber: $value"
                        })
                    }
                    0x05 -> {

                        //写入psam卡;
                        mSpiHelper!!.writeScData(ByteUtil.byteToHex(dev), "00B0960006")
                    }
                    0x07 -> {
                        mReadLength = ByteUtil.copyBytes(respondBuff, 3, 1)
                        if (mReadLength[0].toInt() == 0) {
                            return
                        }
                        mReadData = ByteUtil.copyBytes(respondBuff, 8, mReadLength[0].toInt())
                        if (ByteUtil.byteArrayToIntHighToLow(mReadData) == 0) {
                            return
                        }
                        val tmpbytes = mSpiHelper!!.readScData(ByteUtil.byteToHex(dev), mReadData)
                    }
                    0x08 -> {

                        //                if (ByteUtil.byteArrayToIntHighToLow(ret) != 0) {
                        //                    return;
                        //                }
                        //冷启动复位;
                        mSpiHelper!!.coldStart(ByteUtil.byteToHex(dev))
                    }
                    else -> {}
                }
            }
        }
    }

    override fun receiveSpiData(code: String, respondBuff: ByteArray) {
        if (ByteUtil.notNull(respondBuff)) {
            runOnUiThread { spireceiver!!.append(ByteUtil.byteArrayToHexString(respondBuff) + "    ") }
            Log.d("respondBuff", "respondBuff = " + ByteUtil.byteArrayToHexString(respondBuff))
        }
    }

    override fun receiveUpmcu(code: String, respondBuff: ByteArray) {}
    override fun receiveMcuConfig(code: String, respondBuff: ByteArray) {}

    */
/**
     * Thread for ADC code scanning ;
     *//*

    private inner class AdcReadThread : Thread() {

        override fun run() {
            super.run()
            while (true) {
                try {
                    adcStr = readSDCardFile(ADCPATH)
                } catch (e: Exception) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                runOnUiThread { mTxtView_ADC!!.text = "ADC value : $adcStr" }
            }
        }
    }

    override fun onDestroy() {
        if (terminal != null) {
            terminal!!.stopRead()
            terminal = null
        }
        mSharedPreferencesHelper!!.put(mCmdKey, mRecordSB.toString())
        super.onDestroy()
    }

    private fun stringToChar(string: String): Char {
        var string = string
        if (!ByteUtil.notNull(string)) {
            string = "0"
        }
        val ivalue = string.toInt()
        return ivalue.toChar()
    }

    private fun hexToByteArray(hex: String): ByteArray {
        var hex: String? = hex
        if (!ByteUtil.notNull(hex)) {
            hex = "FFFFFFFFFFFF"
        }
        return ByteUtil.hexStringToByteArray(hex)
    }

    var mToast: Toast? = null
    fun showToast(msg: String?) {
        if (mToast != null) {
            mToast!!.cancel()
            mToast = null
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        mToast.show()
    }

    private fun doRead(
        readSectorStr: String,
        readindexStr: String,
        hexReadKey: String,
        readKeyAreaStr: String
    ) {
        val readSector = stringToChar(readSectorStr)
        val readindex = stringToChar(readindexStr)
        val readkey = hexToByteArray(hexReadKey)
        val readKeyArea = stringToChar(readKeyAreaStr)
        val one = 1.toChar()
        val resetByte = ByteArray(30)
        val cardResult = mCardLanDevCtrl.callCardReset(resetByte)
        val readMsg = mCardLanDevCtrl.callReadOneSectorDataFromCard(
            readSector,
            readindex, one,
            readkey, readKeyArea
        )
        if (readMsg != null && readMsg.size > 0) {
            realData = ByteArray(readMsg.size)
            System.arraycopy(readMsg, 0, realData, 0, readMsg.size)
            val realStr = ByteUtil.byteArrayToHexString(realData)

            //26E700EE2F0804006263646566676869
            debugOnConsole(DemoActivity::class.java, "The information being read is：$realStr")
        }
    }

    private fun doWrite(
        writeSectorStr: String,
        writeindexStr: String,
        writeHexStr: String,
        hexWriteKey: String,
        readKeyAreaStr: String
    ) {
        val writeSector = stringToChar(writeSectorStr)
        val writeindex = stringToChar(writeindexStr)
        val writeKey = hexToByteArray(hexWriteKey)
        val writeBytes = hexToByteArray(writeHexStr)
        val readKeyArea = stringToChar(readKeyAreaStr)
        val one = 1.toChar()
        val writeResult = mCardLanDevCtrl.callWriteOneSertorDataToCard(
            writeBytes,
            writeSector,
            writeindex, one,
            writeKey, readKeyArea
        )
    }

    override fun onLongClick(v: View): Boolean {
        when (v.id) {
            R.id.bt_serial -> callProc(procpath, open_bee_voice)
            R.id.bt_serialledred -> callProc(procpath, open_led_red)
            R.id.bt_serialledgreen -> callProc(procpath, open_led_green)
            else -> {}
        }
        return false
    }

    fun callProc(procpath: String, cmd: String) {
        writeProc(procpath, cmd.toByteArray())
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
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

    companion object {

        private const val S_Reset_buffer_size = 32

        //Sets the file path to which the parameter is written.
        private const val procpath = "/proc/gpio_set/rp_gpio_set"

        //Parameters that control the sound of a buzzer.
        private const val open_bee_voice = "c_24_1_1"
        private const val close_bee_voice = "c_24_1_0"

        //Control the parameters of Red_LED and other switches
        private const val open_led_red = "d_26_0_1"
        private const val close_led_red = "d_26_0_0"

        //Control the parameters of Green_LED and other switches
        private const val open_led_green = "c_5_1_1"
        private const val close_led_green = "c_5_1_0"
        private const val ADCPATH = "/proc/adc/adc_ctrl"
    }
}*/
