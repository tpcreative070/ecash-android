package vn.ecpay.ewallet.common.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import vn.ecpay.ewallet.ECashApplication;

public class CheckNetworkUtil {
    public static boolean isConnected(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi.isConnected() || mobile.isConnected();
    }
    public  boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ECashApplication.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
