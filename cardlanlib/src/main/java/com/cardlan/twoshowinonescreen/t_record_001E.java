package com.cardlan.twoshowinonescreen;

public class t_record_001E{
    byte  tradetype1E;             //交易类型
    byte [] deviceNO1E = new byte[8];           //终端编号
    byte [] tradeserialnumber = new byte[8];    //交易流水号
    byte [] trademoney1E = new byte[4];         //交易金额
    byte [] tradeaftermoney1E = new byte[4];    //交易后余额
    byte [] tradetime1E = new byte[7];          //交易日期时间
    byte [] acceptorcitycode = new byte[2];     //受理方城市代码
    byte [] acceptorissuerlabel = new byte[8];  //受理方机构标识
    byte [] reserve_001E = new byte[6];         //本规范预留
    public int size(){
        return 1+deviceNO1E.length + tradeserialnumber.length + trademoney1E.length + tradeaftermoney1E.length + tradetime1E.length + acceptorcitycode.length + acceptorissuerlabel.length + reserve_001E.length ;
    }
}
