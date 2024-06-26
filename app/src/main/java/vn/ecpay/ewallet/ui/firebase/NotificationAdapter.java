package vn.ecpay.ewallet.ui.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.notification.NotificationObj;
import vn.ecpay.ewallet.ui.callbackListener.OnDeleteItem;
import vn.ecpay.ewallet.ui.contact.adapter.ContactAdapter;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NotificationObj> notificationObjList;
    private WeakReference<Context> mContextWeakReference;
    private boolean isCheckBox;
    private OnDeleteItem onDeleteItem;

    NotificationAdapter(boolean mIsCheckBox, List<NotificationObj> mNotificationObjList, Context context, OnDeleteItem mOnDeleteItem) {
        this.notificationObjList = mNotificationObjList;
        this.mContextWeakReference = new WeakReference<>(context);
        this.isCheckBox = mIsCheckBox;
        this.onDeleteItem = mOnDeleteItem;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        SwipeLayout item = view.findViewById(R.id.swipe_item);
        item.setShowMode(SwipeLayout.ShowMode.PullOut);
        item.addDrag(SwipeLayout.DragEdge.Right, item.findViewById(R.id.layout_option_right));
        return new NotificationAdapter.ItemContactContentHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Context context = mContextWeakReference.get();
        if (context == null) {
            return;
        }
        NotificationAdapter.ItemContactContentHolder itemViewHolder = (NotificationAdapter.ItemContactContentHolder) holder;
        NotificationObj notification = notificationObjList.get(position);
        itemViewHolder.tvTitle.setText(notification.getTitle());
        itemViewHolder.tvBody.setText(notification.getBody());
        itemViewHolder.tvDate.setText(notification.getDate());

        if (notification.getRead().equals(Constant.ON)) {
            itemViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.black));
            itemViewHolder.tvBody.setTextColor(context.getResources().getColor(R.color.black));
            itemViewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            itemViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.gray));
            itemViewHolder.tvBody.setTextColor(context.getResources().getColor(R.color.gray));
            itemViewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.gray));
        }

//        if (isCheckBox) {
//            itemViewHolder.ivCheckBox.setVisibility(View.VISIBLE);
//        } else {
//            itemViewHolder.ivCheckBox.setVisibility(View.GONE);
//        }
        itemViewHolder.layoutContent.setOnClickListener(v -> {
            if (notification.getRead().equals(Constant.ON)) {
                DatabaseUtil.updateNotificationRead(context, Constant.OFF, notification.getId());
                EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_NOTIFICATION));
                notificationObjList.get(position).setRead(Constant.OFF);
                notifyItemChanged(position);
            }
        });

        itemViewHolder.layout_option_right.setOnClickListener(v -> {
            if (null != onDeleteItem) {
                DialogUtil.getInstance().showDialogConfirm(context, context.getResources().getString(R.string.str_dialog_notification_title),
                        context.getResources().getString(R.string.str_confirm_delete), new DialogUtil.OnConfirm() {
                            @Override
                            public void OnListenerOk() {
                                onDeleteItem.onDeleteOK(position);
                            }

                            @Override
                            public void OnListenerCancel() {
                            }
                        });
            }
        });
//
//        if (notification.getIsCheck() == 0) {
//            itemViewHolder.ivCheckBox.setChecked(false);
//        } else {
//            itemViewHolder.ivCheckBox.setChecked(true);
//        }
//
//        itemViewHolder.ivCheckBox.setOnClickListener(v -> {
//            if (itemViewHolder.ivCheckBox.isChecked()) {
//                notificationObjList.get(position).setIsCheck(1);
//            } else {
//                notificationObjList.get(position).setIsCheck(0);
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return notificationObjList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvBody, tvDate;
//        CheckBox ivCheckBox;
        LinearLayout layoutContent;
        LinearLayout layout_option_right;

        ItemContactContentHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvBody = itemView.findViewById(R.id.tv_body);
            tvDate = itemView.findViewById(R.id.tv_date);
//            ivCheckBox = itemView.findViewById(R.id.iv_checkbox);
            layout_option_right  = itemView.findViewById(R.id.layout_option_right);
            layoutContent = itemView.findViewById(R.id.layout_content);
        }
    }
}