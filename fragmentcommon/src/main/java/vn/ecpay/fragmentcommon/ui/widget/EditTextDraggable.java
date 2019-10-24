package vn.ecpay.fragmentcommon.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import androidx.appcompat.widget.AppCompatEditText;

public class EditTextDraggable extends AppCompatEditText {

    private boolean allowDrag = false;

    public EditTextDraggable(Context context) {
        super(context);
    }

    public EditTextDraggable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextDraggable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return allowDrag ? super.onDragEvent(event) : true;
    }
}
