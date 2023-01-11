package com.example.cardlanandroiddevice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen.CardLanAdcHelper;
import com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen.CardLanSerialHelper;
import com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen.CardLanSpiHelper;
import com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen.CardLanSpiHelper.SpiCallBackLinstener;
import com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen.CardLanStandardBus;
import com.example.cardlanandroiddevice.cardlanLib.utils.ByteUtil;
import com.example.cardlanandroiddevice.data.KeyErrorException;
import com.example.cardlanandroiddevice.data.TerminalConsumeDataForSystem;
import com.example.cardlanandroiddevice.thread.DefaultBaseNonUIThread;
import com.example.cardlanandroiddevice.util.CardReadWriteUtil;
import com.example.cardlanandroiddevice.util.CardlanLog;
import com.example.cardlanandroiddevice.util.SharedPreferencesHelper;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by cardlan on 18-5-24.
 */

public class DemoActivity
        extends Activity
        implements OnClickListener, OnLongClickListener, SpiCallBackLinstener {

    CardLanStandardBus mCardLanDevCtrl = new CardLanStandardBus();
    byte[] realData;
    private static final int S_Reset_buffer_size = 32;
    EditText mEditxt_sector_read, mEditxt_read_index, mEditxt_read_key, mEditxt_read_key_type;
    EditText mEditxt_sector_write, mEditxt_write_index, mEditxt_wirte_key, mEditxt_write_key_type;
    CardReadWriteUtil mReadWriteUtil = new CardReadWriteUtil();
    LinearLayout mRelayout_des;
    boolean mIsNeedShowCrypt = true;
    TerminalConsumeDataForSystem terminal;
    FileInputStream mFileInputStream;
    byte[] buffer = new byte[32];
    int size = 0;
    String adcStr = null;
    /**
     * Total number of sectors read, default 4
     */
    private int mSectorReadNumber = 4;
    /**
     * Sector reads index, default 0
     */
    private int mSectorReadIndex = 1;
    private String mReadOrWriteKeyHexStr;
    private int consumeFee = 1;
    //Sets the file path to which the parameter is written.
    private static final String procpath = "/proc/gpio_set/rp_gpio_set";
    //Parameters that control the sound of a buzzer.
    private static final String open_bee_voice = "c_24_1_1";
    private static final String close_bee_voice = "c_24_1_0";
    //Control the parameters of Red_LED and other switches
    private static final String open_led_red = "d_26_0_1";
    private static final String close_led_red = "d_26_0_0";
    //Control the parameters of Green_LED and other switches
    private static final String open_led_green = "c_5_1_1";
    private static final String close_led_green = "c_5_1_0";
    private static final String ADCPATH = "/proc/adc/adc_ctrl";
    TextView mTxtView_calcu_cpu_mac_value, mTxtView_cp_value,
            mTxtView_cpu_card_remain_fee;
    EditText mEditxt_cpu_record, mEditxt_cpu_consume_fee;
    StringBuilder mRecordSB = new StringBuilder();
    SharedPreferencesHelper mSharedPreferencesHelper;
    String mCmdKey = "cpuCmdRecord";
    private TextView mInitDev_status_value;
    private TextView mTxtViewCryptContent;
    private TextView mCpuCryptContent;
    private TextView mTvResetCard;
    private TextView mCpucmdContent;
    private TextView mTxtView_ADC;
    private TextView mRead_result;
    private TextView mWrite_value;
    private TextView mTv_qc_result;

    byte[] CardSn = new byte[16];
    private StringBuilder mStringBuilder;
    String realQrCode = null;
    String QRCodeTailSplit = "\r\n";
    private FileDescriptor QrCodeFlag = mCardLanDevCtrl.callSerialOpen("/dev/ttyAMA4", 115200, 0);
    private int ReadCardFlag = mCardLanDevCtrl.callCardReset(CardSn);
    private EditText mTTS_editText;
    private CardLanSpiHelper mSpiHelper;
    private TextView mRelayReceiver;
    private String mLastValue_str;
    private TextView spireceiver;
    private byte[] mReadLength;
    private byte[] mReadData;
    private EditText mEditxtCpUwirte;
    private TextView psamdata_textview;
    private TextView cardNumber;
    private TextView mCPUwriteStatusvalue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        terminal = new TerminalConsumeDataForSystem();
        mStringBuilder = new StringBuilder();
        psamdata_textview = findViewById(R.id.psam_tv);
        Button mBtn_initDev = findViewById(R.id.mBtn_initDev);
        Button mBtn_reset_card = findViewById(R.id.mBtn_reset_card);
        Button mBtn_read_card = findViewById(R.id.mBtn_read_card);
        Button mBtn_write_card = findViewById(R.id.mBtn_write_card);
        mBtn_initDev.setOnClickListener(this);
        mBtn_reset_card.setOnClickListener(this);
        mBtn_read_card.setOnClickListener(this);
        mBtn_write_card.setOnClickListener(this);
        if (mSpiHelper == null) {
            mSpiHelper = new CardLanSpiHelper();
            mSpiHelper.setSpiCallBackLinstener(this);
            mSpiHelper.start();
        }

        mSharedPreferencesHelper = new SharedPreferencesHelper(this, "cpuCmd");
        mEditxt_sector_read = findViewById(R.id.mEditxt_sector_read);
        mEditxt_read_index = findViewById(R.id.mEditxt_read_index);
        mEditxt_read_key = findViewById(R.id.mEditxt_read_key);
        mEditxt_read_key_type = findViewById(R.id.mEditxt_read_key_type);
        mRead_result = findViewById(R.id.mTxtView_read_result);
        mWrite_value = findViewById(R.id.mTxtView_write_statusvalue);
        mEditxt_sector_write = findViewById(R.id.mEditxt_sector_write);
        mEditxt_write_index = findViewById(R.id.mEditxt_write_index);
        mEditxt_wirte_key = findViewById(R.id.mEditxt_wirte_key);
        mEditxt_write_key_type = findViewById(R.id.mEditxt_write_key_type);
        mTvResetCard = findViewById(R.id.tv_reset_card);
        mRelayout_des = findViewById(R.id.mRelayout_des);
        mTxtViewCryptContent = findViewById(R.id.mTxtView_crypt_content);
        Button mBtn_CPUwrite = findViewById(R.id.mBtn_CPUwrite_card);
        Button mBtn_encrypt = findViewById(R.id.mBtn_encrypt);
        Button mBtn_cpuDecrypt = findViewById(R.id.mBtn_cpu_encrypt);
        mTxtViewCryptContent = findViewById(R.id.mTxtView_crypt_content);
        Button mBtn_cpu_consume = findViewById(R.id.mBtn_cpu_consume);
        mCpuCryptContent = findViewById(R.id.mTxtView_cpucrypt_content);
        Button mBtn_cpu_smd = findViewById(R.id.mBtn_select_cpu);
        mCpucmdContent = findViewById(R.id.mTxtView_cpu_cmd_value);
        mTxtView_ADC = findViewById(R.id.mTxtView_ADC);
        spireceiver = findViewById(R.id.psam_tv);
        cardNumber = findViewById(R.id.psam_card);
        Button bt_tts_btn = findViewById(R.id.bt_tts_btn);
        mBtn_CPUwrite.setOnClickListener(this);
        mBtn_cpu_smd.setOnClickListener(this);
        mBtn_encrypt.setOnClickListener(this);
        mBtn_cpuDecrypt.setOnClickListener(this);
        mBtn_cpu_consume.setOnClickListener(this);
        bt_tts_btn.setOnClickListener(this);
        //mRelayout_des.setVisibility(mIsNeedShowCrypt ? View.VISIBLE : View.GONE);
        mInitDev_status_value = findViewById(R.id.mTxtView_initDev_status_value);
        mEditxt_cpu_consume_fee = findViewById(R.id.mEditxt_cpu_consume_fee);
        mTxtView_calcu_cpu_mac_value = findViewById(R.id.mTxtView_calcu_cpu_mac_value);
        mTxtView_cp_value = findViewById(R.id.mTxtView_cp_value);
        mTxtView_cpu_card_remain_fee = findViewById(R.id.mTxtView_cpu_card_remain_fee);
        mEditxtCpUwirte = findViewById(R.id.mEditxt_CPUwirte_key);
        mCPUwriteStatusvalue = findViewById(R.id.mTxtView_CPUwrite_statusvalue);
        Button mBtn_calcu_cpu_mac = findViewById(R.id.mBtn_calcu_cpu_mac);
        mBtn_calcu_cpu_mac.setOnClickListener(this);

        Button mBtn_buzzer = findViewById(R.id.bt_serial);
        Button mBtn_ledRed = findViewById(R.id.bt_serialledred);
        Button mBtn_ledGreen = findViewById(R.id.bt_serialledgreen);
        Button mBtn_Serial_QC = findViewById(R.id.bt_serial_btn);
        mTv_qc_result = findViewById(R.id.tv_serial_result);
        mTTS_editText = findViewById(R.id.ed_send);
        Button btn_Relay = findViewById(R.id.signal_btn);
        Button btn_low = findViewById(R.id.signal_btn_low);
        Button mBtn_psam1 = findViewById(R.id.bt_psamdata1);
        Button mBtn_psam2 = findViewById(R.id.bt_psamdata2);
        Button mBtn_psam3 = findViewById(R.id.bt_psamdata3);
        Button mBtn_psam4 = findViewById(R.id.bt_psamdata4);
        Button mBtn_psam5 = findViewById(R.id.bt_psamdata5);
        Button mBtn_psam6 = findViewById(R.id.bt_psamdata6);
        Button mBtn_clear = findViewById(R.id.bt_clear);


        mBtn_psam1.setOnClickListener(this);
        mBtn_psam2.setOnClickListener(this);
        mBtn_psam3.setOnClickListener(this);
        mBtn_psam4.setOnClickListener(this);
        mBtn_psam5.setOnClickListener(this);
        mBtn_psam6.setOnClickListener(this);
        mBtn_clear.setOnClickListener(this);

        //mRelayReceiver = findViewById(R.id.signal_receiver);
//        AdcReadThread adcThread     = new AdcReadThread();
//        adcThread.start();
        mBtn_buzzer.setOnClickListener(this);
        mBtn_ledRed.setOnClickListener(this);
        mBtn_ledGreen.setOnClickListener(this);
        mBtn_buzzer.setOnLongClickListener(this);
        mBtn_ledRed.setOnLongClickListener(this);
        mBtn_ledGreen.setOnLongClickListener(this);
        mBtn_Serial_QC.setOnClickListener(this);
        btn_Relay.setOnClickListener(this);
        btn_low.setOnClickListener(this);
        String cmds = (String) mSharedPreferencesHelper.getSharedPreference(mCmdKey, "");
        if (cmds != null && !cmds.equals("")) {
            mEditxt_cpu_record.setText(cmds);
            mRecordSB.append(cmds);
        }
        AdcTest();
    }

    private void AdcTest() {

        CardLanAdcHelper mAdcHelper = new CardLanAdcHelper(new CardLanAdcHelper.ICallback() {
            @Override
            public void value_change(final String value) {


                mTxtView_ADC.post(new Runnable() {
                    @Override
                    public void run() {

                        int key_value = Integer.parseInt(value);
                        String key_str;
                        if (0 <= key_value && 500 > key_value) {
                            key_str = "左";
                        } else if (500 <= key_value && 1000 > key_value) {
                            key_str = "下";
                        } else if (1000 <= key_value && 2000 > key_value) {
                            key_str = "右";
                        } else if (2000 <= key_value && 3000 > key_value) {
                            key_str = "上";
                        } else {
                            key_str = "unknown";
                        }

                        if (key_str != "unknown" && mLastValue_str != key_str) {
                            terminal.callProc();
                        }
                        mLastValue_str = key_str;
                        //                        if (value != null) {
                        //                            mTerminalConsumeData.callProc();
                        //                        }
                        mTxtView_ADC.setText("ADC value : " + value == null ? "null" : value + " key:" + key_str);
                    }
                });

            }
        }
        );
    }

    @Override
    public void onClick(View v) {
        char readSector = stringToChar(mEditxt_sector_read.getText().toString());
        char readindex = stringToChar(mEditxt_read_index.getText().toString());
        byte[] readkey = hexToByteArray(mEditxt_read_key.getText().toString());
        char writeSector = stringToChar(mEditxt_sector_write.getText().toString());
        char writeindex = stringToChar(mEditxt_write_index.getText().toString());
        byte[] writekey = hexToByteArray(mEditxt_wirte_key.getText().toString());
        char aa = 0x0a;
        char bb = 0x0b;
        char one = 1;
        String rkey = mEditxt_read_key_type.getText().toString();
        char readKeyType = bb;

        if (ByteUtil.notNull(rkey)) {
            byte[] readkeys = ByteUtil.hexStringToByteArray(rkey);
            readKeyType = (char) (readkeys[0] & 0xff);

        }

        String wkey = mEditxt_write_key_type.getText().toString();
        char writeKeyType = bb;
        if (ByteUtil.notNull(wkey)) {
            byte[] writekeys = ByteUtil.hexStringToByteArray(wkey);
            writeKeyType = (char) (writekeys[0] & 0xff);
        }

        switch (v.getId()) {
            //init_device;
            case R.id.mBtn_initDev:
                int initResult = mCardLanDevCtrl.callInitDev();
                if (0 == initResult || -2 == initResult || -3 == initResult || -4 == initResult) {
                    mInitDev_status_value.setText(getResources().getString(R.string.init_dev) + " success!");

                } else {
                    mInitDev_status_value.setText(getResources().getString(R.string.init_dev) + " failure!");
                }
                showToast(mInitDev_status_value.getText().toString());
                break;
            //reset card
            case R.id.mBtn_reset_card:
                mReadOrWriteKeyHexStr = null;
//                byte[] cardNumber = new byte[S_Reset_buffer_size];
//                int cardResult = mCardLanDevCtrl.callCardReset(cardNumber);
                byte[] resetbyte = mReadWriteUtil.getCardResetBytes();
                if (ByteUtil.notNull(resetbyte)) {
                    mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString(resetbyte);
                    mTvResetCard.setText(mReadOrWriteKeyHexStr);

                } else {
                    showToast("reset：failure!");
                    mTvResetCard.setText("");
                }

                break;
            //read M1 card;
            case R.id.mBtn_read_card:
                byte[] resetByte = mReadWriteUtil.getCardResetBytes();
                if (!ByteUtil.notNull(resetByte)) {
                    showToast(getResources().getString(R.string.not_find_card));
                    return;
                }
                try {
                    mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString
                            (terminal.calculateNormalCardKey(resetByte));
                } catch (KeyErrorException e) {
                    e.printStackTrace();
                }

                byte sector = ByteUtil.intToByteTwo(readSector);
                byte index = ByteUtil.intToByteTwo(readindex);
                byte[] readTemp = null;
                // the subsequent reads and writes need to be written using the computed read key
                readTemp = mReadWriteUtil.callReadJNI(ByteUtil.byteToHex(sector),
                        ByteUtil.byteToHex(index), mReadOrWriteKeyHexStr, null);
                if (ByteUtil.notNull(readTemp)) {
                    mRead_result.setText(ByteUtil.byteArrayToHexString(readTemp));
                }
                //showToast(ByteUtil.byteArrayToHexString(readTemp));


                break;
            //action for write to M1_card.
            case R.id.mBtn_write_card:
                mReadOrWriteKeyHexStr = null;
                byte[] resetBytes = mReadWriteUtil.getCardResetBytes();
                if (!ByteUtil.notNull(resetBytes)) {
                    showToast(getResources().getString(R.string.not_find_card));
                    return;
                }
                try {
                    mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString
                            (terminal.calculateNormalCardKey(resetBytes));
                } catch (KeyErrorException e) {
                    e.printStackTrace();
                }

                //int money= 100000;
                int writeResult = mReadWriteUtil.callWriteJNI("4",
                        "1",
                        ByteUtil.byteArrayToHexString(new byte[4]),
                        mReadOrWriteKeyHexStr, null);
                if (writeResult == 5) {
                    mWrite_value.setText(getResources().getString(R.string.m1_write_value) + writeResult);
                    showToast(getResources().getString(R.string.writing_successfully));
                }
                break;
            case R.id.mBtn_encrypt:// M1card encrypt
                mReadOrWriteKeyHexStr = null;
                resetBytes = mReadWriteUtil.getCardResetBytes();
                if (!ByteUtil.notNull(resetBytes)) {
                    showToast(getResources().getString(R.string.not_find_card));
                    mTxtViewCryptContent.setText("");
                    return;
                }
                try {
                    mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString
                            (terminal.calculateNormalCardKey(resetBytes));
                    mTxtViewCryptContent.setText(mReadOrWriteKeyHexStr);
                } catch (KeyErrorException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.mBtn_calcu_cpu_mac://calculate cpu mac
                byte[] macBytes = terminal.calculateMac(0);
                if (ByteUtil.notNull(macBytes)) {
                    String showCardKey = getResources().getString(R.string
                            .show_card_mac_value);
                    mTxtView_calcu_cpu_mac_value.setText(String.format(showCardKey,
                            ByteUtil.byteArrayToHexArray(macBytes)));
                } else {
                    CardlanLog.debugOnConsole(DemoActivity.class, "MAC to calculate failure!");
                }

                break;

            case R.id.mBtn_CPUwrite_card:
                byte[] receivePpseBytes = new byte[256];
                byte[] sendCmdppse = null;
                if (mEditxtCpUwirte.getText() != null) {

                    sendCmdppse = ByteUtil.hexStringToByteArray(mEditxtCpUwirte.getText().toString());
                    if (sendCmdppse == null) {
                        showToast(getResources().getString(R.string.write_cpu_empty));
                        return;
                    }
                    int ppseresult = mReadWriteUtil.callSendCpuCmd(sendCmdppse, receivePpseBytes);
                    mCPUwriteStatusvalue.setText(ByteUtil.byteArrayToHexString(receivePpseBytes));
                }
                break;

            // CPU Card consumption
            case R.id.mBtn_cpu_consume:
                new DefaultBaseNonUIThread<byte[]>() {
                    @Override
                    public void doRun() {
                        final byte[] cpuCumsumeBytes = terminal.cpuConsume(consumeFee);
                        if (ByteUtil.notNull(cpuCumsumeBytes)) {
                            final String showCardKey = getResources().getString(R.string.show_card_cpu_consume);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTxtView_cp_value.setText(String.format
                                            (showCardKey, ByteUtil.byteArrayToHexArray(cpuCumsumeBytes)));

                                }
                            });
                        } else {
                            CardlanLog.debugOnConsole(DemoActivity.class, "Consumption failed!");
                        }
                    }

                    @Override
                    public void doHandlerMsg(byte[] handlerEntity) {
                        CardlanLog.debugOnConsole(DemoActivity.class, "Consumption failed!" + ByteUtil.byteArrayToHexString(handlerEntity));

                    }

                    @Override
                    public void doHandlerMessage(Message handlerMessage) {

                    }
                }.start();
                break;
            //action for cpu_encrypt.
            case R.id.mBtn_cpu_encrypt:
                resetBytes = mReadWriteUtil.getCardResetBytes();
                mReadOrWriteKeyHexStr = null;
                if (!ByteUtil.notNull(resetBytes)) {
                    showToast(getResources().getString(R.string.not_find_card));
                    return;
                }
                CpuCardSecretKeyHelper cpuCardHelper = new CpuCardSecretKeyHelper();
                mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString
                        (cpuCardHelper.getCpuCardKey(resetBytes));
                if (mReadOrWriteKeyHexStr != null) {
                    mCpuCryptContent.setText(mReadOrWriteKeyHexStr);
                } else {
                    showToast(getResources().getString(R.string.encryption_failed));
                }

                break;
            case R.id.bt_serial:
                callProc(procpath, close_bee_voice);
                break;
            case R.id.bt_serialledred:
                callProc(procpath, close_led_red);
                break;
            case R.id.bt_serialledgreen:
                callProc(procpath, close_led_green);
                break;

            //action for File selection
            case R.id.mBtn_select_cpu:
                //File selection
                new DefaultBaseNonUIThread<byte[]>() {
                    @Override
                    public void doRun() {
                        mReadWriteUtil.initDev();
                        byte[] cardNumber = new byte[S_Reset_buffer_size];
                        int cardResult = mCardLanDevCtrl.callCardReset(cardNumber);
                        final byte[] initCpuConsumeBytes = terminal.selectCpuFile();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!ByteUtil.notNull(initCpuConsumeBytes)) {
                                    return;
                                } else {
                                    mCpucmdContent.setText(getResources().getString(R.string.cpu_cmd) + ByteUtil.byteArrayToHexString(initCpuConsumeBytes));
                                }
                            }
                        });
                    }

                    @Override
                    public void doHandlerMsg(byte[] handlerEntity) {
                        CardlanLog.debugOnConsole(DemoActivity.class, "Select File is failed!" + ByteUtil.byteArrayToHexString(handlerEntity));
                    }

                    @Override
                    public void doHandlerMessage(Message handlerMessage) {

                    }
                }.start();

                break;
            case R.id.bt_serial_btn:
                CardLanSerialHelper serialTest = new CardLanSerialHelper("/dev/ttyAMA4", 115200, 0, 64);
                serialTest.start();
                serialTest.setCallBack(new CardLanSerialHelper.FileCallBack() {
                    @Override
                    public void getResult(byte[] buffer, int size) {
                        if (buffer == null || buffer.length <= 0) {
                            return;
                        }
                        boolean one = buffer[0] != 0;
                        boolean two = buffer[1] != 5;
                        boolean three = buffer[2] != 0;

                        if (!one || !two || !three) {
                            return;
                        }

                        if (size <= 0) {
                            return;
                        }
                        String tempQrCode = new String(buffer, 0, size);
                        Log.d("byte", "buffer= " + ByteUtil.byteArrayToHexString(buffer));
                        mStringBuilder.append(tempQrCode);
                        if (mStringBuilder.indexOf(QRCodeTailSplit) < 0) {
                            //还没有读完二维码数据
                            return;
                        }
                        if (null == (mStringBuilder.toString())) {
                            //没有扫描到二维码，直接返回
                            return;
                        }
                        Log.d("result", "mStringBuilder=" + mStringBuilder);
                        realQrCode = mStringBuilder.substring(0, mStringBuilder.indexOf(QRCodeTailSplit));
                        mStringBuilder.replace(0, mStringBuilder.length(), "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ByteUtil.notNull(realQrCode.toString())) {
                                    //CardLanDevUtil.openProc();
                                    mTv_qc_result.setText(": " + realQrCode.toString());
                                    terminal.callProc();

                                }

                            }
                        });
                    }
                });
