package cn.org.bjca.finger;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author :Reginer in  2017/8/4 6:08.
 *         联系方式:QQ:282921012
 *         功能描述:上下电
 */
public class PowerUtils {
    /**
     * 通过配置文件上电.
     */
    public static void powerFingerOn() throws Exception {
        if (FileUtils.fileExists()) {
            Finger mFinger = new Gson().fromJson(FileUtils.getTextFromFile(), Finger.class);
            FingerGpio mFingerGpio = new FingerGpio(mFinger.getFinger().getPowerPath());
            if (mFinger.getFinger().getPowerType().equals("MAIN")) {
                mFingerGpio.powerOnDevice(getGpio(mFinger));
            } else {
                mFingerGpio.powerOnDeviceOut(getGpio(mFinger));
            }
        } else {
            throw new IOException("配置文件不存在");
        }
    }

    /**
     * 通过配置文件下电.
     */
    public static void powerFingerOff() throws IOException {
        if (FileUtils.fileExists()) {
            Gson gson = new Gson();
            Finger mFinger =gson.fromJson(FileUtils.getTextFromFile(), Finger.class);
            FingerGpio mFingerGpio = new FingerGpio(mFinger.getFinger().getPowerPath());
            if (mFinger.getFinger().getPowerType().equals("MAIN")) {
                mFingerGpio.powerOffDevice(getGpio(mFinger));
            } else {
                mFingerGpio.powerOffDeviceOut(getGpio(mFinger));
            }
        } else {
            throw new IOException("配置文件不存在");
        }
    }

    private static int[] getGpio(Finger finger) {
        int[] gpio = new int[0];
        List<Integer> mList = finger.getFinger().getGpio();
        for (int i = 0; i < mList.size(); i++) {
            gpio = Arrays.copyOf(gpio, gpio.length + 1);
            gpio[i] = mList.get(i);
        }
        return gpio;
    }
}
