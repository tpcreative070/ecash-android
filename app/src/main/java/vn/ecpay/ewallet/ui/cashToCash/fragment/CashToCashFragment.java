package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.callbackListener.MultiTransferListener;
import vn.ecpay.ewallet.ui.callbackListener.UpdateMasterKeyListener;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashSuccessWithQRCodeActivity;
import vn.ecpay.ewallet.ui.function.CashOutFunction;
import vn.ecpay.ewallet.ui.function.UpdateMasterKeyFunction;
import vn.ecpay.ewallet.ui.lixi.MyLixiActivity;
import vn.ecpay.ewallet.ui.lixi.adapter.CashTotalAdapter;

@SuppressLint("ParcelCreator")
public class CashToCashFragment extends ECashBaseFragment implements MultiTransferListener {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverECash;
    @BindView(R.id.tv_number_wallet)
    TextView tvNumberWallet;
    @BindView(R.id.tv_error_wallet)
    TextView tvErrorWallet;

    @BindView(R.id.sw_qr_code)
    Switch swQrCode;
    @BindView(R.id.layout_qr_code)
    RelativeLayout layoutQrCode;
    @BindView(R.id.rv_cash_values)
    RecyclerView rvCashValues;
    @BindView(R.id.tv_total_send)
    TextView tvTotalSend;
    @BindView(R.id.tv_error_amount)
    TextView tvErrorAmount;

    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.tv_error_content)
    TextView tvErrorContent;

    @BindView(R.id.btn_confirm)
    Button btConfirm;

    protected AccountInfo accountInfo;
    protected int balance;
    @BindView(R.id.toolbar_center_text)
    protected TextView toolbarCenterText;
    private CashTotalAdapter cashValueAdapter;
    private List<CashTotal> valuesListAdapter;
    private List<Contact> multiTransferList;
    protected long totalMoney;
    protected String typeSend;
    private long mLastClickTime = 0;

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
        accountInfo = DatabaseUtil.getAccountInfo(getActivity());
        if (null == accountInfo) {
            CommonUtils.restartApp((CashToCashActivity) getActivity());
        }
        updateType();
        updateContent();
        setAdapter();
        Utils.disableButtonConfirm(getActivity(), btConfirm, true);
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (totalMoney > 0 && tvNumberWallet.getText().length() > 0) {
                        Utils.disableButtonConfirm(getContext(), btConfirm, false);
                    } else {
                        Utils.disableButtonConfirm(getContext(), btConfirm, true);
                    }
                } else {
                    Utils.disableButtonConfirm(getContext(), btConfirm, true);
                }
            }
        });

    }

    protected void updateType() {
        toolbarCenterText.setText(getResources().getString(R.string.str_transfer));
        typeSend = Constant.TYPE_ECASH_TO_ECASH;
    }

    protected void setData() {
        updateContent();
        totalMoney = 0;
        tvNumberWallet.setText(getString(R.string.str_chose_wallet_transfer));
        if (multiTransferList != null && multiTransferList.size() > 0) {
            multiTransferList.clear();
        }
        tvTotalSend.setText(CommonUtils.formatPriceVND(totalMoney));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        Utils.disableButtonConfirm(getActivity(), btConfirm, true);
        setAdapter();
    }

    private void setAdapter() {
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverECash.setText(CommonUtils.formatPriceVND(balance));
        if (ECashApplication.isCancelAccount) {
            layoutQrCode.setVisibility(View.GONE);
        }

        valuesListAdapter = DatabaseUtil.getAllCashTotal(getActivity());
        if (ECashApplication.isCancelAccount) {
            for (int i = 0; i < valuesListAdapter.size(); i++) {
                valuesListAdapter.get(i).setTotal(valuesListAdapter.get(i).getTotalDatabase());
                valuesListAdapter.get(i).setTotalDatabase(0);
            }
            updateTotalMoneyCancelAccount();
        }
        Collections.reverse(valuesListAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvCashValues.setLayoutManager(mLayoutManager);
        cashValueAdapter = new CashTotalAdapter(valuesListAdapter, getActivity(),tvErrorWallet, this::updateTotalMoney);
        rvCashValues.setAdapter(cashValueAdapter);
        if (null != multiTransferList)
            cashValueAdapter.setNumberTransfer(multiTransferList.size());
    }

    private void updateTotalMoneyCancelAccount() {
        totalMoney = 0;
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            totalMoney = totalMoney + (valuesListAdapter.get(i).getTotal() * valuesListAdapter.get(i).getParValue());
        }
        tvTotalSend.setText(CommonUtils.formatPriceVND(totalMoney));
    }

    protected void updateTotalMoney() {
        totalMoney = 0;
        if (null != multiTransferList) {
            if (multiTransferList.size() > 0) {
                for (int i = 0; i < valuesListAdapter.size(); i++) {
                    totalMoney = totalMoney + (valuesListAdapter.get(i).getTotal() * valuesListAdapter.
                            get(i).getParValue() * (multiTransferList.size()));
                }
            }
        }
        tvTotalSend.setText(CommonUtils.formatPriceVND(totalMoney));
        if (totalMoney > 0) {
            tvErrorAmount.setText("");
            if (edtContent.getText().length() > 0) {
                Utils.disableButtonConfirm(getActivity(), btConfirm, false);
            } else {
                Utils.disableButtonConfirm(getActivity(), btConfirm, true);
            }
        } else {
            Utils.disableButtonConfirm(getActivity(), btConfirm, true);
        }
    }

    @OnClick({R.id.layout_chose_wallet, R.id.btn_confirm, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_chose_wallet:
                if (getBaseActivity() != null) {
                    if (getBaseActivity() instanceof CashToCashActivity) {
                        ((CashToCashActivity) getBaseActivity()).addFragment(FragmentContactTransferCash.newInstance(this, true), true);
                    } else if (getBaseActivity() instanceof MyLixiActivity) {
                        ((MyLixiActivity) getBaseActivity()).addFragment(FragmentContactTransferCash.newInstance(this, false), true);
                    }
                }
                break;
            case R.id.btn_confirm:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                validateData();
                break;
            case R.id.iv_back:
                if (getBaseActivity() != null) {
                    // Log.e("getBaseActivity() back",getBaseActivity().getLocalClassName());
                    getBaseActivity().onBackPressed();
                }
                break;
        }
    }

    private void validateData() {
        showProgress();
        tvErrorAmount.setText("");
        tvErrorWallet.setText("");
        tvErrorContent.setText("");
        if (totalMoney == 0) {
            tvErrorAmount.setText(R.string.err_dit_not_money_transfer);
            dismissProgress();
            return;
        }

        if (multiTransferList != null) {
            if (multiTransferList.size() == 0) {
                dismissProgress();
                tvErrorWallet.setText(R.string.err_not_input_number_username);
                return;
            }
        } else {
            dismissProgress();
            tvErrorWallet.setText(R.string.err_not_input_number_username);
            return;
        }

        if (edtContent.getText().toString().isEmpty()) {
            dismissProgress();
            tvErrorContent.setText(R.string.err_dit_not_content);
            return;
        }
        if (swQrCode.isChecked()) {
            if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                Intent intent = new Intent(getActivity(), CashToCashSuccessWithQRCodeActivity.class);
                intent.putExtra(Constant.CASH_TOTAL_TRANSFER, (Serializable) valuesListAdapter);
                intent.putExtra(Constant.CONTACT_MULTI_TRANSFER, (Serializable) multiTransferList);
                intent.putExtra(Constant.CONTENT_TRANSFER, edtContent.getText().toString());
                intent.putExtra(Constant.TYPE_TRANSFER, typeSend);
                startActivity(intent);
                dismissProgress();
            } else {
                dismissProgress();
            }
        } else {
            UpdateMasterKeyFunction updateMasterKeyFunction = new UpdateMasterKeyFunction(ECashApplication.getActivity());
            showProgress();
            updateMasterKeyFunction.updateLastTimeAndMasterKey(true,new UpdateMasterKeyListener() {
                @Override
                public void onUpdateMasterSuccess() {
                    CashOutFunction cashOutSocketFunction = new CashOutFunction(CashToCashFragment.this, valuesListAdapter,
                            multiTransferList, edtContent.getText().toString(), typeSend);
                    cashOutSocketFunction.handleCashOutSocket(() -> cashOutSuccess());
                }

                @Override
                public void onUpdateMasterFail(String code) {
                    dismissProgress();
                    CheckErrCodeUtil.errorMessage(getActivity(), code);
                }

                @Override
                public void onRequestTimeout() {

                }
            });
        }
    }

    private void showDialogErr(int err) {
        if (getBaseActivity() != null) {
            if (getBaseActivity() instanceof CashToCashActivity) {
                ((CashToCashActivity) getBaseActivity()).showDialogError(getString(err));
            } else if (getBaseActivity() instanceof MyLixiActivity) {
                ((MyLixiActivity) getBaseActivity()).showDialogError(getString(err));
            }
        }
    }

    private void cashOutSuccess() {
        if (WalletDatabase.numberRequest == 0) {
            if (ECashApplication.isCancelAccount) {
                handleCancelAccount();
            } else {
                dismissProgress();
                showDialogSendOk();
                restartSocket();
                EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
            }
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> cashOutSuccess());
                }
            }, 1000);
        }
    }

    private void handleCancelAccount() {
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        if (balance == 0) {
            dismissProgress();
            if (getActivity() != null)
                ((CashToCashActivity) getActivity()).onBackPressed();
        } else {
            showDialogSendOk();
            dismissProgress();
            restartSocket();
            EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    validateData();
                } else {
                    dismissProgress();
                    showDialogPermissions(getString(R.string.str_permission_store_setting));
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
        updateTotalMoney();
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
        if (walletId.length() > 0) {
            tvErrorWallet.setText("");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        Log.e("CashToCash", event.getData());
        if (event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS) || event.getData().equals(Constant.EVENT_PAYMENT_SUCCESS) || event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            setData();
                            updateTotalMoney();
                        });
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, 3000);
        }

        if (event.getData().equals(Constant.EVENT_CONNECT_SOCKET_FAIL)) {
            dismissProgress();
            showDialogErr(R.string.err_socket_timeout);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    protected void showDialogSendOk() {
        DialogUtil.getInstance().showDialogContinueAndExit(getActivity(), getResources().getString(R.string.str_transfer_success),
                getResources().getString(R.string.str_eCash_money_transfer_successfully), getResources().getColor(R.color.black), new DialogUtil.OnConfirm() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    private void updateContent() {
        edtContent.setText(String.format("%s %s", CommonUtils.getFullName(accountInfo), getString(R.string.str_transfer)));
    }
}