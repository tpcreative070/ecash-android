package vn.ecpay.fragmentcommon.ui.widget.recyclerview;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public interface OnItemClickListener {
    void onItemClick(RecyclerView recyclerView, View view, int position, long id);
}
