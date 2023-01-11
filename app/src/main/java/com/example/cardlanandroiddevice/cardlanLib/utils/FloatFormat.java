package com.example.cardlanandroiddevice.cardlanLib.utils;


/**
 * 浮点类型的帮助工具类
 * <p>
 * Created by zhoushenghua on 18-6-22.
 *
 * @author zhoushenghua
 */

public class FloatFormat {


    /**
     * 根据基数,保留小数点. 支持正负整数
     * <p>
     * 比如value = 199, pointNumber = 2, 那么返回的字符串为"1.99".
     *
     * @param value       目标除数
     * @param pointNumber 基于10进制的小数点位数,
     * @return String  格式化后的字符串
     */
    public static String formatInt(int value, int pointNumber) {
        //处理不需要保留小数点的情况
        if (pointNumber <= 0) {
            return String.valueOf(value);
        }
        //处理负整数
        if (value < 0) {
            String valueStr = String.valueOf(value);
            return "-" + formatInt(Integer.parseInt(valueStr.substring(1)), pointNumber);
        }
        //处理正整数
        int divider = 1;
        for (int i = 0; i < pointNumber; i++) {
            divider = 10 * divider;
        }

        int headvalue = value / divider;
        int tailValue = value % divider;

        StringBuilder sb = new StringBuilder();

        sb.append(headvalue);
        sb.append(".");

        int zerolength = String.valueOf(divider).length() - String.valueOf(tailValue).length
                () - 1;

        for (int i = 0; i < zerolength; i++) {
            sb.append(0);
        }

        sb.append(tailValue);

        return sb.toString();
    }

    /**
     * 格式化，两个小数点
     *
     * @param value 　被格式化的数据
     * @return String 格式化后的字符串
     */
    public static String formatIntDefaultTwoPoint(int value) {

        return formatInt(value, 2);
    }

    /**
     * 格式化，两个小数点
     *
     * @param intValue 　被格式化的数据
     * @return String 格式化后的字符串
     */
    public static String formatIntDefaultTwoPoint(String intValue) {
        try {
            int value = Integer.parseInt(intValue);
            return formatInt(value, 2);

        } catch (Exception e) {
            UtilLog.debugOnConsole(FloatFormat.class, "请输入正确的int 数值");
            return null;
        }
    }
}
