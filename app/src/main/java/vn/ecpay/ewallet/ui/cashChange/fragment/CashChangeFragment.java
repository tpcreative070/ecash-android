package vn.ecpay.ewallet.ui.cashChange.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.cashChange.CashChangeActivity;
import vn.ecpay.ewallet.ui.cashChange.module.CashChangeModule;
import vn.ecpay.ewallet.ui.cashChange.presenter.CashChangePresenter;
import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;

public class CashChangeFragment extends ECashBaseFragment implements CashChangeView {

    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.tv_500)
    TextView tv500;
    @BindView(R.id.tv_number_500)
    TextView tvNumber500;
    @BindView(R.id.tv_200)
    TextView tv200;
    @BindView(R.id.tv_number_200)
    TextView tvNumber200;
    @BindView(R.id.tv_100)
    TextView tv100;
    @BindView(R.id.tv_number_100)
    TextView tvNumber100;
    @BindView(R.id.tv_50)
    TextView tv50;
    @BindView(R.id.tv_number_50)
    TextView tvNumber50;
    @BindView(R.id.tv_20)
    TextView tv20;
    @BindView(R.id.tv_number_20)
    TextView tvNumber20;
    @BindView(R.id.tv_10)
    TextView tv10;
    @BindView(R.id.tv_number_10)
    TextView tvNumber10;
    @BindView(R.id.btn_cash_change)
    Button btnCashChange;
    @BindView(R.id.btn_cash_take)
    Button btnCashTake;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.layout_change)
    LinearLayout layoutChange;
    @BindView(R.id.tv_total_money_cash_change)
    TextView tvTotalMoneyCashChange;
    @BindView(R.id.tv_total_money_cash_take)
    TextView tvTotalMoneyCashTake;
    private AccountInfo accountInfo;
    private long balance;
    private int slDatabase500, slDatabase200, slDatabase100, slDatabase50, slDatabase20, slDatabase10;
    private int totalChange500 = 0, totalChange200 = 0, totalChange100 = 0, totalChange50 = 0, totalChange20 = 0, totalChange10 = 0;
    private int totalTake500 = 0, totalTake200 = 0, totalTake100 = 0, totalTake50 = 0, totalTake20 = 0, totalTake10 = 0;
    private int totalMoneyChange, totalMoneyTake;
    private ArrayList<CashLogs> listCashSend;
    @Inject
    CashChangePresenter cashChangePresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.cash_change_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashChangeModule(this)).inject(this);
        cashChangePresenter.setView(this);
        cashChangePresenter.onViewCreate();
        setData();
        getMoneyDatabase();
    }

    private void setData() {
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
    }

    private void getMoneyDatabase() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        slDatabase500 = WalletDatabase.getTotalMoney("500000", Constant.STR_CASH_IN);
        slDatabase200 = WalletDatabase.getTotalMoney("200000", Constant.STR_CASH_IN);
        slDatabase100 = WalletDatabase.getTotalMoney("100000", Constant.STR_CASH_IN);
        slDatabase50 = WalletDatabase.getTotalMoney("50000", Constant.STR_CASH_IN);
        slDatabase20 = WalletDatabase.getTotalMoney("20000", Constant.STR_CASH_IN);
        slDatabase10 = WalletDatabase.getTotalMoney("10000", Constant.STR_CASH_IN);

        tvNumber500.setText(String.valueOf(slDatabase500));
        tvNumber200.setText(String.valueOf(slDatabase200));
        tvNumber100.setText(String.valueOf(slDatabase100));
        tvNumber50.setText(String.valueOf(slDatabase50));
        tvNumber20.setText(String.valueOf(slDatabase20));
        tvNumber10.setText(String.valueOf(slDatabase10));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CashChangeActivity) getActivity()).updateTitle(getString(R.string.str_change_cash));
    }

    @OnClick({R.id.btn_cash_change, R.id.btn_cash_take, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cash_change:
                DialogUtil.getInstance().showDialogChoseCash(true, getActivity(), accountInfo, getString(R.string.str_cash_change), new DialogUtil.OnResultChoseCash() {
                    @Override
                    public void OnListenerOk(int sl500, int sl200, int sl100, int sl50, int sl20, int sl10) {
                        totalChange10 = sl10;
                        totalChange20 = sl20;
                        totalChange50 = sl50;
                        totalChange100 = sl100;
                        totalChange200 = sl200;
                        totalChange500 = sl500;
                        layoutChange.setVisibility(View.VISIBLE);
                        updateTotalMoneyChangeAndTake();
                    }
                });
                break;
            case R.id.btn_cash_take:
                if (totalMoneyChange == 0) {
                    ((CashChangeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_chose_money_transfer));
                    return;
                }
                DialogUtil.getInstance().showDialogChoseCash(false, getActivity(), accountInfo, getString(R.string.str_cash_take), new DialogUtil.OnResultChoseCash() {
                    @Override
                    public void OnListenerOk(int sl500, int sl200, int sl100, int sl50, int sl20, int sl10) {
                        totalTake10 = sl10;
                        totalTake20 = sl20;
                        totalTake50 = sl50;
                        totalTake100 = sl100;
                        totalTake200 = sl200;
                        totalTake500 = sl500;
                        layoutChange.setVisibility(View.VISIBLE);
                        updateTotalMoneyChangeAndTake();
                    }
                });
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void updateTotalMoneyChangeAndTake() {
        totalMoneyChange = totalChange500 * 500000 + totalChange200 * 200000 + totalChange100 * 100000 + totalChange50 * 50000 + totalChange20 * 20000 + totalChange10 * 10000;
        tvTotalMoneyCashChange.setText(CommonUtils.formatPriceVND(totalMoneyChange));

        totalMoneyTake = totalTake500 * 500000 + totalTake200 * 200000 + totalTake100 * 100000 + totalTake50 * 50000 + totalTake20 * 20000 + totalTake10 * 10000;
        tvTotalMoneyCashTake.setText(CommonUtils.formatPriceVND(totalMoneyTake));
    }

    private void validateData() {
        if (totalMoneyChange == 0) {
            ((CashChangeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_chose_money_transfer));
            return;
        }
        if (totalMoneyTake == 0) {
            ((CashChangeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_chose_money_take));
            return;
        }
        if (totalMoneyChange != totalMoneyTake) {
            ((CashChangeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_conflict_take_and_change));
            return;
        }
        cashChangePresenter.requestChangeCash();
    }

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }
}
