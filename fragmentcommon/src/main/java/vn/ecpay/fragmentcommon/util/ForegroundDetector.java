package vn.ecpay.fragmentcommon.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import java.util.concurrent.CopyOnWriteArrayList;

@SuppressLint("NewApi")
public class ForegroundDetector implements Application.ActivityLifecycleCallbacks {

    private static ForegroundDetector Instance = null;
    private int refs;
    private boolean wasInBackground = true;
    private long enterBackgroundTime = 0;
    private CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    public ForegroundDetector(Application application) {
        Instance = this;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static ForegroundDetector getInstance() {
        return Instance;
    }

    public boolean isForeground() {
        return refs > 0;
    }

    public boolean isBackground() {
        return refs == 0;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++refs == 1) {
            if (System.currentTimeMillis() - enterBackgroundTime < 200) {
                wasInBackground = false;
            }
//            if (BuildVars.LOGS_ENABLED) {
//                FileLog.d("switch to foreground");
//            }
            for (Listener listener : listeners) {
                try {
                    listener.onBecameForeground();
                } catch (Exception e) {
//                    FileLog.e(e);
                }
            }
        }
    }

    public boolean isWasInBackground(boolean reset) {
        if (reset && Build.VERSION.SDK_INT >= 21 && (System.currentTimeMillis() - enterBackgroundTime < 200)) {
            wasInBackground = false;
        }
        return wasInBackground;
    }

    public void resetBackgroundVar() {
        wasInBackground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (--refs == 0) {
            enterBackgroundTime = System.currentTimeMillis();
            wasInBackground = true;
//            if (BuildVars.LOGS_ENABLED) {
//                FileLog.d("switch to background");
//            }
            for (Listener listener : listeners) {
                try {
                    listener.onBecameBackground();
                } catch (Exception e) {
//                    FileLog.e(e);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    public interface Listener {
        void onBecameForeground();

        void onBecameBackground();
    }
}
