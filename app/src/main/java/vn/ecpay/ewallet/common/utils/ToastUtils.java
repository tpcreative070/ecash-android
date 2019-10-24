package vn.ecpay.ewallet.common.utils;

import android.os.Looper;

import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import vn.ecpay.ewallet.R;
import vn.ecpay.fragmentcommon.BaseApplication;
import vn.ecpay.fragmentcommon.util.LayoutHelper;
import vn.ecpay.fragmentcommon.util.Utilities;


public class ToastUtils {

    public static final void showGenericError() {
        showMessage(R.string.msg_generic_error_message);
    }

    public static final void showMessage(@StringRes int resId) {
        if (BaseApplication.applicationContext != null) {
            showMessage(BaseApplication.applicationContext.getString(resId));
        }
    }

    public static final void showMessage(@NonNull String message) {
        Runnable runnable = () -> {
            if (BaseApplication.applicationContext != null) {
                LinearLayout view = new LinearLayout(BaseApplication.applicationContext);
                view.setOrientation(LinearLayout.VERTICAL);
                view.setPadding(Utilities.dp(16), Utilities.dp(8), Utilities.dp(16), Utilities.dp(8));
                view.setBackground(ThemeUtils.createCornerDrawable(0xccff7302, Utilities.dp(8)));
                TextView tvMessage = new TextView(BaseApplication.applicationContext);
                tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
                tvMessage.setTextColor(0xffffffff);
                tvMessage.setText(message);
                view.addView(tvMessage, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
                Toast toast = new Toast(BaseApplication.applicationContext);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            Utilities.runOnUIThread(runnable);
        }
    }
}
