package vn.ecpay.ewallet.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashChange.CashChangeActivity;
import vn.ecpay.ewallet.ui.cashIn.CashInActivity;
import vn.ecpay.ewallet.ui.cashOut.CashOutActivity;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.home.module.HomeModule;
import vn.ecpay.ewallet.ui.home.presenter.HomePresenter;
import vn.ecpay.ewallet.ui.home.view.HomeView;

public class HomeFragment extends ECashBaseFragment implements HomeView {
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.iv_bell)
    ImageView ivBell;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    @BindView(R.id.viewShopPay)
    LinearLayout viewShopPay;
    @BindView(R.id.viewCablePay)
    LinearLayout viewCablePay;
    @BindView(R.id.layout_active_account)
    RelativeLayout layoutActiveAccount;
    @BindView(R.id.layout_full_info)
    LinearLayout layoutFullInfo;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.layout_eDong)
    RelativeLayout layoutEDong;
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
        accountInfo = ECashApplication.getAccountInfo();
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (listEDongInfo.size() > 0) {
            eDongInfoCashIn = listEDongInfo.get(0);
            tvHomeAccountEdong.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
            tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(listEDongInfo.get(0).getUsableBalance()));
        }

        dbAccountInfo = DatabaseUtil.getAccountInfo(accountInfo.getUsername(), getActivity());
        if (KeyStoreUtils.getMasterKey(getActivity()) != null && dbAccountInfo != null) {
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

    private void updateActiveAccount() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    updateAccountInfo();
                });
            }
        }, 500);
    }

    private void updateBalance() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
                    balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
                    tvHomeAccountBalance.setText(CommonUtils.formatPriceVND(balance));

                    listEDongInfo = ECashApplication.getListEDongInfo();
                    if (listEDongInfo.size() > 0) {
                        for (int i = 0; i < listEDongInfo.size(); i++) {
                            if (listEDongInfo.get(i).getAccountIdt().equals(eDongInfoCashIn.getAccountIdt())) {
                                tvHomeAccountEdong.setText(String.valueOf(listEDongInfo.get(i).getAccountIdt()));
                                tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(listEDongInfo.get(i).getUsableBalance()));
                            }
                        }
                    } else {
                        tvHomeAccountEdong.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
                        tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(listEDongInfo.get(0).getUsableBalance()));
                    }
                });
            }
        }, 1000);
    }

    private void showDialogEDong(ArrayList<EdongInfo> listEDongInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.str_chose_edong_account));
        String[] eDong = new String[listEDongInfo.size()];
        for (int i = 0; i < listEDongInfo.size(); i++) {
            eDong[i] = String.valueOf(listEDongInfo.get(i).getAccountIdt());
        }

        builder.setItems(eDong, (dialog, which) -> {
            tvHomeAccountEdong.setText(String.valueOf(listEDongInfo.get(which).getAccountIdt()));
            tvHomeEDongBalance.setText(CommonUtils.formatPriceVND(listEDongInfo.get(which).getUsableBalance()));
            eDongInfoCashIn = listEDongInfo.get(which);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick({R.id.layout_eDong, R.id.iv_qr_code, R.id.iv_bell, R.id.layout_account_info, R.id.layout_cash_in, R.id.layout_cash_out, R.id.layout_change_cash, R.id.layout_transfer_cash, R.id.viewElectronPay, R.id.viewWaterPay, R.id.viewShopPay, R.id.viewCablePay, R.id.layout_active_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_qr_code:
            case R.id.iv_bell:
            case R.id.viewElectronPay:
            case R.id.viewWaterPay:
            case R.id.viewShopPay:
            case R.id.viewCablePay:
                ((MainActivity) getActivity()).showDialogError(getString(R.string.err_doing));
                break;
            case R.id.layout_account_info:
                break;
            case R.id.layout_cash_in:
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentCashIn = new Intent(getActivity(), CashInActivity.class);
                        getActivity().startActivity(intentCashIn);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }

                break;
            case R.id.layout_cash_out:
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentCashOut = new Intent(getActivity(), CashOutActivity.class);
                        getActivity().startActivity(intentCashOut);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;
            case R.id.layout_change_cash:
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentTransferCash = new Intent(getActivity(), CashChangeActivity.class);
                        getActivity().startActivity(intentTransferCash);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;
            case R.id.layout_transfer_cash:
                if (ECashApplication.getAccountInfo() != null) {
                    if (dbAccountInfo != null) {
                        Intent intentTransferCash = new Intent(getActivity(), CashToCashActivity.class);
                        getActivity().startActivity(intentTransferCash);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        ((MainActivity) getActivity()).showDialogError(getString(R.string.str_dialog_active_acc));
                    }
                } else {
                    ECashApplication.get(getActivity()).showDialogSwitchLogin(getString(R.string.str_dialog_not_login));
                }
                break;

            case R.id.layout_eDong:
                if (listEDongInfo != null) {
                    showDialogEDong(listEDongInfo);
                }
                break;

            case R.id.layout_active_account:
                if (PermissionUtils.checkPermissionReadPhoneState(this, null)) {
                    startRegisterPassword();
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
        homePresenter.activeAccWalletInfo(accountInfo, getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CONTACT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                homePresenter.syncContact(getActivity(), accountInfo);
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
        if (event.getData().equals(Constant.UPDATE_ACCOUNT_LOGIN)) {
            updateAccountInfo();
        }

        if (event.getData().equals(Constant.UPDATE_MONEY)) {
            updateBalance();
        }

        if (event.getData().equals(Constant.UPDATE_MONEY_SOCKET)) {
            updateBalance();
        }

        if (event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)) {
            updateBalance();
        }
        EventBus.getDefault().removeStickyEvent(event);
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
        addMyContact(mAccountInfo);
        homePresenter.syncContact(getActivity(), mAccountInfo);
        ECashApplication.setAccountInfo(mAccountInfo);
        DatabaseUtil.saveAccountInfo(mAccountInfo, getActivity());
        updateActiveAccount();
        dismissLoading();
        ((MainActivity) getActivity()).showDialogError(getString(R.string.str_active_account_success));
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
        ((MainActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void onSyncContactSuccess() {
        ((MainActivity) getActivity()).showDialogError(getResources().getString(R.string.str_sync_contact_success));
        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
    }

    @Override
    public void onSyncContactFail(String err) {
        ((MainActivity) getActivity()).showDialogError(err);
    }
}
