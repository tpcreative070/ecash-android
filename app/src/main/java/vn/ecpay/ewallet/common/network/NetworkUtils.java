package vn.ecpay.ewallet.common.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.ToastUtils;
import vn.ecpay.fragmentcommon.BaseApplication;

public class NetworkUtils {

    public static final boolean isNetworkConnected(boolean showToast) {
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (!isConnected && showToast) {
            ToastUtils.showMessage(R.string.err_upload);
        }
        return isConnected;
    }

    public static final boolean isReachable() {
//        try {
//            return InetAddress.getByName(BuildConfig.HOSTV3).isReachable(10 * 1000);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return false;
    }
}
