package vn.ecpay.ewallet.ui.contact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.callbackListener.ContactTransferListener;
import vn.ecpay.ewallet.ui.callbackListener.OnDeleteItem;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_VIEW = 0;
    private static final int CONTENT_VIEW = 1;

    private List<Contact> mCountriesModelList;
    private WeakReference<Context> mContextWeakReference;
    private OnDeleteItem onDeleteItem;
    private ContactTransferListener contactTransferListener;

    public ContactAdapter(List<Contact> mCountriesModelList, Context context, OnDeleteItem mOnDeleteItem, ContactTransferListener contactTransferListener) {
        this.mCountriesModelList = mCountriesModelList;
        this.mContextWeakReference = new WeakReference<>(context);
        this.onDeleteItem = mOnDeleteItem;
        this.contactTransferListener = contactTransferListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_VIEW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contact_header, parent, false);
            return new SectionHeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        SwipeLayout item = view.findViewById(R.id.swipe_item);
        item.setShowMode(SwipeLayout.ShowMode.PullOut);
        item.addDrag(SwipeLayout.DragEdge.Left, item.findViewById(R.id.layout_option_left));
        item.addDrag(SwipeLayout.DragEdge.Right, item.findViewById(R.id.layout_option_right));
        return new ItemContactContentHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mCountriesModelList.get(position).isSection) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Context context = mContextWeakReference.get();
        if (context == null) {
            return;
        }
        if (SECTION_VIEW == getItemViewType(position)) {

            ContactAdapter.SectionHeaderViewHolder sectionHeaderViewHolder = (ContactAdapter.SectionHeaderViewHolder) holder;
            Contact sectionItem = mCountriesModelList.get(position);
            sectionHeaderViewHolder.tvHeader.setText(sectionItem.getFullName());
            return;
        }

        ItemContactContentHolder itemViewHolder = (ItemContactContentHolder) holder;
        Contact contactTransferModel = mCountriesModelList.get(position);
        itemViewHolder.tvName.setText(context.getString(R.string.str_item_transfer_cash,
                contactTransferModel.getFullName(), String.valueOf(contactTransferModel.getWalletId())));
        itemViewHolder.tvPhone.setText(contactTransferModel.getPhone());
        itemViewHolder.layoutEdit.setOnClickListener(v -> DialogUtil.getInstance().showDialogEditContact(context, contactTransferModel, new DialogUtil.OnContactUpdate() {
            @Override
            public void OnListenerOk(String name) {
                DatabaseUtil.updateNameContact(context, name, contactTransferModel.getWalletId());
                if (!contactTransferModel.getFullName().equals(name)) {
                    mCountriesModelList.get(position).setFullName(name);
                    notifyItemChanged(position);
                }
            }
        }));

        itemViewHolder.layoutDelete.setOnClickListener(v -> {
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

        if (contactTransferModel.isAddTransfer) {
            itemViewHolder.iv_multi_chose.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.iv_multi_chose.setVisibility(View.GONE);
        }

        itemViewHolder.view_foreground.setOnClickListener(v -> {
            if (contactTransferModel.isAddTransfer) {
                mCountriesModelList.get(position).setAddTransfer(false);
                itemViewHolder.iv_multi_chose.setVisibility(View.GONE);
            } else {
                mCountriesModelList.get(position).setAddTransfer(true);
                itemViewHolder.iv_multi_chose.setVisibility(View.VISIBLE);
            }
            contactTransferListener.addContactChange();
        });
    }


    @Override
    public int getItemCount() {
        return mCountriesModelList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        LinearLayout layoutEdit, layoutDelete;
        ImageView iv_multi_chose;
        RelativeLayout view_foreground;

        ItemContactContentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            layoutEdit = itemView.findViewById(R.id.layout_edit);
            layoutDelete = itemView.findViewById(R.id.layout_delete);
            iv_multi_chose = itemView.findViewById(R.id.iv_multi_chose);
            view_foreground = itemView.findViewById(R.id.view_foreground);
        }
    }

    public static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        SectionHeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_header);
        }
    }
}