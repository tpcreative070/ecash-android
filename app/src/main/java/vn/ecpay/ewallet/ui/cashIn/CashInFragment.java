package vn.ecpay.ewallet.ui.cashIn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.ui.cashIn.adapter.CashValueAdapter;
import vn.ecpay.ewallet.ui.cashIn.module.CashInModule;
import vn.ecpay.ewallet.ui.cashIn.presenter.CashInPresenter;
import vn.ecpay.ewallet.ui.cashIn.view.CashInView;
import vn.ecpay.ewallet.ui.function.SyncCashService;
import vn.ecpay.ewallet.ui.function.UpdateMasterKeyFunction;
import vn.ecpay.ewallet.ui.interfaceListener.UpdateMasterKeyListener;

import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_EDONG_TO_ECASH;

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
    private long mLastClickTime = 0;

    @Inject
    CashInPresenter cashInPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cash_in;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != getActivity()) {
            getActivity().startService(new Intent(getActivity(), SyncCashService.class));
            ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashInModule(this)).inject(this);
        }
        cashInPresenter.setView(this);
        cashInPresenter.onViewCreate();
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        listEDongInfo = ECashApplication.getListEDongInfo();
        setData();
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

    private void setData() {
        setAdapter();
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
        listEDongInfo = ECashApplication.getListEDongInfo();

        if (listEDongInfo.size() > 0) {
            eDongInfoCashIn = listEDongInfo.get(0);
            tvEdongWallet.setText(listEDongInfo.get(0).getAccountIdt());
            tvOverEdong.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEDong(listEDongInfo.get(0))));
        }
    }

    private void showDialogEDong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn tài khoản Edong");
        String[] eDong = new String[listEDongInfo.size()];
        for (int i = 0; i < listEDongInfo.size(); i++) {
            eDong[i] = listEDongInfo.get(i).getAccountIdt();
        }

        builder.setItems(eDong, (dialog, which) -> {
            tvEdongWallet.setText(listEDongInfo.get(which).getAccountIdt());
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
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
        UpdateMasterKeyFunction updateMasterKeyFunction = new UpdateMasterKeyFunction(getActivity());
        showLoading();
        updateMasterKeyFunction.updateLastTimeAndMasterKey(new UpdateMasterKeyListener() {
            @Override
            public void onUpdateMasterSuccess() {
                cashInPresenter.transferMoneyEDongToECash(getActivity(),totalMoney, eDongInfoCashIn, listQuality, accountInfo, listValue);
            }

            @Override
            public void onUpdateMasterFail() {
                dismissLoading();
                showDialogError(R.string.err_change_database);
            }

            @Override
            public void onRequestTimeout() {
                dismissLoading();
                showDialogError(getResources().getString(R.string.err_upload));
            }
        });
    }

    private void showDialogCashInOk() {
        DialogUtil.getInstance().showDialogContinueAndExit(getActivity(), getString(R.string.str_transaction_success),
                getResources().getString(R.string.str_dialog_cash_in_success, CommonUtils.formatPriceVND(totalMoney)),getResources().getColor(R.color.green), new DialogUtil.OnConfirm() {
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

    public void showDialogError(int err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(err));
    }

    @Override
    public void transferMoneySuccess(CashInResponse cashInResponse) {
        Gson gson = new Gson();
        String jsonCashInResponse = gson.toJson(cashInResponse);
        CacheData_Database cacheData_database = new CacheData_Database();
        cacheData_database.setTransactionSignature(cashInResponse.getId());
        cacheData_database.setResponseData(jsonCashInResponse);
        cacheData_database.setType(TYPE_SEND_EDONG_TO_ECASH);
        DatabaseUtil.saveCacheData(cacheData_database, getActivity());
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CASH_IN));
    }

    @Override
    public void getEDongInfoSuccess() {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                dismissProgress();
                showDialogCashInOk();
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_BALANCE));
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
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void reloadData() {
        if (WalletDatabase.numberRequest == 0) {
            cashInPresenter.getEDongInfo(accountInfo);
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
