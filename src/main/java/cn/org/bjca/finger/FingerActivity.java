package cn.org.bjca.finger;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.org.bjca.finger.model.SignSubmitModel;
import cn.org.bjca.finger.value.ConstanceValue;
import cn.org.bjca.finger.value.Constant;
import cn.org.bjca.finger.zkyw.R;

/**
 * 小板子
 */
public class FingerActivity extends Activity {
    private ImageView mImgView = null;
    private TextView tvClose = null;
    private TextView tv_tip = null;
    private ImageView imageClose = null;


    private TextView tv_restart_finger = null;
    private TextView tv_submit = null;
    private TextView tv_restart = null;
    private TextView tv_ignore = null;

    private LinearLayout lin_restart_finger;
    private RelativeLayout rl_error_finger;


    private Context mContext = null;
    private Bitmap mBitmap;
    private String fingerprintResultCode = null;
    private String handwrittenSignFile;
    // 5秒内没有采集到指纹，则展示出跳过采集的按钮
    private int mMaxHoldTime = 5 * 1000;

    private int mEveryGetImageTime = 3000;

    private MyHandler myHandler;

    private static final String TAG = FingerActivity.class.getSimpleName();

    private String mDeviceName = "";

    private ReaderCollection mReaders;
    private Reader mReader = null;

    private boolean mStop = false;

    int m_DPI = -1;

    private Thread mThread;

    public boolean isHandClose = false;

    private SignSubmitModel model = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_fingerprint);

        mContext = this.getApplicationContext();


        model = new SignSubmitModel();

        myHandler = new MyHandler(this);
        this.initViewAndData();

        mStop = false;
        opening = false;

        //进入页面就打开指纹采集功能
        openDeviceAndRequestDevice();
    }

    void initViewAndData() {

        handwrittenSignFile = getIntent().getStringExtra(Constant.HANDWRITTEN_SIGN_FILE);

        mImgView = findViewById(R.id.imageView);
        imageClose = findViewById(R.id.image_close);
        tvClose = findViewById(R.id.tv_close);
        tv_tip = findViewById(R.id.tv_tip);

        lin_restart_finger = findViewById(R.id.lin_restart_finger);
        tv_restart_finger = findViewById(R.id.tv_restart_finger);
        tv_submit = findViewById(R.id.tv_submit);

        tv_restart = findViewById(R.id.tv_restart);
        tv_ignore = findViewById(R.id.tv_ignore);

        rl_error_finger = findViewById(R.id.rl_error_finger);

        //点击dialog外部不消失
        this.setFinishOnTouchOutside(false);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//关闭页面不做任何操作

                if (mThread != null) {
                    mThread.interrupt();
                }

//                toast("关闭页面");
                isHandClose = true;
                fingerprintResultCode = ConstanceValue.FINGERPRINT_CANCEL_CODE;
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishActivity();
                    }
                }, 0);
                finishActivity();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳过指纹
                fingerprintResultCode = ConstanceValue.FINGERPRINT_NO_CODE;

                model.setHandwrittenSignFile(handwrittenSignFile);
                model.setSignFile(handwrittenSignFile);
                //及时中断，避免线程还在走显示红框
//                closeDevice();
                isHandClose = true;
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishActivity();
                    }
                }, 0);

            }
        });

//        tvClose.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setEnableClose();
//            }
//        }, mMaxHoldTime);

        tv_restart_finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//重新录用指纹

                mImgView.setImageResource(R.mipmap.finger_image);
                lin_restart_finger.setVisibility(View.GONE);
                tv_tip.setVisibility(View.VISIBLE);
                tvClose.setVisibility(View.VISIBLE);

                closeDevice();  //1、指纹录用错误时，需尝试关闭设备
                if (mThread != null) {
                    mThread = null;
                }
                openDeviceAndRequestDevice();
//                myHandler.obtainMessage(MyHandler.codeRestartFinger).sendToTarget();
            }
        });


        //提交签名
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageSuccess(mBitmap);
            }
        });

        tv_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳过指纹
                fingerprintResultCode = ConstanceValue.FINGERPRINT_NO_CODE;

                model.setHandwrittenSignFile(handwrittenSignFile);
                model.setSignFile(handwrittenSignFile);
                //及时中断，避免线程还在走显示红框
//                closeDevice();
                isHandClose = true;
                finishActivity();
            }
        });

        tv_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//错误之后重新录用指纹

                rl_error_finger.setVisibility(View.GONE);
                lin_restart_finger.setVisibility(View.GONE);
                mImgView.setVisibility(View.VISIBLE);
                mImgView.setImageResource(R.mipmap.finger_image);

                tv_tip.setVisibility(View.VISIBLE);
                tvClose.setVisibility(View.VISIBLE);

