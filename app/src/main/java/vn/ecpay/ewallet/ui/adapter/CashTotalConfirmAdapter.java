package vn.ecpay.ewallet.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.model.cashValue.CashTotal;

public class CashTotalConfirmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CashTotal> listCashValue;

    public CashTotalConfirmAdapter(List<CashTotal> mListCashValue, Context activity) {
        this.context = activity;
        this.listCashValue = mListCashValue;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cash_value_total_firm, parent, false);
        return new CashTotalConfirmAdapter.ItemValueHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (context == null) {
            return;
        }
        CashTotalConfirmAdapter.ItemValueHolder itemViewHolder = (CashTotalConfirmAdapter.ItemValueHolder) holder;
        CashTotal cashTotal = listCashValue.get(position);
        //itemViewHolder.tv_value.setText(CommonUtils.formatPriceVND(cashTotal.getParValue()));
        itemViewHolder.tv_value.setText(CommonUtils.formatPrice(cashTotal.getParValue()));
        itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
    }


    @Override
    public int getItemCount() {
        return listCashValue.size();
    }

    public static class ItemValueHolder extends RecyclerView.ViewHolder {

        TextView tv_value, tv_total;

        public ItemValueHolder(View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_total = itemView.findViewById(R.id.tv_total);
        }
    }
}