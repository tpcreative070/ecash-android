package vn.ecpay.fragmentcommon;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import vn.ecpay.fragmentcommon.util.FileLog;
import vn.ecpay.fragmentcommon.util.ForegroundDetector;
import vn.ecpay.fragmentcommon.util.Utilities;

public class BaseApplication extends Application {

    public static Handler applicationHandler;
    public static Context applicationContext;
    public static volatile boolean isScreenOn;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationHandler = new Handler(Looper.getMainLooper());
        applicationContext = this;
        new ForegroundDetector(this);
        try {
            final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            final BroadcastReceiver mReceiver = new ScreenReceiver();
            applicationContext.registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PowerManager pm = (PowerManager) applicationContext.getSystemService(Context.POWER_SERVICE);
            isScreenOn = pm.isScreenOn();
            FileLog.d("screen state = " + isScreenOn);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Utilities.checkDisplaySize(applicationContext, newConfig);
    }
}
