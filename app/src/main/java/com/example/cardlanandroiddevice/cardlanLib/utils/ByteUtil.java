package com.example.cardlanandroiddevice.cardlanLib.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * byte 操作工具类
 *
 * @author zhoushenghua
 */
public class ByteUtil {

    /**
     * 两个byte 数组叠加. 将 desBytes 添加到 srcBytes
     *
     * @param srcBytes
     * @param desBytes
     * @return byte[]
     */
    public static byte[] addBytes(byte srcBytes, byte desBytes) {

        // copy array
        byte[] returnArray = new byte[2];
        returnArray[0] = srcBytes;
        returnArray[1] = desBytes;

        return returnArray;
    }
    /**
     * 两个byte 数组叠加. 将 desBytes 添加到 srcBytes
     *
     * @param srcBytes 被添加的byte s数组
     * @param desBytes 　添加的byte 数组
     * @return byte[]　返回添加后的数组
     */
    public static byte[] addBytes(byte[] srcBytes, byte[] desBytes) {
        if (!notNull(srcBytes) && !notNull(desBytes)) {
            // no array to add
            return null;
        }
        if (notNull(srcBytes) && !notNull(desBytes)) {
            return srcBytes;
        }

        if (!notNull(srcBytes) && notNull(desBytes)) {
            return desBytes;
        }
        // copy array
        byte[] returnArray = new byte[srcBytes.length + desBytes.length];
        System.arraycopy(srcBytes, 0, returnArray, 0, srcBytes.length);
        System.arraycopy(desBytes, 0, returnArray, srcBytes.length, desBytes.length);
        return returnArray;
    }

    /**
     * 将 一个desByte 添加到 srcBytes
     *
     * @param srcBytes 　被添加的byte 数组
     * @param desByte  　添加的byte 数组
     * @return byte[]　返回添加后的数组
     */
    public static byte[] addBytes(byte[] srcBytes, byte desByte) {
        byte[] desByteArray = new byte[]{desByte};
        return addBytes(srcBytes, desByteArray);
    }

    /**
     * 将一个byte 插入到 byte数组中
     *
     * @param srcBytes 　被插入的byte 数组
     * @param desByte  　插入的byte
     * @param index    建议 index 小于等于srcBytes 的长度，如果大于，那么直接在后面添加
     * @return byte[]　返回被插入后的数组
     */
    public static byte[] insertByte(byte[] srcBytes, byte desByte, int index) {
        if (!notNull(srcBytes)) {
            //此时表示源数组为null，那么直接创建一个数组并且返回
            return new byte[]{desByte};
        }
        byte[] desByteArray = new byte[]{desByte};
        int srcLength = srcBytes.length;
        if (srcLength <= index) {
            //直接后面插入
            return addBytes(srcBytes, desByteArray);
        } else {
            // copy array
            byte[] returnArray = new byte[srcBytes.length + 1];
            System.arraycopy(srcBytes, 0, returnArray, 0, index);
            System.arraycopy(desByteArray, 0, returnArray, index, desByteArray.length);
            System.arraycopy(srcBytes, index, returnArray, index + 1, srcLength - index);
            return returnArray;
        }
    }

    /**
     * 将一个 desBytes 数组 插入到 srcBytes数组中
     *
     * @param srcBytes 　被插入的byte 数组
     * @param desBytes 　插入的byte数组
     * @param index    建议 index 小于等于srcBytes 的长度，如果大于，那么直接在后面添加
     * @return byte[]　返回被插入后的数组
     */
    public static byte[] insertBytes(byte[] srcBytes, byte[] desBytes, int index) {
        if (!notNull(srcBytes) && !notNull(desBytes)) {
            // no array to add
            return null;
        }
        if (notNull(srcBytes) && !notNull(desBytes)) {
            return srcBytes;
        }

        if (!notNull(srcBytes) && notNull(desBytes)) {
            return desBytes;
        }
        int srcLength = srcBytes.length;
        if (srcLength <= index) {
            //直接后面插入
            return addBytes(srcBytes, desBytes);
        } else {
            // copy array
            byte[] returnArray = new byte[srcBytes.length + desBytes.length];
            System.arraycopy(srcBytes, 0, returnArray, 0, index);
            System.arraycopy(desBytes, 0, returnArray, index, desBytes.length);
            System.arraycopy(srcBytes, index, returnArray, index + desBytes.length, srcLength -
                    index);
            return returnArray;
        }
    }

