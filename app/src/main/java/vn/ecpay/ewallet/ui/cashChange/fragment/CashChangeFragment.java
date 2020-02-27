package vn.ecpay.ewallet.ui.cashChange.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

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
import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.ui.adapter.CashTotalChangeAdapter;
import vn.ecpay.ewallet.ui.adapter.CashTotalConfirmAdapter;
import vn.ecpay.ewallet.ui.cashChange.CashChangeActivity;
import vn.ecpay.ewallet.ui.cashChange.module.CashChangeModule;
import vn.ecpay.ewallet.ui.cashChange.presenter.CashChangePresenter;
import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;
import vn.ecpay.ewallet.ui.cashOut.CashOutActivity;
import vn.ecpay.ewallet.ui.function.SyncCashService;
import vn.ecpay.ewallet.ui.function.UpdateMasterKeyFunction;
import vn.ecpay.ewallet.ui.interfaceListener.UpdateMasterKeyListener;

import static vn.ecpay.ewallet.common.utils.CommonUtils.getEncrypData;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_CASH_EXCHANGE;

public class CashChangeFragment extends ECashBaseFragment implements CashChangeView {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverECash;
    @BindView(R.id.rv_cash_values)
    RecyclerView rvCashValues;
    @BindView(R.id.tv_total_money_cash_change)
    TextView tvTotalMoneyCashChange;
    @BindView(R.id.tv_total_money_cash_take)
    TextView tvTotalMoneyCashTake;
    @BindView(R.id.layout_change)
    LinearLayout layoutChange;
    @BindView(R.id.btn_cash_change)
    Button btnCashChange;
    @BindView(R.id.btn_cash_take)
    Button btnCashTake;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private String publicKeyOrganization;
    private AccountInfo accountInfo;
    private long balance;
    private long totalMoneyChange, totalMoneyTake;
    private List<Integer> listQualitySend;
    private List<Integer> listValueSend;

