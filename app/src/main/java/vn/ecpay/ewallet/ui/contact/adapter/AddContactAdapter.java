package vn.ecpay.ewallet.ui.contact.adapter;

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

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.contact.AddContactActivity;
import vn.ecpay.ewallet.ui.callbackListener.OnItemContactClickListener;

public class AddContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final AccountInfo accountInfo;
    List<Contact> contactList;
    WeakReference<Context> mContextWeakReference;
    private OnItemContactClickListener onItemContactClickListener;

    public AddContactAdapter(List<Contact> mCountriesModelList, Context context, OnItemContactClickListener mOnItemContactClickListener) {
        this.contactList = mCountriesModelList;
        this.mContextWeakReference = new WeakReference<>(context);
        this.onItemContactClickListener = mOnItemContactClickListener;
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_add, parent, false);
        SwipeLayout item = view.findViewById(R.id.swipe_item);
        item.setShowMode(SwipeLayout.ShowMode.PullOut);
        item.addDrag(SwipeLayout.DragEdge.Right, item.findViewById(R.id.layout_option_right));
        return new AddContactAdapter.ItemContactContentHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Context context = mContextWeakReference.get();
        if (context == null) {
            return;
        }
        AddContactAdapter.ItemContactContentHolder itemViewHolder = (AddContactAdapter.ItemContactContentHolder) holder;
        Contact contact = contactList.get(position);
        itemViewHolder.tvName.setText(context.getString(R.string.str_item_transfer_cash,
                contact.getFullName(), String.valueOf(contact.getWalletId())));
        itemViewHolder.tvPhone.setText(contact.getPhone());

        itemViewHolder.layoutAdd.setOnClickListener(v -> {
            if (contact.getWalletId().equals(accountInfo.getWalletId())) {
                ((AddContactActivity) context).showDialogError(context.getResources().getString(R.string.err_add_contact_conflict));
            } else {
                List<Contact> listContact = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
                for (int i = 0; i < listContact.size(); i++) {
                    if (listContact.get(i).getWalletId().equals(contact.getWalletId())) {
                        ((AddContactActivity) context).showDialogError(context.getResources().getString(R.string.err_add_contact_duplicate));
                        return;
                    }
                }
                if (onItemContactClickListener != null) {
                    onItemContactClickListener.OnclickListener(contact);
                    contactList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPhone;
        public LinearLayout layoutAdd;


        public ItemContactContentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            layoutAdd = itemView.findViewById(R.id.layout_add);
        }
    }
}