package vn.ecpay.fragmentcommon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import vn.ecpay.fragmentcommon.util.FileLog;

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            FileLog.d("screen off");
            BaseApplication.isScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            FileLog.d("screen on");
            BaseApplication.isScreenOn = true;
        }
    }
}
