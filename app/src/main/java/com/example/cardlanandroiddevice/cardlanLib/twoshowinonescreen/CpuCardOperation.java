package com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen;


import com.example.cardlanandroiddevice.cardlanLib.utils.ByteUtil;
import com.example.cardlanandroiddevice.cardlanLib.utils.UtilLog;


public class CpuCardOperation {

    private static volatile CpuCardOperation sInstance;

//    static {
//        System.loadLibrary("cardlan_StandardBus");//之前在build.gradle里面设置的so名字，必须一致
//    }

    public static CpuCardOperation getInstance() {
        if (sInstance == null) {
            synchronized (CpuCardOperation.class) {
                if (sInstance == null) {
                    sInstance = new CpuCardOperation();
                }
            }
        }
        return sInstance;
    }

    public CpuCardInfo read_CpuCardInfo() {
        CpuCardInfo info = new CpuCardInfo();
        byte[] buffer = new byte[2046*4];
        int len;
        int index = 0;
        len = new CardLanStandardBus().JTBCardread(buffer);
        if (len <= 0 ) {
            return null;
        }

        //-------电子现金应用 和 电子钱包应用 公用文件
        //001A  联互通变长记录文件（0x1A）的交通信息记录
        len = info.record_001A_1.length;
        info.record_001A_1 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_2.length;
        info.record_001A_2 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_3.length;
        info.record_001A_3 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_4.length;
        info.record_001A_4 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_5.length;
        info.record_001A_5 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_6.length;
        info.record_001A_6 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_7.length;
        info.record_001A_7 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_8.length;
        info.record_001A_8 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_9.length;
        info.record_001A_9 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_10.length;
        info.record_001A_10 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_11.length;
        info.record_001A_11 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_12.length;
        info.record_001A_12 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_13.length;
        info.record_001A_13 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_14.length;
        info.record_001A_14 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_15.length;
        info.record_001A_15 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_16.length;
        info.record_001A_16 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_17.length;
        info.record_001A_17 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len = info.record_001A_18.length;
        info.record_001A_18 = ByteUtil.copyBytes(buffer, index, len);
        index += len;

        //001E 互联互通循环记录文件
        for(int i  = 0 ; i < info.record_001E.length; i++) {
            info.record_001E[i] = new t_record_001E();
            len = 1;
            info.record_001E[i].tradetype1E = ByteUtil.copyBytes(buffer, index, len)[0];
            index += len;
            len =  info.record_001E[i].deviceNO1E.length;
            info.record_001E[i].deviceNO1E = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_001E[i].tradeserialnumber.length;
            info.record_001E[i].tradeserialnumber = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_001E[i].trademoney1E.length;
            info.record_001E[i].trademoney1E = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_001E[i].tradeaftermoney1E.length;
            info.record_001E[i].tradeaftermoney1E = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_001E[i].tradetime1E.length;
            info.record_001E[i].tradetime1E = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_001E[i].acceptorcitycode.length;
            info.record_001E[i].acceptorcitycode = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_001E[i].acceptorissuerlabel.length;
            info.record_001E[i].acceptorissuerlabel = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_001E[i].reserve_001E.length;
            info.record_001E[i].reserve_001E = ByteUtil.copyBytes(buffer, index, len);
            index += len;
        }

        //共用余额文件
        len =  4;
        info.balance = ByteUtil.copyBytes(buffer, index, len);
        index += len;

        //-------- 电子钱包应用 start ------------
        //0015 公共应用信息文件
        len =  info.issuerlabel.length;
        info.issuerlabel = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  1;
        info.apptypelabel = ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  1;
        info.issuerappversion = ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  info.appserialnumber.length;
        info.appserialnumber = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.appstarttime.length;
        info.appstarttime = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.appendtime.length;
        info.appendtime = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.FCI.length;
        info.FCI = ByteUtil.copyBytes(buffer, index, len);
        index += len;

        //0016 持卡人基本信息文件
        len =  1;
        info.CardFlag = ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  1;
        info.Bank_Employees = ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  info.Cardholder_Name.length;
        info.Cardholder_Name = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.Cardholder_ID_Number.length;
        info.Cardholder_ID_Number = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  1;
        info.Cardholder_ID_type = ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;

        //0017 管理信息文件
        len =  info.countrycode.length;
        info.countrycode = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.provincecode.length;
        info.provincecode = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.citycode.length;
        info.citycode = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.unioncardtype.length;
        info.unioncardtype = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  1;
        info.cardtype = ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  info.reserve_0017.length;
        info.reserve_0017 = ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.settlenumber.length;
        info.settlenumber = ByteUtil.copyBytes(buffer, index, len);
        index += len;

        //0018 交易明细文件
        for(int i  = 0 ; i < info.record_0018.length; i++) {
            info.record_0018[i] = new t_record_0018();
            len =  info.record_0018[i].tradenumber.length;
            info.record_0018[i].tradenumber = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_0018[i].overdraftlimit.length;
            info.record_0018[i].overdraftlimit = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_0018[i].trademoney.length;
            info.record_0018[i].trademoney = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  1;
            info.record_0018[i].tradetype = ByteUtil.copyBytes(buffer, index, len)[0];
            index += len;
            len =  info.record_0018[i].deviceNO.length;
            info.record_0018[i].deviceNO = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_0018[i].tradedate.length;
            info.record_0018[i].tradedate = ByteUtil.copyBytes(buffer, index, len);
            index += len;
            len =  info.record_0018[i].tradetime.length;
            info.record_0018[i].tradetime = ByteUtil.copyBytes(buffer, index, len);
            index += len;
        }

        //0005 ~ 0008, 0019 发卡机构自定义文件
        //0005 应用控制信息文件
        len =  info.RFU_1.length;
        info.RFU_1= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.RFU_2.length;
        info.RFU_2= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.industry_code.length;
        info.industry_code= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.Card_Version.length;
        info.Card_Version= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  1;
        info.appflag= ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  1;
        info.Card_flag= ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  info.Connectivity_falg.length;
        info.Connectivity_falg= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.reserve_0005_1.length;
        info.reserve_0005_1= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.reserve_0005_2.length;
        info.reserve_0005_2= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.reserve_0005_3.length;
        info.reserve_0005_3= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  1;
        info.main_card_type= ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  1;
        info.sub_card_type= ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  1;
        info.deposit= ByteUtil.copyBytes(buffer, index, len)[0];
        index += len;
        len =  info.Date_Of_Annual_Survey.length;
        info.Date_Of_Annual_Survey= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.Business_bitmap.length;
        info.Business_bitmap= ByteUtil.copyBytes(buffer, index, len);
        index += len;

        //0019 本地复合交易记录文件
        len =  info.record_0019_1.length;
        info.record_0019_1= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_2.length;
        info.record_0019_2= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_3.length;
        info.record_0019_3= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_4.length;
        info.record_0019_4= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_5.length;
        info.record_0019_5= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_6.length;
        info.record_0019_6= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_7.length;
        info.record_0019_7= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_8.length;
        info.record_0019_8= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        len =  info.record_0019_9.length;
        info.record_0019_9= ByteUtil.copyBytes(buffer, index, len);
        index += len;

        //0006 预留信息文件
        len =  info.reserve_0006.length;
        info.reserve_0006= ByteUtil.copyBytes(buffer, index, len);
        index += len;
        //-------- 电子钱包应用 end ------------

        UtilLog.debugOnConsole(this.getClass(), "buffer:----------start-------------");
        for(int i = 0; i < 50; i++) {
            UtilLog.debugOnConsole(this.getClass(),"buffer:"+ i + "\n"+ ByteUtil.byteArrayToHexString(ByteUtil.copyBytes(buffer, i*128, 128)));
        }
        UtilLog.debugOnConsole(this.getClass(),"buffer:----------end-------------");

        return info;
    }
}
