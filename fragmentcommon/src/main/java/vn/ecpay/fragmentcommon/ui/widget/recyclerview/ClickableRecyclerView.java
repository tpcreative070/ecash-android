package vn.ecpay.fragmentcommon.ui.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ClickableRecyclerView extends RecyclerView {

    private OnItemClickListener onItemClickListener;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                final int position = getChildAdapterPosition(v);
                final long id = getChildItemId(v);
                onItemClickListener.onItemClick(ClickableRecyclerView.this, v, position, id);
            }
        }
    };

    private RecyclerView.OnChildAttachStateChangeListener attachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (onItemClickListener != null) {
                view.setOnClickListener(clickListener);
            }

        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            if (onItemClickListener != null) {
                view.setOnClickListener(null);
            }
        }
    };

    public ClickableRecyclerView(Context context) {
        this(context, null);
    }

    public ClickableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnChildAttachStateChangeListener(attachListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
