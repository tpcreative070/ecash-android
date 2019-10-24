package vn.ecpay.fragmentcommon.ui.actionbar;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.*;
import android.os.Build;
import vn.ecpay.fragmentcommon.util.Utilities;

public class Theme {

    public static final int ACTION_BAR_COLOR = 0xffffffff;
    public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 0x7f000000;
    public static final int ACTION_BAR_MEDIA_PICKER_COLOR = 0xff333333;
    public static final int ACTION_BAR_CHANNEL_INTRO_COLOR = 0xffffffff;
    public static final int ACTION_BAR_PLAYER_COLOR = 0xffffffff;
    public static final int ACTION_BAR_TITLE_COLOR = 0xffffffff;
    public static final int ACTION_BAR_SUBTITLE_COLOR = 0x80ffffff;
    public static final int ACTION_BAR_PROFILE_COLOR = 0xff598fba;
    public static final int ACTION_BAR_PROFILE_SUBTITLE_COLOR = 0xffd7eafa;
    public static final int ACTION_BAR_MAIN_AVATAR_COLOR = 0xff5085b1;
    public static final int ACTION_BAR_ACTION_MODE_TEXT_COLOR = 0xff737373;
    public static final int ACTION_BAR_SELECTOR_COLOR = 0xff406d94;

    public static final int INPUT_FIELD_SELECTOR_COLOR = 0xffd6d6d6;
    public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = 0xff3d3d3d;
    public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = 0x40ffffff;
    public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 0x2f000000;
    public static final int ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR = 0x2f000000;
    public static final int ACTION_BAR_MODE_SELECTOR_COLOR = 0xfff0f0f0;
    public static final int ACTION_BAR_BLUE_SELECTOR_COLOR = 0xff4981ad;
    public static final int ACTION_BAR_CYAN_SELECTOR_COLOR = 0xff39849d;
    public static final int ACTION_BAR_GREEN_SELECTOR_COLOR = 0xff48953d;
    public static final int ACTION_BAR_ORANGE_SELECTOR_COLOR = 0xffe67429;
    public static final int ACTION_BAR_PINK_SELECTOR_COLOR = 0xffd44e7b;
    public static final int ACTION_BAR_RED_SELECTOR_COLOR = 0xffbc4b41;
    public static final int ACTION_BAR_VIOLET_SELECTOR_COLOR = 0xff735fbe;
    public static final int ACTION_BAR_YELLOW_SELECTOR_COLOR = 0xffef9f09;

    public static final int ATTACH_SHEET_TEXT_COLOR = 0xff757575;

    public static final int DIALOGS_MESSAGE_TEXT_COLOR = 0xff8f8f8f;
    public static final int DIALOGS_NAME_TEXT_COLOR = 0xff4d83b3;
    public static final int DIALOGS_ATTACH_TEXT_COLOR = 0xff4d83b3;
    public static final int DIALOGS_PRINTING_TEXT_COLOR = 0xff4d83b3;
    public static final int DIALOGS_DRAFT_TEXT_COLOR = 0xffdd4b39;

    public static final int CHAT_UNREAD_TEXT_COLOR = 0xff5695cc;
    public static final int CHAT_ADD_CONTACT_TEXT_COLOR = 0xff4a82b5;

    public static final int SEARCH_COLOR_FILTER = 0xff333a42;

    public static final int BLACK_60 = 0x99000000;
    public static final int BLACK_50 = 0x80000000;
    public static final int TRANSPARENT = 0x00000000;
    public static final int WHITE = 0xffffffff;
    public static final int SCARLET = 0xffd0021b;
    public static final int SEPARATE_LINE = 0xffd9d9d9;

    public static final int TAG_STROKE = 0xffcccccc;
    public static final int TAG_TEXT_COLOR = 0xff4a4a4a;
    public static final int COVER_MASK = 0xdf1f3043;
    public static final int EMPTY_VIEW_TEXT_COLOR = 0xff4a4a4a;

    public static final int DEFAULT_BACKGROUND = 0xfff9f9fb;
    public static final int DETAIL_BACKGROUND = 0xccf9f9fb;
    //Color UI V2.0
    public static final int CTXT_0 = 0xffffffff;
    public static final int CTXT_01 = 0xff4a4a4a;
    public static final int CTXT_02 = 0xff9b9b9b;
    public static final int CTXT_03 = 0xb2ffffff;
    public static final int CM1 = 0xffd0011b;
    public static final int CLINE_01 = 0xff9b9b9b;
    public static final int CTXT_BTN01 = 0xff4a4a4a;
    public static final int CTXT_BTN02 = 0xffd0011b;
    public static final int CTXT_BTN03 = 0xffffffff;
    public static final int CBG_BTN01 = 0x00000000;//0xffffffff;
    public static final int CBG_BTN02 = 0xffd0011b;
    public static final int CBG_BTN03 = 0xcc4a4a4a;
    public static final int[] SOLID_COLORS = new int[]{0xffff8a80
            , 0xff8c9eff
            , 0xffffd600
            , 0xffbdbdbd
            , 0xffff80ab
            , 0xff00bfa5
            , 0xffb0bec5
            , 0xffea80fc
            , 0xff64b5f6
            , 0xff66bb6a
            , 0xfffb8c00
            , 0xff4dd0e1
            , 0xffd4e157
            , 0xffff8a65
            , 0xffb39ddb
            , 0xffbcaaa4,};
    private static Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static Drawable createBarSelectorDrawable(int color) {
        return createBarSelectorDrawable(color, true);
    }

    public static Drawable createBarSelectorDrawable(int color, boolean masked) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = null;
            if (masked) {
                maskPaint.setColor(0xffffffff);
                maskDrawable = new Drawable() {
                    @Override
                    public void draw(Canvas canvas) {
                        android.graphics.Rect bounds = getBounds();
                        canvas.drawCircle(bounds.centerX(), bounds.centerY(), Utilities.dp(18), maskPaint);
                    }

                    @Override
                    public void setAlpha(int alpha) {

                    }

                    @Override
                    public void setColorFilter(ColorFilter colorFilter) {

                    }

                    @Override
                    public int getOpacity() {
                        return PixelFormat.UNKNOWN;
                    }
                };
            }
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{new int[]{}},
                    new int[]{color}
            );
            return new RippleDrawable(colorStateList, null, maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{}, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public static ColorStateList getColorStateList(int color) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{new int[]{}},
                new int[]{color}
        );
        return colorStateList;
    }

    public static Drawable createCornerDrawable(int color, int radius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(radius);
        return gradientDrawable;
    }

}
