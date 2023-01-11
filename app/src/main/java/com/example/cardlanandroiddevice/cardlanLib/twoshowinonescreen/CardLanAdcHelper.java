package com.example.cardlanandroiddevice.cardlanLib.twoshowinonescreen;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CardLanAdcHelper {
    ICallback callback;
    private static final String ADCPATH = "/proc/adc/adc_ctrl";

    public CardLanAdcHelper(ICallback callback) {
        this.callback = callback;
        //获取ADC的值
        ReadAdcThread adcThread = new ReadAdcThread();
        adcThread.start();
    }

    private String readSDCardFile(String path) throws IOException {
        return readItems(path);
    }

    private class ReadAdcThread extends Thread {

        @Override
        public void run() {
            String value;

            while (true) {

                try {
                    value = readSDCardFile(ADCPATH);

                    {
                        value = filter1(value);
                        value = filter2(value);
                        value = filter3(value);
                        value = filter4(value);
                        value = filter5(value);
                    }

                    callback.value_change(value);
                    this.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 1、限幅滤波法（又称程序判断滤波法）
    //    A、方法：
    //    根据经验判断,确定两次采样允许的最大偏差值（设为A）
    //    每次检测到新值时判断：
    //    如果本次值与上次值之差<=A,则本次值有效
    //    如果本次值与上次值之差>A,则本次值无效,放弃本次值,用上次值代替本次值
    //    B、优点：
    //          能有效克服因偶然因素引起的脉冲干扰
    //    C、缺点
    //          无法抑制那种周期性的干扰
    //          平滑度差
    static int filter1_value = 0;

    String filter1(String new_value_str) {
//        int A = 500;
//        int new_value = Integer.parseInt(new_value_str);
//        if (((new_value - filter1_value) > A) || ((filter1_value - new_value) > A)) {
//            return String.valueOf(filter1_value);
//        }
        return new_value_str;
    }

    //2、中位值滤波法
    //    A、方法：
    //    连续采样N次（N取奇数）
    //    把N次采样值按大小排列
    //            取中间值为本次有效值
    //    B、优点：
    //    能有效克服因偶然因素引起的波动干扰
    //    对温度、液位的变化缓慢的被测参数有良好的滤波效果
    //    C、缺点：
    //    对流量、速度等快速变化的参数不宜
    String filter2(String adcStr) {
        return adcStr;
    }

    //3、算术平均滤波法
    //    A、方法：
    //    连续取N个采样值进行算术平均运算
    //    N值较大时：信号平滑度较高,但灵敏度较低
    //    N值较小时：信号平滑度较低,但灵敏度较高
    //    N值的选取：一般流量,N=12;压力：N=4
    //    B、优点：
    //    适用于对一般具有随机干扰的信号进行滤波
    //            这样信号的特点是有一个平均值,信号在某一数值范围附近上下波动
    //    C、缺点：
    //    对于测量速度较慢或要求数据计算速度较快的实时控制不适用
    //            比较浪费RAM
    String filter3(String adcStr) {
        return adcStr;
    }

    //4、递推平均滤波法（又称滑动平均滤波法）
    //    A、方法：
    //    把连续取N个采样值看成一个队列
    //            队列的长度固定为N
    //    每次采样到一个新数据放入队尾,并扔掉原来队首的一次数据.(先进先出原则)
    //    把队列中的N个数据进行算术平均运算,就可获得新的滤波结果
    //    N值的选取：流量,N=12;压力：N=4;液面,N=4~12;温度,N=1~4
    //    B、优点：
    //    对周期性干扰有良好的抑制作用,平滑度高
    //            适用于高频振荡的系统
    //    C、缺点：
    //    灵敏度低
    //            对偶然出现的脉冲性干扰的抑制作用较差
    //    不易消除由于脉冲干扰所引起的采样值偏差
    //            不适用于脉冲干扰比较严重的场合
    //    比较浪费RAM
    static int filter4_define_N = 6;
    List<String> filter4_value_buf = new ArrayList<String>();
    int filter4_i = 0;

    private String filter4(String new_value_str) {
        if (filter4_value_buf.size() == filter4_define_N) {
            filter4_value_buf.remove(filter4_i);
        }
        filter4_value_buf.add(filter4_i++, new_value_str);
        int count = 0;
        int sum = 0;
        if (filter4_i == filter4_define_N) {
            filter4_i = 0;
        }

        for (count = 0; count < filter4_value_buf.size(); count++) {
            int new_value = Integer.parseInt(filter4_value_buf.get(count));
            sum += new_value;
        }

        return String.valueOf(sum / filter4_value_buf.size());
    }

    //5、差值平均滤波法
    static int filter5_define_N = 10;       //10 * 2ms = 20ms
    List<Integer> filter5_value_buf = new ArrayList<Integer>();
    int filter5_i = 0;
    String filter5_old_value_str = "0"; //记录上一次值
    int filter5_value = 0; //返回值

    private String filter5(String new_value_str) {

        if (filter5_value_buf.size() == filter5_define_N) {
            filter5_value_buf.remove(filter5_i);
        }
        filter5_value_buf.add(filter5_i++, Math.abs(Integer.parseInt(filter5_old_value_str) - Integer.parseInt(new_value_str)));
        int count = 0;
        int sum = 0;
        if (filter5_i == filter5_define_N) {
            filter5_i = 0;
        }

        for (count = 1; count < filter5_value_buf.size(); count++) {
            int value = filter5_value_buf.get(count);
            sum += value;
        }
        filter5_old_value_str = new_value_str;
        int A = 10 * filter5_value_buf.size();
        if (sum < A && Math.abs(filter5_value - Integer.parseInt(new_value_str)) > 500) {
            filter5_value = Integer.parseInt(new_value_str);
        }

        return String.valueOf(filter5_value);
    }

    private String readItems(String logFile) {
        // Hard code adding some delay, to distinguish reading from memory and reading disk clearly
        //读取文件
        BufferedReader br = null;
        StringBuffer sb = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
            sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public interface ICallback {
        public void value_change(String value);
    }
}
