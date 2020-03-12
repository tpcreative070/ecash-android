package vn.ecpay.ewallet.ui.TransactionHistory;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.TransactionHistory.fragment.FragmentTransactionsHistoryDetail;

public class TransactionsHistoryDetailActivity extends ECashBaseActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.lixi_activity;
    }

    @Override
    protected void setupActivityComponent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle content = intent.getExtras();
        if (null != content) {
            TransactionsHistoryModel transactionsHistoryModel = (TransactionsHistoryModel) content.getSerializable(Constant.TRANSACTIONS_HISTORY_MODEL);
            addFragment(FragmentTransactionsHistoryDetail.newInstance(transactionsHistoryModel), false);
        }
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }
}