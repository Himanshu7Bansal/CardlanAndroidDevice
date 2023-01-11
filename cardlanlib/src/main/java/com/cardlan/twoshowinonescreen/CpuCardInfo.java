package com.cardlan.twoshowinonescreen;

public class CpuCardInfo {

    //-------交通一卡通卡电子现金应用 start ----------
    //0001 ~ 0004  支付应用专用文件

    //000B 消费交易明细文件

    //000C 圈存交易明细文件

    //0015 ~ 0019 发卡机构自定义文件

    //-------交通一卡通卡电子现金应用 end ----------


    //-------电子现金应用 和 电子钱包应用 公用文件
    //001A  联互通变长记录文件（0x1A）的交通信息记录
    public byte []         record_001A_1  = new byte[128];       //城市轨道应用信息记录
    public byte []         record_001A_2  = new byte[128];       //公共汽电车应用信息记录
    public byte []         record_001A_3  = new byte[128];       //城市水上客运应用信息记录
    public byte []         record_001A_4  = new byte[128];       //出租汽车应用信息记录
    public byte []record_001A_5= new byte[128];       //租赁汽车应用信息记录
    public byte []record_001A_6= new byte[128];       //公共自行车应用信息记录
    public byte []record_001A_7= new byte[112];       //停车收费应用信息记录
    public byte []record_001A_8= new byte[128];       //长途客运应用信息记录
    public byte []record_001A_9= new byte[128];       //轮渡应用信息记录
    public byte []record_001A_10= new byte[128];      //城际铁路应用信息记录
    public byte []record_001A_11= new byte[128];      //民航应用信息记录
    public byte []         record_001A_12 = new byte[128];      //高速公路收费应用信息记录
    public byte []         record_001A_13 = new byte[30];       //优惠信息记录
    public byte []         record_001A_14 = new byte[128];      //城铁应用信息记录
    public byte []         record_001A_15 = new byte[128];      //本规范预留记录2
    public byte []         record_001A_16 = new byte[128];      //本规范预留记录3
    public byte []         record_001A_17 = new byte[128];      //本规范预留记录4
    public byte []         record_001A_18 = new byte[128];      //本规范预留记录5
    //001E 互联互通循环记录文件
    public t_record_001E[] record_001E    = new t_record_001E[30];

    //共用余额文件
    public byte []balance = new byte[4] ;        // 余额

    //-------- 电子钱包应用 start ------------
    //0015 公共应用信息文件
    public byte []issuerlabel= new byte[8];         //发卡机构标识
    public byte apptypelabel;           //应用类型标识(01-只有ED,02-只有EP,03-ED和EP都有)
    public byte issuerappversion;       //发卡机构应用版本
    public byte []appserialnumber= new byte[10];    //应用序列号
    public byte []appstarttime= new byte[4];        //应用启用日期
    public byte []appendtime= new byte[4];          //应用截止日期
    public byte []FCI= new byte[2];                 //发卡机构自定义FCI数据

    //0016 持卡人基本信息文件
    public byte CardFlag;                               //卡类型标识
    public byte Bank_Employees;                        //本行职工标识
    public byte []Cardholder_Name= new byte[20];        //持卡人姓名
    public byte []Cardholder_ID_Number= new byte[32];  //持卡人证件号码
    public byte Cardholder_ID_type;                   //持卡人证件类型

    //0017 管理信息文件
    public byte []countrycode= new byte[4];        //国家代码
    public byte []provincecode= new byte[2];       //省级代码
    public byte []citycode= new byte[2];           //城市代码
    public byte []unioncardtype= new byte[2];      //互通卡种 0000-非互通卡，0001-互通卡
    public byte cardtype;                            //卡类型
    public byte []reserve_0017= new byte[49];      //预留
    //reserve_0017 预留部分 start
    public byte []settlenumber= new byte[4];       //结算单元编号
    //reserve_0017 预留部分 end


    //0018 交易明细文件
    public t_record_0018[] record_0018 = new t_record_0018[10];

    //0005 ~ 0008, 0019 发卡机构自定义文件
    //0005 应用控制信息文件
    public byte []RFU_1= new byte[2];             //RFU
    public byte []RFU_2= new byte[2];             //RFU
    public byte []industry_code= new byte[2];     //行业代码 BCD
    public byte []Card_Version= new byte[2];      //卡片版本号
    public byte appflag;              //应用类型标识 00未启用 01启用
    public byte Card_flag;            //卡类型标识位
    public byte []Connectivity_falg= new byte[2]; //互联互通标识（参与互通城市的标识）
    public byte []reserve_0005_1= new byte[8];    //预留
    public byte []reserve_0005_2= new byte[4];    //预留
    public byte []reserve_0005_3= new byte[4];    //预留
    public byte main_card_type;       //卡类型标识位 01普通 02学生 03老人 04测试 05军人 11成人月票 12老人免费 13老人优惠 14成人季票 15成人年票 70司机 80线路 81脱机采集
    public byte sub_card_type;        //卡子类型 预留
    public byte deposit;              //押金 HEX单位：元
    public byte []Date_Of_Annual_Survey= new byte[4];   //年检日期
    public byte []Business_bitmap= new byte[4];         //业务位图

    //0019 本地复合交易记录文件
    public byte [] record_0019_1= new byte[48];              //互联互通复合交易
    public byte [] record_0019_2= new byte[32];              //本地应用
    public byte [] record_0019_3= new byte[32];              //公共自行车专用
    public byte [] record_0019_4= new byte[64];              //预留
    public byte [] record_0019_5= new byte[16];              //预留
    public byte [] record_0019_6= new byte[16];              //预留
    public byte [] record_0019_7= new byte[32];              //预留
    public byte [] record_0019_8= new byte[32];              //公交专用
    public byte [] record_0019_9= new byte[48];              //互联互通复合交易


    //0006 预留信息文件
    public byte []reserve_0006= new byte[64];
    //-------- 电子钱包应用 end ------------

    //-------- 公交月票钱包应用 start-----------
    //0000 密钥文件
    //0002 电子钱包
    //0015 公共应用基本文件
    //0018 交易明细记录文件
    //0017 复合交易记录文件

    //-------- 公交月票钱包应用 end-----------

    //其他


    public CpuCardInfo() {

    }
}

