package vn.ecpay.ewallet.ui.cashChange.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.ui.cashChange.CashChangeActivity;
import vn.ecpay.ewallet.ui.cashChange.module.CashChangeModule;
import vn.ecpay.ewallet.ui.cashChange.presenter.CashChangePresenter;
import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;
import vn.ecpay.ewallet.ui.cashOut.CashOutActivity;
import vn.ecpay.ewallet.ui.function.CashInFunction;

import static vn.ecpay.ewallet.common.utils.CommonUtils.getEncrypData;

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
    @BindView(R.id.layout_500)
    RelativeLayout layout500;
    @BindView(R.id.layout_200)
    RelativeLayout layout200;
    @BindView(R.id.layout_100)
    RelativeLayout layout100;
    @BindView(R.id.layout_50)
    RelativeLayout layout50;
    @BindView(R.id.layout_20)
    RelativeLayout layout20;
    @BindView(R.id.layout_10)
    RelativeLayout layout10;
    private String publicKeyOrganization;
    private AccountInfo accountInfo;
    private long balance;
    private int slDatabase500, slDatabase200, slDatabase100, slDatabase50, slDatabase20, slDatabase10;
    private int totalChange500 = 0, totalChange200 = 0, totalChange100 = 0, totalChange50 = 0, totalChange20 = 0, totalChange10 = 0;
    private int totalTake500 = 0, totalTake200 = 0, totalTake100 = 0, totalTake50 = 0, totalTake20 = 0, totalTake10 = 0;
    private int totalMoneyChange, totalMoneyTake;
    private List<Integer> listQualitySend;
    private List<Integer> listValueSend;

    private List<Integer> listQualityTake;
    private List<Integer> listValueTake;
    private ArrayList<CashLogs_Database> listCashSend;
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
        cashChangePresenter.getPublicKeyOrganization(getActivity(), accountInfo);
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

        if (slDatabase500 > 0) {
            layout500.setVisibility(View.VISIBLE);
        } else {
            layout500.setVisibility(View.GONE);
        }

        if (slDatabase200 > 0) {
            layout200.setVisibility(View.VISIBLE);
        } else {
            layout200.setVisibility(View.GONE);
        }

        if (slDatabase100 > 0) {
            layout100.setVisibility(View.VISIBLE);
        } else {
            layout100.setVisibility(View.GONE);
        }

        if (slDatabase50 > 0) {
            layout50.setVisibility(View.VISIBLE);
        } else {
            layout50.setVisibility(View.GONE);
        }

        if (slDatabase20 > 0) {
            layout20.setVisibility(View.VISIBLE);
        } else {
            layout20.setVisibility(View.GONE);
        }

        if (slDatabase10 > 0) {
            layout10.setVisibility(View.VISIBLE);
        } else {
            layout10.setVisibility(View.GONE);
        }
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
                DialogUtil.getInstance().showDialogChangeCash(true, getActivity(), accountInfo, getString(R.string.str_cash_change), new DialogUtil.OnResultChoseCash() {
                    @Override
                    public void OnListenerOk(int sl500, int sl200, int sl100, int sl50, int sl20, int sl10) {
                        totalChange10 = sl10;
                        totalChange20 = sl20;
                        totalChange50 = sl50;
                        totalChange100 = sl100;
                        totalChange200 = sl200;
                        totalChange500 = sl500;
                        layoutChange.setVisibility(View.VISIBLE);
                        btnCashChange.setBackgroundResource(R.drawable.bg_border_red);
                        btnCashChange.setTextColor(getResources().getColor(R.color.red));
                        btnCashTake.setBackgroundResource(R.drawable.bg_border_red);
                        btnCashTake.setTextColor(getResources().getColor(R.color.red));
                        updateTotalMoneyChangeAndTake();
                    }
                });
                break;
            case R.id.btn_cash_take:
                if (totalMoneyChange == 0) {
                    DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_chose_money_transfer));
                    return;
                }
                DialogUtil.getInstance().showDialogChangeCash(false, getActivity(), accountInfo, getString(R.string.str_cash_take), new DialogUtil.OnResultChoseCash() {
                    @Override
                    public void OnListenerOk(int sl500, int sl200, int sl100, int sl50, int sl20, int sl10) {
                        totalTake10 = sl10;
                        totalTake20 = sl20;
                        totalTake50 = sl50;
                        totalTake100 = sl100;
                        totalTake200 = sl200;
                        totalTake500 = sl500;
                        layoutChange.setVisibility(View.VISIBLE);
                        btnCashChange.setBackgroundResource(R.drawable.bg_border_red);
                        btnCashChange.setTextColor(getResources().getColor(R.color.red));
                        btnCashTake.setBackgroundResource(R.drawable.bg_border_red);
                        btnCashTake.setTextColor(getResources().getColor(R.color.red));
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
        if (null == publicKeyOrganization) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_get_public_key_organize));
            return;
        }
        if (totalMoneyChange == 0) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_chose_money_transfer));
            return;
        }
        if (totalMoneyTake == 0) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_chose_money_take));
            return;
        }
        if (totalMoneyChange != totalMoneyTake) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_conflict_take_and_change));
            return;
        }

        DialogUtil.getInstance().showDialogConfirmChangeCash(totalChange500, totalChange200, totalChange100, totalChange50, totalChange20, totalChange10,
                totalTake500, totalTake200, totalTake100, totalTake50, totalTake20, totalTake10, getActivity(), () -> {
                    showLoading();
                    getListCashSend();
                    getListCashTake();
                    getCashEncrypt(totalChange10, totalChange20, totalChange50, totalChange100, totalChange200, totalChange500, publicKeyOrganization);
                });
    }

    private void getListCashSend() {
        listQualitySend = new ArrayList<>();
        listValueSend = new ArrayList<>();
        if (totalChange10 > 0) {
            listQualitySend.add(totalChange10);
            listValueSend.add(10000);
        }
        if (totalChange20 > 0) {
            listQualitySend.add(totalChange20);
            listValueSend.add(20000);
        }
        if (totalChange50 > 0) {
            listQualitySend.add(totalChange50);
            listValueSend.add(50000);
        }
        if (totalChange100 > 0) {
            listQualitySend.add(totalChange100);
            listValueSend.add(100000);
        }
        if (totalChange200 > 0) {
            listQualitySend.add(totalChange200);
            listValueSend.add(200000);
        }
        if (totalChange500 > 0) {
            listQualitySend.add(totalChange500);
            listValueSend.add(500000);
        }
    }

    private void getListCashTake() {
        listQualityTake = new ArrayList<>();
        listValueTake = new ArrayList<>();
        if (totalTake10 > 0) {
            listQualityTake.add(totalTake10);
            listValueTake.add(10000);
        }
        if (totalTake20 > 0) {
            listQualityTake.add(totalTake20);
            listValueTake.add(20000);
        }
        if (totalTake50 > 0) {
            listQualityTake.add(totalTake50);
            listValueTake.add(50000);
        }
        if (totalTake100 > 0) {
            listQualityTake.add(totalTake100);
            listValueTake.add(100000);
        }
        if (totalTake200 > 0) {
            listQualityTake.add(totalTake200);
            listValueTake.add(200000);
        }
        if (totalTake500 > 0) {
            listQualityTake.add(totalTake500);
            listValueTake.add(500000);
        }
    }

    private void getCashEncrypt(int sl10, int sl20, int sl50, int sl100, int sl200, int sl500,
                                String keyPublicReceiver) {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        listCashSend = new ArrayList<>();
        if (sl10 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("10000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl10; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (sl20 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("20000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl20; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl50 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("50000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl50; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl100 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("100000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl100; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl200 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("200000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl200; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (sl500 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("500000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl500; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (listCashSend.size() > 0) {
            String[][] cashSendArray = new String[listCashSend.size()][3];
            for (int i = 0; i < listCashSend.size(); i++) {
                CashLogs_Database cash = listCashSend.get(i);
                String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                cashSendArray[i] = moneyItem;
            }

            String encData = getEncrypData(cashSendArray, keyPublicReceiver);
            if (encData.isEmpty()) {
                dismissProgress();
                ((CashOutActivity) getActivity()).showDialogError("không lấy được endCrypt data và ID");
                return;
            }
            cashChangePresenter.requestChangeCash(encData, listQualityTake, accountInfo, listValueTake);
        } else {
            dismissProgress();
        }
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
        ((CashChangeActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void loadPublicKeyOrganizeSuccess(String issuerKpValue) {
        this.publicKeyOrganization = issuerKpValue;
    }

    @Override
    public void changeCashSuccess(CashInResponse cashInResponse) {
        saveTransactionLogs(cashInResponse);
        //update money send
        saveCashChangeSend(cashInResponse);
        //start service
//        startService(cashInResponse);
        CashInFunction cashInFunction = new CashInFunction(cashInResponse, accountInfo, getActivity());
        cashInFunction.handelCash();
    }

    private void saveCashChangeSend(CashInResponse cashInResponse) {
        for (int i = 0; i < listCashSend.size(); i++) {
            CashLogs_Database cashLogs = listCashSend.get(i);
            cashLogs.setTransactionSignature(cashInResponse.getId());
            cashLogs.setType(Constant.STR_CASH_OUT);
            DatabaseUtil.saveCashToDB(cashLogs, getActivity(), accountInfo.getUsername());
        }
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

//    private void startService(CashInResponse responseData) {
//        Intent intent = new Intent(getActivity(), EDongToECashService.class);
//        intent.putExtra(Constant.EDONG_TO_ECASH, responseData);
//        intent.putExtra(Constant.ACCOUNT_INFO, accountInfo);
//        if (getActivity() != null) {
//            getActivity().startService(intent);
//        }
//
//
//    }

    private void showDialogCashChangeOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success), "Đổi tiền thành công", new DialogUtil.OnConfirm() {
            @Override
            public void OnListenerOk() {
                getMoneyDatabase();
                layoutChange.setVisibility(View.GONE);
                btnCashChange.setBackgroundResource(R.drawable.bg_border_blue);
                btnCashChange.setTextColor(getResources().getColor(R.color.blue));
                btnCashTake.setBackgroundResource(R.drawable.bg_border_blue);
                btnCashTake.setTextColor(getResources().getColor(R.color.blue));
            }

            @Override
            public void OnListenerCancel() {
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.UPDATE_MONEY) ||
                event.getData().equals(Constant.UPDATE_MONEY_SOCKET)) {
            dismissProgress();
            showDialogCashChangeOk();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }
}
