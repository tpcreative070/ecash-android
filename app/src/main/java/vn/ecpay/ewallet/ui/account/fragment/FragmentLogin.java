package vn.ecpay.ewallet.ui.account.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;

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
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseDataEdong;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.account.module.LoginModule;
import vn.ecpay.ewallet.ui.account.presenter.LoginPresenter;
import vn.ecpay.ewallet.ui.account.view.LoginView;
import vn.ecpay.fragmentcommon.ui.widget.CircleImageView;

public class FragmentLogin extends ECashBaseFragment implements LoginView {
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.ed_user_name)
    EditText edUserName;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.layout_register)
    LinearLayout layoutRegister;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;
    @BindView(R.id.tv_not_login)
    TextView tvNotLogin;
    @BindView(R.id.iv_account)
    CircleImageView ivAccount;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.layout_is_exit)
    LinearLayout layoutIsExit;

    private AccountInfo accountInfo;
    private String userName, pass;
    private boolean isTimeOut;

    @Inject
    LoginPresenter loginPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_login;
    }

    @OnClick({R.id.btn_login, R.id.layout_register, R.id.tv_not_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                validateData();
                break;
            case R.id.layout_register:
                if (KeyStoreUtils.getMasterKey(getActivity()) != null) {
                    List<AccountInfo> listAccount = DatabaseUtil.getAllAccountInfo(getContext());
                    if (null != listAccount) {
                        if (listAccount.size() > 0) {
                            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_wallet_is_exit));
                        } else {
                            ((AccountActivity) getActivity()).addFragment(new FragmentRegister(), true);
                        }
                    } else {
                        ((AccountActivity) getActivity()).addFragment(new FragmentRegister(), true);
                    }
                } else {
                    ((AccountActivity) getActivity()).addFragment(new FragmentRegister(), true);
                }
                break;
            case R.id.tv_not_login:
                break;
        }
    }

    public static FragmentLogin newInstance(boolean isTimeOut) {
        Bundle args = new Bundle();
        args.putBoolean(Constant.IS_SESSION_TIMEOUT, isTimeOut);
        FragmentLogin fragment = new FragmentLogin();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (null != bundle) {
            this.isTimeOut = bundle.getBoolean(Constant.IS_SESSION_TIMEOUT);
        }

        ECashApplication.get(getActivity()).getApplicationComponent().plus(new LoginModule(this)).inject(this);
        loginPresenter.setView(this);
        loginPresenter.onViewCreate();
        edPassword.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                validateData();
                return true;
            }
            return false;
        });
        edPassword.setText("123456");
    }

    private void checkAccountExit() {
        if (KeyStoreUtils.getMasterKey(getActivity()) != null) {
            List<AccountInfo> listAccount = DatabaseUtil.getAllAccountInfo(getContext());
            if (listAccount != null) {
                if (listAccount.size() > 0) {
                    accountInfo = listAccount.get(0);
                    edUserName.setVisibility(View.GONE);
                    layoutIsExit.setVisibility(View.VISIBLE);
                    tvName.setText(CommonUtils.getFullName(accountInfo));
                    tvPhone.setText(accountInfo.getPersonMobilePhone());
                    edUserName.setText(accountInfo.getUsername());
                } else {
                    edUserName.setVisibility(View.VISIBLE);
                    layoutIsExit.setVisibility(View.GONE);
                }
            } else {
                edUserName.setVisibility(View.VISIBLE);
                layoutIsExit.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loginPresenter.onViewResume();
        ((AccountActivity) getActivity()).updateTitle("Đăng nhập");
        checkAccountExit();
    }

    private void validateData() {
        userName = edUserName.getText().toString();
        pass = edPassword.getText().toString();
        if (userName.isEmpty()) {
            showDialogError(getString(R.string.err_not_input_username));
            return;
        }

        if (pass.isEmpty()) {
            showDialogError(getString(R.string.err_not_input_pass));
            return;
        }

        if (accountInfo != null) {
            if (!userName.equals(accountInfo.getUsername())) {
                showDialogError(getString(R.string.err_device_acc_exit));
                return;
            }
        }

        if (PermissionUtils.checkPermissionReadPhoneState(this, null)) {
            getIMEI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getIMEI();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getIMEI() {
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
        loginPresenter.requestLogin(accountInfo, userName, pass);
    }

    public void requestOTPSuccess(AccountInfo accountInfo) {
        Toast.makeText(getActivity(), R.string.err_send_otp_active_account, Toast.LENGTH_LONG).show();
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", "", "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                loginPresenter.activeAccount(accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                loginPresenter.requestOTPActiveAccount(accountInfo);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void requestOTPFail(String err, AccountInfo accountInfo) {
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", err, "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                loginPresenter.activeAccount(accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                loginPresenter.requestOTPActiveAccount(accountInfo);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void requestGetEDongInfoSuccess(ResponseDataEdong response, AccountInfo accountInfo) {
        ECashApplication.setAccountInfo(accountInfo);
        if (response.getListAcc().size() > 0) {
            ECashApplication.setListEDongInfo(response.getListAcc());
        }
        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
        if (isTimeOut) {
            Objects.requireNonNull(getActivity()).finish();
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    @Override
    public void activeAccountSuccess() {
        loginPresenter.requestLogin(accountInfo, userName, pass);
    }

    @Override
    public void onStart() {
        super.onStart();
        loginPresenter.onViewStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        loginPresenter.onViewPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loginPresenter.onViewDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        loginPresenter.onViewStop();
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
    public void showDialogError(String responseMessage) {
        ((AccountActivity) getActivity()).showDialogError(responseMessage);
    }

    @Override
    public void requestLoginSuccess(AccountInfo mAccountInfo) {
        mAccountInfo.setUsername(userName);
        mAccountInfo.setToken(CommonUtils.encryptPassword(pass));
        loginPresenter.getEDongInfo(mAccountInfo);
        if (KeyStoreUtils.getMasterKey(getActivity()) != null &&
                KeyStoreUtils.getPrivateKey(getActivity()) != null) {
            ECashApplication.masterKey = KeyStoreUtils.getMasterKey(getActivity());
            ECashApplication.privateKey = KeyStoreUtils.getPrivateKey(getActivity());
        }
    }

    @Override
    public void requestActiveAccount() {
        loginPresenter.requestOTPActiveAccount(accountInfo);
    }
}
