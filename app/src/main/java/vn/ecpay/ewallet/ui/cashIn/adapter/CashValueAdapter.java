package vn.ecpay.ewallet.ui.cashIn.adapter;

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
import vn.ecpay.ewallet.ui.interfaceListener.UpDownMoneyListener;

public class CashValueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CashTotal> listCashValue;
    private UpDownMoneyListener upDownMoneyListener;

    public CashValueAdapter(List<CashTotal> mListCashValue, Context activity, UpDownMoneyListener upDownMoneyListener) {
        this.context = activity;
        this.listCashValue = mListCashValue;
        this.upDownMoneyListener = upDownMoneyListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cash_value, parent, false);
        return new CashValueAdapter.ItemValueHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (context == null) {
            return;
        }
        CashValueAdapter.ItemValueHolder itemViewHolder = (CashValueAdapter.ItemValueHolder) holder;
        CashTotal cashTotal = listCashValue.get(position);
      //  itemViewHolder.tv_value.setText(CommonUtils.formatPriceVND(cashTotal.getParValue()));
        itemViewHolder.tv_value.setText(CommonUtils.formatPrice(cashTotal.getParValue()));
        itemViewHolder.iv_down.setOnClickListener(v -> {
            if (cashTotal.getTotal() > 0) {
                cashTotal.setTotal(cashTotal.getTotal() - 1);
                itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
                if (upDownMoneyListener != null) {
                    upDownMoneyListener.onUpDownMoneyListener();
                }
            }
        });

        itemViewHolder.iv_up.setOnClickListener(v -> {
            cashTotal.setTotal(cashTotal.getTotal() + 1);
            itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
            if (upDownMoneyListener != null) {
                upDownMoneyListener.onUpDownMoneyListener();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listCashValue.size();
    }

    public static class ItemValueHolder extends RecyclerView.ViewHolder {

        TextView tv_value, tv_total;
        ImageView iv_down, iv_up;

        public ItemValueHolder(View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_total = itemView.findViewById(R.id.tv_total);
            iv_down = itemView.findViewById(R.id.iv_down);
            iv_up = itemView.findViewById(R.id.iv_up);
        }
    }
}