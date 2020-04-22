package vn.ecpay.ewallet.ui.cashOut;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
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
import java.util.Collections;
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
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.ui.callbackListener.UpdateMasterKeyListener;
import vn.ecpay.ewallet.ui.cashOut.adapter.CashOutAdapter;
import vn.ecpay.ewallet.ui.cashOut.module.CashOutModule;
import vn.ecpay.ewallet.ui.cashOut.presenter.CashOutPresenter;
import vn.ecpay.ewallet.ui.cashOut.view.CashOutView;
import vn.ecpay.ewallet.ui.function.SyncCashService;
import vn.ecpay.ewallet.ui.function.UpdateMasterKeyFunction;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

import static vn.ecpay.ewallet.common.utils.Constant.EVENT_CASH_OUT_MONEY;

public class CashOutFragment extends ECashBaseFragment implements CashOutView {

    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.iv_drop_down)
    ImageView ivDropDown;
    @BindView(R.id.tv_eDong_wallet)
    TextView tvEDongWallet;
    @BindView(R.id.layout_eDong)
    RelativeLayout layoutEDong;
    @BindView(R.id.tv_over_edong)
    TextView tvOverEdong;
    @BindView(R.id.tv_edong)
    TextView tvEdong;
    @BindView(R.id.layout_eDong_send)
    RelativeLayout layoutEDongSend;
    @BindView(R.id.rv_cash_values)
    RecyclerView rvCashValues;
    @BindView(R.id.tv_total_send)
    TextView tvTotalSend;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private AccountInfo accountInfo;
    private ArrayList<EdongInfo> listEDongInfo;
    private EdongInfo edongInfo;
    private long balance;
    private long totalMoney;
    private String encData;
    private String refId;
    private String publicKeyOrganization;
    private ResponseMessSocket responseMess;
    private ArrayList<CashLogs_Database> listCashSend;
    private List<CashTotal> valuesListAdapter;
    private CashOutAdapter cashOutAdapter;
    private long mLastClickTime = 0;

    @Inject
    CashOutPresenter cashOutPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cash_out_pickup;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != getActivity()) {
            getActivity().startService(new Intent(getActivity(), SyncCashService.class));
            ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashOutModule(this)).inject(this);
        }
        cashOutPresenter.setView(this);
        cashOutPresenter.onViewCreate();
        accountInfo = DatabaseUtil.getAccountInfo(getActivity());
        if (null == accountInfo) {
            CommonUtils.restartApp((CashOutActivity) getActivity());
        }
        setData();
        cashOutPresenter.getPublicKeyOrganization(getActivity(), accountInfo, false);
    }

    private void setData() {
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (listEDongInfo.size() > 0) {
            edongInfo = listEDongInfo.get(0);
            tvEDongWallet.setText(listEDongInfo.get(0).getAccountIdt());
            tvOverEdong.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEDong(listEDongInfo.get(0))));
            tvEdong.setText(listEDongInfo.get(0).getAccountIdt());
        }
        Utils.disableButtonConfirm(getActivity(), btnConfirm, true);
        updateMoneyCancelAccount();
    }

    private void updateMoneyCancelAccount() {
        valuesListAdapter = DatabaseUtil.getAllCashTotal(getActivity());
        Collections.reverse(valuesListAdapter);
        if (ECashApplication.isCancelAccount) {
            for (int i = 0; i < valuesListAdapter.size(); i++) {
                valuesListAdapter.get(i).setTotal(valuesListAdapter.get(i).getTotalDatabase());
                valuesListAdapter.get(i).setTotalDatabase(0);
            }
            Utils.disableButtonConfirm(getActivity(), btnConfirm, false);
        }
        setAdapter();
        updateTotalMoney();
    }

    private void setAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvCashValues.setLayoutManager(mLayoutManager);
        cashOutAdapter = new CashOutAdapter(valuesListAdapter, getActivity(), this::updateTotalMoney);
        rvCashValues.setAdapter(cashOutAdapter);
    }

    private void updateTotalMoney() {
        totalMoney = 0;
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            totalMoney = totalMoney + (valuesListAdapter.get(i).getTotal() * valuesListAdapter.get(i).getParValue());
        }
        tvTotalSend.setText(CommonUtils.formatPriceVND(totalMoney));
        if (totalMoney > 0) {
            Utils.disableButtonConfirm(getActivity(), btnConfirm, false);
        } else {
            Utils.disableButtonConfirm(getActivity(), btnConfirm, true);
        }
    }

    private void showDialogEDong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.str_chose_edong_account));
        String[] eDong = new String[listEDongInfo.size()];
        for (int i = 0; i < listEDongInfo.size(); i++) {
            eDong[i] = listEDongInfo.get(i).getAccountIdt();
        }
        builder.setItems(eDong, (dialog, which) -> {
            tvEDongWallet.setText(listEDongInfo.get(which).getAccountIdt());
            tvOverEdong.setText(String.valueOf(listEDongInfo.get(which).getUsableBalance()));
            edongInfo = listEDongInfo.get(which);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogCashOutOk() {
        DialogUtil.getInstance().showDialogContinueAndExit(getActivity(), getString(R.string.str_transaction_success),
                getResources().getString(R.string.str_dialog_cash_out_success, CommonUtils.formatPriceVND(totalMoney)), getResources().getColor(R.color.green), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        setData();
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
    public void onResume() {
        if (getActivity() != null)
            ((CashOutActivity) getActivity()).updateTitle(getString(R.string.str_cash_out));
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                validateData();
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void validateData() {
        if (totalMoney == 0) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_chose_money));
            return;
        }
        if (null == publicKeyOrganization) {
            cashOutPresenter.getPublicKeyOrganization(getActivity(), accountInfo, true);
        }

        showLoading();
        UpdateMasterKeyFunction updateMasterKeyFunction = new UpdateMasterKeyFunction(ECashApplication.getActivity());
        updateMasterKeyFunction.updateLastTimeAndMasterKey(new UpdateMasterKeyListener() {
            @Override
            public void onUpdateMasterSuccess() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        showProgress();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        getCashEncrypt();
                        return null;
                    }
                }.execute();
            }

            @Override
            public void onUpdateMasterFail(String code) {
                dismissLoading();
                CheckErrCodeUtil.errorMessage(getActivity(), code);
            }

            @Override
            public void onRequestTimeout() {

            }

        });
    }


    private void getCashEncrypt() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        listCashSend = new ArrayList<>();
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            if (valuesListAdapter.get(i).getTotal() > 0) {
                List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney(String.valueOf(valuesListAdapter.get(i).getParValue()), Constant.STR_CASH_IN);
                for (int j = 0; j < valuesListAdapter.get(i).getTotal(); j++) {
                    listCashSend.add(cashList.get(j));
                }
            }
        }

        if (listCashSend.size() > 0) {
            String[][] cashSendArray = new String[listCashSend.size()][3];
            for (int i = 0; i < listCashSend.size(); i++) {
                CashLogs_Database cash = listCashSend.get(i);
                String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                cashSendArray[i] = moneyItem;
            }

            encData = CommonUtils.getEncrypData(cashSendArray, publicKeyOrganization);
            responseMess = new ResponseMessSocket();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(Constant.CREDIT_DEBIT_EWALLET);
            responseMess.setTime(CommonUtils.getCurrentTime());
            responseMess.setType(Constant.TYPE_SEND_ECASH_TO_EDONG);
            responseMess.setContent(Constant.STR_EMPTY);
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getId(responseMess, getActivity()));
            refId = CommonUtils.getId(responseMess, getActivity());
            responseMess.setRefId(refId);
            if (refId.isEmpty() || encData.isEmpty()) {
                dismissProgress();
                DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_upload));
                return;
            }
            cashOutPresenter.sendECashToEDong(getActivity(), encData, refId, totalMoney, edongInfo, accountInfo);
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
    public void loadPublicKeyOrganizeSuccess(String publicKeyOrganization) {
        this.publicKeyOrganization = publicKeyOrganization;
    }

    @Override
    public void loadPublicKeyOrganizeSuccessForValidate(String issuerKpValue) {
        this.publicKeyOrganization = publicKeyOrganization;
        validateData();
    }

    @Override
    public void showDialogError(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void sendECashToEDongSuccess() {
        EventBus.getDefault().postSticky(new EventDataChange(EVENT_CASH_OUT_MONEY, responseMess, listCashSend));
    }

    @Override
    public void getEDongInfoSuccess() {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (ECashApplication.isCancelAccount) {
                    handleCancelAccount();
                } else {
                    dismissProgress();
                    showDialogCashOutOk();
                    EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_BALANCE));
                }
            });
    }

    private void handleCancelAccount() {
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        if (balance == 0) {
            dismissProgress();
            if (getActivity() != null)
                getActivity().onBackPressed();
        } else {
            dismissProgress();
            showDialogCashOutOk();
            EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_BALANCE));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)) {
            reloadData();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void reloadData() {
        if (WalletDatabase.numberRequest == 0) {
            cashOutPresenter.getEDongInfo(accountInfo, getActivity());
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    reloadData();
                }
            }, 1000);
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }
}
