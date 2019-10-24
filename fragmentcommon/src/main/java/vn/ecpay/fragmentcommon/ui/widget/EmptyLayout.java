package vn.ecpay.fragmentcommon.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import vn.ecpay.fragmentcommon.util.LayoutHelper;

public class EmptyLayout extends FrameLayout {

    private LinearLayout linearLayout;
    private ImageView icon;
    private TextView tvTitle;
    private TextView tvErrorMessage;

    public EmptyLayout(@NonNull Context context) {
        this(context, null);
    }

    public EmptyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        icon = new ImageView(context);
        icon.setVisibility(GONE);
        linearLayout.addView(icon, LayoutHelper.createLinear(80, 80, Gravity.CENTER_HORIZONTAL));

        tvTitle = new TextView(context);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setSingleLine();
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setVisibility(GONE);
        linearLayout.addView(tvTitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 24, 0, 0));

        tvErrorMessage = new TextView(context);
        tvErrorMessage.setTextColor(0xff555555);
        tvErrorMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f);
        tvErrorMessage.setGravity(Gravity.CENTER);
        linearLayout.addView(tvErrorMessage, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 16, 8, 16, 0));
    }

    public void setErrorMessage(String errorMessage) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(errorMessage);
        }
    }

    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
            tvTitle.setVisibility(!TextUtils.isEmpty(title) ? VISIBLE : GONE);
        }
    }

    public void setIcon(@DrawableRes int resId) {
        if (icon != null) {
            icon.setImageResource(resId);
            icon.setVisibility(resId != 0 ? VISIBLE : GONE);
        }
    }
}