    /**
     * 判断数组不为null 或者长度不为0
     *
     * @param bytes 　源
     * @return return true,if byte array is not null;
     */
    public static boolean notNull(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 此方法只计算从后面往前的byte null，直到没有null。
     *
     * <p><br> 不支持中间的null 字符移除操作<br> if you want to remove all null
     * common, you can use {@link #removeAllNullBytes(byte[])} for that
     *
     * @param bytes 　源
     * @return byte[]　返回被删除NULL的字节数组
     */
    public static byte[] removeNULLByte(byte[] bytes) {
        if (notNull(bytes)) {
            // 如果第一个都为null byte 的话,后面的数据不做,由于后期有关于00 发送的数据，因此此处屏蔽
//            if (bytes[0] == 0) {
//                return null;
//            }

            int copyEndIndex = 0;
            //此for 循环 只寻找到当前byte 最后一个 byte 不为null的索引即可
            for (int i = bytes.length - 1; i >= 0; i--) {
                if (bytes[i] == 0) {
                    continue;
                } else {
                    copyEndIndex = i;
                    break;
                }
            }
            if (copyEndIndex > 0) {
                // copy and return,
                //由于需要 包含最后那个，因此需要 + 1
                byte[] returnBytes = new byte[copyEndIndex + 1];
                System.arraycopy(bytes, 0, returnBytes, 0, copyEndIndex + 1);
                return returnBytes;
            }

        }
        return null;
    }

    /**
     * 判断一个数组是否为0数组
     *
     * @param bytes 　源数组
     * @return boolean return true ,if all byte is 0
     */
    public static boolean byteArrayIsNull(byte[] bytes) {
        if (!notNull(bytes)) {
            return true;
        }
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 字节取反(~),他跟reverse不一样．
     * @param src　源
     * @return byte 取反的结果
     */
    public static byte naByte(byte src) {
        return (byte) ~src;
    }

    /**
     * 字节数组取反(~)
     * @param src　源
     * @return byte[] 返回的结果集
     */
    public static byte[] naBytes(byte[] src) {
        byte[] returnBytes = null;
        if (notNull(src)) {
            returnBytes = new byte[src.length];
            for (int i = 0; i < src.length; i++) {
                returnBytes[i] = naByte(src[i]);
            }
        }
        return returnBytes;
    }



    /**
     * 此方法只计算从后面往前的byte 0xff，直到没有0xff。
     * <p><br> 不支持中间的0xff 字符移除操作<br>
     *
     * @param bytes 源
     * @return byte[]　返回移除0xFF 后的数组
     */
    public static byte[] removeFFByte(byte[] bytes) {
        if (notNull(bytes)) {
            int copyEndIndex = 0;
            byte lastFF = (byte) 0xff;
            //此for 循环 只寻找到当前byte 最后一个 byte 不为0xff的索引即可
            for (int i = bytes.length - 1; i < bytes.length; i--) {
                if (bytes[i] == lastFF) {
                    continue;
                } else {
                    copyEndIndex = i;
                    break;
                }
            }
            if (copyEndIndex > 0) {
                // copy and return,
                //由于需要 包含最后那个，因此需要 + 1
                byte[] returnBytes = new byte[copyEndIndex + 1];
                System.arraycopy(bytes, 0, returnBytes, 0, copyEndIndex + 1);
                return returnBytes;
            }

        }
        return null;
    }

    /**
     * 移除byte数组后面的 null 字符，按照等份去寻找。
     * <p><br> 不支持中间的null 字符移除操作<br> if you want to remove all null
     * item, you can use {@link #removeAllNullBytes(byte[])} for that
     *
     * @param bytes            　源
     * @param equalPartsNumber 每等份的数量
     * @return byte[]　返回移除后的数组
     */
    public static byte[] removeNULLByte(byte[] bytes, int equalPartsNumber) {
        int copyCopyEndIndex = getLastNotNullIndex(bytes, 0, equalPartsNumber, bytes.length);
        UtilLog.debugOnConsole(ByteUtil.class, "the copy size is " + copyCopyEndIndex);
        if (copyCopyEndIndex > 0) {
            //必须要包含复制索引那个值,因此+1
            byte[] returnBytes = new byte[copyCopyEndIndex + 1];
            System.arraycopy(bytes, 0, returnBytes, 0, copyCopyEndIndex + 1);
            return returnBytes;
        }
        return null;
    }

    /**
     * 按照等份寻找　最后一个不为null字节的索引
     *
     * @param bytes            源
     * @param startIndex       开始索引
     * @param equalPartsNumber 　等份
     * @param loopNumber       　要遍历的字节数量
     * @return int 返回不为null 字节的索引
     */
    private static int getLastNotNullIndex(byte[] bytes, int startIndex, int equalPartsNumber,
                                           int loopNumber) {
        if (notNull(bytes)) {
            if (startIndex >= bytes.length) {
                //起始遍历点大于数组的长度，则直接返回null
                return 0;
            }

            // 需要 循环的等份数量。
            // for example， the loopNumber is 10, the equalPartsNumber is 5, so the forPartNumber
            // is 2, if the equalPartsNumber is 4, the forPartNumber is 2
            int forPartNumber = loopNumber / equalPartsNumber;
            int remain = loopNumber % equalPartsNumber;

            if (forPartNumber <= 0) {
                //表示无法在继续分段了，那么就直接 循环剩下的数据了
                //查找不为 null 的索引值
                int copyEndIndex = startIndex;
                for (int i = 0; i < loopNumber; i++) {
                    if (bytes[i + startIndex] != 0) {
                        copyEndIndex = i + startIndex;
                        continue;
                    } else {
                        break;
                    }
                }
                return copyEndIndex;
            }

            //由于 forPartNumber 的计算方式，所以这里取byte值时，是一定不会溢出的
            for (int i = 0; i < forPartNumber; i++) {

                if (bytes[startIndex + equalPartsNumber - 1] == 0) {
                    return getLastNotNullIndex(bytes, startIndex, equalPartsNumber,
                            equalPartsNumber - 1);
                } else {
                    startIndex = ((i + 1) * equalPartsNumber) - 1;
                }
                if (i == forPartNumber - 1) {
                    if (remain > 0) {
                        return getLastNotNullIndex(bytes, startIndex, equalPartsNumber,
                                remain);
                    }
                }

            }

        }
        return 0;
    }

    /**
     * 移除byte数组中，所有的null 字符。
     * <p><br> 支持中间的null 字符移除
     *
     * @param bytes 源
     * @return byte[] 返回移除所有null字节的数组
     */
    public static byte[] removeAllNullBytes(byte[] bytes) {
        if (notNull(bytes)) {
            //当前新数组 非null的索引值
            int newArrayIndex = 0;
            byte[] copyBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] != 0) {
                    copyBytes[newArrayIndex] = bytes[i];
                    newArrayIndex++;
                }
            }
            return removeNULLByte(copyBytes);
        }
        return null;
    }

