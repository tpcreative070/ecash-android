package vn.ecpay.ewallet.ui.cashToCash.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.lang.ref.WeakReference;
import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.model.contactTransfer.Contact;


public class ContactTransferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    List<Contact> contactList;
    WeakReference<Context> mContextWeakReference;
    private onItemTransferListener onItemTransferListener;

    public interface onItemTransferListener {
        void onItemClick(Contact contact);
    }

    public ContactTransferAdapter(List<Contact> mContactList, Context context, onItemTransferListener onItemTransferListener) {
        this.contactList = mContactList;
        this.mContextWeakReference = new WeakReference<>(context);
        this.onItemTransferListener = onItemTransferListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_VIEW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contact_header, parent, false);
            return new ContactTransferAdapter.SectionHeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_transfer, parent, false);
        SwipeLayout item = view.findViewById(R.id.swipe_item);
        item.setShowMode(SwipeLayout.ShowMode.PullOut);
        item.addDrag(SwipeLayout.DragEdge.Right, item.findViewById(R.id.layout_option_right));
        return new ContactTransferAdapter.ItemContactContentHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (contactList.get(position).isSection) {
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

            ContactTransferAdapter.SectionHeaderViewHolder sectionHeaderViewHolder = (ContactTransferAdapter.SectionHeaderViewHolder) holder;
            Contact sectionItem = contactList.get(position);
            sectionHeaderViewHolder.tvHeader.setText(sectionItem.getFullName());
            return;
        }

        ContactTransferAdapter.ItemContactContentHolder itemViewHolder = (ContactTransferAdapter.ItemContactContentHolder) holder;
        Contact contactTransferModel = contactList.get(position);
        itemViewHolder.tvName.setText(context.getString(R.string.str_item_transfer_cash,
                contactTransferModel.getFullName(), String.valueOf(contactTransferModel.getWalletId())));
        itemViewHolder.tvPhone.setText(contactTransferModel.getPhone());

        itemViewHolder.layoutTransfer.setOnClickListener(v -> {
            if(null != onItemTransferListener){
                onItemTransferListener.onItemClick(contactTransferModel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPhone;
        public LinearLayout layoutTransfer;


        public ItemContactContentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
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