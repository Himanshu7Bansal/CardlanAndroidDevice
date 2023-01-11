package com.cardlan.twoshowinonescreen;


import com.cardlan.out.log.BaseLogContainer;
import com.cardlan.utils.ByteUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CardLanSerialHelper extends BaseLogContainer {

    //Test_SendThread mSendThread;
    Test_ReadThread mReadThread;

    FileInputStream mFileInputStream;
    OutputStream    mFileOutputStream;

    FileDescriptor mFd;
    String         path;
    int            baudrate;
    int            flags;
    String         result;
    FileCallBack   mCallBack;
    //ReceiverCallBack mReceiver;
    //private  Serial485Thread mThread;

    //public void setReceiver(ReceiverCallBack receiver) {
//        mReceiver = receiver;
//    }

    public void setCallBack(FileCallBack callBack) {
        mCallBack = callBack;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void start() {
        if (mReadThread != null &&!mReadThread.isAlive()) {
            mReadThread.start();
        }
    }

    /**
     * 485send;
     */
//    public void start485() {
//        if (mSendThread != null &&!mSendThread.isAlive()) {
//            mSendThread.start();
//        }
//        if (mThread != null &&!mThread.isAlive()) {
//            mThread.start();
//        }
//    }

    enum Test_Status {
        unknow,
        normal,
        init_error,
        read_error,
        send_error
    }

    Test_Status status;
    /**
     * The Buffer.
     */
    public byte[] mSend_buffer;
    //public byte[] mRead_buffer;
    public int size;

    /**
     *
     * @param path  devPath
     * @param baudrate
     * @param flags
     * @param buffer_size
     */
    public CardLanSerialHelper(String path, int baudrate, int flags, int buffer_size) {
        this.path = path;
        this.baudrate = baudrate;
        this.flags = flags;
        size = buffer_size;
        mSend_buffer = new byte[buffer_size];
        status = Test_Status.unknow;

//        if ("/dev/ttyAMA2".equals(path)) {
//        File file = new File("/proc/serial_ctrl/485_ctrl");
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write("1".getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        } else if ("/dev/ttyAMA4".equals(path)) {
//        }
        check();
        mReadThread = new Test_ReadThread();

    }

    public void setStatus(Test_Status status) {
        this.status = status;
    }

    public String getStatus() {
        return status.toString();
    }


    public int check() {

        if (path.isEmpty() || baudrate <= 0 || flags < 0) {
            setStatus(Test_Status.init_error);
            return -1;
        }

        if (mFd == null) {
            CardLanStandardBus dev = new CardLanStandardBus();
            this.mFd = dev.callSerialOpen(this.path, this.baudrate, this.flags);

            if (mFd.valid() == false) {
                setStatus(Test_Status.init_error);
                return -2;
            }
        }

        if (mFileInputStream == null) {
            mFileInputStream = new FileInputStream(mFd);
            if (mFileInputStream == null) {
                setStatus(Test_Status.init_error);
                return -3;
            }
        }

        if (mFileOutputStream == null) {
            mFileOutputStream = new FileOutputStream(mFd);
            if (mFileOutputStream == null) {
                setStatus(Test_Status.init_error);
                return -3;
            }
        }
        setStatus(Test_Status.normal);
        return 0;
    }
    //485 received Thread;
    private class Serial485Thread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {

                    if (mFileInputStream == null) {
                        return;
                    }

                    int receiveSize = mFileInputStream.read(mSend_buffer);
                    printLog("receiveSize:"+receiveSize);
                    if (receiveSize > 0) {

                     //mReceiver.onReceive(mSend_buffer,receiveSize);
                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(receiveSize > 0) {
//                                onSerial485DataReceived(buffer, receiveSize);
//                            }
//                        }
//                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    //485 send Thread;
//    private class Test_SendThread extends Thread {
//        @Override
//        public void run() {
//            super.run();
//
//            ByteUtil.addBytes(mSend_buffer, "1234567890".getBytes());
//            while (!isInterrupted()) {
//                if (write(mSend_buffer) != 0) {
//                    setStatus(Test_Status.send_error);
//                }
//
//                SystemClock.sleep(500);
//            }
//        }
//    }

    public int write(byte[] Send_Byte) {
        if (mFileOutputStream == null || Send_Byte == null) {
            return -1;
        }
        try {
            File             file = new File("/proc/serial_ctrl/485_ctrl");
            FileOutputStream fos  = new FileOutputStream(file);
            fos.write("1".getBytes());
            mFileOutputStream.write(Send_Byte);
            printLog("send : "+ByteUtil.byteArrayToHexString(Send_Byte));
            fos.write("0".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //QRcode Thread;
    private class Test_ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] mRead_buffer = new byte[64];
                    if (mFileInputStream == null) return;
                    size = mFileInputStream.read(mRead_buffer);
                    printLog("size =" +size);
                    if(size > 0) {
                        if (mCallBack != null) {
                            mCallBack.getResult(mRead_buffer,size);
                        }
                        //setResult(Arrays.toString(mRead_buffer).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
//                try {
//                    if (read() < 0) {
//                        setStatus(Test_Status.read_error);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }

    }

    public int read() throws IOException {
        int size;
        if (mFileInputStream == null) {
            setStatus(Test_Status.read_error);
            return -1;
        }
        byte[] mRead_buffer = new byte[64];
        size = mFileInputStream.read(mRead_buffer);
        return size;
    }

    public interface FileCallBack {
        /**
         * Open the data received from the serial port
         * @param result
         * @param size
         */
        void getResult(byte[] result, int size);

    }

//    public interface ReceiverCallBack {
//        void onReceive(byte[] result,int size);
//
//    }
    private void printLog(String message) {
    mBaseLogClass.printLog(message);
    }
}

