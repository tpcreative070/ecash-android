package vn.ecpay.ewallet.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Bui on 7/6/2017.
 */

public class Utils {
  public static String parseDateToddMMyyyyHHmmss(String time) {
    if (time==null || time.equals(""))
      return "";
    String inputPattern = "yyyyMMdd HH:mm:ss";
    String outputPattern = "dd/MM/yyyy - HH:mm:ss";
    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

    Date date = null;
    String str = null;

    try {
      date = inputFormat.parse(time);
      str = outputFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return str;
  }
  public static boolean isOnline(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
    return (netInfo != null && netInfo.isConnected());
  }

  public static void isClick(final View view) {
    view.setEnabled(false);
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        view.setEnabled(true);
      }
    }, 1000);
  }

  private static void disable(ViewGroup layout, boolean isDisable) {
    layout.setEnabled(isDisable);
    for (int i = 0; i < layout.getChildCount(); i++) {
      View child = layout.getChildAt(i);
      if (child instanceof ViewGroup) {
        disable((ViewGroup) child, isDisable);
      } else {
        child.setEnabled(isDisable);
      }
    }
  }


  public static boolean isAppRunning(Context context) {
    final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
    if (procInfos != null) {
      for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
        if (processInfo.processName.equals(context.getPackageName())) {
          return true;
        }
      }
    }
    return false;
  }
}
