package vn.ecpay.fragmentcommon.ui.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vn.ecpay.fragmentcommon.util.FileLog;

public class WorkaroundLinearLayoutManager extends LinearLayoutManager {
    public WorkaroundLinearLayoutManager(Context context) {
        super(context);
    }

    public WorkaroundLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WorkaroundLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (ArrayIndexOutOfBoundsException e) {
            FileLog.e(e);
        }
    }
}
