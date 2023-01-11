package com.cardlan.twoshowinonescreen;

import java.io.File;
import java.io.FileOutputStream;

/**
 * cardlan dev operation util, this  final  class has used in {@link CardLanStandardBus},
 * it's initialized in {@link CardLanStandardBus#CardLanStandardBus()}.
 * @author zhoushenghua
 */
public final class CardLanDevUtil {

    //Sets the file path to which the parameter is written.
    private static final String procpath        = "/proc/gpio_set/rp_gpio_set";
    //Parameters that control the sound of a buzzer.
    private static final String open_bee_voice  = "c_24_1_1";
    private static final String close_bee_voice = "c_24_1_0";
    //Control the parameters of Red_LED and other switches
    private static final String open_led_red    = "d_26_0_1";
    private static final String close_led_red   = "d_26_0_0";
    //Control the parameters of Green_LED and other switches
    private static final String open_led_green  = "c_5_1_1";
    private static final String close_led_green = "c_5_1_0";

    private String writeProc(String path, byte[] buffer) {
        try {
            if (path == null || "".equals(path)) {
                path = procpath;
            }
            File             file = new File(path);
            FileOutputStream fos  = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "write error!";
        }
        return (buffer.toString());
    }

    /**
     * open buzzer
     */
    void openBuzzer() {
        writeProc(procpath, open_bee_voice.getBytes());
    }

    /**
     * close buzzer
     */
    void closeBuzzer() {
        writeProc(procpath, close_bee_voice.getBytes());
    }

    /**
     * open RedLED
     */
    void openRedLED() {
        writeProc(procpath, open_led_red.getBytes());
    }

    /**
     * close RedLED
     */
    void closeRedLED() {
        writeProc(procpath, close_led_red.getBytes());
    }

    /**
     * open GreenLED
     */
    void openGreenLED() {
        writeProc(procpath, open_led_green.getBytes());
    }

    /**
     * close GreenLED
     */
    void closeGreenLED() {
        writeProc(procpath, close_led_green.getBytes());
    }
}
