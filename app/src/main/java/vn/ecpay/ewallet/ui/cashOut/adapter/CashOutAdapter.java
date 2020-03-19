package vn.ecpay.ewallet.ui.cashOut.adapter;

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
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.ui.callbackListener.UpDownMoneyListener;

public class CashOutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CashTotal> listCashValue;
    private UpDownMoneyListener upDownMoneyListener;

    public CashOutAdapter(List<CashTotal> mListCashValue, Context activity, UpDownMoneyListener upDownMoneyListener) {
        this.context = activity;
        this.listCashValue = mListCashValue;
        this.upDownMoneyListener = upDownMoneyListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cash_value_total, parent, false);
        return new CashOutAdapter.ItemValueHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (context == null) {
            return;
        }
        CashOutAdapter.ItemValueHolder itemViewHolder = (CashOutAdapter.ItemValueHolder) holder;
        CashTotal cashTotal = listCashValue.get(position);
        itemViewHolder.tv_value.setText(CommonUtils.formatPriceVND(cashTotal.getParValue()));
        itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
        itemViewHolder.tv_sl_value.setText(String.valueOf(cashTotal.getTotalDatabase()));

        itemViewHolder.iv_down.setOnClickListener(v -> {
            if (cashTotal.getTotal() > 0) {
                cashTotal.setTotal(cashTotal.getTotal() - 1);
                cashTotal.setTotalDatabase(cashTotal.getTotalDatabase() + 1);
                itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
                itemViewHolder.tv_sl_value.setText(String.valueOf(cashTotal.getTotalDatabase()));
                if (null != upDownMoneyListener) {
                    upDownMoneyListener.onUpDownMoneyListener();
                }
            }
        });

        itemViewHolder.iv_up.setOnClickListener(v -> {
            if (cashTotal.getTotalDatabase() > 0) {
                cashTotal.setTotal(cashTotal.getTotal() + 1);
                cashTotal.setTotalDatabase(cashTotal.getTotalDatabase() - 1);
                itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
                itemViewHolder.tv_sl_value.setText(String.valueOf(cashTotal.getTotalDatabase()));
                if (null != upDownMoneyListener) {
                    upDownMoneyListener.onUpDownMoneyListener();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCashValue.size();
    }

    public static class ItemValueHolder extends RecyclerView.ViewHolder {
        TextView tv_value, tv_sl_value, tv_total;
        ImageView iv_down, iv_up;
        public ItemValueHolder(View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_sl_value = itemView.findViewById(R.id.tv_sl_value);
            tv_total = itemView.findViewById(R.id.tv_total);
            iv_down = itemView.findViewById(R.id.iv_down);
            iv_up = itemView.findViewById(R.id.iv_up);
        }
    }
}