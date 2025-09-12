package cn.zfizwy.wechatservice.config;

/**
 * 微信应用的APP ID和APP SECRET常量定义。
 * 这些常量用于标识和验证应用程序的身份，对安全性要求较高。
 * 请不要将这些值暴露在公开的代码仓库中。
 */
public class Constant {
    // 微信应用的APP ID，用于标识应用。
    public static final String APP_ID = "wx6636e48407d0083e";

    // 微信应用的APP SECRET，用于验证应用的身份。
    public static final String APP_SECRET = "fb9e37378417517d5cb0dc9474fca851";

    /**
     * 商户号
     */
    public static String MERCHANT_ID = "1696171815";
    /**
     * 商户API私钥路径
     */
//    public static String PRIVATE_KEY_PATH = "C:\\Users\\FengXue\\Desktop\\MyDemo\\MinDa\\MinDaBackCloud\\key\\1696171815_20241025_cert\\apiclient_key.pem";
    public static String PRIVATE_KEY_PATH = "/opt/key/1696171815_20241025_cert/apiclient_key.pem";
    /**
     * 商户证书序列号
     */
    public static String MERCHANT_SERIAL_NUMBER = "1B45AF181F05EBC7FE9879737663AAAA1E3099EB";
    /**
     * 商户APIV3密钥
     */
    public static String API_V3_KEY = "FengXue8888888888888888888888888";

    /**
     * 通知地址
     */
    public  static  String NOTIFY_URL = "https://github.com/FengXueCode/MinDaBackEnd";
}
