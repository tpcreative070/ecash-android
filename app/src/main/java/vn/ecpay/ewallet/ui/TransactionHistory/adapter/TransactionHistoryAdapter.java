package vn.ecpay.ewallet.ui.TransactionHistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import vn.ecpay.ewallet.R;

import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.fragmentcommon.ui.widget.CircleImageView;

import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_FAIL;
import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_SUCESSS;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_ECASH_EXCHANGE;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_ECASH_TO_EDONG;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_EDONG_TO_ECASH;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_MONEY;


public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    List<TransactionsHistoryModel> mTransactionHistory;
    WeakReference<Context> mContextWeakReference;

    public TransactionHistoryAdapter(List<TransactionsHistoryModel> mTransactionHistory, Context context) {

        this.mTransactionHistory = mTransactionHistory;
        this.mContextWeakReference = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = mContextWeakReference.get();
        if (viewType == SECTION_VIEW) {
            return new TransactionHistoryAdapter.SectionHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_title, parent, false));
        }
        return new TransactionHistoryAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transactionhistory, parent, false), context);
    }


    @Override
    public int getItemViewType(int position) {
        if (mTransactionHistory.get(position).isSection) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Context context = mContextWeakReference.get();
        if (context == null) {
            return;
        }
        if (SECTION_VIEW == getItemViewType(position)) {

            TransactionHistoryAdapter.SectionHeaderViewHolder sectionHeaderViewHolder = (TransactionHistoryAdapter.SectionHeaderViewHolder) holder;
            TransactionsHistoryModel sectionItem = mTransactionHistory.get(position);
            sectionHeaderViewHolder.tvHeader.setText(sectionItem.getTransactionDate());
            return;
        }

        TransactionHistoryAdapter.ItemViewHolder itemViewHolder = (TransactionHistoryAdapter.ItemViewHolder) holder;
        TransactionsHistoryModel transactionsHistoryModel = mTransactionHistory.get(position);
        itemViewHolder.tvFullName.setText(context.getString(R.string.str_item_transfer_cash,
                transactionsHistoryModel.getReceiverName(), String.valueOf(transactionsHistoryModel.getReceiverAccountId())));

        //itemViewHolder.tvTransactionStatus.setText(transactionsHistoryModel.getTransactionStatus());
        if(Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_SUCESSS)
        {
            itemViewHolder.tvTransactionStatus.setText("Thành công");
        }else if(Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_FAIL)
        {
            itemViewHolder.tvTransactionStatus.setText("Thất bại");
        }
        itemViewHolder.tvTransactionAmount.setText(transactionsHistoryModel.getTransactionAmount());
        //itemViewHolder.tvTransactionDate.setText(transactionsHistoryModel.getTransactionDate());
        String strTransactionDateTime = transactionsHistoryModel.getTransactionDate();
        String strYear = strTransactionDateTime.substring(0, 4);
        String strMonth = strTransactionDateTime.substring(4, 6);
        String strDay = strTransactionDateTime.substring(6, 8);
        itemViewHolder.tvTransactionDate.setText(strDay+"/"+strMonth+"/"+strYear);

        switch (transactionsHistoryModel.getTransactionType())
        {
            case TYPE_SEND_ECASH_TO_EDONG:
                itemViewHolder.tvTransactionType.setText("Rút Cash");
                itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_cash_out_gray);
                break;
            case TYPE_SEND_MONEY:
                itemViewHolder.tvTransactionType.setText("Chuyển khoản");
                itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_transfer_gray);
                break;
            case TYPE_SEND_EDONG_TO_ECASH:
                itemViewHolder.tvTransactionType.setText("Nạp Cash");
                itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_cash_in_gray);
                break;
            case TYPE_ECASH_EXCHANGE:
                itemViewHolder.tvTransactionType.setText("Đổi Cash");
                itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_transfer_gray);
                break;

        }
    }

    @Override
    public int getItemCount() {return mTransactionHistory.size();}


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvFullName, tvTransactionType, tvTransactionStatus, tvTransactionAmount, tvTransactionDate;
        public RelativeLayout viewBackground, viewForeground;
        public CircleImageView ivTransactionIcon;


        public ItemViewHolder(View itemView, final Context context) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tv_fullName);
            tvTransactionType = itemView.findViewById(R.id.tv_transactionType);
            tvTransactionStatus = itemView.findViewById(R.id.tv_transactionStatus);
            tvTransactionAmount = itemView.findViewById(R.id.tv_transactionAmount);
            tvTransactionDate = itemView.findViewById(R.id.tv_transactionDate);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            ivTransactionIcon = itemView.findViewById(R.id.iv_transactionIcon);
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
