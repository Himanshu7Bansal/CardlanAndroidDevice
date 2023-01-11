package com.example.cardlanandroiddevice.cardlanLib.out.log;

/**
 * 这个是一个日志类库．
 *
 * @author zhoushenghua
 */
public class OutLog {

    /**
     * 设置是否可以在console中打印消息
     */
    private static boolean printOnConsole = true;
    /**
     * 设置是否需要将日志写入到文件
     */
    private static boolean writeLogIntoFile = false;
    /**
     * 写入日志文件的路径
     */
    private static String writeFilePath;

    /**
     * 输出日志到　console 中
     *
     * @param clazz 　当前类
     * @param msg   　日志消息
     */
    public static void debugOnConsole(Class<?> clazz, String msg) {
        if (printOnConsole) {
            System.out.println("[" + clazz.getSimpleName() + "] INFO : " + msg);
        }

    }

    /**
     * 输出异常到　console 中
     *
     * @param clazz 当前类
     * @param e     异常
     */
    public static void debugOnConsole(Class<?> clazz, Exception e) {
        if (printOnConsole) {
            e.printStackTrace();
        }

    }

    /**
     * 输出错误到　console 中
     *
     * @param clazz 当前类
     * @param msg   错误消息
     */
    public static void errorOnConsole(Class<?> clazz, String msg) {
        if (printOnConsole) {
            System.out.println("[" + clazz.getSimpleName() + "] ERROR : " + msg);
        }
    }

    /**
     * 设置是否可以在console中打印消息
     *
     * @param printOnConsole boolean 类型,if true , the method XXXOnConsole will print message on
     *                       console.
     */
    public static void setPrintOnConsole(boolean printOnConsole) {
        printOnConsole = printOnConsole;
    }

    /**
     * 设置是否需要将日志写入到文件
     *
     * @param writeLogIntoFile boolean 类型,if true, the log will write to file.
     */
    public static void setWriteLogIntoFile(boolean writeLogIntoFile) {
        writeLogIntoFile = writeLogIntoFile;
    }

    /**
     * 写入日志文件的路径
     *
     * @param writeFilePath the file path which will write
     */
    public static void setWriteFilePath(String writeFilePath) {
        writeFilePath = writeFilePath;
    }
}
