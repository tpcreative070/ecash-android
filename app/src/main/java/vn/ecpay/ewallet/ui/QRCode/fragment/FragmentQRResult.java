package vn.ecpay.ewallet.ui.QRCode.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.TransactionHistory.fragment.FragmentTransactionsHistoryDetail;

public class FragmentQRResult extends FragmentTransactionsHistoryDetail {
    @BindView(R.id.toolbar_center_text)
    TextView toolbar_center_text;

    public static FragmentQRResult newInstance(TransactionsHistoryModel transactionsHistoryModel) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.TRANSACTIONS_HISTORY_MODEL, transactionsHistoryModel);
        FragmentQRResult fragment = new FragmentQRResult();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_scan_qr_result;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar_center_text.setText(getResources().getString(R.string.str_transactions_history_detail));
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ((QRCodeActivity)getActivity()).onBackPressed();
    }
}
