package vn.ecpay.ewallet.ui.QRCode.fragment;

import android.os.Bundle;

import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.TransactionHistory.fragment.FragmentTransactionsHistoryDetail;

public class FragmentQRResult extends FragmentTransactionsHistoryDetail {

    public static FragmentQRResult newInstance(TransactionsHistoryModel transactionsHistoryModel) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.TRANSACTIONS_HISTORY_MODEL, transactionsHistoryModel);
        FragmentQRResult fragment = new FragmentQRResult();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ((QRCodeActivity)getActivity()).onBackPressed();
    }
}
