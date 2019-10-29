package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.TransactionHistory.TransactionsHistoryDetailActivity;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.AdapterCashLogTransactionsHistory;

import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_FAIL;
import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_SUCCESS;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_CASH_EXCHANGE;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_ECASH_TO_ECASH;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_ECASH_TO_EDONG;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_EDONG_TO_ECASH;

public class FragmentTransactionsHistoryDetail extends ECashBaseFragment {
    @BindView(R.id.tv_total_money_transfer)
    TextView tvTotalMoneyTransfer;
    @BindView(R.id.tv_human_code)
    TextView tvHumanCode;
    @BindView(R.id.tv_history_name)
    TextView tvHistoryName;
    @BindView(R.id.tv_history_phone)
    TextView tvHistoryPhone;
    @BindView(R.id.tv_history_type)
    TextView tvHistoryType;
    @BindView(R.id.tv_history_total)
    TextView tvHistoryTotal;
    @BindView(R.id.tv_history_content)
    TextView tvHistoryContent;
    @BindView(R.id.tv_history_date)
    TextView tvHistoryDate;
    @BindView(R.id.rv_list_cash_transfer)
    RecyclerView rvListCashTransfer;
    @BindView(R.id.layout_share)
    RelativeLayout layoutShare;
    @BindView(R.id.layout_download)
    RelativeLayout layoutDownload;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_transactions_status)
    TextView tvTransactionsStatus;
    @BindView(R.id.layout_qr_code)
    LinearLayout layoutQrCode;
    private TransactionsHistoryModel transactionsHistoryModel;
    private AdapterCashLogTransactionsHistory adapterCashLogTransactionsHistory;

    public static FragmentTransactionsHistoryDetail newInstance(TransactionsHistoryModel transactionsHistoryModel) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.TRANSACTIONS_HISTORY_MODEL, transactionsHistoryModel);
        FragmentTransactionsHistoryDetail fragment = new FragmentTransactionsHistoryDetail();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.transactions_history_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.transactionsHistoryModel = (TransactionsHistoryModel) bundle.getSerializable(Constant.TRANSACTIONS_HISTORY_MODEL);
            updateView();
            setAdapterListCash();
        }
    }

    private void updateView() {
        tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
        tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
        tvHistoryPhone.setText(transactionsHistoryModel.getReceiverPhone());
        tvHistoryTotal.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount())));
        tvHistoryContent.setText(transactionsHistoryModel.getTransactionContent());

        tvHistoryDate.setText(CommonUtils.getDateTransfer(getActivity(), transactionsHistoryModel.getTransactionDate()));

        switch (transactionsHistoryModel.getTransactionType()) {
            case TYPE_SEND_ECASH_TO_EDONG:
                layoutQrCode.setVisibility(View.GONE);
                tvType.setText(getResources().getString(R.string.str_cash_out));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_out));
                tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                        CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                break;
            case TYPE_ECASH_TO_ECASH:
                layoutBottom.setVisibility(View.VISIBLE);
                layoutQrCode.setVisibility(View.VISIBLE);
                if (transactionsHistoryModel.getCashLogType().equals(Constant.STR_CASH_IN)) {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                } else {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                }
                tvType.setText(getResources().getString(R.string.str_transfer));
                tvHistoryType.setText(getResources().getString(R.string.str_transfer));
                break;
            case TYPE_SEND_EDONG_TO_ECASH:
                layoutQrCode.setVisibility(View.GONE);
                tvType.setText(getResources().getString(R.string.str_cash_in));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_in));
                tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                        CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                break;
            case TYPE_CASH_EXCHANGE:
                layoutQrCode.setVisibility(View.GONE);
                tvType.setText(getResources().getString(R.string.str_cash_change));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_change));
                tvTotalMoneyTransfer.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount())));
                break;

        }

        if (Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_SUCCESS) {
            tvTransactionsStatus.setText(getResources().getString(R.string.str_cash_take_success));
        } else if (Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_FAIL) {
            tvTransactionsStatus.setText(getResources().getString(R.string.str_fail));
        }
    }

    private void setAdapterListCash() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        List<CashLogTransaction> listCashLogTransaction = WalletDatabase.getAllCashByTransactionLog(transactionsHistoryModel.getTransactionSignature());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvListCashTransfer.setLayoutManager(mLayoutManager);
        adapterCashLogTransactionsHistory = new AdapterCashLogTransactionsHistory(listCashLogTransaction, getActivity());
        rvListCashTransfer.setAdapter(adapterCashLogTransactionsHistory);
    }

    private void generateQRCode(){

    }

    @OnClick({R.id.layout_share, R.id.layout_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_share:
                break;
            case R.id.layout_download:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TransactionsHistoryDetailActivity) getActivity()).updateTitle(getResources().getString(R.string.str_transactions_history_detail));
    }
}
