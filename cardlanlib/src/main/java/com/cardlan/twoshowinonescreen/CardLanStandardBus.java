package com.cardlan.twoshowinonescreen;


import com.cardlan.out.log.OutLog;
import com.cardlan.utils.ByteUtil;

import java.io.FileDescriptor;

/**
 * Created by wuxd on 2019/1/10.
 */

public class CardLanStandardBus {
    static {
        System.loadLibrary("cardlan_StandardBus");
    }

    private CardLanDevUtil mCardLanDevUtil;

    /**
     * the public Constructor .
     * the instance of {@link CardLanDevUtil} will be initialized
     */
    public CardLanStandardBus() {
        /* Check access permission */
//        try {
        /* Missing read/write permission, trying to chmod the file */
//            Process su;
//            String cmd = "chmod 777 " + "/dev/spidev0.0" + "\n"
//                    + "exit\n";
//            su = Runtime.getRuntime().exec(cmd);
//            su.getOutputStream().write(cmd.getBytes());
//            if ((su.waitFor() != 0)) {
//                throw new SecurityException();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new SecurityException();
//        }
        mCardLanDevUtil = new CardLanDevUtil();
    }

    public int callReadOne2FiveSectorDataFromCard(char VerifyFlag, byte[] key_array, char
            mode, byte[] returnArray) {
        return ReadOne2FiveSectorDataFromCard(VerifyFlag, key_array, mode,returnArray);
    }

    /**
     * open buzzer
     */
    public void callOpenBuzzer() {
        mCardLanDevUtil.openBuzzer();
    }

    /**
     * close buzzer
     */
    public void callCloseBuzzer() {
        mCardLanDevUtil.closeBuzzer();
    }

    /**
     * open RedLED
     */
    public void callOpenRedLED() {
        mCardLanDevUtil.openRedLED();
    }

    /**
     * close RedLED
     */
    public void callCloseRedLED() {
        mCardLanDevUtil.closeRedLED();
    }

    /**
     * open GreenLED
     */
    public void callOpenGreenLED() {
        mCardLanDevUtil.openGreenLED();
    }

    /**
     * close GreenLED
     */
    public void callCloseGreenLED() {
        mCardLanDevUtil.closeGreenLED();
    }

    /**
     * init the device.
     *
     * @return int if return value equal {@link DeviceCardConfig#INIT_DEVICE_STATUS_SUCCESS}, it
     * means init devcie success, else not.
     */
    public int callInitDev() {
        return InitDev();
    }

    /**
     * stop search card
     */
    public void callCardHalt() {
        CardHalt();
    }

    /**
     * card reset
     *
     * @param CardSn array for saving card serial number.
     * @return int if return value equal {@link DeviceCardConfig#CARD_RESET_STATUS_MONE_SUCCESS
     * } it means mifare1 card , else equal {@link DeviceCardConfig#CARD_RESET_STATUS_CPU_SUCCESS
     * } it means cpu card．
     */
    public int callCardReset(byte[] CardSn) {
        if (!ByteUtil.notNull(CardSn)) {
            OutLog.debugOnConsole(this.getClass(), new Exception("the param is null"));
            return -1;
        }
        if (CardSn.length < DeviceCardConfig.CARD_RESET_SIZE) {
            OutLog.debugOnConsole(this.getClass(), new Exception("the param length is small than " +
                    DeviceCardConfig.CARD_RESET_SIZE));
            return -2;
        }
        return CardReset(CardSn,0);
    }

    /**
     * read data from mifare1 card
     *
     * @param SectorNo   　sector index
     * @param BlockNo    　area index
     * @param VerifyFlag verify flag.Suggest options(1)
     * @param key_array  　key byte array
     * @param mode       　key check mode. suggest options(0x0a(read)/0x0b(write)).
     * @return byte[] return byte array data
     */
    public byte[] callReadOneSectorDataFromCard(char SectorNo, char BlockNo, char
            VerifyFlag, byte[] key_array, char mode) {

        return ReadOneSectorDataFromCard(SectorNo, BlockNo, VerifyFlag, key_array, mode);
    }

    /**
     * write data array into mifare1 card
     *
     * @param SectorArray 　write data source array
     * @param SectorNo    　sector index
     * @param BlockNo     　area index
     * @param VerifyFlag  　verify flag.Suggest options(1)
     * @param key_array   　key byte array
     * @param mode        　key check mode. suggest options(0x0a(read)/0x0b(write)).
     * @return int return status, if the value equal
     * {@link DeviceCardConfig#MONE_CARD_WRITE_SUCCESS_STATUS} , it
     * means write data success.
     */
    public int callWriteOneSertorDataToCard(byte[] SectorArray, char SectorNo, char
            BlockNo, char VerifyFlag, byte[] key_array, char mode) {

        return WriteOneSertorDataToCard(SectorArray, SectorNo, BlockNo, VerifyFlag, key_array,
                mode);
    }

