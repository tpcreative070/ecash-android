package vn.ecpay.ewallet.common.utils.bottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;

import vn.ecpay.ewallet.common.utils.interfaces.ConfirmChangeCashListener;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.ui.adapter.CashTotalConfirmAdapter;

public class ConfirmChangeCashBottomSheet extends BaseBottomSheetDialogFragment {
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.rv_cash_take)
    RecyclerView rv_cash_take;
    @BindView(R.id.rv_cash_change)
    RecyclerView rv_cash_change;

    @BindView(R.id.tv_total_money_send)
    TextView tv_total_money_send;
    @BindView(R.id.tv_total_money_take)
    TextView tv_total_money_take;

    private List<CashTotal> valueListCashChange;
    private List<CashTotal> valueListCashTake;
    private ConfirmChangeCashListener confirmChangeCashListener;
    public ConfirmChangeCashBottomSheet(List<CashTotal> valueListCashChange, List<CashTotal> valueListCashTake, ConfirmChangeCashListener confirmChangeCashListener) {
        this.valueListCashChange = valueListCashChange;
        this.valueListCashTake = valueListCashTake;
        this.confirmChangeCashListener = confirmChangeCashListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_change_cash, container, false);
        return view;
    }

    @Override
    protected void initView() {
        super.initView();
        rv_cash_change.setAdapter(new CashTotalConfirmAdapter(CommonUtils.getListCashConfirm(valueListCashChange), getActivity()));
        rv_cash_take.setAdapter(new CashTotalConfirmAdapter(CommonUtils.getListCashConfirm(valueListCashTake), getActivity()));
        tv_total_money_send.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valueListCashChange)));
        tv_total_money_take.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valueListCashTake)));
        btnConfirm.setOnClickListener(v -> {
            dismiss();
            confirmChangeCashListener.onConfirmChangeCash();
        });
    }
}
