package vn.ecpay.ewallet.common.utils;

public class SecurityNDK {
    static
    {
        System.loadLibrary("SecurityUtils");
    }
    public static native String getPrivateKey();
    public static native String getPublicKey();
}

