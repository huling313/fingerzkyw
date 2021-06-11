package cn.org.bjca.finger.value;

public class ConstanceValue {

    public final static String FINGERPRINT_SUCCESS_CODE = "0";//指纹识别成功
    public final static String FINGERPRINT_CANCEL_CODE = "1";//取消指纹识别
    public final static String FINGERPRINT_NO_CODE = "2";//跳过指纹识别

    public final static int FINGERPRINT_REQUEST_CODE = 1000;//调转中科指纹页面请求码
    public final static int FINGERPRINT_RESULT_CODE = 1001;//页面返回码
    public final static int FINGERPRINT_RESULT_IGNORE_CODE = 1002;//跳过指纹返回码
    public final static int FINGERPRINT_RESULT_CANCEL_CODE = 1003;//取消指纹返回码

}