    /**
     * open serial . if you want san qr code , you must call this method.
     *
     * @param path     　file path,suggest ('/dev/ttyAMA4')
     * @param baudrate 　115200
     * @param flags    　suggest value 0
     * @return FileDescriptor file desc
     */
    public FileDescriptor callSerialOpen(String path, int baudrate, int flags) {
        return SerialOpen(path, baudrate, flags);
    }

    /**
     * close serial
     */
    public void callSerialClose() {
        SerialClose();
    }

    /**
     * invoke the native {@link #CpuSendCmd} method
     *
     * @param cmdArray     cmd byte array
     * @param receiveArray 　the array for saving result value
     * @return int status
     */
    public int callCpuSendCmd(byte[] cmdArray, byte[] receiveArray) {
        return CpuSendCmd(cmdArray, receiveArray);
    }

    ////////SPI

    /**
     * open spi
     *
     * @param path  spi path
     * @param flags flag
     * @return Object return spi obj
     */
    public Object callSPIOpen(String path, int flags) {
        return SPIOpen(path, flags);
    }

    /**
     * close current SPI
     */
    public void callSPIClose() {
        SPIClose();
    }

    /**
     * send spi cmd
     *
     * @param cmdBytes cmd byte array
     * @return byte[] spi call back data
     */
    public byte[] callSPITransfer(byte[] cmdBytes) {
        byte[] result = SPITransfer(cmdBytes);
        //Log.i("result:", "callSPITransfer: "+ ByteUtil.byteArrayToHexString(cmdBytes) + "->" +  ByteUtil.byteArrayToHexString(result));
        return result;
    }

    //==================================JNI part==========================================

    //JNI Part add by li bin

    /**
     * init the device.
     *
     * @return int if return value equal {@link DeviceCardConfig#INIT_DEVICE_STATUS_SUCCESS}, it
     * means init devcie success, else not.
     */
    private native static int InitDev();

    /**
     * card reset
     *
     * @param CardSn array for saving card serial number.
     * @return int if return value equal {@link DeviceCardConfig#CARD_RESET_STATUS_MONE_SUCCESS
     * } it means mifare1 card , else equal {@link DeviceCardConfig#CARD_RESET_STATUS_CPU_SUCCESS
     * } it means cpu card．
     */
    private native static int CardReset(byte[] CardSn,int type);

    /**
     * read data from mifare1 card
     *
     * @param SectorNo   　sector index
     * @param BlockNo    　area index
     * @param VerifyFlag verify flag.Suggest options(1)
     * @param key_array  　key byte array
     * @param mode       　key check mode. suggest options(0x0a(read)/0x0b(write)).
     * @return byte[] return byte array data
     */
    private native static byte[] ReadOneSectorDataFromCard(char SectorNo, char BlockNo, char
            VerifyFlag, byte[] key_array, char mode);

    /**
     * write data array into mifare1 card
     *
     * @param SectorArray 　write data source array
     * @param SectorNo    　sector index
     * @param BlockNo     　area index
     * @param VerifyFlag  　verify flag.Suggest options(1)
     * @param key_array   　key byte array
     * @param mode        　key check mode. suggest options(0x0a(read)/0x0b(write)).
     * @return int return status, if the value equal
     * {@link DeviceCardConfig#MONE_CARD_WRITE_SUCCESS_STATUS} , it
     * means write data success.
     */
    private native static int WriteOneSertorDataToCard(byte[] SectorArray, char SectorNo, char
            BlockNo, char VerifyFlag, byte[] key_array, char mode);

    /**
     * stop search card
     */
    private native void CardHalt();

    /**
     * read all byte array
     */
    private native int ReadOne2FiveSectorDataFromCard(char VerifyFlag, byte[] key_array, char
            mode, byte[] returnArray);


    ///==================================scan code =======================================

    /**
     * open serial . if you want san qr code , you must call this method.
     *
     * @param path     　file path,suggest ('/dev/ttyAMA4')
     * @param baudrate 　115200
     * @param flags    　suggest value 0
     * @return FileDescriptor file desc
     */
    private native static FileDescriptor SerialOpen(String path, int baudrate, int flags);

    /**
     * close serial
     */
    private native void SerialClose();


    ///=================================cpu card =======================================

    /**
     * send cpu card cmd
     *
     * @param cmdArray     　cmd array
     * @param receiveArray 　array for saving result value
     * @return int operation status
     */
    private native int CpuSendCmd(byte[] cmdArray, byte[] receiveArray);

