package vn.ecpay.ewallet.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

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
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.ResponseData;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.response.Denomination;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.cashChange.CashChangeActivity;
import vn.ecpay.ewallet.ui.cashIn.CashInActivity;
import vn.ecpay.ewallet.ui.cashOut.CashOutActivity;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.firebase.NotificationActivity;
import vn.ecpay.ewallet.ui.function.SyncCashService;
import vn.ecpay.ewallet.ui.home.module.HomeModule;
import vn.ecpay.ewallet.ui.home.presenter.HomePresenter;
import vn.ecpay.ewallet.ui.home.view.HomeView;
import vn.ecpay.ewallet.ui.lixi.MyLixiActivity;
import vn.ecpay.ewallet.ui.payTo.PayToActivity;
import vn.ecpay.ewallet.ui.toPay.ToPayActivity;

public class HomeFragment extends ECashBaseFragment implements HomeView {
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.iv_bell)
    ImageView ivBell;
    @BindView(R.id.tvHomeAccountName)
    TextView tvHomeAccountName;
    @BindView(R.id.tvHomeAccountId)
    TextView tvHomeAccountId;
    @BindView(R.id.tvHomeAccountBalance)
    TextView tvHomeAccountBalance;
    @BindView(R.id.tvHomeAccountEdong)
    TextView tvHomeAccountEdong;
    @BindView(R.id.tvHomeEdongBalance)
    TextView tvHomeEDongBalance;
    @BindView(R.id.layout_account_info)
    CardView layoutAccountInfo;
    @BindView(R.id.layout_cash_in)
    LinearLayout layoutCashIn;
    @BindView(R.id.layout_cash_out)
    LinearLayout layoutCashOut;
    @BindView(R.id.layout_change_cash)
    LinearLayout layoutChangeCash;
    @BindView(R.id.layout_transfer_cash)
    LinearLayout layoutTransferCash;
    @BindView(R.id.viewElectronPay)
    LinearLayout viewElectronPay;
    @BindView(R.id.viewWaterPay)
    LinearLayout viewWaterPay;
    @BindView(R.id.viewPaymentRequest)
    LinearLayout viewShopPay;
    @BindView(R.id.viewCreateBill)
    LinearLayout viewCablePay;
    @BindView(R.id.layout_active_account)
    RelativeLayout layoutActiveAccount;
    @BindView(R.id.layout_full_info)
    LinearLayout layoutFullInfo;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.layout_eDong)
    RelativeLayout layoutEDong;
    @BindView(R.id.tvNotificationCount)
    TextView tvNotificationCount;
    @BindView(R.id.tvNumberLixi)
    TextView tvNumberLixi;
    @BindView(R.id.tvPaymentRequest)
    TextView tvPaymentRequest;

    private AccountInfo accountInfo;
    private ArrayList<EdongInfo> listEDongInfo;
    private long balance;
    private EdongInfo eDongInfoCashIn;
    private AccountInfo dbAccountInfo;

    @Inject
    HomePresenter homePresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.home_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new HomeModule(this)).inject(this);
        homePresenter.setView(this);
        homePresenter.onViewCreate();
        updateAccountInfo();
    }

    private void updateAccountInfo() {
        tvPaymentRequest.setText(String.format(getString(R.string.str_payment_request), "\n"));
        accountInfo = ECashApplication.getAccountInfo();
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (null != listEDongInfo) {
            if (listEDongInfo.size() > 0) {
                eDongInfoCashIn = listEDongInfo.get(0);
                tvHomeAccountEdong.setText(listEDongInfo.get(0).getAccountIdt());
                tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEDong(listEDongInfo.get(0))));
            }
        }

        dbAccountInfo = DatabaseUtil.getAccountInfo(accountInfo.getUsername(), getActivity());
        if (KeyStoreUtils.getMasterKey(getActivity()) != null && dbAccountInfo != null) {
            homePresenter.getCashValues(accountInfo, getActivity());
            updateNotification();
            updateNumberLixi();
            //todo sync data
           // syncData();
            accountInfo = dbAccountInfo;
            tvHomeAccountName.setText(CommonUtils.getFullName(accountInfo));
            tvHomeAccountId.setText(String.valueOf(accountInfo.getWalletId()));
            layoutActiveAccount.setVisibility(View.GONE);
            layoutFullInfo.setVisibility(View.VISIBLE);
            //request permission contact
            PermissionUtils.checkPermissionReadContact(this, null);
            WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
            if (WalletDatabase.getAllCash() != null) {
                if (WalletDatabase.getAllCash().size() > 0) {
                    balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
                    tvHomeAccountBalance.setText(CommonUtils.formatPriceVND(balance));
                } else {
                    tvHomeAccountBalance.setText(CommonUtils.formatPriceVND(0l));
                }
            } else {
                tvHomeAccountBalance.setText(CommonUtils.formatPriceVND(0l));
            }
        } else {
            layoutActiveAccount.setVisibility(View.VISIBLE);
            layoutFullInfo.setVisibility(View.GONE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void syncData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (null != getActivity()) {
                    getActivity().startService(new Intent(getActivity(), SyncCashService.class));
                    if (DatabaseUtil.getAllCacheData(getActivity()).size() > 0) {
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CASH_IN));
                    }
                }

                if (DatabaseUtil.checkTransactionsLogs(getActivity()) && DatabaseUtil.checkCashLogs(getActivity())) {
                    ECashApplication.setIsChangeDataBase(false);
                } else {
                    ECashApplication.setIsChangeDataBase(true);
                }
                return null;
            }
        }.execute();
    }

    private void updateNotification() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (DatabaseUtil.getSizeNotification(getActivity()) > 0) {
                tvNotificationCount.setVisibility(View.VISIBLE);
                tvNotificationCount.setText(String.valueOf(DatabaseUtil.getSizeNotification(getActivity())));
            } else {
                tvNotificationCount.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private void updateNumberLixi() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (DatabaseUtil.getAllLixiUnRead(getActivity()).size() > 0) {
                tvNumberLixi.setVisibility(View.VISIBLE);
                tvNumberLixi.setText(String.valueOf(DatabaseUtil.getAllLixiUnRead(getActivity()).size()));
            } else {
                tvNumberLixi.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private void updateActiveAccount() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    updateAccountInfo();
                    dismissLoading();
                });
            }
        }, 2000);
    }

    private void updateBalance() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvHomeAccountBalance.setText(CommonUtils.formatPriceVND(balance));

        listEDongInfo = ECashApplication.getListEDongInfo();
        if (listEDongInfo.size() > 0) {
            for (int i = 0; i < listEDongInfo.size(); i++) {
                if (listEDongInfo.get(i).getAccountIdt().equals(eDongInfoCashIn.getAccountIdt())) {
                    tvHomeAccountEdong.setText(listEDongInfo.get(i).getAccountIdt());
                    tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEDong(listEDongInfo.get(0))));
                }
            }
        } else {
            tvHomeAccountEdong.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
            tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEDong(listEDongInfo.get(0))));
        }
    }

    private void showDialogEDong(ArrayList<EdongInfo> listEDongInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.str_chose_edong_account));
        String[] eDong = new String[listEDongInfo.size()];
        for (int i = 0; i < listEDongInfo.size(); i++) {
            eDong[i] = listEDongInfo.get(i).getAccountIdt();
        }

        builder.setItems(eDong, (dialog, which) -> {
            tvHomeAccountEdong.setText(listEDongInfo.get(which).getAccountIdt());
            tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEDong(listEDongInfo.get(0))));
            eDongInfoCashIn = listEDongInfo.get(which);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick({R.id.layout_lixi, R.id.layout_eDong, R.id.iv_qr_code, R.id.layout_notification, R.id.layout_account_info, R.id.layout_cash_in, R.id.layout_cash_out, R.id.layout_change_cash, R.id.layout_transfer_cash, R.id.viewElectronPay, R.id.viewWaterPay, R.id.viewPaymentRequest, R.id.viewCreateBill, R.id.layout_active_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_notification:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (dbAccountInfo != null) {
                    Intent intentNoti = new Intent(getActivity(), NotificationActivity.class);
                    if (getActivity() != null) {
                        getActivity().startActivity(intentNoti);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                } else {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                }
                break;
            case R.id.iv_qr_code:
            case R.id.viewElectronPay:
            case R.id.viewWaterPay:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (getActivity() != null)
                    ((MainActivity) getActivity()).showDialogError(getString(R.string.err_doing));
                break;
            case R.id.viewCreateBill:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentPayTo = new Intent(getActivity(), ToPayActivity.class);
                        if (getActivity() != null) {
                            getActivity().startActivity(intentPayTo);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    if (getActivity() != null)
                        ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;
            case R.id.layout_account_info:
                break;
            case R.id.viewPaymentRequest:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentPayTo = new Intent(getActivity(), PayToActivity.class);
                        if (getActivity() != null) {
                            getActivity().startActivity(intentPayTo);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    if (getActivity() != null)
                        ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;

            case R.id.layout_cash_in:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentCashIn = new Intent(getActivity(), CashInActivity.class);
                        if (getActivity() != null) {
                            getActivity().startActivity(intentCashIn);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    if (getActivity() != null)
                        ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }

                break;
            case R.id.layout_cash_out:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentCashOut = new Intent(getActivity(), CashOutActivity.class);
                        if (getActivity() != null) {
                            getActivity().startActivity(intentCashOut);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    if (getActivity() != null)
                        ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;
            case R.id.layout_change_cash:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentTransferCash = new Intent(getActivity(), CashChangeActivity.class);
                        if (getActivity() != null) {
                            getActivity().startActivity(intentTransferCash);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    if (getActivity() != null)
                        ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;
            case R.id.layout_transfer_cash:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentTransferCash = new Intent(getActivity(), CashToCashActivity.class);
                        if (getActivity() != null) {
                            getActivity().startActivity(intentTransferCash);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    if (getActivity() != null)
                        ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;

            case R.id.layout_eDong:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (listEDongInfo != null) {
                    showDialogEDong(listEDongInfo);
                }
                break;

            case R.id.layout_active_account:
                if (ECashApplication.FBToken.isEmpty()) {
                    DialogUtil.getInstance().showDialogWarning(getActivity(), getString(R.string.err_upload));
                    return;
                }

                if (PermissionUtils.checkPermissionReadPhoneState(this, null)) {
                    startRegisterPassword();
                }
                break;
            case R.id.layout_lixi:
                if (ECashApplication.isIsChangeDataBase()) {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.err_change_database));
                    return;
                }
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentTransferCash = new Intent(getActivity(), MyLixiActivity.class);
                        if (getActivity() != null) {
                            getActivity().startActivity(intentTransferCash);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    if (getActivity() != null)
                        ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void startRegisterPassword() {
        SharedPreferences prefs = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        String IMEI = prefs.getString(Constant.DEVICE_IMEI, null);
        if (IMEI == null) {
            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            // get IMei of device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                IMEI = telephonyManager.getImei();
            } else {
                IMEI = telephonyManager.getDeviceId();
            }
            // save IMei of device to shared preference
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.DEVICE_IMEI, IMEI);
            editor.apply();
        }
        homePresenter.getOTPActiveAccount(accountInfo, getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CONTACT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (getActivity() != null) {
                    if (CommonUtils.getListPhoneNumber(getActivity()).size() > 0) {
                        homePresenter.syncContact(getActivity(), accountInfo);
                    }
                }
            }
        } else if (requestCode == PermissionUtils.PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRegisterPassword();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
      //  Log.e("Home Event Bus", new Gson().toJson(event.getData()));
        if (event.getData().equals(Constant.UPDATE_ACCOUNT_LOGIN)) {
            updateAccountInfo();
        }

        if (event.getData().equals(Constant.EVENT_UPDATE_ACCOUNT_INFO)) {
            updateAccountInfo();
        }

        if (event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)
                || event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS) ||
                event.getData().equals(Constant.EVENT_PAYMENT_SUCCESS) || event.getData().equals(Constant.EVENT_UPDATE_BALANCE)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> updateBalance());
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, 2000);
        }

        if (event.getData().equals(Constant.UPDATE_NOTIFICATION)) {
            updateNotification();
        }

        if (event.getData().equals(Constant.EVENT_UPDATE_LIXI)) {
            updateNumberLixi();
        }
        if (event.getData().equals(Constant.PAYTO_SUCCESS)) {
            updateAccountInfo();
        }
        if (event.getData().equals(Constant.EVENT_CONNECT_SOCKET_FAIL)) {
            dismissLoading();
        }

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
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
    public void onActiveAccountSuccess(AccountInfo mAccountInfo) {
        if (PermissionUtils.isReadContact(getActivity())) {
            if (getActivity() != null) {
                if (CommonUtils.getListPhoneNumber(getActivity()).size() > 0) {
                    accountInfo.setWalletId(mAccountInfo.getWalletId());
                    homePresenter.syncContact(getActivity(), accountInfo);
                }
            }
        }
        addMyContact(mAccountInfo);
        ECashApplication.setAccountInfo(mAccountInfo);
        DatabaseUtil.saveAccountInfo(mAccountInfo, getActivity());
        updateActiveAccount();
        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
        Toast.makeText(getActivity(), getString(R.string.str_active_account_success), Toast.LENGTH_LONG).show();
    }

    private void addMyContact(AccountInfo mAccountInfo) {
        Contact contact = new Contact();
        contact.setWalletId(mAccountInfo.getWalletId());
        contact.setFullName(CommonUtils.getFullName(mAccountInfo));
        contact.setPhone(mAccountInfo.getPersonMobilePhone());
        contact.setPublicKeyValue(mAccountInfo.getEcKeyPublicValue());
        contact.setTerminalInfo(mAccountInfo.getTerminalInfo());
        DatabaseUtil.saveOnlySingleContact(getActivity(), contact);
    }

    @Override
    public void showDialogError(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @Override
    public void onSyncContactSuccess() {
        Toast.makeText(getActivity(), getString(R.string.str_sync_contact_success), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSyncContactFail(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @Override
    public void onGetOTPActiveAccountSuccess(ResponseData responseData, String publicKeyBase64) {
        accountInfo.setEcKeyPublicValue(publicKeyBase64);
        Toast.makeText(getActivity(), getResources().getString(R.string.str_send_otp_success), Toast.LENGTH_LONG).show();
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", "", "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                homePresenter.activeAccWalletInfo(accountInfo, responseData, otp, getActivity());
            }

            @Override
            public void onRetryOTP() {
                homePresenter.getOTPActiveAccount(accountInfo, getActivity());
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void requestOTPFail(String err, ResponseData responseData) {
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", err, "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                homePresenter.activeAccWalletInfo(accountInfo, responseData, otp, getActivity());
            }

            @Override
            public void onRetryOTP() {
                homePresenter.getOTPActiveAccount(accountInfo, getActivity());
            }

            @Override
            public void onCancel() {
                ((AccountActivity) getActivity()).onBackPressed();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getCashValuesSuccess(List<Denomination> cashValuesList) {
        if (null != cashValuesList) {
            if (cashValuesList.size() > 0) {
                DatabaseUtil.deleteAllCashValue(getActivity());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        for (int i = 0; i < cashValuesList.size(); i++) {
                            DatabaseUtil.saveCashValue(cashValuesList.get(i), getActivity());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        dismissProgress();
                    }
                }.execute();
            }
        } else {
            dismissLoading();
        }
    }
}
