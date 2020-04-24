package vn.ecpay.ewallet.ui.account.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

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
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseDataEdong;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.account.module.RegisterModule;
import vn.ecpay.ewallet.ui.account.presenter.RegisterPresenter;
import vn.ecpay.ewallet.ui.account.view.RegisterView;

public class FragmentRegister extends ECashBaseFragment implements RegisterView {
    @BindView(R.id.edt_user_name)
    EditText edtUserName;
    @BindView(R.id.tv_error_user_name)
    TextView tvErrorUsername;

    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.tv_error_name)
    TextView tvErrorName;

    @BindView(R.id.edt_cmnd)
    EditText edtCmnd;
    @BindView(R.id.tv_error_cmnd)
    TextView tvErrorCMNN;

    @BindView(R.id.edt_pass)
    EditText edtPass;
    @BindView(R.id.tv_error_pass)
    TextView tvErrorPass;

    @BindView(R.id.edt_re_pass)
    EditText edtRePass;
    @BindView(R.id.tv_error_re_pass)
    TextView tvErrorRePass;

    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.tv_error_phone)
    TextView tvErrorPhone;

    @BindView(R.id.btn_confirm)
    Button edtConfirm;

    @Inject
    RegisterPresenter registerPresenter;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;

    private  boolean denyPermission;

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

        toolbarCenterText.setText(getResources().getString(R.string.str_register_account));
        Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, true);
        edtUserName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!edtUserName.getText().toString().isEmpty()) {
                    tvErrorUsername.setText("");
                    String userName = edtUserName.getText().toString();
                    if (!CommonUtils.isValidateUserName(userName)) {
                        tvErrorUsername.setText(getString(R.string.err_validate_user_name_fail));
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
                    tvErrorName.setText("");
                    if (!CommonUtils.isValidateName(name))
                        tvErrorName.setText(getString(R.string.err_validate_name_fail));
                }
            }
        });

        edtCmnd.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!edtCmnd.getText().toString().isEmpty()) {
                    tvErrorCMNN.setText("");
                    if (!CommonUtils.validatePassPort(edtCmnd.getText().toString())) {
                        tvErrorCMNN.setText(getString(R.string.err_validate_cmnd_fail));
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
                    tvErrorPhone.setText("");
                    if (!CommonUtils.isValidatePhoneNumber(edtPhone.getText().toString())) {
                        tvErrorPhone.setText(getResources().getString(R.string.err_validate_phone_fail));
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
        addTextChange();
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
                    showLoading();
                    startRegisterPassword();
                }else{
                    denyPermission =true;
                }
            }
            default:
                break;
        }
    }

    private void validateData() {
        tvErrorUsername.setText("");
        tvErrorName.setText("");
        tvErrorPass.setText("");
        tvErrorRePass.setText("");
        tvErrorCMNN.setText("");
        tvErrorPhone.setText("");
        userName = edtUserName.getText().toString();
        name = edtName.getText().toString();
        cmnd = edtCmnd.getText().toString();
        phone = edtPhone.getText().toString();
        pass = edtPass.getText().toString();
        rePass = edtRePass.getText().toString();
        showProgress();
        if (userName.isEmpty()) {
            tvErrorUsername.setText(getString(R.string.err_user_name_null));
            dismissProgress();
            return;
        }

        if (userName.length() < 3) {
            tvErrorUsername.setText(getString(R.string.err_user_name_lenght));
            dismissProgress();
            return;
        }

        if (name.isEmpty()) {
            tvErrorName.setText(getString(R.string.err_name_null));
            dismissProgress();
            return;
        }

        if (name.length() < 2) {
            tvErrorName.setText(getString(R.string.err_name_lenght));
            dismissProgress();
            return;
        }

        if (cmnd.isEmpty()) {
            tvErrorCMNN.setText(getString(R.string.err_cmnd_null));
            dismissProgress();
            return;
        }

        if (phone.isEmpty()) {
            tvErrorPhone.setText(getString(R.string.err_phone_null));
            dismissProgress();
            return;
        }

        if (pass.isEmpty()) {
            tvErrorPass.setText(getString(R.string.err_pass_null));
            dismissProgress();
            return;
        }

        if (rePass.isEmpty()) {
            tvErrorRePass.setText(getString(R.string.err_repass_null));
            dismissProgress();
            return;
        }

        if (!CommonUtils.isValidatePass(pass)) {
            tvErrorPass.setText(getString(R.string.err_pass_bigger_six_char));
            dismissProgress();
            return;
        }

        if (!pass.equals(rePass)) {
            tvErrorRePass.setText(getString(R.string.err_pass_duplicate_fail));
            dismissProgress();
            return;
        }

        if (!CommonUtils.isValidateUserName(userName)) {
            tvErrorUsername.setText(getString(R.string.err_validate_user_name_fail));
            dismissProgress();
            return;
        }

        if (!CommonUtils.isValidateName(name)) {
            tvErrorName.setText(getString(R.string.err_validate_name_fail));
            return;
        }

        if (!CommonUtils.validatePassPort(cmnd)) {
            tvErrorCMNN.setText(getString(R.string.err_validate_cmnd_fail));
            dismissProgress();
            return;
        }

        if (!CommonUtils.isValidatePhoneNumber(phone)) {
            tvErrorPhone.setText(getString(R.string.err_validate_phone_fail));
            dismissProgress();
            return;
        }
        if (ECashApplication.FBToken.isEmpty()) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getString(R.string.err_upload));
            dismissProgress();
            return;
        }
        if (PermissionUtils.checkPermissionReadPhoneState(this, null)) {
            startRegisterPassword();
        } else {
            dismissProgress();
            if(denyPermission){
                showDialogPermissions(getString(R.string.str_permission_contact_setting));
            }
        }
    }

    private void addTextChange() {
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getText()) {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, false);
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, true);
                }
            }
        });
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getText()) {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, false);
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, true);
                }
            }
        });
        edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getText()) {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, false);
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, true);
                }
            }
        });
        edtRePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getText()) {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, false);
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, true);
                }
            }
        });
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getText()) {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, false);
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, true);
                }
            }
        });
        edtCmnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getText()) {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, false);
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), edtConfirm, true);
                }
            }
        });
    }

    private boolean getText() {
        userName = edtUserName.getText().toString();
        name = edtName.getText().toString();
        cmnd = edtCmnd.getText().toString();
        phone = edtPhone.getText().toString();
        pass = edtPass.getText().toString();
        rePass = edtRePass.getText().toString();
        boolean check = true;
        if (userName.length() == 0) {
            check = false;
        }
        if (name.length() == 0) {
            check = false;
        }
        if (cmnd.length() == 0) {
            check = false;
        }
        if (phone.length() == 0) {
            check = false;
        }
        if (pass.length() == 0) {
            check = false;
        }
        if (rePass.length() == 0) {
            check = false;
        }
        return check;
    }

    public void requestOTPSuccess(AccountInfo accountInfo) {
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", "", "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                registerPresenter.activeAccount(getActivity(), accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                registerPresenter.retryOTP(getActivity(), accountInfo);
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
                registerPresenter.activeAccount(getActivity(), accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                registerPresenter.retryOTP(getActivity(), accountInfo);
            }

            @Override
            public void onCancel() {
                ((AccountActivity) getActivity()).onBackPressed();
            }
        });
    }

    @Override
    public void registerSuccess(AccountInfo mAccountInfo, String privateKeyBase64, String publicKeyBase64) {
        DatabaseUtil.changeMasterKeyDatabase(getActivity(), mAccountInfo.getMasterKey());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                KeyStoreUtils.saveMasterKey(mAccountInfo.getMasterKey(), getActivity());
                KeyStoreUtils.saveKeyPrivateWallet(privateKeyBase64, getActivity());
                DatabaseUtil.saveAccountInfo(mAccountInfo, getActivity());
            }
        }, 200);
        requestOTPSuccess(mAccountInfo);
    }

    @Override
    public void onUserNameFail() {
        if (isAdded() && getActivity() != null)
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_user_is_exit));
        edtUserName.requestFocus();
    }

    @Override
    public void showDialogError(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @Override
    public void onIDNumberFail(String idNumberFail) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), idNumberFail);
    }

    @Override
    public void onPhoneNumberFail(String phoneNumberFail) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), phoneNumberFail);
        edtPhone.requestFocus();
    }

    @Override
    public void registerFail(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
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
        registerPresenter.loginAccount(getActivity(), accountInfo);
    }

    @Override
    public void loginSuccess(AccountInfo accountInfo) {
        ECashApplication.setAccountInfo(accountInfo);
        if (PermissionUtils.isReadContact(getActivity())) {
            if (CommonUtils.getListPhoneNumber(getActivity()).size() > 0) {
                registerPresenter.syncContact(getActivity(), accountInfo);
            }
        }
        registerPresenter.getEDongInfo(getActivity(), accountInfo);
//        getActivity().startService(new Intent(getActivity(), WebSocketsService.class));
    }

    @Override
    public void getEDongInfoSuccess(AccountInfo accountInfo, ResponseDataEdong responseDataEdong) {
        ECashApplication.setAccountInfo(accountInfo);
        if (null != responseDataEdong.getListAcc()) {
            if (responseDataEdong.getListAcc().size() > 0) {
                ECashApplication.setListEDongInfo(responseDataEdong.getListAcc());
            }
        }
        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        if (getActivity() != null)
            getActivity().finish();
    }

    @Override
    public void onSyncContactSuccess() {
    }

    @Override
    public void onSyncContactFail(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @Override
    public void onOTPexpried() {
        if (null != getActivity())
            ((AccountActivity) getActivity()).onBackPressed();
    }

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }

    @OnClick({R.id.iv_back, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (getActivity() != null)
                    ((AccountActivity) getActivity()).onBackPressed();
                break;
            case R.id.btn_confirm:
                if (KeyStoreUtils.getMasterKey(getActivity()) != null) {
                    List<AccountInfo> listAccount = DatabaseUtil.getAllAccountInfo(getContext());
                    if (listAccount != null) {
                        if (listAccount.size() > 0) {
                           // DialogUtil.getInstance().showDialogWarning(getActivity(), getString(R.string.err_device_acc_exit));
                            AccountInfo accountInfo = listAccount.get(0);
                            if(accountInfo!=null){
                                registerPresenter.retryOTP(getActivity(),accountInfo);
                                return;
                            }
                        }
                    }
                }
                validateData();
                break;
        }
    }
}
