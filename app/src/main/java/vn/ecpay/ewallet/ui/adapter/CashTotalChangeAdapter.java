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

public class CashTotalChangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CashTotal> listCashValue;
    private boolean showGridView =false;

    public CashTotalChangeAdapter(List<CashTotal> mListCashValue,boolean showGridView, Context activity) {
        this.context = activity;
        this.showGridView = showGridView;
        this.listCashValue = mListCashValue;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= null;
        if(showGridView){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cash_values_gridview, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cash_value_total_firm, parent, false);
        }
        return new CashTotalChangeAdapter.ItemValueHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (context == null) {
            return;
        }
        CashTotalChangeAdapter.ItemValueHolder itemViewHolder = (CashTotalChangeAdapter.ItemValueHolder) holder;
        CashTotal cashTotal = listCashValue.get(position);
        itemViewHolder.tv_value.setText(CommonUtils.formatPrice(cashTotal.getParValue()));
        itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotalDatabase()));
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