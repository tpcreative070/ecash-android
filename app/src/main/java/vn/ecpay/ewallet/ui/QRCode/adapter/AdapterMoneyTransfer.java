package vn.ecpay.ewallet.ui.QRCode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;

public class AdapterMoneyTransfer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<QRCashTransfer> qrCashTransfers;
    Context context;

    public AdapterMoneyTransfer(ArrayList<QRCashTransfer> mQrCashTransfers, Context context) {
        this.qrCashTransfers = mQrCashTransfers;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cash_transfer, parent, false), context);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        QRCashTransfer qrCashTransfer = qrCashTransfers.get(position);
        itemViewHolder.tvMoney.setText(CommonUtils.formatPriceVND(qrCashTransfer.getParValue()));
        itemViewHolder.tvNumberMoney.setText("1");
        if (qrCashTransfer.isSuccess()) {
            itemViewHolder.ivStatus.setImageResource(R.drawable.ic_check_success);
        } else {
            itemViewHolder.ivStatus.setImageResource(R.drawable.ic_check_fail);
        }
    }

    @Override
    public int getItemCount() {
        return qrCashTransfers.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMoney, tvNumberMoney;
        public ImageView ivStatus;

        public ItemViewHolder(View itemView, final Context context) {
            super(itemView);
            tvMoney = itemView.findViewById(R.id.tv_money);
            tvNumberMoney = itemView.findViewById(R.id.tv_number_money);
            ivStatus = itemView.findViewById(R.id.iv_status);
        }
    }
}