    /**
     * ASCII 转化成 hex string for example , "0" ascii is 30 , so convert to string is "30"
     *
     * @param str 需要被转化的　字符
     * @return String　返回转化ascii 后的hex String
     */
    public static String asciiToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString();
    }

    /**
     * byte array 转化成　hex String
     *
     * @param bytes 源
     * @return String　hex
     */
    public static String byteArrayToHexArray(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (notNull(bytes)) {
            for (int i = 0; i < bytes.length; i++) {
                sb.append(byteToHexString(bytes[i]));
            }
//            UtilLog.debugOnConsole(ByteUtil.class,"the hex array is " + sb.toString());
        } else {
            UtilLog.debugOnConsole(ByteUtil.class, new Exception("the common array is null"));
        }
        return sb.toString();
    }

    /**
     * 16进制字符串转化成 ascii 码， ascii 即我们常用的string 。
     *
     * @param hex hex 字符
     * @return String　ascii 字符
     */
    public static String hexStringToASCII(String hex) {

        StringBuilder sb   = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }

    /**
     * hex 字符串转化成 int 值.
     *
     * <p><br> 字符串最大值"7fffffff"
     *
     * @param hex 最大长度为8，也就是对应int 四个字节,但是不能超过最大值 0x7fffffff
     * @return if return -1 , it means the hex is not correct
     */
    public static int hexToInt(String hex) {
        int decimal = -1;
        if (hex != null && !"".equals(hex)) {
            decimal = Integer.parseInt(hex, 16) & 0xff;
        }
        return decimal;
    }


    /**
     * copy byte 数组
     *
     * @param srcBytes 被copy 的字节数组
     * @param start    　开始位置
     * @param length   　长度
     * @return byte[] 返回拷贝后的数组
     */
    public static byte[] copyBytes(byte[] srcBytes, int start, int length) {
        if (notNull(srcBytes)) {
            if (start >= 0 && length > 0) {
                if (length <= srcBytes.length - start) {
                    //copy array
                    byte[] returnBytes = new byte[length];
                    System.arraycopy(srcBytes, start, returnBytes, 0, length);
                    return returnBytes;
                }
            }
        }
        return null;
    }

    /**
     * copy (srcBytes) 数组 到某个指定的数组(desBytes)中. 这里要注意 srcBytes 的长度 + start 必须小于等于 desBytes的长度
     *
     * @param srcBytes 拷贝的数组
     * @param desBytes 目的存放数组
     * @param start    第二个参数（desBytes）的起始位置,
     */
    public static void copyBytes(byte[] srcBytes, byte[] desBytes, int start) {
        if (!notNull(srcBytes)) {
            UtilLog.debugOnConsole(ByteUtil.class, "没有拷贝的数组");
            return;
        }
        if (!notNull(desBytes)) {
            UtilLog.debugOnConsole(ByteUtil.class, "目标为null");
            return;
        }
        if ((srcBytes.length + start) > desBytes.length) {
            UtilLog.debugOnConsole(ByteUtil.class, "拷贝的数组与起始位置 太长");
            return;
        }
        System.arraycopy(srcBytes, 0, desBytes, start, srcBytes.length);
    }

    /**
     * copy all byte 数组
     *
     * @param srcBytes 　源
     * @return byte[]　返回copy 的字节数组
     */
    public static byte[] copyAllBytes(byte[] srcBytes) {
        return copyBytes(srcBytes, 0, srcBytes.length);
    }

    /**
     * byte 数组，自我异或。 . every item xor 0xff. 比如 0x01 它的异或 是 0xFE
     *
     * @param bytes 　源
     * @return byte[] 异或后的字节数组
     * @throws Exception 异常 the byte array is null
     */
    public static byte[] xorItself(byte[] bytes) throws Exception {
        if (!notNull(bytes)) {
            throw new Exception("the byte array is null");
        }
        byte[] returnBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            returnBytes[i] = (byte) (bytes[i] ^ 0xff);
        }
        return returnBytes;
    }

    /**
     * byte xor 0xff 。 比如 0x01 它的异或 是 0xFE
     *
     * @param bte 源
     * @return byte　return xor byte
     * @throws Exception 类型转化失败
     */
    public static byte xorItself(byte bte) throws Exception {
        return (byte) (bte ^ 0xff);
    }

    /**
     * xor与 byte 数组 异或。 从第一个到最后最后一个进行异或操作.
     *
     * @param bytes 源
     * @param xor   xor int param
     * @return int　xor result
     * @throws Exception byte array is null
     */
    public static int xor(byte[] bytes, int xor) throws Exception {
        if (!notNull(bytes)) {
            throw new Exception("the byte array is null");
        }

        int xorInt = xor;
        for (int i = 0; i < bytes.length; i++) {
            xorInt = xorInt ^ bytes[i];
        }
        return xorInt;
    }

    /**
     * xor与 byte 数组 异或。 从第一个到最后最后一个进行异或操作.
     *
     * @param bytes 源
     * @param xor   byte param
     * @return int  xor result
     * @throws Exception byte array is null
     */
    public static int xor(byte[] bytes, byte xor) throws Exception {
        if (!notNull(bytes)) {
            throw new Exception("the byte array is null");
        }

        int xorInt = xor;
        for (int i = 0; i < bytes.length; i++) {
            xorInt = xorInt ^ bytes[i];
        }
        return xorInt;
    }

    /**
     * 无符号计算整数，低位在左，高位在右. 如果byte 数组的长度大于４，那么只会取前面的４个字节
     *
     * @param bytes 源
     * @return int　result
     */
    public static int symbolBytesToInt(byte[] bytes) {
        int returnV = 0;
        if (notNull(bytes)) {
            int fori = bytes.length > 4 ? 4 : bytes.length;
            for (int i = 0; i < fori; i++) {
                int add = bytes[i] << ((i + 1) * 2);
                returnV = returnV + add;
            }
        }
        return returnV;
    }

    /**
     * 判断校验字是否合法
     *
     * @param hexData hex 类型的字符串
     * @param hexCode 被匹配的hex字符串
     * @return boolean　return true if xor result equal hex code
     */
    public static boolean verifyCode(String hexData, String hexCode) {
        if (hexData == null || hexData.length() <= 0) {
            return false;
        }
        int fori = hexData.length() % 2 == 0 ? hexData.length() / 2 : (hexData.length() / 2) + 1;
        int xor = 0;
        for (int i = 0; i < fori; i++) {
            xor = xor ^ hexToInt(hexData.substring(2 * i, 2 * i + 2));
//            xor = xor ^ Integer.parseInt(Cards.substring(2*i,2*i+2));
        }
        return xor == hexToInt(hexCode);
    }

    /**
     * 创建　校验字符
     *
     * @param bytes 　byte 数组
     * @param cmd   　命令
     * @return int value
     */
    public static int createCode(byte[] bytes, int cmd) {

        if (!notNull(bytes)) {
            throw new NullPointerException(" the bytes is null");
        }
        return createCode(new String(bytes), cmd);
    }

    /**
     * xor 操作获得校验字
     *
     * @param hexData byte 数组
     * @param cmd     命令
     * @return int value
     */
    public static int createCode(String hexData, int cmd) {
        if (hexData == null || hexData.length() <= 0) {
            throw new NullPointerException("Cards is null");
        }
        int fori = hexData.length() % 2 == 0 ? hexData.length() / 2 : (hexData.length() / 2) + 1;
        int xor = cmd;
        for (int i = 0; i < fori; i++) {
            xor = xor ^ hexToInt(hexData.substring(2 * i, 2 * i + 2));
        }
        return xor;
    }

    /**
     * 将一个小于 255 的 int 转化成 byte
     * 此方法已经过时，不建议使用．
     *
     * @param value int
     * @return byte result
     * @throws Exception bigger than 255
     */
    public static byte intToByte(int value) throws Exception {
        if (value > 255) {
            throw new Exception("can not convert int value to byte when int value is greater than" +
                    " 255");
        }
        return (byte) value;
    }

    /**
     * 将一个 int 转化成 byte
     *
     * @param value int
     * @return byte value
     */
    public static byte intToByteTwo(int value) {
        int byteint = value & 0xff;
        return new Integer(byteint).byteValue();
    }

    /**
     * int 类型的字符串转化成char
     *
     * @param intStr int 类型的字符
     * @return char　value
     */
    public static char intStringToChar(String intStr) {
        if (!ByteUtil.notNull(intStr)) {
            intStr = "0";
        }
        int ivalue = Integer.parseInt(intStr);
        byte reByte = new Integer(ivalue).byteValue();
        return (char) reByte;
    }

    /**
     * 长度为2的十六进制 类型的字符串转化成char
     *
     * @param hexStr hex string
     * @return char value
     */
    public static char hexStringToChar(String hexStr) {
        if (!ByteUtil.notNull(hexStr)) {
            hexStr = "00";
        }
        byte reByte = hexStringToByte(hexStr);
        return (char) reByte;
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param b byte[] 需要转换的字节数组
     * @return String 十六进制字符串
     */
    public static String byteArrayToHex(byte[] b) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs   = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param b 需要转换的字节
     * @return String 十六进制字符串
     */
    public static final String byteToHex(byte b) {
        StringBuilder sb   = new StringBuilder();
        String        stmp = Integer.toHexString(b & 0xff);
        if (stmp.length() == 1) {
            sb.append("0").append(stmp);
        } else {
            sb.append(stmp);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 将一个byte 转化成 十六进制的 字符串
     *
     * @param bte 　被转化的字节
     * @return String 用大写表示
     */
    public static String byteToHexString(byte bte) {
//     * 此方法依赖了{@link DatatypeConverter#printHexBinary(byte[])}
        String hexString = byteToHex(bte);
//        String hexString = DatatypeConverter.printHexBinary(new byte[]{bte});
        return hexString.toUpperCase();
    }

    /**
     * 将一个int 转化成 十六进制的 字符串
     *
     * @param intValue 　被转化的　int
     * @return String　hex
     */
    public static String intToHexString(int intValue) {
        String hexString = null;
        try {
            hexString = byteArrayToHexString(intToByteArray(intValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexString;
    }

    /**
     * 将一个int 转化成 十六进制的 字符串
     *
     * @param intValue 　被转化的　int
     * @return String　hex
     */
    public static String intToHexStringSingle(int intValue) {
        String hexString = null;
        try {
            hexString = byteToHexString(intToByte(intValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexString;
    }

    /**
     * 判断list 不为null
     *
     * @param list 源
     * @return boolean value
     */
    public static boolean notNull(List<?> list) {
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 产生校验字，hex string
     *
     * @param hexData hex 类型的字符串
     * @param hexCmd  hex 类型的字符串
     * @return 返回 十六进制(HEX)类型的字符串
     */
    public static String createCode(String hexData, String hexCmd) {
        int xor = createCode(hexData, hexToInt(hexCmd));
        try {
            return byteToHexString(intToByteTwo(xor));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * int 转byte 数组 高位在前，低位在后
     *
     * @param res 被转化的int值
     * @return byte[]　字节长度为4 的数组
     */
    public static byte[] intToByteArray(int res) {
        byte[] targets = new byte[4];

        targets[3] = (byte) (res & 0xff);// 最低位
        targets[2] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[1] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[0] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * byte 数组 转 int 高位在前，低位在后。
     *
     * @param b 　byte 数组
     * @return int　value 如果长度大于４，那么直接返回0
     */
    public static int byteArrayToIntHighToLow(byte[] b) {
//        byte[] a = new byte[4];
//        int i = a.length - 1, j = b.length - 1;
//        for (; i >= 0; i--, j--) {//从b的尾部(即int值的低位)开始copy数据
//            if (j >= 0)
//                a[i] = b[j];
//            else
//                a[i] = 0;//如果b.length不足4,则将高位补0
//        }
//        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
//        int v1 = (a[1] & 0xff) << 16;
//        int v2 = (a[2] & 0xff) << 8;
//        int v3 = (a[3] & 0xff);
        int returnValue = 0;
        if (notNull(b)) {
            if (b.length > 4) {
                return returnValue;
            }
            for (int k = 0; k < b.length; k++) {
                int leftOffset = (b.length - 1 - k) * 8;
                returnValue = returnValue + ((b[k] & 0xff) << leftOffset);
            }
        }
        return returnValue;
//        return v0 + v1 + v2 + v3;
    }

    /**
     * byte 数组 转 int ，低位在前，高位在后。
     *
     * @param b byte 数组
     * @return int value 如果长度大于４，那么直接返回0
     */
    public static int byteArrayToIntLowToHigh(byte[] b) {

        int returnValue = 0;
        if (notNull(b)) {
            if (b.length > 4) {
                //数组太大，直接返回0
                return returnValue;
            }
            for (int i = 0; i < b.length; i++) {
                int leftOffset = i * 8;
                returnValue = returnValue + ((b[i] & 0xff) << leftOffset);
            }
        }
        return returnValue;
    }

    /**
     * byte 转 int,无符号的转
     *
     * @param b 被转化的byte 字节
     * @return int　返回值
     */
    public static int byteToInt(byte b) {
        return b & 0xff;
    }

    /**
     * int 转 char，注意这里只取第一个char
     *
     * @param val 　被转化的int值
     * @return char　转化后的value
     */
    public static char intToChar(int val) {
        return (char) (val & 0xff);
    }

    /**
     * hexStr 转 char，注意 这个是单个char 的字符串
     *
     * @param hexStr 单个字节的hex 字符
     * @return char 默认0x00;
     */
    public static char intToChar(String hexStr) {
        if (notNull(hexStr)) {
            byte hesByte = new Integer(hexToInt(hexStr)).byteValue();
//            return hesByte;
        }
        return 0x00;
    }

    /**
     * 反转byte 数组
     *
     * @param srcBytes 　源
     * @return byte[]　反转后的数组
     */
    public static byte[] reverseByteArray(byte[] srcBytes) {
        if (notNull(srcBytes)) {
            byte[] returnBytes = new byte[srcBytes.length];
            for (int i = srcBytes.length - 1; i >= 0; i--) {
                returnBytes[returnBytes.length - 1 - i] = srcBytes[i];
            }
            return returnBytes;
        }
        return null;
    }

    /**
     * hex string 反转
     *
     * @param hexStr 　被反转的hex string
     * @return String 反转后的hex
     */
    public static String reverseHexString(String hexStr) {
        if (notNull(hexStr)) {
            int           fori = hexStr.length() / 2;
            StringBuilder sb   = new StringBuilder();
            for (int i = fori - 1; i >= 0; i--) {
                sb.append(hexStr.substring(2 * i, 2 * i + 2));
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 判断字符不为null 或者长度不为0
     *
     * @param string 需要判断的字符
     * @return boolean　返回值
     */
    public static boolean notNull(String string) {
        if (string != null && string.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断map不为null 或者长度不为0
     *
     * @param map 需要判断的　map 集合
     * @return boolean　返回值
     */
    public static boolean notNull(Map map) {
        if (map != null && map.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 将十六进制的字符串，转化为普通字符串。
     *
     * @param hexStr 　被转化的hex string
     * @return String 转化后的　string
     */
//     * 依赖了{@link DatatypeConverter#parseHexBinary(String)}方法，因此需要导入此包，否则不能编译通过
    public static String hexStringToString(String hexStr) {
        if (notNull(hexStr)) {
//            byte[] hexBytes = DatatypeConverter.parseHexBinary(hexStr);
            byte[] hexBytes = hexStringToByteArray(hexStr);
            return new String(hexBytes);
        }
        return null;
    }

    /**
     * 十六进制串转化为byte数组
     *
     * @param hex 被转化的hex string
     * @return the array of byte
     */
    public static final byte[] hexStringToByteArray(String hex)
            throws IllegalArgumentException
    {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap    = "" + arr[i++] + arr[i];
            int    byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    /**
     * 长度为2的十六进制串转化为单byte
     *
     * @param hex 被转化的hex string,长度为２
     * @return the array of byte
     */
    public static final byte hexStringToByte(String hex)
            throws IllegalArgumentException
    {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }

        int byteint = Integer.parseInt(hex, 16) & 0xFF;
        byte reByte = new Integer(byteint).byteValue();
        return reByte;
    }

    /**
     * 将byte 数组 转化成 hex string
     *
     * @param src 　byte 数组
     * @return String　hex string
     */
    public static String byteArrayToHexString(byte[] src) {
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

    /**
     * char 数组转化成byte 数组
     *
     * @param chars 被转化的　char 数组
     * @return byte[]　转后的byte 数组
     */
    public static byte[] charArrayToByteArray(char[] chars) {
        //由于注释的方法在处理负数的字节会有问题，因此不建议使用
//        Charset cs = Charset.forName("UTF-8");
//        CharBuffer cb = CharBuffer.allocate(chars.length);
//        cb.put(chars);
//        cb.flip();
//        ByteBuffer bb = cs.encode(cb);
//        return removeNULLByte(bb.array());
        byte[] reBytes = null;
        if (notNull(chars)) {
            reBytes = new byte[chars.length];
            for (int i = 0; i < chars.length; i++) {
                reBytes[i] = (byte) chars[i];
            }
        }
        return reBytes;
    }

    /**
     * char 字符比如'a' 转化成byte，//由于方法在处理负数的字节会有问题，因此不建议使用 你可以用{@link #charToOxByte(char)} 代替
     *
     * @param chr 　要转化的　char
     * @return byte[] 转后的byte数组　不支持负数
     */
    public static byte charToByte(char chr) {
        Charset    cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(1);
        cb.put(chr);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array()[0];
    }

    /**
     * 判断　char　数组不为null
     *
     * @param objs char数组
     * @return boolean　返回值
     */
    public static boolean notNull(char[] objs) {
        if (objs != null && objs.length > 0) {
            return true;
        }
        return false;
    }

    /**
     * byte 数组转化成 char 数组，不支持负数的转化,建议不使用
     *
     * @param bytes 　被转化的数组，不支持负数的转化
     * @return char[]　转后的数组
     */
    public static char[] byteArrayToCharArray(byte[] bytes) {
        //由于注释的方法在处理负数的字节会有问题，因此采用直接转化
//        Charset cs = Charset.forName("UTF-8");
//        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
//        bb.put(bytes);
//        bb.flip();
//        CharBuffer cb = cs.decode(bb);
//        return cb.array();
        char[] reChars = null;
        if (notNull(bytes)) {
            reChars = new char[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                reChars[i] = (char) bytes[i];
            }
        }
        return reChars;
    }

    /**
     * byte 转化成 char 'a'，次方法转化负数的时候会有问题，因此不建议使用
     *
     * @param bte 　转化的byte 字节
     * @return char　转化后的char
     */
    public static char byteToChar(byte bte) {
        Charset    cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.put(bte);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array()[0];
    }

    /**
     * byte 转化成 char(0x)
     *
     * @param bte 转化的字节
     * @return char　转化后的char
     */
    public static char byteToOxChar(byte bte) {
        return (char) bte;
    }

    /**
     * byte 转化成 char
     *
     * @param car 转化的char 字符
     * @return byte　转化后的字符
     */
    public static byte charToOxByte(char car) {
        return (byte) car;
    }

    /**
     * 比较　两个字符类
     *
     * @param one 　第一个匹配的字符
     * @param two 　第二个匹配的字符
     * @return return true　if equals
     */
    public static boolean compareString(String one, String two) {
        if (!notNull(one) && !notNull(two)) {
            return true;
        }
        if (!notNull(one) || !notNull(two)) {
            return false;
        }
        return one.equals(two);
    }
    /**
     * 比较　两个 Byte 数组
     *
     * @param oneBytes 　第一个匹配的字符
     * @param twoBytes 　第二个匹配的字符
     * @return return true　if equals
     */
    public static boolean compareBytes(byte[] oneBytes, byte[] twoBytes) {

        return Arrays.equals(oneBytes, twoBytes);
    }

    /**
     * 替换数组中的某个数组
     * @param srcBytes the src bytes, which will be replace from startIndex to sum(startIndex,
     *                 length of param new bytes).
     * @param newBytes the new bytes
     * @param startIndex where start replace
     * @return boolean return true if replace success
     */
    public static boolean replaceBytes(byte[] srcBytes, byte[] newBytes, int startIndex) {
        if (!notNull(srcBytes)) {
            printLog( "the src bytes is null");
            return false;
        }
        if (!notNull(newBytes)) {
            printLog( "the new bytes is null, nothing to replace");
            return false;
        }
        if ((newBytes.length + startIndex) > srcBytes.length) {
            printLog( "replace length is bigger than src bytes length");
            return false;
        }
//        printLog("before replace :" + byteArrayToHex(srcBytes));
        System.arraycopy(newBytes, 0, srcBytes, startIndex, newBytes.length);
//        printLog("after replace :" + byteArrayToHex(srcBytes));
        return true;
    }

    /**
     * 打印日志
     * @param msg　日志消息
     */
    private static void printLog(String msg) {
        UtilLog.debugOnConsole(ByteUtil.class, msg);
    }


}
