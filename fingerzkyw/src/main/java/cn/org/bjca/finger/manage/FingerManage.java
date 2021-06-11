package cn.org.bjca.finger.manage;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

import cn.org.bjca.finger.DeviceControlSpd;


/*************************************************************************************************
 * <pre>
 * @包路径： cn.org.bjca.trust.hospital.finger
 * @版权所有： 北京数字认证股份有限公司 (C) 2020
 *
 * @类描述:
 * @版本: V4.0.0
 * @作者 daizhenhong
 * @创建时间 2020-06-22 15:20
 *
 * @修改记录：
-----------------------------------------------------------------------------------------------
----------- 时间      |   修改人    |     修改的方法       |         修改描述   ---------------
-----------------------------------------------------------------------------------------------
</pre>
 ************************************************************************************************/
public class FingerManage {



    static public boolean enableGetFingerImage(Activity activity) {
        return true;
    }

    static public void closeFingerDevice() {
        try {
            DeviceControlSpd deviceControlSpd = new DeviceControlSpd(DeviceControlSpd.PowerType.NEW_MAIN, 74, 57, 16);
            deviceControlSpd.PowerOffDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定参数上电
     */
    static public void powerOnParams() {
        try {
            DeviceControlSpd deviceControlSpd = new DeviceControlSpd(DeviceControlSpd.PowerType.NEW_MAIN, 74, 57, 16);
            deviceControlSpd.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
