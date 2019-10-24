package vn.ecpay.ewallet.common.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import vn.ecpay.fragmentcommon.NotificationCenter;


public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && TextUtils.equals(intent.getAction(), "android.net.conn.CONNECTIVITY_CHANGE")) {
            NotificationCenter.getInstance().didNotificationReceive(5, null);
        }
    }
}
