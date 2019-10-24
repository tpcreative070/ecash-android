package vn.ecpay.fragmentcommon.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import vn.ecpay.fragmentcommon.util.LayoutHelper;

public class LoadingLayout extends FrameLayout {

    private RadialProgressView progressBar;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0x33000000);
        progressBar = new RadialProgressView(context);
        addView(progressBar, LayoutHelper.createFrame(48, 48, Gravity.CENTER));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
