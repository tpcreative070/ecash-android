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
import vn.ecpay.ewallet.common.base.CircleImageView;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;

import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_FAIL;
import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_SUCCESS;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_CASH_EXCHANGE;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_ECASH_TO_ECASH;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_LIXI;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_ECASH_TO_EDONG;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_EDONG_TO_ECASH;


public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    List<TransactionsHistoryModel> mTransactionHistory;
    WeakReference<Context> mContextWeakReference;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(TransactionsHistoryModel transactionsHistoryModel);
    }

    public TransactionHistoryAdapter(List<TransactionsHistoryModel> mTransactionHistory, Context context, OnItemClickListener mOnItemClickListener) {
        this.mTransactionHistory = mTransactionHistory;
        this.mContextWeakReference = new WeakReference<>(context);
        this.onItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = mContextWeakReference.get();
        if (viewType == SECTION_VIEW) {
            return new TransactionHistoryAdapter.SectionHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_header, parent, false));
        }
        return new TransactionHistoryAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transactionhistory, parent, false));
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

        if (Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_SUCCESS) {
            itemViewHolder.tvTransactionStatus.setText(context.getString(R.string.str_cash_take_success));
        } else if (Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_FAIL) {
            itemViewHolder.tvTransactionStatus.setText(context.getString(R.string.str_fail));
        }
        itemViewHolder.tvTransactionDate.setText(CommonUtils.getDateTransfer(context, transactionsHistoryModel.getTransactionDate()));

        switch (transactionsHistoryModel.getTransactionType()) {
            case TYPE_SEND_ECASH_TO_EDONG:
                itemViewHolder.tvTransactionType.setText(context.getString(R.string.str_cash_out));
                itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_cash_out_gray);
                itemViewHolder.tvTransactionAmount.setText(context.getString(R.string.str_type_cash_out,
                        CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                break;
            case TYPE_ECASH_TO_ECASH:
                if (transactionsHistoryModel.getCashLogType().equals(Constant.STR_CASH_IN)) {
                    itemViewHolder.tvTransactionType.setText(context.getString(R.string.str_transfer));
                    itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_transfer_gray);
                    itemViewHolder.tvTransactionAmount.setText(context.getString(R.string.str_type_cash_in,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                } else {
                    itemViewHolder.tvTransactionType.setText(context.getString(R.string.str_transfer));
                    itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_transfer_gray);
                    itemViewHolder.tvTransactionAmount.setText(context.getString(R.string.str_type_cash_out,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                }
                break;
            case TYPE_LIXI:
                if (transactionsHistoryModel.getCashLogType().equals(Constant.STR_CASH_IN)) {
                    itemViewHolder.tvTransactionType.setText(context.getString(R.string.str_lixi));
                    itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_transfer_gray);
                    itemViewHolder.tvTransactionAmount.setText(context.getString(R.string.str_type_cash_in,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                } else {
                    itemViewHolder.tvTransactionType.setText(context.getString(R.string.str_lixi));
                    itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_transfer_gray);
                    itemViewHolder.tvTransactionAmount.setText(context.getString(R.string.str_type_cash_out,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                }
                break;
            case TYPE_SEND_EDONG_TO_ECASH:
                itemViewHolder.tvTransactionType.setText(context.getString(R.string.str_cash_in));
                itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_cash_in_gray);
                itemViewHolder.tvTransactionAmount.setText(context.getString(R.string.str_type_cash_in,
                        CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                break;
            case TYPE_CASH_EXCHANGE:
                itemViewHolder.tvTransactionType.setText(context.getString(R.string.str_cash_change));
                itemViewHolder.ivTransactionIcon.setImageResource(R.drawable.ic_transfer_gray);
                itemViewHolder.tvTransactionAmount.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()) / 2));
                break;

        }

        itemViewHolder.item_view.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(transactionsHistoryModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTransactionHistory.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvFullName, tvTransactionType, tvTransactionStatus, tvTransactionAmount, tvTransactionDate;
        public RelativeLayout item_view;
        public CircleImageView ivTransactionIcon;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tv_fullName);
            tvTransactionType = itemView.findViewById(R.id.tv_transactionType);
            tvTransactionStatus = itemView.findViewById(R.id.tv_transactionStatus);
            tvTransactionAmount = itemView.findViewById(R.id.tv_transactionAmount);
            tvTransactionDate = itemView.findViewById(R.id.tv_transactionDate);
            item_view = itemView.findViewById(R.id.item_view);
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
