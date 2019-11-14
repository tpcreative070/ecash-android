package vn.ecpay.ewallet.ui.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.notification.NotificationObj;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<NotificationObj> notificationObjList;
    WeakReference<Context> mContextWeakReference;
    boolean isCheckBox;

    public NotificationAdapter(boolean mIsCheckBox, List<NotificationObj> mNotificationObjList, Context context) {
        this.notificationObjList = mNotificationObjList;
        this.mContextWeakReference = new WeakReference<>(context);
        this.isCheckBox = mIsCheckBox;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
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

        if (isCheckBox) {
            itemViewHolder.ivCheckBox.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.ivCheckBox.setVisibility(View.GONE);
        }
        itemViewHolder.layoutContent.setOnClickListener(v -> {
            if (notification.getRead().equals(Constant.ON)) {
                DatabaseUtil.updateNotificationRead(context, Constant.OFF, notification.getId());
                notificationObjList.get(position).setRead(Constant.OFF);
                notifyItemChanged(position);
            }
        });

        if (notification.getIsCheck() == 0) {
            itemViewHolder.ivCheckBox.setChecked(false);
        } else {
            itemViewHolder.ivCheckBox.setChecked(true);
        }

        itemViewHolder.ivCheckBox.setOnClickListener(v -> {
            if (itemViewHolder.ivCheckBox.isChecked()) {
                notificationObjList.get(position).setIsCheck(1);
            } else {
                notificationObjList.get(position).setIsCheck(0);
            }
        });

    }


    @Override
    public int getItemCount() {
        return notificationObjList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvBody, tvDate;
        CheckBox ivCheckBox;
        RelativeLayout layoutContent;

        public ItemContactContentHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvBody = itemView.findViewById(R.id.tv_body);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivCheckBox = itemView.findViewById(R.id.iv_checkbox);
            layoutContent = itemView.findViewById(R.id.layout_content);
        }
    }
}