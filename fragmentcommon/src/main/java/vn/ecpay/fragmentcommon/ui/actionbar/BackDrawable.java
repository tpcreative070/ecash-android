package vn.ecpay.fragmentcommon.ui.actionbar;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import vn.ecpay.fragmentcommon.util.Utilities;


public class BackDrawable extends Drawable {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean reverseAngle = false;
    private long lastFrameTime;
    private boolean animationInProgress;
    private float finalRotation;
    private float currentRotation;
    private int currentAnimationTime;
    private boolean alwaysClose;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();

    public BackDrawable(boolean close) {
        super();
        paint.setColor(0xffffffff);
        paint.setStrokeWidth(Utilities.dp(2));
        alwaysClose = close;
    }

    public void setRotation(float rotation, boolean animated) {
        lastFrameTime = 0;
        if (currentRotation == 1) {
            reverseAngle = true;
        } else if (currentRotation == 0) {
            reverseAngle = false;
        }
        lastFrameTime = 0;
        if (animated) {
            if (currentRotation < rotation) {
                currentAnimationTime = (int) (currentRotation * 300);
            } else {
                currentAnimationTime = (int) ((1.0f - currentRotation) * 300);
            }
            lastFrameTime = System.currentTimeMillis();
            finalRotation = rotation;
        } else {
            finalRotation = currentRotation = rotation;
        }
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!Utilities.equalFloat(currentRotation, finalRotation)) {
            if (lastFrameTime != 0) {
                long dt = System.currentTimeMillis() - lastFrameTime;

                currentAnimationTime += dt;
                if (currentAnimationTime >= 300) {
                    currentRotation = finalRotation;
                } else {
                    if (currentRotation < finalRotation) {
                        currentRotation = interpolator.getInterpolation(currentAnimationTime / 300.0f) * finalRotation;
                    } else {
                        currentRotation = 1.0f - interpolator.getInterpolation(currentAnimationTime / 300.0f);
                    }
                }
            }
            lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }

        int rD = (int) ((117 - 255) * currentRotation);
        int c = Color.rgb(255 + rD, 255 + rD, 255 + rD);
        paint.setColor(c);

        canvas.save();
        canvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
        float rotation = currentRotation;
        if (!alwaysClose) {
            canvas.rotate(currentRotation * (reverseAngle ? -225 : 135));
        } else {
            canvas.rotate(135 + currentRotation * (reverseAngle ? -180 : 180));
            rotation = 1.0f;
        }
        canvas.drawLine(-Utilities.dp(7) - Utilities.dp(1) * rotation, 0, Utilities.dp(8), 0, paint);
        float startYDiff = -Utilities.dp(0.5f);
        float endYDiff = Utilities.dp(7) + Utilities.dp(1) * rotation;
        float startXDiff = -Utilities.dp(7.0f) + Utilities.dp(7.0f) * rotation;
        float endXDiff = Utilities.dp(0.5f) - Utilities.dp(0.5f) * rotation;
        canvas.drawLine(startXDiff, -startYDiff, endXDiff, -endYDiff, paint);
        canvas.drawLine(startXDiff, startYDiff, endXDiff, endYDiff, paint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return Utilities.dp(24);
    }

    @Override
    public int getIntrinsicHeight() {
        return Utilities.dp(24);
    }
}