    private List<Integer> listQualityTake;
    private List<Integer> listValueTake;
    private ArrayList<CashLogs_Database> listCashSend;
    @Inject
    CashChangePresenter cashChangePresenter;
    private List<CashTotal> valuesListAdapter;
    private List<CashTotal> valueListCashChange;
    private List<CashTotal> valueListCashTake;
    private CashTotalChangeAdapter cashValueAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.cash_change_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        if (getActivity() != null)
            ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashChangeModule(this)).inject(this);
        cashChangePresenter.setView(this);
        cashChangePresenter.onViewCreate();
        setData();
        cashChangePresenter.getPublicKeyOrganization(getActivity(), accountInfo);
    }

    private void setData() {
        setAdapter();
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverECash.setText(CommonUtils.formatPriceVND(balance));
    }

    private void setAdapter() {
        valuesListAdapter = DatabaseUtil.getAllCashTotal(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvCashValues.setLayoutManager(mLayoutManager);
        cashValueAdapter = new CashTotalChangeAdapter(valuesListAdapter, getActivity());
        rvCashValues.setAdapter(cashValueAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            ((CashChangeActivity) getActivity()).updateTitle(getString(R.string.str_change_cash));
    }

    @OnClick({R.id.btn_cash_change, R.id.btn_cash_take, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cash_change:
                if (valuesListAdapter.size() == 0)
                    return;
                DialogUtil.getInstance().showDialogCashChange(getActivity(), valuesListCash -> {
                    valueListCashChange = valuesListCash;
                    layoutChange.setVisibility(View.VISIBLE);
                    btnCashChange.setBackgroundResource(R.drawable.bg_border_red);
                    btnCashChange.setTextColor(getResources().getColor(R.color.red));
                    btnCashTake.setBackgroundResource(R.drawable.bg_border_red);
                    btnCashTake.setTextColor(getResources().getColor(R.color.red));
                    updateTotalMoneyChangeAndTake();
                });
                break;
            case R.id.btn_cash_take:
                if (totalMoneyChange == 0) {
                    DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_chose_money_transfer));
                    return;
                }
                DialogUtil.getInstance().showDialogCashTake(getActivity(), valuesListCash -> {
                    valueListCashTake = valuesListCash;
                    layoutChange.setVisibility(View.VISIBLE);
                    btnCashChange.setBackgroundResource(R.drawable.bg_border_red);
                    btnCashChange.setTextColor(getResources().getColor(R.color.red));
                    btnCashTake.setBackgroundResource(R.drawable.bg_border_red);
                    btnCashTake.setTextColor(getResources().getColor(R.color.red));
                    updateTotalMoneyChangeAndTake();
                });
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void updateTotalMoneyChangeAndTake() {
        totalMoneyChange = CommonUtils.getTotalMoney(valueListCashChange);
        totalMoneyTake = CommonUtils.getTotalMoney(valueListCashTake);
        tvTotalMoneyCashChange.setText(CommonUtils.formatPriceVND(totalMoneyChange));
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
        showDialog();
    }

    @SuppressLint("StaticFieldLeak")
    private void showDialog() {
        Dialog mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.dialog_confirm_change_cash);

        Button btnConfirm;
        RecyclerView rv_cash_take, rv_cash_change;
        btnConfirm = mDialog.findViewById(R.id.btn_confirm);
        TextView tv_total_money_send = mDialog.findViewById(R.id.tv_total_money_send);
        TextView tv_total_money_take = mDialog.findViewById(R.id.tv_total_money_take);
        rv_cash_take = mDialog.findViewById(R.id.rv_cash_take);
        rv_cash_change = mDialog.findViewById(R.id.rv_cash_change);

        tv_total_money_send.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valueListCashChange)));
        tv_total_money_take.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valueListCashTake)));

        rv_cash_take.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_cash_change.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_cash_change.setAdapter(new CashTotalConfirmAdapter(CommonUtils.getListCashConfirm(valueListCashChange), getActivity()));
        rv_cash_take.setAdapter(new CashTotalConfirmAdapter(CommonUtils.getListCashConfirm(valueListCashTake), getActivity()));

        btnConfirm.setOnClickListener(v -> {
            mDialog.dismiss();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    showProgress();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    getListCashSend();
                    getListCashTake();
                    getCashChangeEncrypt(publicKeyOrganization);
                    return null;
                }
            }.execute();
        });

        mDialog.show();
    }

    private void getListCashSend() {
        listQualitySend = new ArrayList<>();
        listValueSend = new ArrayList<>();
        for (int i = 0; i < valueListCashChange.size(); i++) {
            if (valueListCashChange.get(i).getTotal() > 0) {
                // Log.e("valueListCashChange ",valueListCashChange.get(i).getParValue()+"");
                listQualitySend.add(valueListCashChange.get(i).getTotal());
                listValueSend.add(valueListCashChange.get(i).getParValue());
            }
        }
    }

    private void getListCashTake() {
        listQualityTake = new ArrayList<>();
        listValueTake = new ArrayList<>();
        for (int i = 0; i < valueListCashTake.size(); i++) {
            if (valueListCashTake.get(i).getTotal() > 0) {
                Log.e("valueListCashTake ", valueListCashTake.get(i).getParValue() + "");
                listQualityTake.add(valueListCashTake.get(i).getTotal());
                listValueTake.add(valueListCashTake.get(i).getParValue());
            }
        }
    }

    private void getCashChangeEncrypt(String keyPublicReceiver) {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        listCashSend = new ArrayList<>();
        for (int i = 0; i < valueListCashChange.size(); i++) {
            if (valueListCashChange.get(i).getTotal() > 0) {
                // Log.e("valueListCashChange ",valueListCashChange.get(i).getParValue()+"");
                List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney(String.valueOf(valueListCashChange.get(i).getParValue()), Constant.STR_CASH_IN);
                for (int j = 0; j < valueListCashChange.get(i).getTotal(); j++) {
                    listCashSend.add(cashList.get(j));
                }
            }
        }

        String[][] cashSendArray = new String[listCashSend.size()][3];
        for (int i = 0; i < listCashSend.size(); i++) {
            CashLogs_Database cash = listCashSend.get(i);
            String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
            cashSendArray[i] = moneyItem;
        }

        String encData = getEncrypData(cashSendArray, keyPublicReceiver);
        if (encData.isEmpty()) {
            dismissProgress();
            if (getActivity() != null)
                ((CashOutActivity) getActivity()).showDialogError("không lấy được endCrypt data và ID");
            return;
        }
        UpdateMasterKeyFunction updateMasterKeyFunction = new UpdateMasterKeyFunction(getActivity());
        showLoading();
        updateMasterKeyFunction.updateLastTimeAndMasterKey(new UpdateMasterKeyListener() {
            @Override
            public void onUpdateMasterSuccess() {
                cashChangePresenter.requestChangeCash(encData, listQualityTake, accountInfo, listValueTake);
            }

            @Override
            public void onUpdateMasterFail() {
                dismissLoading();
                showDialogError(getResources().getString(R.string.err_upload));
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
        ((CashChangeActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void loadPublicKeyOrganizeSuccess(String issuerKpValue) {
        this.publicKeyOrganization = issuerKpValue;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void changeCashSuccess(CashInResponse cashInResponse) {
        if (null != getActivity())
            getActivity().startService(new Intent(getActivity(), SyncCashService.class));
        DatabaseUtil.saveCashOut(cashInResponse.getId(), listCashSend, getActivity(), accountInfo.getUsername());
        Gson gson = new Gson();
        String jsonCashInResponse = gson.toJson(cashInResponse);
        CacheData_Database cacheData_database = new CacheData_Database();
        cacheData_database.setTransactionSignature(cashInResponse.getId());
        cacheData_database.setResponseData(jsonCashInResponse);
        cacheData_database.setType(TYPE_CASH_EXCHANGE);
        DatabaseUtil.saveCacheData(cacheData_database, getActivity());
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CASH_IN));
    }

    private void showDialogCashChangeOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                getResources().getString(R.string.str_change_cash_success), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        setData();
                        layoutChange.setVisibility(View.GONE);
                        btnCashChange.setBackgroundResource(R.drawable.bg_border_blue);
                        btnCashChange.setTextColor(getResources().getColor(R.color.blue));
                        btnCashTake.setBackgroundResource(R.drawable.bg_border_blue);
                        btnCashTake.setTextColor(getResources().getColor(R.color.blue));
                        if (valueListCashTake != null) {
                            valueListCashTake.clear();
                        }

                    }

                    @Override
                    public void OnListenerCancel() {
                        if (getActivity() != null)
                            getActivity().finish();
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
        if (event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)) {
            reloadData();
        }
        if (event.getData().equals(Constant.EVENT_PAYMENT_SUCCESS)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            dismissProgress();
                            setData();
                        });
                    } catch (NullPointerException ignored) {
                    }
                }
            }, 500);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void reloadData() {
        if (WalletDatabase.numberRequest == 0) {
            dismissProgress();
            setData();
            showDialogCashChangeOk();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> reloadData());
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