    ///====================================SPI============================================

    /**
     * open spi
     *
     * @param path  spi path
     * @param flags flag
     * @return Object return spi obj
     */
    private native Object SPIOpen(String path, int flags);

    /**
     * close current SPI
     */
    private native void SPIClose();

    /**
     * send spi cmd
     *
     * @param spiBytes cmd byte array
     * @return byte[] spi call back data
     */
    private native byte[] SPITransfer(byte[] spiBytes);



    /**
     * DES card key , the key byte array is your data source, the data byte array is your key
     * byte array. this method only used for mifare1 card.
     *
     * @param keyArray  　source byte array
     * @param dataArray 　key byte array
     * @return byte[] return the des result array,the array length is
     * {@link DeviceCardConfig#DES_OUT_ARRAY_LENGTH}
     */
    public byte[] callDesCard(byte[] keyArray, byte[] dataArray) {
        byte[] outArray = new byte[DeviceCardConfig.DES_OUT_ARRAY_LENGTH];
        DesCard(keyArray, dataArray, outArray);
        return outArray;
    }

    /**
     * method for des cpu card
     *
     * @param desType  encrypt / decrypt
     * @param desMode  　ECB,CBC model
     * @param srcBytes 　data source
     * @param outBytes 　the result array for saving des value, this array length must equal the data
     *                 source length,or bigger than it.
     * @param keyBytes 　the key byte array , (support  8,16,24 bits)
     * @return char　return the des status
     */
    public char callRunDes(char desType, char desMode, byte[] srcBytes, byte[]
            outBytes, byte[] keyBytes) {
        char keyLength = ByteUtil.intToChar(keyBytes.length);
        return RunDes(desType, desMode, srcBytes, outBytes, srcBytes.length, keyBytes, keyLength);
    }

    /**
     * this method is used to calculation cpu card  mac .
     *
     * @param initIn         encrypt / decrypt, allow null
     * @param srcBytes       data source
     * @param outBytes       the result array for saving des value, this array length must equal
     *                       the data source length,or bigger than it.
     * @param keyBytes       the key byte array , (support  8,16,24 bits)
     * @return char　return the des status
     */
    public char MacAnyLength(byte[] initIn, byte[] srcBytes, byte[] outBytes, byte[] keyBytes) {
        char keyLength = ByteUtil.intToChar(keyBytes.length);
        return MacAnyLength(initIn, srcBytes, outBytes, srcBytes.length, keyBytes, keyLength);
    }

    /**
     * DES card key (native), the key byte array is your data source, the data byte array is your key
     * byte array. this method only used for mifare1 card.
     *
     * @param keyArray  　source byte array
     * @param dataArray 　key byte array
     * @param outArray 　the result array for saving des value
     * @return int return the des status
     */
    private native static int DesCard(byte[] keyArray, byte[] dataArray, byte[] outArray);

    /**
     * method for des cpu card
     *
     * @param desType  encrypt / decrypt
     * @param desMode  　ECB,CBC model
     * @param srcBytes 　data source
     * @param outBytes 　the result array for saving des value, this array length must equal the data
     *                 source length,or bigger than it.
     * @param srcBytesLength 　the length of data source.
     * @param keyBytes 　the key byte array , (support  8,16,24 bits)
     * @param keyLength 　the length of key byte array.
     * @return char　return the des status
     */
    private native static char RunDes(char desType, char desMode, byte[] srcBytes, byte[]
            outBytes, int srcBytesLength, byte[] keyBytes, char keyLength);

    /**
     * this method is used to calculation cpu card  mac .
     *
     * @param initInBytes         encrypt / decrypt, allow null
     * @param srcBytes       data source
     * @param outBytes       the result array for saving des value, this array length must equal
     *                       the data source length,or bigger than it.
     * @param srcBytesLength 　the length of data source.
     * @param keyBytes       the key byte array , (support  8,16,24 bits)
     * @param keyLength 　the length of key byte array.
     * @return char　return the des status
     */
    private native static char MacAnyLength(byte[] initInBytes, byte[] srcBytes, byte[]
            outBytes, int srcBytesLength, byte[] keyBytes, char keyLength);

//    public static native int read(byte[]data);

//    public static native int read(byte[] csn ,byte[] data);

    public static native int write(byte[]data,int len);

    public native static FileDescriptor open(String path, int baudrate, int flags);

    public native void close();

    public native int JTBCardread(byte[] data);

    public native int JTBMonthlyCardConsume(int money);

    public native int JTBCardLock();

    public native int YLCardread(byte[] data);

    public native int YLCardHandleChannle1(int money ,byte[] data);
}
