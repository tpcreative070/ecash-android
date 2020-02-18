package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.function.CashOutFunction;
import vn.ecpay.ewallet.ui.interfaceListener.MultiTransferListener;
import vn.ecpay.ewallet.ui.lixi.MyLixiActivity;
import vn.ecpay.ewallet.ui.lixi.adapter.CashTotalAdapter;
import vn.ecpay.ewallet.webSocket.WebSocketsService;

public class CashToCashFragment extends ECashBaseFragment implements MultiTransferListener {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverECash;
    @BindView(R.id.tv_number_wallet)
    TextView tvNumberWallet;
    @BindView(R.id.sw_qr_code)
    Switch swQrCode;
    @BindView(R.id.layout_qr_code)
    RelativeLayout layoutQrCode;
    @BindView(R.id.rv_cash_values)
    RecyclerView rvCashValues;
    @BindView(R.id.tv_total_send)
    TextView tvTotalSend;
    @BindView(R.id.edt_content)
    EditText edtContent;
    protected AccountInfo accountInfo;
    protected int balance;
    @BindView(R.id.toolbar_center_text)
    protected TextView toolbarCenterText;
    private CashTotalAdapter cashValueAdapter;
    private List<CashTotal> valuesListAdapter;
    private List<Contact> multiTransferList;
    protected long totalMoney;
    protected String typeSend;


    public static CashToCashFragment newInstance(ArrayList<Contact> listContactTransfer) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constant.CONTACT_TRANSFER_MODEL, listContactTransfer);
        CashToCashFragment fragment = new CashToCashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_transfer_money_pick;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.multiTransferList = bundle.getParcelableArrayList(Constant.CONTACT_TRANSFER_MODEL);
            if (null != multiTransferList) {
                if (multiTransferList.size() > 0) {
                    updateWalletSend();
                }
            }
        }
        updateType();
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        setData();
    }

    protected void updateType() {
        toolbarCenterText.setText(getResources().getString(R.string.str_transfer));
        typeSend = Constant.TYPE_ECASH_TO_ECASH;
    }

    protected void setData() {
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
        cashValueAdapter = new CashTotalAdapter(valuesListAdapter, getActivity(), this::updateTotalMoney);
        rvCashValues.setAdapter(cashValueAdapter);
        if (null != multiTransferList)
            cashValueAdapter.setNumberTransfer(multiTransferList.size());
    }

    protected void updateTotalMoney() {
        totalMoney = 0;
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            totalMoney = totalMoney + (valuesListAdapter.get(i).getTotal() * valuesListAdapter.get(i).getParValue() * (multiTransferList.size()));
        }
        tvTotalSend.setText(CommonUtils.formatPriceVND(totalMoney));
    }

    @OnClick({R.id.layout_chose_wallet, R.id.btn_confirm, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_chose_wallet:
                if (getActivity() != null) {
                    try {
                        ((CashToCashActivity) getActivity()).addFragment(FragmentContactTransferCash.newInstance(this), true);
                    } catch (ClassCastException e) {
                        ((MyLixiActivity) getActivity()).addFragment(FragmentContactTransferCash.newInstance(this), true);
                    }
                }
                break;
            case R.id.btn_confirm:
                validateData();
                break;
            case R.id.iv_back:
                if (getActivity() != null)
                    try {
                        ((CashToCashActivity) getActivity()).onBackPressed();
                    } catch (ClassCastException e) {
                        ((MyLixiActivity) getActivity()).onBackPressed();
                    }
                break;
        }
    }

    private void validateData() {
        showProgress();
        if (totalMoney == 0) {
            dismissProgress();
            if (getActivity() != null)
                showDialogErr(R.string.err_dit_not_money_transfer);
            return;
        }

        if (multiTransferList != null) {
            if (multiTransferList.size() == 0) {
                dismissProgress();
                if (getActivity() != null)
                    showDialogErr(R.string.err_not_input_number_username);
                return;
            }
        } else {
            dismissProgress();
            if (getActivity() != null)
                showDialogErr(R.string.err_not_input_number_username);
            return;
        }

        if (edtContent.getText().toString().isEmpty()) {
            dismissProgress();
            if (getActivity() != null)
                showDialogErr(R.string.err_dit_not_content);
            return;
        }

        if (swQrCode.isChecked()) {
            if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                if (!CommonUtils.isExternalStorageWritable()) {
                    dismissProgress();
                    if (getActivity() != null)
                        showDialogErr(R.string.err_store_image);
                    return;
                }
                CashOutFunction cashOutSocketFunction = new CashOutFunction(getActivity(), valuesListAdapter,
                        multiTransferList, edtContent.getText().toString(), typeSend);
                cashOutSocketFunction.handleCashOutQRCode(this::cashOutSuccess);
            }
        } else {
            CashOutFunction cashOutSocketFunction = new CashOutFunction(getActivity(), valuesListAdapter,
                    multiTransferList, edtContent.getText().toString(), typeSend);
            cashOutSocketFunction.handleCashOutSocket(this::cashOutSuccess);
        }
    }

    private void showDialogErr(int err) {
        try {
            ((CashToCashActivity) getActivity()).showDialogError(getString(err));
        } catch (ClassCastException e) {
            ((MyLixiActivity) getActivity()).showDialogError(getString(err));
        }
    }

    private void cashOutSuccess() {
        dismissProgress();
//        if (getActivity() != null) {

//            getActivity().startService(new Intent(getActivity(), WebSocketsService.class));
//        }
        restartSocket();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> setAdapter());
                EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
            }
        }, 500);
        showDialogSendOk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProgress();
                    CashOutFunction cashOutSocketFunction = new CashOutFunction(getActivity(), valuesListAdapter,
                            multiTransferList, edtContent.getText().toString(), typeSend);
                    cashOutSocketFunction.handleCashOutQRCode(this::cashOutSuccess);
                }
            }
            default:
                break;
        }
    }

    @Override
    public void onMultiTransfer(ArrayList<Contact> contactList) {
        this.multiTransferList = contactList;
        setAdapter();
        updateWalletSend();
    }

    private void updateWalletSend() {
        StringBuilder walletId = new StringBuilder();
        for (int i = 0; i < multiTransferList.size(); i++) {
            if (i == 0) {
                walletId.append(multiTransferList.get(i).getWalletId());
            } else {
                walletId.append("; ").append(multiTransferList.get(i).getWalletId());
            }
        }
        if (null != cashValueAdapter) {
            cashValueAdapter.setNumberTransfer(multiTransferList.size());
        }
        tvNumberWallet.setText(walletId.toString());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.CASH_OUT_MONEY_FAIL)) {
            dismissProgress();
            Toast.makeText(getActivity(), getResources().getString(R.string.err_upload), Toast.LENGTH_LONG).show();
        }

        if (event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)||event.getData().equals(Constant.EVENT_PAYMENT_SUCCESS)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            setData();
                        });
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, 500);
        }

        if (event.getData().equals(Constant.EVENT_CONNECT_SOCKET_FAIL)) {
            dismissProgress();
            Toast.makeText(getActivity(), getResources().getString(R.string.err_upload), Toast.LENGTH_LONG).show();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    protected void showDialogSendOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                "chuyển tiền thành công", new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        setData();
                        updateTotalMoney();
                    }

                    @Override
                    public void OnListenerCancel() {
                        getActivity().finish();
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }
}