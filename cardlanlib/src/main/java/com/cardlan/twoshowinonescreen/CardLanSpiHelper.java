package com.cardlan.twoshowinonescreen;


import com.cardlan.out.log.BaseLogContainer;
import com.cardlan.utils.ByteUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class CardLanSpiHelper
        extends BaseLogContainer
{

    private byte               head            = 0x02;
    private byte               end             = 0x03;
    private byte[]             respond_cmd     = {head, 0x10, end, (byte) 0xFF};
    private byte[]             respond_data    = {head, 0x10, end, (byte) 0xFF};
    private byte[]             get_cmd         = {head, 0x20, end, (byte) 0xFF};
    private byte[]             get_data        = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    private byte[]             lenArray        = new byte[4];
    private byte               cmd_open        = 0x01;
    private byte               cmd_close       = 0x02;
    private byte               cmd_write       = 0x03;
    private byte               cmd_read        = 0x04;
    private byte               cmd_colde_start = 0x05;
    private byte               cmd_hot_reset   = 0x06;
    private byte               cmd_data_length = 0x07;
    private byte               cmd_baudrate    = 0x08;
    private CardLanStandardBus mCardLanDevCtrl = new CardLanStandardBus();
    //每次发送命令的长度
    private int                sendLength      = 4;
    int count = 2;
    private byte[] mReadLength;
    private byte[] mReadData;
    String scData;
    //小键盘显示的数据数组;{0x1B,0x53,0x30} 为数据头,后16个是默认空格的数据;
    byte[] Y485_line_1 = {0x1B, 0x53, 0x30, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20};
    private boolean mSpi_exit_Flag = true;
    private String  spiPath        = "/dev/spidev2.0";
    private int     flags          = 0;

    private boolean mSpiOpenSuccess;

    /* mcu config start */
    public int get_mcu_config() {
        byte[] send_data = {0x02, (byte) 0xF0, 0x01, 0x00, 0x00, 0x00, 0x00, 0x03};
        /*send to mcu*/
        sendCmd(send_data);
        return 0;
    }
    /* mcu config end */
    /* upmcu start*/

    public DataInputStream buff = null;
    public String mcu_file_path;
    public int    mcu_file_size;
    public int    send_data_number;
    public int status = 0; /* 0空闲 1烧录中 2烧录异常*/


    public int Upmcu_set_filepath(String file_path) {
        try {
            buff = new DataInputStream(new FileInputStream(file_path));//缓冲
            mcu_file_size = buff.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (buff != null) {
                try {
                    buff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.mcu_file_path = file_path;
        return 0;
    }

    public int Start_upgrade_mcu() {
        try {
            buff = new DataInputStream(new FileInputStream(mcu_file_path));//缓冲
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        byte[] send_data = {0x02, (byte) 0xC0, 0x01, 0x00, 0x00, 0x00, 0x00, 0x03};
        /*send to mcu*/
        sendCmd(send_data);
        send_data_number = 0;
        status = 1;
        return 0;
    }

    public int finish_upgrade_mcu() {
        byte[] send_data = {0x02, (byte) 0xC0, 0x05, 0x00, 0x00, 0x00, 0x00, 0x03};
        /*send to mcu*/
        sendCmd(send_data);
        printLog("send_data_number:" + String.valueOf(send_data_number));
        printLog("send data over");
        status = 0;
        return 0;
    }

    public void receiveUpmcu(byte[] ar_msg) {

        if (ar_msg.length < 8) {
            return;
        }
        byte head = ar_msg[0];
        byte dev = ar_msg[1];
        byte cmd = ar_msg[2];
        byte ret_len = ar_msg[3];
        byte end = ar_msg[7];
        byte[] ret = ByteUtil.copyBytes(ar_msg, 8, ret_len);
        switch (cmd) {
            case 0x01:
            case 0x02: {

                if (status != 1) {
                    return;
                }
                if (ByteUtil.byteArrayToIntLowToHigh(ret) != 0) {
                    printLog( "upmcu false send_data_number:" + String.valueOf(send_data_number));
                    status = 2;
                    return;
                }
                if (buff == null) {
                    printLog(" buff is null");
                    status = 2;
                    return;
                }
                int i;
                byte[] buffer_data = new byte[40];        //buff 除4的余数为0
                try {
                    int temp;
                    for (i = 0; i < buffer_data.length; i++) {
                        if ((temp = buff.read()) == -1) {
                            break;
                        }
                        buffer_data[i] = (byte) temp;
                    }
                    if (i <= 0) {
                        finish_upgrade_mcu();
                        return;
                    }
                    byte[] send_data = new byte[8 + i];
                    byte[] send_arg_len = ByteUtil.intToByteArray(i);
                    try {
                        send_data[0] = 0x02;
                        send_data[1] = (byte) 0xC0;
                        send_data[2] = 0x02;
                        send_data[3] = (byte) (i & 0xff);// 最低位
                        send_data[4] = (byte) ((i >> 8) & 0xff);// 次低位
                        send_data[5] = (byte) ((i >> 16) & 0xff);// 次高位
                        send_data[6] = (byte) (i >>> 24);// 最高位,无符号右移。
                        send_data[7] = 0x03;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    byte[] file_data = ByteUtil.copyBytes(buffer_data, 0, i);
                    send_data_number += i;
                    ByteUtil.copyBytes(file_data, send_data, 8);
                    /*send to mcu*/
                    sendCmd(send_data);

                } catch (IOException e1) {
                    if (buff != null) {
                        try {
                            buff.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    e1.printStackTrace();
                }
            }
            break;
            case (byte) 0xFF: {
                printLog("upgrade fail");
                status = 2;
            }
            break;
            default: {

            }
            break;
        }
    }

    /* upmcu end */
    public CardLanSpiHelper() {
//        mSPIDispathNonUIThread = new SPIDispathNonUIThread();
//        mSPIDispathNonUIThread.start();
//        Spi_rev_thread thread = new Spi_rev_thread();
//        mSpi_exit_Flag = false;
//        thread.start();

    }

    /**
     * Open spi.
     */
    public void openSPI() {
        if (!mSpiOpenSuccess) {
            Object obj = mCardLanDevCtrl.callSPIOpen(spiPath, flags);
            if (obj != null) {
                mSpiOpenSuccess = true;
            }
            mBaseLogClass.printLog(obj.toString());
        }
    }

    public void start() {
        Spi_rev_thread thread = new Spi_rev_thread();
        mSpi_exit_Flag = false;
        thread.start();
    }

    public void exitSPIThread() {
        mSpi_exit_Flag = true;

        mSpiOpenSuccess = false;
    }

    /**
     * Open sc.
     *
     * @param scNumber the sc number
     */

    public void openSc(String scNumber) {
        if (!ByteUtil.notNull(scNumber)) {
            mBaseLogClass.printLog("openSc --> scNumber : " + scNumber);
            return;
        }
        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_open);
        cmdBytes = ByteUtil.addBytes(cmdBytes, lenArray);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        byte[] resultBytes = sendCmd(cmdBytes);
        mBaseLogClass.printLog("openSc", resultBytes);
    }

    /**
     * Cold start.
     *
     * @param scNumber the sc number
     */
    public void coldStart(String scNumber) {
        if (!ByteUtil.notNull(scNumber)) {
            mBaseLogClass.printLog("coldStart --> scNumber : " + scNumber);
            return;
        }
        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_colde_start);
        cmdBytes = ByteUtil.addBytes(cmdBytes, lenArray);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        byte[] resultBytes = sendCmd(cmdBytes);
        mBaseLogClass.printLog("coldStart", resultBytes);
    }

    /**
     * Close sc.
     *
     * @param scNumber the sc number
     */
    public void closeSc(String scNumber) {
        if (!ByteUtil.notNull(scNumber)) {
            mBaseLogClass.printLog("closeSc --> scNumber : " + scNumber);
            return;
        }
        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_close);
        cmdBytes = ByteUtil.addBytes(cmdBytes, lenArray);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        byte[] resultBytes = sendCmd(cmdBytes);
        mBaseLogClass.printLog("closeSc", resultBytes);
    }

    /**
     * Get can read data byte [ ].
     *
     * @param scNumber the sc number
     * @return the byte [ ]
     */
    /*
     *获取可读数据,(spi),第一次先拿要读数据的长度;第二次才获取数据;
     */
    public byte[] getCanReadData(String scNumber) {
        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_data_length);
        cmdBytes = ByteUtil.addBytes(cmdBytes, lenArray);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        byte[] resultBytes = sendCmd(cmdBytes);

        return resultBytes;
    }


    public void receiveMcuConfig(byte[] respondBuff) {

    }

    /**
     * Read data length byte [ ].
     *
     * @param scNumber the sc number
     * @return the byte [ ]
     */
    public byte[] readDataLength(String scNumber) {
        if (!ByteUtil.notNull(scNumber)) {
            mBaseLogClass.printLog("readDataLength --> scNumber : " + scNumber);
            return null;
        }
        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_data_length);
        cmdBytes = ByteUtil.addBytes(cmdBytes, lenArray);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        byte[] resultBytes = sendCmd(cmdBytes);
        mBaseLogClass.printLog("readDataLength", resultBytes);
        return resultBytes;
    }

    /**
     * Set baudrate byte [ ].
     *
     * @param scNumber the sc number
     * @param baudrate the baudrate
     * @return the byte [ ]
     */
    public byte[] setBaudrate(String scNumber, String baudrate) {
        if (!ByteUtil.notNull(scNumber)) {
            mBaseLogClass.printLog("setBaudrate --> scNumber : " + scNumber);
            //            return null;
            baudrate = "2580";
        }

        byte[] sendArrayLength = null;
        byte[] baudrateBytes = ByteUtil.hexStringToByteArray(baudrate);
        if (!ByteUtil.notNull(baudrate)) {
            mBaseLogClass.printLog("setBaudrate --> baudrate : " + baudrate);
            sendArrayLength = lenArray;
        } else {
            int sendDataLength = baudrateBytes.length;
            sendArrayLength = ByteUtil.reverseByteArray(ByteUtil.intToByteArray(sendDataLength));
        }

        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_baudrate);
        cmdBytes = ByteUtil.addBytes(cmdBytes, sendArrayLength);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        cmdBytes = ByteUtil.addBytes(cmdBytes, baudrateBytes);

        return sendCmd(cmdBytes);
    }

    /**
     * Read sc data byte [ ].
     *
     * @param scNumber   the sc number
     * @param readLength the read length
     * @return the byte [ ]
     */
    public byte[] readScData(String scNumber, byte[] readLength) {
        if (!ByteUtil.notNull(scNumber)) {
            mBaseLogClass.printLog("readScData --> scNumber : " + scNumber);
            return null;
        }

        byte[] sendArrayLength = null;

        if (!ByteUtil.notNull(readLength)) {
            mBaseLogClass.printLog("readScData --> readLength : " + readLength);
            sendArrayLength = lenArray;
        } else {
            int sendDataLength = readLength.length;
            sendArrayLength = ByteUtil.reverseByteArray(ByteUtil.intToByteArray(sendDataLength));
        }
        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_read);
        cmdBytes = ByteUtil.addBytes(cmdBytes, sendArrayLength);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        cmdBytes = ByteUtil.addBytes(cmdBytes, readLength);
        byte[] resultBytes = sendCmd(cmdBytes);
        mBaseLogClass.printLog("readScData", resultBytes);
        return resultBytes;
    }

    /**
     * Write sc data.
     *
     * @param scNumber the sc number
     * @param scData   the sc data
     */
    public void writeScData(String scNumber, String scData) {
        if (!ByteUtil.notNull(scNumber)) {
            mBaseLogClass.printLog("writeScData --> scNumber : " + scNumber);
            return;
        }

        byte[] sendArrayLength = null;

        if (!ByteUtil.notNull(scData)) {
            mBaseLogClass.printLog("writeScData --> scData : " + scData);
            sendArrayLength = lenArray;
        } else {
            //            byte[] scDataBytes = ByteUtil.hexStringToByteArray(scData);
            int sendDataLength = scData.length() / 2;
            sendArrayLength = ByteUtil.reverseByteArray(ByteUtil.intToByteArray(sendDataLength));
        }

        byte scNumberByte = ByteUtil.hexStringToByte(scNumber);
        byte[] scDataBytes = ByteUtil.hexStringToByteArray(scData);
        byte[] cmdBytes = ByteUtil.addBytes(head, scNumberByte);
        cmdBytes = ByteUtil.addBytes(cmdBytes, cmd_write);
        cmdBytes = ByteUtil.addBytes(cmdBytes, sendArrayLength);
        cmdBytes = ByteUtil.addBytes(cmdBytes, end);
        cmdBytes = ByteUtil.addBytes(cmdBytes, scDataBytes);
        byte[] resultBytes = sendCmd(cmdBytes);
        mBaseLogClass.printLog("writeScData", resultBytes);
    }
    /**
     * Set high level input;
     *
     */
    public void setHighFrequency() {

        byte[] str_arg_len = {0x00, 0x00, 0x00, 0x00};
        byte[] scDataBytes = null;
            scDataBytes = "B_06".getBytes();
            if (!ByteUtil.notNull(scDataBytes)) {
                mBaseLogClass.printLog("writeScData --> scData : " + scDataBytes);
                str_arg_len[0] = (byte) scDataBytes.length;
            } else {
                //            byte[] scDataBytes = ByteUtil.hexStringToByteArray(scData);

            }
        write((byte) 0x10, (byte) 0x01, str_arg_len, scDataBytes);

        mBaseLogClass.printLog("writeScData", scDataBytes);
    }
    /**
     * Set low level input;
     *
     */
    public void setLowFrequency() {

        byte[] str_arg_len = {0x00, 0x00, 0x00, 0x00};
        byte[] scDataBytes = null;

            scDataBytes = "B_06".getBytes();
            if (!ByteUtil.notNull(scDataBytes)) {
                mBaseLogClass.printLog("writeScData --> scData : " + scDataBytes);
                str_arg_len[0] = (byte) scDataBytes.length;
            } else {
                //            byte[] scDataBytes = ByteUtil.hexStringToByteArray(scData);

            }

        write((byte) 0x10, (byte) 0x02, str_arg_len, scDataBytes);
        mBaseLogClass.printLog("writeScData", scDataBytes);
    }
    /**
     * TTS for Chinese;
     *
     * @param scData Text to Speech;
     */
    public void setCmd_play(String scData) {
        if (!ByteUtil.notNull(scData)) {
            mBaseLogClass.printLog("writeScData --> scNumber : " + scData);
            return;
        }

        byte[] str_arg_len = {0x00, 0x00, 0x00, 0x00};
        byte[] scDataBytes = null;
        try {
            scDataBytes = scData.getBytes("GB2312");
            if (!ByteUtil.notNull(scDataBytes)) {
                mBaseLogClass.printLog("writeScData --> scData : " + scDataBytes);
                str_arg_len[0] = (byte) scDataBytes.length;
            } else {
                //            byte[] scDataBytes = ByteUtil.hexStringToByteArray(scData);

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        write((byte) 0x30, (byte) 0x03, str_arg_len, scDataBytes);

        mBaseLogClass.printLog("writeScData", scDataBytes);
    }


    public byte[] write(byte dev_number, byte cmd, byte[] ar_arg_len, byte[] arg_array) {
        if (ar_arg_len == null) {
            return null;
        }
        /*
            Verify transmission parameter length
        * */
        int arg_len = ar_arg_len[0] + ar_arg_len[1] + ar_arg_len[2] + ar_arg_len[3];
        if (arg_array == null && arg_len == 0) {
            /*Sp without spi*/

        } else if (arg_array != null && arg_array.length == arg_len) {
            /*Sp with spi*/

        } else /* error */ {
            return null;
        }
        byte[] Send_Data_Bytes = new byte[0];
        Send_Data_Bytes = ByteUtil.addBytes(Send_Data_Bytes, head);
        Send_Data_Bytes = ByteUtil.addBytes(Send_Data_Bytes, dev_number);
        Send_Data_Bytes = ByteUtil.addBytes(Send_Data_Bytes, cmd);
        Send_Data_Bytes = ByteUtil.addBytes(Send_Data_Bytes, ar_arg_len);
        Send_Data_Bytes = ByteUtil.addBytes(Send_Data_Bytes, end);
        Send_Data_Bytes = ByteUtil.addBytes(Send_Data_Bytes, arg_array);

        return sendCmd(Send_Data_Bytes);
    }

    public byte[] write(String dev_number, String cmd, String str_arg_len, String str_arg) {
        if (ByteUtil.notNull(dev_number) == false || ByteUtil.notNull(cmd) == false || ByteUtil
                .notNull(str_arg_len) == false || str_arg_len.length() != 8) {
            return null;
        }
        byte b_dev_number = ByteUtil.hexStringToByte(dev_number);
        byte b_cmd = ByteUtil.hexStringToByte(cmd);
        byte[] ar_arg_len = ByteUtil.hexStringToByteArray(str_arg_len);
        byte[] ar_arg = null;
        if (str_arg != null || str_arg.isEmpty() != true) {
            //TODO 如果str_arg有中文字符会存在闪退bug;
            ar_arg = ByteUtil.hexStringToByteArray(str_arg);
        }
        return write(b_dev_number, b_cmd, ar_arg_len, ar_arg);
    }

    public byte[] sendCmd(byte[] cmdBytes) {
        if (!ByteUtil.notNull(cmdBytes)) {
            return null;
        }

        int length = cmdBytes.length;
        int remain = length % sendLength;
        int fori = remain > 0 ? (length / sendLength) + 1 : length / sendLength;
        byte[] returnBytes = null;

        for (int i = 0; i < fori; i++) {
            byte[] sendBytes = new byte[sendLength];
            int start = i * sendLength;
            int cmdLength = sendLength;
            if (remain > 0) {
                if (i == fori - 1) {
                    //判断最后一次
                    cmdLength = length - i * sendLength;
                    System.arraycopy(cmdBytes, start, sendBytes, 0, cmdLength);
                } else {
                    sendBytes = ByteUtil.copyBytes(cmdBytes, start, cmdLength);
                }
            } else {
                sendBytes = ByteUtil.copyBytes(cmdBytes, start, cmdLength);
            }

            byte[] callBackBytes = mCardLanDevCtrl.callSPITransfer(sendBytes);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            returnBytes = ByteUtil.addBytes(returnBytes, callBackBytes);
        }
        return returnBytes;
    }

    /**
     * 收的线程;
     */
    class Spi_rev_thread extends Thread {

        private int index = 0;

        @Override
        public void run() {
            super.run();
            while (!mSpi_exit_Flag) {
                openSPI();
                byte[] buff = null;
                //                byte[] bytes = FileHelper.getFileBytes("/sys/class/gpio/gpio59/value", null);
                String gpio59_flag = readItems("/sys/class/gpio/gpio59/value");
                //                String gpio59_flag = new String(bytes);
                if (gpio59_flag.compareTo("0") == 0) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index++;
                    continue;
                }
                buff = SendResqustPacket();
                if (!ByteUtil.notNull(buff)) {
                    continue;
                }
                if (buff[0] == 0xFF && buff[1] == 0xFF && buff[2] == 0xFF && buff[3] == 0xFF) {
                    continue;
                }

                buff = getReadData(buff[0]);
                if (!ByteUtil.notNull(buff)) {
                    continue;
                }
                if (index <= 0) {
                    index++;
                    continue;
                }
                //dispatch spi buffer
                //mSPIDispathNonUIThread.addMessage(buff, mSPIDispathNonUIThread);
                for (int i = 0; i < buff.length; i++) {
                    if (buff.length < 8 + i) {
                        continue;
                    }
                    if (buff[i] != head || buff[i + 7] != end) {
                        continue;
                    }
                    int arg1_len = buff[3] - buff[3] % 4 + (buff[3] % 4 != 0 ? 4 : 0);
                    int arg2_len = buff[4] - buff[4] % 4 + (buff[4] % 4 != 0 ? 4 : 0);
                    int arg3_len = buff[5] - buff[5] % 4 + (buff[5] % 4 != 0 ? 4 : 0);
                    int arg4_len = buff[6] - buff[6] % 4 + (buff[6] % 4 != 0 ? 4 : 0);
                    byte[] respond_buff = ByteUtil.copyBytes(buff, i, 8 + arg1_len + arg2_len + arg3_len + arg4_len);
                    if (ByteUtil.notNull(respond_buff) == false) {
                        continue;
                    }

                    {
                        byte code = respond_buff[0];
                        if (mSpiCallBackLinstener != null) {
                            mSpiCallBackLinstener.receiveSpiData(ByteUtil.byteToHexString(code), respond_buff);
                        }
                        //RxBus.get().send(ByteUtil.byteToInt(code), respond_buff);
                    }

                    {
                        byte code = respond_buff[1];
                        switch (code) {
                            case 0x50:
                            case 0x51:
                            case 0x52:
                            case 0x60:
                            case 0x61:
                            case 0x62: {
                                if (mSpiCallBackLinstener != null) {
                                    mSpiCallBackLinstener.receivePasmData("pasm", respond_buff);
                                }
                                //RxBus.get().send(0x5060, respond_buff);
                            }
                            break;
                            case 0x20: //这个是小键盘;
                                if (respond_buff.length < 8) {
                                    return;
                                }
                                byte keyByte = respond_buff[8];
                                String key = "";
                                if (keyByte == 0x41) {
                                    key = "F1";
                                } else if (keyByte == 0x42) {
                                    key = "F2";
                                } else if (keyByte == 0x2E) {
                                    key = ".";
                                } else if (keyByte == 0x08) {
                                    key = "清除";
                                } else if (keyByte == 0x0D) {
                                    key = "确定";
                                } else if (keyByte >= 0x30 && keyByte <= 0x39) {
                                    byte start = 0x30;
                                    key = String.valueOf(keyByte - start);
                                } else {
                                    key = "unknow";
                                }

                                if (mSpiCallBackLinstener != null) {
                                    mSpiCallBackLinstener.receiveKeyCode(key, keyByte);
                                }
                                break;
                            case (byte) 0xC0: {
                                if (mSpiCallBackLinstener != null) {
                                    mSpiCallBackLinstener.receiveUpmcu("upmcu", respond_buff);
                                }
                            }
                            break;
                            case (byte) 0xF0: {
                                if (mSpiCallBackLinstener != null) {
                                    mSpiCallBackLinstener.receiveMcuConfig("mcu config", respond_buff);
                                }
                            }
                            break;
                            default: {
                                //RxBus.get().send(code, respond_buff);
                            }
                            break;
                        }
                    }
            }
            }
        }
    }

    private SpiCallBackLinstener mSpiCallBackLinstener;

    /**
     * 回显键盘数值.
     *
     * @param keyByte
     */
    public void sendKeyboard(byte keyByte) {
        //发送键盘键值;
        byte[] uHeadBytes = {0x02, 0x20, 0x01, 0x13, 0x00, 0x00, 0x00, 0x03};

        byte[] Y485_line_2 = {0x1B, 0x53, 0x31, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20};

        if (keyByte >= 0x30 && keyByte <= 0x39) {
            if (count > 18) { //行界
                return;
            } else {
                count++;
                Y485_line_1[count] = keyByte;
            }
        } else if (keyByte == 0x08) { //清除按键;
            if (count < 3) { //行界
                return;
            } else {
                Y485_line_1[count] = 0x20;
                count--;
            }
        } else if (keyByte == 0x2E) {
            //只能存在一个小数点.
            if (contains(keyByte)) {
                return;
            } else {
                if (count > 18 || count < 3) { //行界
                    return;
                } else {
                    count++;
                    Y485_line_1[count] = keyByte;
                }
            }

        }
        sendCmd(ByteUtil.addBytes(uHeadBytes, Y485_line_1));

    }

    private boolean contains(byte keyByte) {
        for (byte b : Y485_line_1) {
            if (b == keyByte) {
                return true;
            }
        }
        return false;
    }

    public void setSpiCallBackLinstener(SpiCallBackLinstener mSpiCallBackLinstener) {
        this.mSpiCallBackLinstener = mSpiCallBackLinstener;
    }

    public interface SpiCallBackLinstener {
        void receiveKeyCode(String keyCode, byte keyByte);

        void receivePasmData(String code, byte[] respondBuff);

        void receiveSpiData(String code, byte[] respondBuff);

        void receiveUpmcu(String code, byte[] respondBuff);

        void receiveMcuConfig(String code, byte[] respondBuff);
    }


    public byte[] SendResqustPacket() {
        byte[] buff = null;
        byte[] returnbuff = null;
        /*==========================新版的写法==================================*/
        mCardLanDevCtrl.callSPITransfer(respond_cmd);
        buff = mCardLanDevCtrl.callSPITransfer(get_data);
        if (buff == null || buff.length != 4) {
            return null;
        }
        returnbuff = ByteUtil.addBytes(returnbuff, buff);
        printLog(ByteUtil.byteArrayToHexString(returnbuff));
        return returnbuff;
        /*==================================旧版的写法===================================*/
//        for (int i = 0; i < 3; i++) {
//            buff = mCardLanDevCtrl.callSPITransfer(respond_data);
//            if (buff == null || buff.length != 4) {
//                continue;
//            }
//            if (buff[0] == head) {
//                break;
//            }
//        }
//        if (buff == null || buff.length != 4 || buff[0] != head) {
//            return null;
//        }
//        returnbuff = ByteUtil.addBytes(returnbuff, buff);
//        buff = mCardLanDevCtrl.callSPITransfer(get_data);
//        if (buff == null || buff.length != 4 || buff[3] != end) {
//            return null;
//        }
//        returnbuff = ByteUtil.addBytes(returnbuff, buff);
//        Log.d(" Spi Respond Data:", ByteUtil.byteArrayToHexString(returnbuff));
//
//        return returnbuff;
    }

    public byte[] getReadData(int len) {
        byte[] returnbuff = null;
        /*================================新版的写法================================*/
        mCardLanDevCtrl.callSPITransfer(get_cmd);
        for (int i = 0; i < len; i = i + 4) {
            byte[] callBackBytes = mCardLanDevCtrl.callSPITransfer(get_data);
            if (callBackBytes == null || callBackBytes.length != 4) {
                return null;
            }
            returnbuff = ByteUtil.addBytes(returnbuff, callBackBytes);
        }
        if (ByteUtil.notNull(returnbuff) == false) {
            return null;
        }
        printLog(" Spi Read Data :" + ByteUtil.byteArrayToHexString(returnbuff));
        return returnbuff;

        /*=================================旧版的写法===============================*/
//        for (int i = 0; i < len; i = i + 4) {
//            byte[] callBackBytes = mCardLanDevCtrl.callSPITransfer(get_data);
//            if (callBackBytes == null || callBackBytes.length != 4) {
//                return null;
//            }
//            returnbuff = ByteUtil.addBytes(returnbuff, callBackBytes);
//        }
//
//        if (ByteUtil.notNull(returnbuff) == false) {
//            return null;
//        }
//        Log.d(" Spi Read Data", ByteUtil.byteArrayToHexString(returnbuff));
//        return returnbuff;
    }


    private void printLog(String message) {
        mBaseLogClass.printLog(message);
    }
    private String readItems(String logFile) {
        // Hard code adding some delay, to distinguish reading from memory and reading disk clearly
        //读取文件
        BufferedReader br = null;
        StringBuffer   sb = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
            sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }
}
