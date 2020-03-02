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
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.ui.cashOut.adapter.CashOutAdapter;
import vn.ecpay.ewallet.ui.interfaceListener.UpDownMoneyListener;

public class CashChangeBottomSheet extends BaseBottomSheetDialogFragment {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.btn_confirm)
    Button btnOk;
    @BindView(R.id.rv_cash_change)
    RecyclerView rv_cash_change;
    List<CashTotal> valuesList;
    private DialogUtil.OnResultChoseCash onResultChoseCash;

    public CashChangeBottomSheet(DialogUtil.OnResultChoseCash onResultChoseCash) {
        this.onResultChoseCash = onResultChoseCash;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chose_cash_change, container, false);
        return view;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle.setText(getString(R.string.str_select_the_face_value_you_want_to_change));
        valuesList = DatabaseUtil.getAllCashTotal(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_cash_change.setLayoutManager(mLayoutManager);
        rv_cash_change.setAdapter(new CashOutAdapter(valuesList, getActivity(), new UpDownMoneyListener() {
            @Override
            public void onUpDownMoneyListener() {
                tvTotalMoney.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valuesList)));
            }
        }));
        btnOk.setOnClickListener(v -> {
            if (onResultChoseCash != null) {
                if (CommonUtils.getTotalMoney(valuesList) > 0) {
                    dismiss();
                    onResultChoseCash.OnListenerOk(valuesList);
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.err_chose_money_transfer), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
