package com.cardlan.out.log;


/**
 * 实现了日志打印的基本类 Created by zhoushenghua on 18-7-19.
 *
 * @author zhoushenghua
 */

public class BaseLogClass {

    private Class<?> mClazz;

    /**
     * 构造一个　mClazz 标签的日志类
     */
    public BaseLogClass() {
    }

    /**
     * 初始化　tag
     *　
     * @param mClazz 标签类
     */
    public BaseLogClass init(Class<?> mClazz) {
        this.mClazz = mClazz;
        return this;
    }

    /**
     * 打印　string 类型的日志信息
     *
     * @param exceptionMsg 日志信息
     */
    public BaseLogClass printLog(Exception exceptionMsg) {
        if (mClazz != null) {
            OutLog.errorOnConsole(mClazz, exceptionMsg.getMessage());
        } else {
            OutLog.errorOnConsole(this.getClass(), exceptionMsg.getMessage());
        }
        return this;
    }

    /**
     * 打印　string 类型的日志信息
     *
     * @param tag          标志
     * @param exceptionMsg 日志信息
     */
    public BaseLogClass printLog(String tag, Exception exceptionMsg) {
        if (mClazz != null) {
            OutLog.errorOnConsole(mClazz, tag + " -- >" + exceptionMsg.getMessage());
        } else {
            OutLog.errorOnConsole(this.getClass(), tag + " -- >" + exceptionMsg.getMessage());
        }
        return this;
    }

    /**
     * 打印　string 类型的日志信息
     *
     * @param logMsg 日志信息
     */
    public BaseLogClass printLog(String logMsg) {
        if (mClazz != null) {
            OutLog.debugOnConsole(mClazz, "String message :" + logMsg);
        } else {
            OutLog.debugOnConsole(this.getClass(), "String message :" + logMsg);
        }
        return this;
    }

    /**
     * 打印　string 类型的日志信息
     *
     * @param codeLineNumber 代码所在行，由于代码行是变化的，因此这个行数只能作为参考
     * @param logMsg         　日志信息
     */
    public BaseLogClass printLog(int codeLineNumber, String logMsg) {
        if (mClazz != null) {
            OutLog.debugOnConsole(mClazz, "code line [ " + codeLineNumber + "] String message :"
                    + logMsg);
        } else {
            OutLog.debugOnConsole(this.getClass(), "code line [ " + codeLineNumber + "] String " +
                    "message :" + logMsg);
        }
        return this;
    }

    /**
     * 打印　string 类型的日志信息
     *
     * @param tag 标志
     * @param logMsg         　日志信息
     */
    public BaseLogClass printLog(String tag, String logMsg) {
        if (mClazz != null) {
            OutLog.debugOnConsole(mClazz, "code line [ " + tag + "] String message :"
                    + logMsg);
        } else {
            OutLog.debugOnConsole(this.getClass(), "code line [ " + tag + "] String " +
                    "message :" + logMsg);
        }
        return this;
    }

    /**
     * 打印　byte 数组 类型的日志信息
     *
     * @param codeLineNumber 代码所在行,由于代码行是变化的，因此这个行数只能作为参考
     * @param combinationMsg 组合消息,组合在 param logMsgBytes 之前．
     * @param logMsg         日志信息
     */
    public BaseLogClass printLog(int codeLineNumber, String combinationMsg, String logMsg) {
        if (mClazz != null) {
            OutLog.debugOnConsole(this.getClass(), "code line [ " + codeLineNumber + "] byte " +
                    "array message:" + combinationMsg + "---> " + logMsg);
        } else {
            OutLog.debugOnConsole(mClazz, "code line [ " + codeLineNumber + "] byte array " +
                    "message:" + combinationMsg + "---> " + logMsg);
        }
        return this;
    }

