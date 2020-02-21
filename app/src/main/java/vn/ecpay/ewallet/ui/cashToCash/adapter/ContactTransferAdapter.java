package vn.ecpay.ewallet.ui.cashToCash.adapter;

import android.content.Context;
import android.util.Log;
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

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.interfaceListener.MultiTransferListener;

import static vn.ecpay.ewallet.ECashApplication.getActivity;


public class ContactTransferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_VIEW = 0;
    private static final int CONTENT_VIEW = 1;

    List<Contact> contactList;
    WeakReference<Context> mContextWeakReference;
    ArrayList<Contact> multiTransferList;
    private boolean limitChoice;
    private Context context;

    public ContactTransferAdapter(List<Contact> mContactList, Context context, boolean limitChoice)
        {
        this.contactList = mContactList;
        this.mContextWeakReference = new WeakReference<>(context);
        this.limitChoice = limitChoice;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        multiTransferList = new ArrayList<>();
        if (viewType == SECTION_VIEW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contact_header, parent, false);
            return new ContactTransferAdapter.SectionHeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_transfer, parent, false);
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

        if (contactTransferModel.isAddTransfer) {
            itemViewHolder.iv_multi_chose.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.iv_multi_chose.setVisibility(View.GONE);
        }

        itemViewHolder.view_foreground.setOnClickListener(v -> {
            if (contactTransferModel.isAddTransfer) {
                contactList.get(position).setAddTransfer(false);
                itemViewHolder.iv_multi_chose.setVisibility(View.GONE);
            } else {
                if(limitChoice){
                    if(contactList!=null){
                        if(CommonUtils.getCountTransfer(contactList)==10){
                            if(getActivity()!=null){
                                if(getActivity() instanceof ECashBaseActivity){
                                    ((ECashBaseActivity) getActivity()).showDialogError(getActivity().getString(R.string.str_error_limited_select_contact));
                                }else{
                                    Log.e("getActivity ", getActivity().getLocalClassName());
                                }
                            }
                            return;
                        }


                    }
                }
                contactList.get(position).setAddTransfer(true);
                itemViewHolder.iv_multi_chose.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        RelativeLayout view_foreground;
        ImageView iv_multi_chose;

        ItemContactContentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            view_foreground = itemView.findViewById(R.id.view_foreground);
            iv_multi_chose = itemView.findViewById(R.id.iv_multi_chose);
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