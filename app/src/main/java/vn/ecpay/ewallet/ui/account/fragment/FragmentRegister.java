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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.greenrobot.eventbus.EventBus;

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
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseDataEdong;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.account.module.RegisterModule;
import vn.ecpay.ewallet.ui.account.presenter.RegisterPresenter;
import vn.ecpay.ewallet.ui.account.view.RegisterView;
import vn.ecpay.ewallet.webSocket.WebSocketsService;

public class FragmentRegister extends ECashBaseFragment implements RegisterView {
    @BindView(R.id.edt_user_name)
    EditText edtUserName;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_cmnd)
    EditText edtCmnd;
    @BindView(R.id.edt_pass)
    EditText edtPass;
    @BindView(R.id.edt_re_pass)
    EditText edtRePass;
    @BindView(R.id.btn_confirm)
    Button edtConfirm;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @Inject
    RegisterPresenter registerPresenter;

    private String userName, name, cmnd, phone, pass, rePass;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_register;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new RegisterModule(this)).inject(this);
        registerPresenter.setView(this);
        registerPresenter.onViewCreate();
        edtUserName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!edtUserName.getText().toString().isEmpty()) {
                    String userName = edtUserName.getText().toString();
                    if (!CommonUtils.isValidateUserName(userName)) {
                        ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_user_name_fail));
                    } else {
                        registerPresenter.checkUSerNameAccount(userName);
                    }
                }
            }
        });

        edtName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!edtName.getText().toString().isEmpty()) {
                    String name = edtName.getText().toString();
                    if (!CommonUtils.isValidateName(name))
                        ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_name_fail));
                }
            }
        });

        edtCmnd.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!edtCmnd.getText().toString().isEmpty()) {
                    if (!CommonUtils.validatePassPort(edtCmnd.getText().toString())) {
                        ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_cmnd_fail));
                        return;
                    }
                }

                if (!edtCmnd.getText().toString().isEmpty() &&
                        !edtPhone.getText().toString().isEmpty()) {
                    registerPresenter.checkIdAndPhoneNumberAccount(edtCmnd.getText().toString(), edtPhone.getText().toString());
                }
            }
        });

        edtPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!edtPhone.getText().toString().isEmpty()) {
                    if (!CommonUtils.isValidatePhoneNumber(edtPhone.getText().toString())) {
                        ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_phone_fail));
                        return;
                    }
                }

                if (!edtCmnd.getText().toString().isEmpty() &&
                        !edtPhone.getText().toString().isEmpty()) {
                    registerPresenter.checkIdAndPhoneNumberAccount(edtCmnd.getText().toString(), edtPhone.getText().toString());
                }
            }
        });

        edtRePass.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                validateData();
                return true;
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        if (KeyStoreUtils.getMasterKey(getActivity()) != null) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_device_acc_exit));
        }
        validateData();
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
        registerPresenter.requestRegister(getActivity(), userName, cmnd, pass, name, phone);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRegisterPassword();
                }
            }
            default:
                break;
        }
    }

    private void validateData() {
        userName = edtUserName.getText().toString();
        name = edtName.getText().toString();
        cmnd = edtCmnd.getText().toString();
        phone = edtPhone.getText().toString();
        pass = edtPass.getText().toString();
        rePass = edtRePass.getText().toString();

        if (userName.isEmpty()) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_user_name_null));
            return;
        }

        if (name.isEmpty()) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_name_null));
            return;
        }

        if (cmnd.isEmpty()) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_cmnd_null));
            return;
        }

        if (phone.isEmpty()) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_phone_null));
            return;
        }

        if (pass.isEmpty()) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_pass_null));
            return;
        }

        if (rePass.isEmpty()) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_repass_null));
            return;
        }

        if (!CommonUtils.isValidatePass(pass)) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_pass_bigger_six_char));
            return;
        }

        if (!pass.equals(rePass)) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_pass_duplicate_fail));
            return;
        }

        if (!CommonUtils.isValidateUserName(userName)) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_user_name_fail));
            return;
        }

        if (!CommonUtils.isValidateName(name)) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_name_fail));
            return;
        }

        if (!CommonUtils.validatePassPort(cmnd)) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_cmnd_fail));
            return;
        }

        if (!CommonUtils.isValidatePhoneNumber(phone)) {
            ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_validate_phone_fail));
            return;
        }

        if (PermissionUtils.checkPermissionReadPhoneState(this, null)) {
            startRegisterPassword();
        }

    }

    public void requestOTPSuccess(AccountInfo accountInfo) {
        Toast.makeText(getActivity(), R.string.str_send_otp_success, Toast.LENGTH_LONG).show();
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", "", "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                registerPresenter.activeAccount(accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                registerPresenter.retryOTP(accountInfo);
            }

            @Override
            public void onCancel() {
                ((AccountActivity) getActivity()).onBackPressed();
            }
        });
    }

    public void requestOTPFail(String err, AccountInfo accountInfo) {
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", err, "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                registerPresenter.activeAccount(accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                registerPresenter.retryOTP(accountInfo);
            }

            @Override
            public void onCancel() {
                ((AccountActivity) getActivity()).onBackPressed();
            }
        });
    }

    public void onResume() {
        super.onResume();
        ((AccountActivity) getActivity()).updateTitle("Đăng ký tài khoản");
    }

    @Override
    public void registerSuccess(AccountInfo mAccountInfo, String privateKeyBase64, String publicKeyBase64) {
        registerPresenter.syncContact(getActivity(), mAccountInfo);
        KeyStoreUtils.saveKeyPrivateWallet(privateKeyBase64, getActivity());
        KeyStoreUtils.saveMasterKey(mAccountInfo.getMasterKey(), getActivity());
        DatabaseUtil.saveAccountInfo(mAccountInfo, getActivity());
        requestOTPSuccess(mAccountInfo);
    }

    @Override
    public void onUserNameFail() {
        ((AccountActivity) getActivity()).showDialogError(getString(R.string.err_user_is_exit));
        edtUserName.requestFocus();
    }

    @Override
    public void showDialogError(String err) {
        ((AccountActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void onIDNumberFail(String idNumberFail) {
        ((AccountActivity) getActivity()).showDialogError(idNumberFail);
    }

    @Override
    public void onPhoneNumberFail(String phoneNumberFail) {
        ((AccountActivity) getActivity()).showDialogError(phoneNumberFail);
        edtPhone.requestFocus();
    }

    @Override
    public void registerFail(String err) {
        ((AccountActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void retryOTPSuccess(AccountInfo accountInfo) {
        requestOTPSuccess(accountInfo);
    }

    @Override
    public void requestOtpErr(AccountInfo accountInfo, String err) {
        requestOTPFail(err, accountInfo);
    }

    @Override
    public void activeAccountSuccess(AccountInfo accountInfo) {
        accountInfo.setUsername(userName);
        accountInfo.setPassword(CommonUtils.encryptPassword(pass));
        registerPresenter.loginAccount(accountInfo);
    }

    @Override
    public void loginSuccess(AccountInfo accountInfo) {
        ECashApplication.setAccountInfo(accountInfo);
        registerPresenter.getEDongInfo(accountInfo);
        getActivity().startService(new Intent(getActivity(), WebSocketsService.class));
    }

    @Override
    public void getEDongInfoSuccess(AccountInfo accountInfo, ResponseDataEdong responseDataEdong) {
        ECashApplication.setAccountInfo(accountInfo);
        if (responseDataEdong.getListAcc().size() > 0) {
            ECashApplication.setListEDongInfo(responseDataEdong.getListAcc());
        }
        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onSyncContactSuccess() {
        ((AccountActivity) getActivity()).showDialogError(getResources().getString(R.string.str_sync_contact_success));
    }

    @Override
    public void onSyncContactFail(String err) {
        ((AccountActivity) getActivity()).showDialogError(err);
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
