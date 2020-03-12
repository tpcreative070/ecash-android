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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.ui.cashIn.adapter.CashValueAdapter;
import vn.ecpay.ewallet.ui.callbackListener.UpDownMoneyListener;

public class CashTakeBottomSheet extends BaseBottomSheetDialogFragment {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_total_receive)
    TextView tvTotalReceive;
    @BindView(R.id.tv_change_norm)
    TextView tvChangeNorm;
    @BindView(R.id.tv_error)
    TextView tvError;

    @BindView(R.id.btn_confirm)
    Button btnOk;
    @BindView(R.id.rv_cash_change)
    RecyclerView rv_cash_change;
    List<CashTotal> valuesList;
    private long totalChange =0;
    private long totalTake=0;
    private DialogUtil.OnResultChoseCash onResultChoseCash;

    public CashTakeBottomSheet(long totalChange,DialogUtil.OnResultChoseCash onResultChoseCash) {
        this.totalChange = totalChange;
        this.onResultChoseCash = onResultChoseCash;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chose_cash_take, container, false);
        return view;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle.setText(getString(R.string.str_select_the_face_value_you_want_to_receive));
        valuesList = DatabaseUtil.getAllCashValues(getActivity());
        tvChangeNorm.setText(CommonUtils.formatPriceVND(totalChange));
        Utils.disableButtonConfirm(getActivity(),btnOk,true);

        rv_cash_change.setAdapter(new CashValueAdapter(valuesList, getActivity(), new UpDownMoneyListener() {
            @Override
            public void onUpDownMoneyListener() {
                tvTotalReceive.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valuesList)));
                 totalTake =CommonUtils.getTotalMoney(valuesList);
                if(totalTake> totalChange){
                    tvError.setText(getString(R.string.str_the_total_number_of_eCash_that_you_want_to_receive_is_exceeded));
                    Utils.disableButtonConfirm(getActivity(),btnOk,true);
                }
                if(totalTake== totalChange){
                    tvError.setText("");
                    Utils.disableButtonConfirm(getActivity(),btnOk,false);
                }
                if(totalTake< totalChange){
                    Utils.disableButtonConfirm(getActivity(),btnOk,true);
                    tvError.setText(getString(R.string.err_conflict_take_and_change));
                }
            }
        }));
        btnOk.setOnClickListener(v -> {
            if (onResultChoseCash != null) {
                if (totalTake > 0) {
                    dismiss();
                    onResultChoseCash.OnListenerOk(valuesList);
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.err_chose_money_transfer), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}