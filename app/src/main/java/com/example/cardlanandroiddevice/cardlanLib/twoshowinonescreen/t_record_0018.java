package com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen;

public class t_record_0018{
    public byte [] tradenumber = new byte[2];       //ED/EP联机或脱机交易序号
    public byte [] overdraftlimit = new byte[3];    //透支限额
    public byte [] trademoney = new byte[4];        //交易金额
    public byte tradetype;            //交易类型
    public byte [] deviceNO = new byte[6];          //终端机编号
    public byte [] tradedate = new byte[4];         //交易日期(终端)
    public byte [] tradetime = new byte[3];         //交易时间(终端)

    public int size(){
        return tradenumber.length + overdraftlimit.length + trademoney.length + 1 + deviceNO.length + tradedate.length + tradetime.length;
    }
}