//                    terminal.callProc();
//                    new ReadThread().start();
                break;
            //TTS test button
            case R.id.bt_tts_btn:
                //send (cmd and Default for voiceText)
                try {
                    if (mTTS_editText.getText() != null && mSpiHelper != null) {
                        byte[] arg = mTTS_editText.getText().toString().getBytes("GB2312");
                        byte[] str_arg_len = {0x00, 0x00, 0x00, 0x00};
                        str_arg_len[0] = (byte) arg.length;
                        //byte[] str_arg_len = {arg.length, 0x00, 0x00, 0x00};
                        //str_arg_len[1] = (byte) arg2.length;
                        //str_arg_len[2] = (byte) arg3.length;
                        mSpiHelper.write((byte) 0x30, (byte) 0x03, str_arg_len, arg);
                        //ttsplay(arg1,arg2,arg3);
                    } else {
                        mSpiHelper = new CardLanSpiHelper();

                        byte[] arg = mTTS_editText.getText().toString().getBytes("GB2312");
                        byte[] str_arg_len = {0x00, 0x00, 0x00, 0x00};
                        str_arg_len[0] = (byte) arg.length;
                        mSpiHelper.write((byte) 0x30, (byte) 0x03, str_arg_len, arg);

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.signal_btn:
                if (mSpiHelper != null) {
                    byte[] arg = "B_06".getBytes();
                    //byte[] arg1 = {'B','0','6'};
                    //byte[] str_arg_len = new byte[4];
                    byte[] str_arg_len = {0x00, 0x00, 0x00, 0x00};
                    str_arg_len[0] = (byte) arg.length;
                    mSpiHelper.write((byte) 0x10, (byte) 0x01, str_arg_len, arg);
                }
                break;
            case R.id.signal_btn_low:
                if (mSpiHelper != null) {
                    byte[] arg = "B_06".getBytes();
                    //byte[] arg1 = {'B','0','6'};
                    byte[] str_arg_len = {0x00, 0x00, 0x00, 0x00};
                    str_arg_len[0] = (byte) arg.length;
                    mSpiHelper.write((byte) 0x10, (byte) 0x02, str_arg_len, arg);
                }
                break;
            case R.id.bt_psamdata1:
                mSpiHelper.openSc("50");
                break;

            case R.id.bt_psamdata2:
                mSpiHelper.openSc("51");
                break;

            case R.id.bt_psamdata3:
                mSpiHelper.openSc("52");
                break;

            case R.id.bt_psamdata4:
                mSpiHelper.openSc("60");
                break;

            case R.id.bt_psamdata5:
                mSpiHelper.openSc("61");
                break;

            case R.id.bt_psamdata6:
                mSpiHelper.openSc("62");
                break;

            case R.id.bt_clear:
                spireceiver.setText("");
                break;

            default:
                break;
        }
    }

    private String readSDCardFile(String path) throws IOException {
        File file = new File(path);
//	        FileInputStream fis = new FileInputStream(file);
//	        String result = streamRead(fis);

        FileReader fr = new FileReader(file);
        char[] buff = new char[1024];
        fr.read(buff);
        String result = String.valueOf(buff);
        return result;
    }

    @Override
    public void receiveKeyCode(String keyCode, byte keyByte) {
        if (ByteUtil.notNull(keyCode)) {
            Log.d("keyCode", "keyCode = " + keyCode + ",keyBtye = " + ByteUtil.byteToHexString(keyByte));
        }
    }

    @Override
    public void receivePasmData(String code, final byte[] respondBuff) {
        if (ByteUtil.notNull(respondBuff)) {
            Log.d("respondBuff", "respondBuff = " + ByteUtil.byteArrayToHexString(respondBuff));
            if ("pasm".equals(code)) {
                if (respondBuff.length < 8) {
                    return;
                }
                byte head = respondBuff[0];
                byte dev = respondBuff[1];
                byte cmd = respondBuff[2];
                byte ret_len = respondBuff[3];
                byte end = respondBuff[7];

                byte[] ret = ByteUtil.copyBytes(respondBuff, 8, ret_len);

                switch (cmd) {
                    case 0x01: {    //open
                        if (ByteUtil.byteArrayToIntHighToLow(ret) != 0) {
                            return;
                        }
                        //设置波特率;
                        mSpiHelper.setBaudrate(ByteUtil.byteToHex(dev), "2580");
                    }
                    break;
                    case 0x02: {

                    }
                    break;
                    case 0x03: {

                        //读数据(spi),获取可读数据长度;
                        mReadLength = mSpiHelper.getCanReadData(ByteUtil.byteToHex(dev));
                    }
                    break;
                    case 0x04: {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (respondBuff[3] == 0x00 && respondBuff[4] == 0x00 && respondBuff[5] == 0x00 && respondBuff[6] == 0x00) {
                                    psamdata_textview.append(" null");
                                    return;
                                }
                                String s_msg = ByteUtil.byteArrayToHexString(respondBuff);
                                String value = s_msg.substring(16);
                                psamdata_textview.append(" " + value);
                                cardNumber.setText("CardNumber: " + value);
                            }
                        });
                    }
                    break;
                    case 0x05: {
                        //写入psam卡;
                        mSpiHelper.writeScData(ByteUtil.byteToHex(dev), "00B0960006");
                    }
                    break;
                    case 0x07: {
                        mReadLength = ByteUtil.copyBytes(respondBuff, 3, 1);
                        if (mReadLength[0] == 0) {
                            return;
                        }
                        mReadData = ByteUtil.copyBytes(respondBuff, 8, mReadLength[0]);
                        if (ByteUtil.byteArrayToIntHighToLow(mReadData) == 0) {
                            return;
                        }
                        byte[] tmpbytes = mSpiHelper.readScData(ByteUtil.byteToHex(dev), mReadData);
                    }
                    break;
                    case 0x08: {
                        //                if (ByteUtil.byteArrayToIntHighToLow(ret) != 0) {
                        //                    return;
                        //                }
                        //冷启动复位;
                        mSpiHelper.coldStart(ByteUtil.byteToHex(dev));
                    }
                    break;
                    default: {

                    }
                    break;
                }
            }
        }
    }

    @Override
    public void receiveSpiData(String code, final byte[] respondBuff) {

        if (ByteUtil.notNull(respondBuff)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    spireceiver.append(ByteUtil.byteArrayToHexString(respondBuff) + "    ");
                }
            });
            Log.d("respondBuff", "respondBuff = " + ByteUtil.byteArrayToHexString(respondBuff));
        }
    }

    @Override
    public void receiveUpmcu(String code, byte[] respondBuff) {

    }

    @Override
    public void receiveMcuConfig(String code, byte[] respondBuff) {

    }

    /**
     * Thread for ADC code scanning ;
     */
    private class AdcReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    adcStr = readSDCardFile(ADCPATH);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTxtView_ADC.setText("ADC value : " + adcStr);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (terminal != null) {
            terminal.stopRead();
            terminal = null;
        }
        mSharedPreferencesHelper.put(mCmdKey, mRecordSB.toString());
        super.onDestroy();

    }

    private char stringToChar(String string) {
        if (!ByteUtil.notNull(string)) {
            string = "0";
        }
        int ivalue = Integer.parseInt(string);
        return (char) ivalue;
    }

    private byte[] hexToByteArray(String hex) {
        if (!ByteUtil.notNull(hex)) {
            hex = "FFFFFFFFFFFF";
        }
        return ByteUtil.hexStringToByteArray(hex);
    }

    Toast mToast;

    public void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void doRead(String readSectorStr, String readindexStr, String hexReadKey, String
            readKeyAreaStr) {
        char readSector = stringToChar(readSectorStr);
        char readindex = stringToChar(readindexStr);
        byte[] readkey = hexToByteArray(hexReadKey);
        char readKeyArea = stringToChar(readKeyAreaStr);

        char one = 1;

        byte[] resetByte = new byte[30];
        int cardResult = mCardLanDevCtrl.callCardReset(resetByte);
        byte[] readMsg = mCardLanDevCtrl.callReadOneSectorDataFromCard(readSector,
                readindex, one,
                readkey, readKeyArea);

        if (readMsg != null && readMsg.length > 0) {
            realData = new byte[readMsg.length];
            System.arraycopy(readMsg, 0, realData, 0, readMsg.length);
            String realStr = ByteUtil.byteArrayToHexString(realData);

            //26E700EE2F0804006263646566676869
            CardlanLog.debugOnConsole(DemoActivity.class, "The information being read is：" + realStr);
        }
    }

    private void doWrite(String writeSectorStr, String writeindexStr, String writeHexStr, String
            hexWriteKey, String readKeyAreaStr) {
        char writeSector = stringToChar(writeSectorStr);
        char writeindex = stringToChar(writeindexStr);
        byte[] writeKey = hexToByteArray(hexWriteKey);
        byte[] writeBytes = hexToByteArray(writeHexStr);
        char readKeyArea = stringToChar(readKeyAreaStr);

        char one = 1;

        int writeResult = mCardLanDevCtrl.callWriteOneSertorDataToCard(writeBytes,
                writeSector,
                writeindex, one,
                writeKey, readKeyArea);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.bt_serial:
                callProc(procpath, open_bee_voice);
                break;
            case R.id.bt_serialledred:
                callProc(procpath, open_led_red);
                break;
            case R.id.bt_serialledgreen:
                callProc(procpath, open_led_green);
                break;
            default:
                break;
        }
        return false;
    }

    public void callProc(String procpath, String cmd) {
        writeProc(procpath, cmd.getBytes());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String writeProc(String path, byte[] buffer) {
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "write error!";
        }
        return (buffer.toString());
    }

}
