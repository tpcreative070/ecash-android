package vn.ecpay.ewallet.ui.cashIn;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.ui.cashIn.adapter.CashValueAdapter;
import vn.ecpay.ewallet.ui.cashIn.module.CashInModule;
import vn.ecpay.ewallet.ui.cashIn.presenter.CashInPresenter;
import vn.ecpay.ewallet.ui.cashIn.view.CashInView;
import vn.ecpay.ewallet.ui.function.CashInFunction;
import vn.ecpay.ewallet.ui.interfaceListener.CashInSuccessListener;

public class CashInFragment extends ECashBaseFragment implements CashInView {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.iv_drop_down)
    ImageView ivDropDown;
    @BindView(R.id.tv_eDong_wallet)
    TextView tvEdongWallet;
    @BindView(R.id.layout_eDong)
    RelativeLayout layoutEDong;
    @BindView(R.id.tv_over_edong)
    TextView tvOverEdong;
    @BindView(R.id.rv_cash_values)
    RecyclerView rvCashValues;
    @BindView(R.id.tv_total_cash_in)
    TextView tvTotalCashIn;
    private AccountInfo accountInfo;
    private ArrayList<EdongInfo> listEDongInfo;
    private long balance;
    private long totalMoney;
    private EdongInfo eDongInfoCashIn;
    private List<Integer> listQuality;
    private List<Integer> listValue;
    private List<CashTotal> valuesListAdapter;
    private CashValueAdapter cashValueAdapter;

    @Inject
    CashInPresenter cashInPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cash_in;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashInModule(this)).inject(this);
        cashInPresenter.setView(this);
        cashInPresenter.onViewCreate();
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        listEDongInfo = ECashApplication.getListEDongInfo();
        setData();
    }

    private void setData() {
        setAdapter();
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));

        if (listEDongInfo.size() > 0) {
            eDongInfoCashIn = listEDongInfo.get(0);
            tvEdongWallet.setText(listEDongInfo.get(0).getAccountIdt());
            tvOverEdong.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEdong(listEDongInfo.get(0).getUsableBalance())));
        }
    }

    private void setAdapter() {
        valuesListAdapter = DatabaseUtil.getAllCashValues(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvCashValues.setLayoutManager(mLayoutManager);
        cashValueAdapter = new CashValueAdapter(valuesListAdapter, getActivity(), this::updateTotalMoney);
        rvCashValues.setAdapter(cashValueAdapter);
    }

    private void updateTotalMoney() {
        totalMoney = 0;
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            totalMoney = totalMoney + (valuesListAdapter.get(i).getTotal() * valuesListAdapter.get(i).getParValue());
        }
        tvTotalCashIn.setText(CommonUtils.formatPriceVND(totalMoney));
    }

    private void updateBalance() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (listEDongInfo.size() > 0) {
            for (int i = 0; i < listEDongInfo.size(); i++) {
                if (listEDongInfo.get(i).getAccountIdt().equals(eDongInfoCashIn.getAccountIdt())) {
                    tvOverEdong.setText(CommonUtils.formatPriceVND(listEDongInfo.get(i).getUsableBalance()));
                    tvEdongWallet.setText(listEDongInfo.get(i).getAccountIdt());
                }
            }
        } else {
            tvEdongWallet.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
            tvOverEdong.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEdong(listEDongInfo.get(0).getUsableBalance())));
        }
    }

    private void showDialogEDong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn tài khoản Edong");
        String[] eDong = new String[listEDongInfo.size()];
        for (int i = 0; i < listEDongInfo.size(); i++) {
            eDong[i] = String.valueOf(listEDongInfo.get(i).getAccountIdt());
        }

        builder.setItems(eDong, (dialog, which) -> {
            tvEdongWallet.setText(String.valueOf(listEDongInfo.get(which).getAccountIdt()));
            tvOverEdong.setText(String.valueOf(listEDongInfo.get(which).getUsableBalance()));
            eDongInfoCashIn = listEDongInfo.get(which);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        ((CashInActivity) getActivity()).updateTitle(getString(R.string.str_cash_in));
        super.onResume();
    }

    @OnClick({R.id.layout_eDong, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_eDong:
                if (listEDongInfo.size() > 1) {
                    showDialogEDong();
                }
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void validateData() {
        if (totalMoney == 0) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_not_select_amount));
            return;
        }
        listQuality = new ArrayList<>();
        listValue = new ArrayList<>();
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            if (valuesListAdapter.get(i).getTotal() > 0) {
                listQuality.add(valuesListAdapter.get(i).getTotal());
                listValue.add(valuesListAdapter.get(i).getParValue());
            }
        }
        cashInPresenter.transferMoneyEDongToECash(totalMoney, eDongInfoCashIn, listQuality, accountInfo, listValue);
    }

    private void showDialogCashInOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                getString(R.string.str_dialog_cash_in_success, CommonUtils.formatPriceVND(totalMoney), String.valueOf(eDongInfoCashIn.getAccountIdt())), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        setData();
                        updateBalance();
                        updateTotalMoney();
                    }

                    @Override
                    public void OnListenerCancel() {
                        if (getActivity() != null)
                            getActivity().finish();
                    }
                });
    }

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }

    @Override
    public void showDialogError(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @Override
    public void transferMoneySuccess(CashInResponse eDongToECash) {
        saveTransactionLogs(eDongToECash);
        cashInPresenter.getEDongInfo(accountInfo);
        CashInFunction cashInFunction = new CashInFunction(eDongToECash, accountInfo, getActivity());
        cashInFunction.handleCash(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    dismissProgress();
                    showDialogCashInOk();
                });
            }
        }, 500));
    }

    private void saveTransactionLogs(CashInResponse cashInResponse) {
        TransactionLog_Database transactionLog = new TransactionLog_Database();
        transactionLog.setSenderAccountId(cashInResponse.getSender());
        transactionLog.setReceiverAccountId(String.valueOf(cashInResponse.getReceiver()));
        transactionLog.setType(cashInResponse.getType());
        transactionLog.setTime(String.valueOf(cashInResponse.getTime()));
        transactionLog.setCashEnc(cashInResponse.getCashEnc());
        transactionLog.setTransactionSignature(cashInResponse.getId());
        transactionLog.setRefId(String.valueOf(cashInResponse.getRefId()));
        DatabaseUtil.saveTransactionLog(transactionLog, getActivity());
    }
}