    /**
     * 打印　int 类型的日志信息
     *
     * @param intLog 日志信息
     */
    public BaseLogClass printLog(int intLog) {
        if (mClazz != null) {
            OutLog.debugOnConsole(mClazz, "int message: " + String.valueOf(intLog));
        } else {
            OutLog.debugOnConsole(this.getClass(), "int message: " + String.valueOf(intLog));
        }
        return this;
    }

    /**
     * 打印　int 类型的日志信息
     *
     * @param codeLineNumber 代码所在行，由于代码行是变化的，因此这个行数只能作为参考
     * @param intLog         日志信息
     */
    public BaseLogClass printLog(int codeLineNumber, int intLog) {
        if (mClazz != null) {
            OutLog.debugOnConsole(mClazz, "code line [ " + codeLineNumber + "] int message: " +
                    String.valueOf(intLog));
        } else {
            OutLog.debugOnConsole(this.getClass(), "code line [ " + codeLineNumber + "] int " +
                    "message: " + String.valueOf(intLog));
        }
        return this;
    }

    /**
     * 打印　byte 数组 类型的日志信息
     *
     * @param logMsgBytes 日志信息
     */
    public BaseLogClass printLog(byte[] logMsgBytes) {
        if (mClazz != null) {
            OutLog.debugOnConsole(this.getClass(), "byte array message:" + byteArrayToHexString
                    (logMsgBytes));
        } else {
            OutLog.debugOnConsole(mClazz, "byte array message:" + byteArrayToHexString
                    (logMsgBytes));
        }
        return this;
    }

    /**
     * 打印　byte 数组 类型的日志信息
     *
     * @param codeLineNumber 代码所在行，由于代码行是变化的，因此这个行数只能作为参考
     * @param logMsgBytes    日志信息
     */
    public BaseLogClass printLog(int codeLineNumber, byte[] logMsgBytes) {
        if (mClazz != null) {
            OutLog.debugOnConsole(this.getClass(), "code line [ " + codeLineNumber + "] byte " +
                    "array message:" + byteArrayToHexString(logMsgBytes));
        } else {
            OutLog.debugOnConsole(mClazz, "code line [ " + codeLineNumber + "] byte array " +
                    "message:" + byteArrayToHexString(logMsgBytes));
        }
        return this;
    }

    /**
     * 打印　byte 数组 类型的日志信息
     *
     * @param codeLineNumber 代码所在行,由于代码行是变化的，因此这个行数只能作为参考
     * @param combinationMsg 组合消息,组合在 param logMsgBytes 之前．
     * @param logMsgBytes    日志信息
     */
    public BaseLogClass printLog(int codeLineNumber, String combinationMsg, byte[] logMsgBytes) {
        if (mClazz != null) {
            OutLog.debugOnConsole(this.getClass(), "code line [ " + codeLineNumber + "] byte " +
                    "array message:" + combinationMsg + "---> " + byteArrayToHexString
                    (logMsgBytes));
        } else {
            OutLog.debugOnConsole(mClazz, "code line [ " + codeLineNumber + "] byte array " +
                    "message:" + combinationMsg + "---> " + byteArrayToHexString
                    (logMsgBytes));
        }
        return this;
    }

    /**
     * 打印　byte 数组 类型的日志信息
     *
     * @param combinationMsg 组合消息,组合在 param logMsgBytes 之前．
     * @param logMsgBytes    日志信息
     */
    public BaseLogClass printLog(String combinationMsg, byte[] logMsgBytes) {
        if (mClazz != null) {
            OutLog.debugOnConsole(this.getClass(),
                                  "array message:" + combinationMsg + "---> " + byteArrayToHexString
                            (logMsgBytes));
        } else {
            OutLog.debugOnConsole(mClazz, " byte array " + "message:" + combinationMsg + "---> "
                    + byteArrayToHexString(logMsgBytes));
        }
        return this;
    }

    /**
     * 将byte 数组 转化成 hex string
     *
     * @param src 　byte 数组
     * @return String　hex string
     */
    private String byteArrayToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int    v  = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }


}
