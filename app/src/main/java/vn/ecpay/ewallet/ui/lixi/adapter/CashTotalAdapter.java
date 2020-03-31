package vn.ecpay.ewallet.ui.lixi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.ui.callbackListener.UpDownMoneyListener;

public class CashTotalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<CashTotal> listCashValue;
    private UpDownMoneyListener upDownMoneyListener;
    private int numberTransfer;

    public CashTotalAdapter(List<CashTotal> mListCashValue, Context activity, UpDownMoneyListener upDownMoneyListener) {
        this.context = activity;
        this.listCashValue = mListCashValue;
        this.upDownMoneyListener = upDownMoneyListener;
        numberTransfer = 0;
    }

    public void setNumberTransfer(int mNumberTransfer) {
        this.numberTransfer = mNumberTransfer;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cash_value_total, parent, false);
        return new ItemValueHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (context == null) {
            return;
        }
        ItemValueHolder itemViewHolder = (ItemValueHolder) holder;
        CashTotal cashTotal = listCashValue.get(position);
        //itemViewHolder.tv_value.setText(CommonUtils.formatPriceVND(cashTotal.getParValue()));
        itemViewHolder.tv_value.setText(CommonUtils.formatPrice(cashTotal.getParValue()));
        itemViewHolder.tv_sl_value.setText(String.valueOf(cashTotal.getTotalDatabase()));
        itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));

        itemViewHolder.iv_down.setOnClickListener(v -> {
            if (cashTotal.getTotal() > 0) {
                cashTotal.setTotal(cashTotal.getTotal() - 1);
                cashTotal.setTotalDatabase(cashTotal.getTotalDatabase() + numberTransfer);
                itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
                itemViewHolder.tv_sl_value.setText(String.valueOf(cashTotal.getTotalDatabase()));
                if (null != upDownMoneyListener) {
                    upDownMoneyListener.onUpDownMoneyListener();
                }
            }
        });

        itemViewHolder.iv_up.setOnClickListener(v -> {
            if (numberTransfer == 0) {
                DialogUtil.getInstance().showDialogWarning(context,context.getResources().getString(R.string.err_not_input_number_username));
                return;
            }
            if (cashTotal.getTotalDatabase() > 0) {
                if (checkValuesTransfer(cashTotal.getTotal() + 1, cashTotal)) {
                    cashTotal.setTotal(cashTotal.getTotal() + 1);
                    cashTotal.setTotalDatabase(cashTotal.getTotalDatabase() - numberTransfer);
                    itemViewHolder.tv_total.setText(String.valueOf(cashTotal.getTotal()));
                    itemViewHolder.tv_sl_value.setText(String.valueOf(cashTotal.getTotalDatabase()));
                    if (null != upDownMoneyListener) {
                        upDownMoneyListener.onUpDownMoneyListener();
                    }
                } else {
                    DialogUtil.getInstance().showDialogErrorTitleMessage(context,context.getString(R.string.str_dialog_notification_title),context.getString(R.string.str_error_not_enough_balance_please_try_again));
                }
            } else {
                DialogUtil.getInstance().showDialogErrorTitleMessage(context,context.getString(R.string.str_dialog_notification_title),context.getString(R.string.str_error_not_enough_balance_please_try_again));

            }
        });
    }

    private boolean checkValuesTransfer(int numberCashTransfer, CashTotal cashTotal) {
        if (numberCashTransfer > 0) {
            int slc = numberCashTransfer * numberTransfer;
            return slc <= cashTotal.getTotal() * numberTransfer + cashTotal.getTotalDatabase();
        }
        return true;
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