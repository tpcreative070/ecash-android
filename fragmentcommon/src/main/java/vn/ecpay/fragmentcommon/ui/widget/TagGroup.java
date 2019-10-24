package vn.ecpay.fragmentcommon.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import vn.ecpay.fragmentcommon.model.Tag;
import vn.ecpay.fragmentcommon.ui.actionbar.Theme;
import vn.ecpay.fragmentcommon.util.Utilities;

import java.util.List;

public class TagGroup extends ViewGroup {


    private int default_border_color = Color.rgb(0x49, 0xC1, 0x20);
    private int default_text_color = Color.rgb(0x49, 0xC1, 0x20);
    private int default_background_color = Color.WHITE;
    private int default_pressed_background_color = Color.rgb(0xED, 0xED, 0xED);
    private float default_border_stroke_width;
    private float default_text_size;
    private float default_horizontal_spacing;
    private float default_vertical_spacing;
    private float default_horizontal_padding;
    private float default_vertical_padding;

    private boolean isAppendMode;
    private int borderColor;
    private int textColor;
    private int backgroundColor;
    private int pressedBackgroundColor;
    private float borderStrokeWidth;
    private float textSize;
    private int horizontalSpacing;
    private int verticalSpacing;
    private int horizontalPadding;
    private int verticalPadding;
    private OnTagClickListener mOnTagClickListener;

    private InternalTagClickListener mInternalTagClickListener = new InternalTagClickListener();

    public TagGroup(Context context) {
        this(context, null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        horizontalSpacing = Utilities.dp(4);
        verticalSpacing = Utilities.dp(8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0;
        int rowWidth = 0;
        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) {
                    rowWidth = childWidth;
                    height += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = childHeight;
                    row++;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += horizontalSpacing;
            }
        }

        height += rowMaxHeight;

        height += getPaddingTop() + getPaddingBottom();

        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) {
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + horizontalSpacing;
            }
        }
    }

    protected TagView getInputTag() {
        if (isAppendMode) {
            final int inputTagIndex = getChildCount() - 1;
            final TagView inputTag = getTagAt(inputTagIndex);
            if (inputTag != null) {
                return inputTag;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


//    public void setTags(String... tags) {
//        removeAllViews();
//
//        for (String tag : tags) {
//            appendTag(tag);
//        }
//
//    }

    public void setTags(@NonNull List<Tag> tags) {
        removeAllViews();

        for (Tag tag : tags) {
            appendTag(tag);
        }

    }

    public void addTag(@NonNull Tag tag) {
        appendTag(tag);
    }

    protected TagView getTagAt(int index) {
        return (TagView) getChildAt(index);
    }

    protected void appendTag(Tag tag) {
        final TagView newTag = new TagView(getContext(), tag);
        newTag.setOnClickListener(mInternalTagClickListener);
        addView(newTag);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new TagGroup.LayoutParams(getContext(), attrs);
    }

    public void setOnTagClickListener(OnTagClickListener l) {
        mOnTagClickListener = l;
    }

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

    public class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    class InternalTagClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final TagView tagView = (TagView) v;
            if (!isAppendMode) {
                if (mOnTagClickListener != null) {
                    mOnTagClickListener.onTagClick(tagView.tag);
                }
            }
        }
    }

    class TagView extends AppCompatTextView {

        public Tag tag;

        public TagView(Context context, @NonNull Tag tag) {
            super(context);
            this.tag = tag;
            setText(tag.name);
            setPadding(Utilities.dp(8), Utilities.dp(4), Utilities.dp(8), Utilities.dp(4));
            setBackground(Theme.createCornerDrawable(tag.backgroundColor, Utilities.dp(4)));
            setTextColor(tag.textColor);
        }
    }
}