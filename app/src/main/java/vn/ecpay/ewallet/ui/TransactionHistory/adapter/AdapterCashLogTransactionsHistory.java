package vn.ecpay.ewallet.ui.TransactionHistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;

public class AdapterCashLogTransactionsHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  List<CashLogTransaction> listCashLogTransaction;
  Context context;

  public AdapterCashLogTransactionsHistory(List<CashLogTransaction> mListCashLogTransaction, Context context) {
    this.listCashLogTransaction = mListCashLogTransaction;
    this.context = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AdapterCashLogTransactionsHistory.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cash_transfer, parent, false));
  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
    AdapterCashLogTransactionsHistory.ItemViewHolder itemViewHolder = (AdapterCashLogTransactionsHistory.ItemViewHolder) holder;
    CashLogTransaction cashLogTransaction = listCashLogTransaction.get(position);
    itemViewHolder.tvMoney.setText(CommonUtils.formatPriceVND(cashLogTransaction.getParValue()));
    itemViewHolder.tvNumberMoney.setText(String.valueOf(cashLogTransaction.getValidCount()));
    if (cashLogTransaction.getStatus() == 1) {
      itemViewHolder.ivStatus.setImageResource(R.drawable.ic_check_success);
    } else {
      itemViewHolder.ivStatus.setImageResource(R.drawable.ic_check_fail);
    }
  }

  @Override
  public int getItemCount() {
    return listCashLogTransaction.size();
  }

  public static class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvMoney, tvNumberMoney;
    public ImageView ivStatus;

    public ItemViewHolder(View itemView) {
      super(itemView);
      tvMoney = itemView.findViewById(R.id.tv_money);
      tvNumberMoney = itemView.findViewById(R.id.tv_number_money);
      ivStatus = itemView.findViewById(R.id.iv_status);
    }
  }
}