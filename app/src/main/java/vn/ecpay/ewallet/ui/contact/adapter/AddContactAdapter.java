package vn.ecpay.ewallet.ui.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.lang.ref.WeakReference;
import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.contact.AddContactActivity;

public class AddContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Contact> mCountriesModelList;
    WeakReference<Context> mContextWeakReference;

    public AddContactAdapter(List<Contact> mCountriesModelList, Context context) {
        this.mCountriesModelList = mCountriesModelList;
        this.mContextWeakReference = new WeakReference<>(context);
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
        Contact contact = mCountriesModelList.get(position);
        itemViewHolder.tvName.setText(context.getString(R.string.str_item_transfer_cash,
                contact.getFullName(), String.valueOf(contact.getWalletId())));
        itemViewHolder.tvPhone.setText(contact.getPhone());

        itemViewHolder.layoutTransfer.setOnClickListener(v -> {
            Intent intentTransferCash = new Intent(context, CashToCashActivity.class);
            intentTransferCash.putExtra(Constant.CONTACT_TRANSFER_MODEL, contact);
            context.startActivity(intentTransferCash);
            ((AddContactActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        itemViewHolder.layoutAdd.setOnClickListener(v -> {
            DatabaseUtil.saveOnlySingleContact(context, contact);
            Toast.makeText(context,context.getResources()
                    .getString(R.string.str_add_contact_success), Toast.LENGTH_LONG).show();
        });
    }


    @Override
    public int getItemCount() {
        return mCountriesModelList.size();
    }

    public static class ItemContactContentHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPhone;
        public LinearLayout layoutAdd, layoutTransfer;


        public ItemContactContentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            layoutAdd = itemView.findViewById(R.id.layout_add);
            layoutTransfer = itemView.findViewById(R.id.layout_option_right);
        }
    }
}