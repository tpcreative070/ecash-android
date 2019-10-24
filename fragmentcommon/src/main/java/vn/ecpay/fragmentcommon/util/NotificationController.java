package vn.ecpay.fragmentcommon.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import vn.ecpay.fragmentcommon.BaseApplication;

public class NotificationController {

    public static final String NOTIFICATION_CHANNEL_ID = "ECPayChannel";
    private static NotificationChannel notificationChannel;

    public static final void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        if (notificationChannel == null) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "ECPay", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            ((NotificationManager) BaseApplication.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }
    }

}
