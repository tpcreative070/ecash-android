package vn.ecpay.ewallet.ui.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    List<Contact> mCountriesModelList;
    WeakReference<Context> mContextWeakReference;
    private onDeleteItem onDeleteItem;

    public interface onDeleteItem {
        void onDeleteOK(int pos);
    }

    public ContactAdapter(List<Contact> mCountriesModelList, Context context, onDeleteItem mOnDeleteItem) {
        this.mCountriesModelList = mCountriesModelList;
        this.mContextWeakReference = new WeakReference<>(context);
        this.onDeleteItem = mOnDeleteItem;
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
        itemViewHolder.layoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showDialogEditContact(context, contactTransferModel, new DialogUtil.OnContactUpdate() {
                    @Override
                    public void OnListenerOk(String name) {
                        DatabaseUtil.updateNameContact(context, name, contactTransferModel.getWalletId());
                        if (!contactTransferModel.getFullName().equals(name)) {
                            mCountriesModelList.get(position).setFullName(name);
                            notifyItemChanged(position);
                        }
                    }
                });
            }
        });

        itemViewHolder.layoutDelete.setOnClickListener(v -> {
            if (null != onDeleteItem) {
                DialogUtil.getInstance().showDialogConfirm(context, context.getResources().getString(R.string.str_dialog_notification_title),
                        context.getResources().getString(R.string.str_confirm_delete), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        DatabaseUtil.deleteContact(context, contactTransferModel.getWalletId());
                        onDeleteItem.onDeleteOK(position);
                    }
                    @Override
                    public void OnListenerCancel() {
                    }
                });
            }
        });

        itemViewHolder.layoutTransfer.setOnClickListener(v -> {
            Intent intentTransferCash = new Intent(context, CashToCashActivity.class);
            intentTransferCash.putExtra(Constant.CONTACT_TRANSFER_MODEL, contactTransferModel);
            context.startActivity(intentTransferCash);
            ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }


    @Override
    public int getItemCount() {
        return mCountriesModelList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPhone;
        public LinearLayout layoutEdit, layoutDelete, layoutTransfer;


        public ItemContactContentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            layoutEdit = itemView.findViewById(R.id.layout_edit);
            layoutDelete = itemView.findViewById(R.id.layout_delete);
            layoutTransfer = itemView.findViewById(R.id.layout_option_right);
        }
    }

    public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_header);
        }
    }
}