//                myHandler.obtainMessage(MyHandler.codeRestartFinger).sendToTarget();

                //进入页面就打开指纹采集功能
                openDeviceAndRequestDevice();
            }
        });


    }

    private void setEnableClose() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvClose.setVisibility(View.VISIBLE);
            }
        });
    }

    private void restartFinger() {
        opening = false;
        if (mReader != null) {
            try {
                mReader.Close();
            } catch (UareUException e) {
                e.printStackTrace();
            }
        }
        if (mThread != null) {
            mThread.interrupt();
        }
        openDeviceAndRequestDevice();
    }

    private void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean opening = false;

    public void openDeviceAndRequestDevice() {
        if (opening) {
            return;
        }
        opening = true;

        this.powerOnParams();

        myHandler.obtainMessage(MyHandler.GET_DEVICES).sendToTarget();

    }


    public String getDeviceName() {
        opening = false;
        try {
            mReaders = Globals.getInstance().getReaders(getApplicationContext());
        } catch (UareUException e) {
            e.printStackTrace();
            return "";
        }
        int size = mReaders.size();

//        toast("获取设备成功,size= " + size);

        if (size > 0) {
            mDeviceName = mReaders.get(0).GetDescription().name;
            registerUsbReceiver(mDeviceName);
            try {
                mReader = Globals.getInstance().getReader(mDeviceName, getApplicationContext());
                mReader.Open(Reader.Priority.EXCLUSIVE);
            } catch (UareUException e) {
                e.printStackTrace();
            }
            mReader = mReaders.get(0);
        } else {
            myHandler.postDelayed(myRunnable, 1800);
        }


        return "";

    }

    /**
     * 指定参数上电
     */
    public void powerOnParams() {
        try {
            Log.d(TAG, "准备上电: " + System.currentTimeMillis());
            DeviceControlSpd deviceControlSpd = new DeviceControlSpd(DeviceControlSpd.PowerType.NEW_MAIN, 74, 57, 16);
            deviceControlSpd.PowerOnDevice();
            Log.d(TAG, "上电完成: " + System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 指定参数下电
     */
    private void powerOffParams() {
        try {
            DeviceControlSpd deviceControlSpd = new DeviceControlSpd(DeviceControlSpd.PowerType.NEW_MAIN, 74, 57, 16);
            deviceControlSpd.PowerOffDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGetFinger() {

        if (mThread != null && mThread.isInterrupted()) {
            //处理中断逻辑
            mThread = null;
            return;
        }

        if (mThread == null) {
            mThread = new Thread(new MyRunnable());
            mThread.start();//线程启动
        }
    }

    int mFailureTime = 0;
    boolean deviceError = false;

    private void getFingerImage() {
        if (opening) {
            return;
        }
        getFingerImageDo();
    }

    private boolean doing = false;

    private void getFingerImageDo() {
        if (mReader == null) {
//            toast("mReader=====null");
            openDeviceAndRequestDevice();

        }
        if (doing) {
            return;
        }

        m_DPI = Globals.GetFirstDPI(mReader);
        if (m_DPI == -100) {
//            toast("设备检测失败！！！");
            deviceError = true;
        }
        Reader.CaptureResult imageResult = null;
        Long timeStart = System.currentTimeMillis();
        try {
            doing = true;
            imageResult = mReader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, m_DPI, -1);
//            imageResult = mReader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, m_DPI, 7000);
        } catch (UareUException e) {
            e.printStackTrace();
        }
        doing = false;
        Long timeStop = System.currentTimeMillis();

//        toast("获取时间：" + (timeStop - timeStart));
        final Reader.CaptureResult cap_result = imageResult;
        // an error occurred
        if (cap_result == null || cap_result.image == null) {

            mFailureTime += 1;

//            toast("获取失败,次数 = " + mFailureTime);

            if (!isHandClose) {

                closeDevice();  //1、指纹录用错误时，需尝试关闭设备
                if (mThread != null) {
                    mThread = null;
                }

                if (deviceError) {//设备异常需重新开启指纹（前提是关闭设备）

                    openDeviceAndRequestDevice();

                    deviceError = false;
                    return;
                }

                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myHandler.obtainMessage(MyHandler.MSG_FAILURE).sendToTarget();
                    }
                }, 2000);


            }
            isHandClose = false;

            return;
        }

        mBitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());
        mBitmap = ImageUtils.replaceBitmapColor(mBitmap, Color.argb(0, 233, 10, 10));

        //Log.e("test", "width" + mBitmap.getWidth() + "__aaa___height" + mBitmap.getHeight());  //width256  height360


        myHandler.obtainMessage(MyHandler.MSG_SUCCESS, mBitmap).sendToTarget();

    }

    void getImageSuccess(Bitmap bitmap) {
        mStop = true;
        if (bitmap == null) {
            fingerprintResultCode = ConstanceValue.FINGERPRINT_NO_CODE;
        } else {
            fingerprintResultCode = ConstanceValue.FINGERPRINT_SUCCESS_CODE;
            //手绘签名bitmap
            Bitmap signBitmap = BitmapFactory.decodeFile(handwrittenSignFile);
            //指纹签名bitmap，把指纹图片指定大小
            Bitmap fingerBitmap = ImageUtils.getNewImage(bitmap, 85, 120);
//            Bitmap fingerBitmap = bitmap;


            //拼接指纹和手绘图片
            Bitmap compoundBitmap = ImageUtils.combineImage(signBitmap, fingerBitmap);

            //手绘指纹图片转成file格式
            File signFiles = ImageUtils.compressImage(compoundBitmap, mContext);

            //压缩指纹图片，并转为file格式
            File fingerprintSignFile = ImageUtils.compressImage(fingerBitmap, mContext);

            model.setHandwrittenSignFile(handwrittenSignFile);
            model.setFingerprintSignFile(fingerprintSignFile.getAbsolutePath());
            model.setSignFile(signFiles.getAbsolutePath());

//            mImgView.setImageBitmap(compoundBitmap);
        }


//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        finishActivity();
//            }
//        }, 1000);
    }


    void closeDevice() {


        if (mReader != null) {
            try {
                mReader.CancelCapture();
                mReader.Close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }
    }


    public void finishActivity() {

        closeDevice();  //尝试关闭设备

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.SUBMIT_DATA, model);
        intent.putExtras(bundle);

        switch (fingerprintResultCode) {
            case ConstanceValue.FINGERPRINT_SUCCESS_CODE:
                setResult(ConstanceValue.FINGERPRINT_RESULT_CODE, intent);
                //成功
                break;
            case ConstanceValue.FINGERPRINT_CANCEL_CODE:
                //取消指纹采集
                setResult(ConstanceValue.FINGERPRINT_RESULT_CANCEL_CODE, intent);
                break;
            case ConstanceValue.FINGERPRINT_NO_CODE:
                //跳过采集
                setResult(ConstanceValue.FINGERPRINT_RESULT_IGNORE_CODE, intent);
                break;
        }

        Log.e("test", model.toString());


        this.finish();
    }

    @Override
    public void finish() {
        mStop = true;

        super.finish();
    }

    private void registerUsbReceiver(String deviceName) {
        PendingIntent mPermissionIntent;
        Context appContext = getApplicationContext();
        mPermissionIntent = PendingIntent.getBroadcast(appContext, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        appContext.registerReceiver(mUsbReceiver, filter);


        try {
            if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(appContext, mPermissionIntent,
                    deviceName)) {
                startGetFinger();
            }
        } catch (DPFPDDUsbException e) {
            e.printStackTrace();
        }
    }


    private static final String ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION";


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            try {
                                mReader.Open(Reader.Priority.EXCLUSIVE);
                            } catch (UareUException e) {
                                e.printStackTrace();
                                return;
                            }
                            startGetFinger();
                        }
                    } else {
                        toast("授权失败！");
                    }
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }
    }


    private static class MyHandler extends Handler {

        final static int codeGetImage = 1000;
        final static int codeRestartFinger = 1001;
        final static int GET_DEVICES = 1002;
        final static int DEVICES_EXCEPTION = 1003;

        final static int MSG_SUCCESS = 0;//获取图片成功的标识
        final static int MSG_FAILURE = 1;//获取图片失败的标识

        private final WeakReference<FingerActivity> mWeakRef;

        private MyHandler(FingerActivity activity) {
            mWeakRef = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            switch (msg.what) {
                case codeGetImage:
//                    mWeakRef.get().getFingerImage();
                    break;
                case codeRestartFinger:
                    mWeakRef.get().getFingerImage();
                    break;
                case MSG_SUCCESS:
                    mWeakRef.get().tv_tip.setVisibility(View.GONE);
                    mWeakRef.get().tvClose.setVisibility(View.GONE);
                    mWeakRef.get().lin_restart_finger.setVisibility(View.VISIBLE);
                    mWeakRef.get().mImgView.setImageBitmap((Bitmap) msg.obj);

                    break;

                case GET_DEVICES:
                    try {
                        mWeakRef.get().getDeviceName();
                    } catch (Exception e) {
                        mWeakRef.get().toast("获取");
                        e.printStackTrace();
                        mWeakRef.get().openDeviceAndRequestDevice();
                    }
                    break;
                case MSG_FAILURE:
                    mWeakRef.get().tv_tip.setVisibility(View.GONE);
                    mWeakRef.get().tvClose.setVisibility(View.GONE);
                    mWeakRef.get().mImgView.setVisibility(View.GONE);
                    mWeakRef.get().lin_restart_finger.setVisibility(View.GONE);
                    mWeakRef.get().rl_error_finger.setVisibility(View.VISIBLE);
                    break;
                case DEVICES_EXCEPTION:
                    mWeakRef.get().openDeviceAndRequestDevice();
                    break;
            }
        }
    }


    //执行相应的业务逻辑
    private final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                opening = false;
                getDeviceName();
            } catch (Exception e) {
                toast("获取");
                e.printStackTrace();
                openDeviceAndRequestDevice();
            }
        }
    };


    class MyRunnable implements Runnable {
        @Override
        public void run() {

//            toast("开始录入指纹");

            getFingerImage();
        }
    }

}
