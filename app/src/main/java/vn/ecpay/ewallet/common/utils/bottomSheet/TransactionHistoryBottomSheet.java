package vn.ecpay.ewallet.common.utils.bottomSheet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.ui.TransactionHistory.MonthYearPickerDialog;
import vn.ecpay.ewallet.ui.callbackListener.HistoryFilterListener;

public class TransactionHistoryBottomSheet extends BaseBottomSheetDialogFragment {
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    private HistoryFilterListener historyFilterListener;

    private String dateFilter = null,
            typeFilter = null,
            statusFilter = null;

    public TransactionHistoryBottomSheet(HistoryFilterListener historyFilterListener) {
        this.historyFilterListener = historyFilterListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter_history, container, false);
        return view;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @OnClick({R.id.layout_date, R.id.layout_type, R.id.layout_status, R.id.btn_apply, R.id.btn_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_date:
                showDialogDate();
                break;
            case R.id.layout_type:
                showDialogChoseType();
                break;
            case R.id.layout_status:
                showDialogChoseStatus();
                break;
            case R.id.btn_apply:
                dismiss();
                historyFilterListener.filter(dateFilter, typeFilter, statusFilter);
                break;
            case R.id.btn_clear:
                dateFilter = null;
                typeFilter = null;
                statusFilter = null;

                tvDate.setText(Constant.STR_EMPTY);
                tvType.setText(Constant.STR_EMPTY);
                tvStatus.setText(Constant.STR_EMPTY);
                break;
        }
    }

    private void showDialogDate() {
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener((view, year, month, dayOfMonth) -> {
            tvDate.setText(getString(R.string.str_history_date_header, String.valueOf(month), String.valueOf(year)));
            String monthCF = String.valueOf(month);
            if (month < 10) {
                monthCF = "0" + month;
            }
            dateFilter = year + monthCF;
        });
        pd.show(getChildFragmentManager(), "MonthYearPickerDialog");
    }

    private void showDialogChoseType() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getResources().getString(R.string.str_cash_in));
        arrayAdapter.add(getResources().getString(R.string.str_cash_out));
        arrayAdapter.add(getResources().getString(R.string.str_transfer));
        arrayAdapter.add(getResources().getString(R.string.str_change_cash));
        arrayAdapter.add(getResources().getString(R.string.str_lixi));
        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            typeFilter = getTypeFilter(arrayAdapter.getItem(which));
            tvType.setText(arrayAdapter.getItem(which));
        });
        builderSingle.show();
    }

    private String getTypeFilter(String type) {
        if (type.equals(getResources().getString(R.string.str_cash_in))) {
            return Constant.TYPE_SEND_EDONG_TO_ECASH;
        } else if (type.equals(getResources().getString(R.string.str_cash_out))) {
            return Constant.TYPE_SEND_ECASH_TO_EDONG;
        } else if (type.equals(getResources().getString(R.string.str_transfer))) {
            return Constant.TYPE_ECASH_TO_ECASH;
        } else if (type.equals(getResources().getString(R.string.str_change_cash))) {
            return Constant.TYPE_CASH_EXCHANGE;
        } else if (type.equals(getResources().getString(R.string.str_lixi))) {
            return Constant.TYPE_LIXI;
        } else return Constant.STR_EMPTY;
    }

    private void showDialogChoseStatus() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getResources().getString(R.string.str_cash_take_success));
        arrayAdapter.add(getResources().getString(R.string.str_fail));

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            statusFilter = getStatusFilter(arrayAdapter.getItem(which));
            tvStatus.setText(arrayAdapter.getItem(which));
        });
        builderSingle.show();
    }

    private String getStatusFilter(String status) {
        if (status.equals(getResources().getString(R.string.str_cash_take_success))) {
            return "0";
        } else if (status.equals(getResources().getString(R.string.str_fail))) {
            return "1";
        } else return Constant.STR_EMPTY;
    }
}
