package com.cardlan.twoshowinonescreen;

/**
 * 设备操作和卡操作相关常量
 * @author zhoushenghua
 */
public class DeviceCardConfig {

    /**
     * Card reset　方法获取卡序列号的数组大小 (value  = 30)
     */
    public static final int CARD_RESET_SIZE = 16;
    /**
     * 初始化设备成功时　返回的状态码 (value  = 0)
     */
    public static final int INIT_DEVICE_STATUS_SUCCESS = 0;
    /**
     * Card reset　方法 成功返回的m1 卡状态码 (value  = 8)
     */
    public static final int CARD_RESET_STATUS_MONE_SUCCESS = 8;
    /**
     * Card reset　方法 成功返回的cpu 卡状态码 (value  = 32)
     */
    public static final int CARD_RESET_STATUS_CPU_SUCCESS = 32;
    /**
     * Card reset　方法 失败返回的m1 卡状态码 1　(value  = 1)
     */
    public static final int CARD_RESET_STATUS_MONE_ERROR_ONE = 1;
    /**
     * DES　算法得出后的密钥长度　(value  = 6)
     */
    public static final int DES_OUT_ARRAY_LENGTH = 6;
    /**
     * m1 卡　写卡成功状态　(value  = 5)
     */
    public static final int MONE_CARD_WRITE_SUCCESS_STATUS = 5;

}
