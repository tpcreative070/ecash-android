package vn.ecpay.ewallet.ui.cashToCash.adapter;

/**
 * Created by Sayan Manna on 13-07-2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.model.contactTransfer.ContactTransferModel;


public class ContactTransferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    List<ContactTransferModel> mCountriesModelList;
    WeakReference<Context> mContextWeakReference;

    public ContactTransferAdapter(List<ContactTransferModel> mCountriesModelList, Context context) {

        this.mCountriesModelList = mCountriesModelList;
        this.mContextWeakReference = new WeakReference<>(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = mContextWeakReference.get();
        if (viewType == SECTION_VIEW) {
            return new SectionHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_title, parent, false));
        }
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name, parent, false), context);
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

            SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
            ContactTransferModel sectionItem = mCountriesModelList.get(position);
            sectionHeaderViewHolder.tvHeader.setText(sectionItem.getFullName());
            return;
        }

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        ContactTransferModel contactTransferModel = mCountriesModelList.get(position);
        itemViewHolder.tvName.setText(context.getString(R.string.str_item_transfer_cash,
                contactTransferModel.getFullName(), String.valueOf(contactTransferModel.getWalletId())));
        itemViewHolder.tvPhone.setText(contactTransferModel.getPhone());
    }


    @Override
    public int getItemCount() {
        return mCountriesModelList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPhone;
        public RelativeLayout viewBackground, viewForeground;


        public ItemViewHolder(View itemView, final Context context) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
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