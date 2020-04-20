package vn.ecpay.ewallet.ui.account.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.CircleImageView;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseDataEdong;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.account.ForgotPasswordActivity;
import vn.ecpay.ewallet.ui.account.module.LoginModule;
import vn.ecpay.ewallet.ui.account.presenter.LoginPresenter;
import vn.ecpay.ewallet.ui.account.view.LoginView;

public class FragmentLogin extends ECashBaseFragment implements LoginView {
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.ed_user_name)
    EditText edUserName;
    @BindView(R.id.tv_error_user_name)
    TextView tvErrorUserName;

    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.tv_error_password)
    TextView tvErrorPassword;

    @BindView(R.id.layout_register)
    LinearLayout layoutRegister;
    @BindView(R.id.tv_error_register)
    TextView tvErrorRegister;

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
                tvErrorRegister.setText("");
                if (KeyStoreUtils.getMasterKey(getActivity()) != null) {
                    List<AccountInfo> listAccount = DatabaseUtil.getAllAccountInfo(getContext());
                    if (null != listAccount) {
                        if (listAccount.size() > 0) {
                            tvErrorRegister.setText(getString(R.string.err_device_acc_exit));
                            // DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_wallet_is_exit));
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
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        Utils.disableButtonConfirm(getBaseActivity(), btnLogin, true);
        addTextChange();
    }

    private void addTextChange() {
        edUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (edPassword.getText().length() > 0) {
                        Utils.disableButtonConfirm(getBaseActivity(), btnLogin, false);
                    } else {
                        Utils.disableButtonConfirm(getBaseActivity(), btnLogin, true);
                    }
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), btnLogin, true);
                }
            }
        });
        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 6) {
                    Utils.disableButtonConfirm(getBaseActivity(), btnLogin, false);
                } else {
                    Utils.disableButtonConfirm(getBaseActivity(), btnLogin, true);
                }

            }
        });

    }

    private void checkAccountExit() {
        if (KeyStoreUtils.getMasterKey(getActivity()) != null) {
            List<AccountInfo> listAccount = DatabaseUtil.getAllAccountInfo(getContext());
            Log.e("keyStore", KeyStoreUtils.getMasterKey(getActivity()));
            if (listAccount != null) {
                if (listAccount.size() > 0) {
                    accountInfo = listAccount.get(0);
                    edUserName.setVisibility(View.GONE);
                    layoutIsExit.setVisibility(View.VISIBLE);
                    tvName.setText(CommonUtils.getFullName(accountInfo));
                    tvPhone.setText(accountInfo.getPersonMobilePhone());
                    edUserName.setText(accountInfo.getUsername());
                    CommonUtils.loadAvatar(getActivity(), ivAccount, accountInfo.getLarge());
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
        checkAccountExit();
    }

    private void validateData() {
        showLoading();
        tvErrorUserName.setText("");
        tvErrorPassword.setText("");
        tvErrorRegister.setText("");
        userName = edUserName.getText().toString();
        pass = edPassword.getText().toString();
        if (userName.isEmpty()) {
            dismissLoading();
            tvErrorUserName.setText(getString(R.string.err_not_input_username));
            return;
        }

        if (pass.isEmpty()) {
            dismissLoading();
            tvErrorPassword.setText(getString(R.string.err_not_input_pass));
            return;
        }

        if (accountInfo != null) {
            if (!userName.equals(accountInfo.getUsername())) {
                dismissLoading();
                tvErrorRegister.setText(getString(R.string.err_device_acc_exit));
                return;
            }
        }
        loginPresenter.requestLogin(getActivity(), accountInfo, userName, pass, tvErrorUserName);
    }

    public void requestOTPSuccess(AccountInfo accountInfo) {
        DialogUtil.getInstance().showDialogInputOTP(getActivity(), "", "", "", new DialogUtil.OnConfirmOTP() {
            @Override
            public void onSuccess(String otp) {
                loginPresenter.activeAccount(getActivity(), accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                loginPresenter.requestOTPActiveAccount(getActivity(), accountInfo, pass);
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
                loginPresenter.activeAccount(getActivity(), accountInfo, otp);
            }

            @Override
            public void onRetryOTP() {
                loginPresenter.requestOTPActiveAccount(getActivity(), accountInfo, pass);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void requestGetEDongInfoSuccess(ResponseDataEdong response, AccountInfo accountInfo) {
        ECashApplication.setAccountInfo(accountInfo);
        if (null != DatabaseUtil.getAccountInfo(getActivity())) {
            WalletDatabase.updateFullAccountInfo(accountInfo.getPersonFirstName(),
                    accountInfo.getPersonLastName(),
                    accountInfo.getPersonMiddleName(),
                    accountInfo.getIdNumber(),
                    accountInfo.getPersonCurrentAddress(),
                    accountInfo.getPersonEmail(),
                    accountInfo.getLarge(),
                    accountInfo.getSessionId(),
                    accountInfo.getUsername(),
                    accountInfo.getToken());
        }
        if (response.getListAcc().size() > 0) {
            ECashApplication.setListEDongInfo(response.getListAcc());
        }
        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
        if (isTimeOut) {
            dismissProgress();
            if (getActivity() != null)
                getActivity().finish();
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            dismissProgress();
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    @Override
    public void activeAccountSuccess() {
        loginPresenter.requestLogin(getActivity(), accountInfo, userName, pass, tvErrorUserName);
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
        DialogUtil.getInstance().showDialogWarning(getActivity(), responseMessage);
    }

    @Override
    public void requestLoginSuccess(AccountInfo mAccountInfo) {
        restartSocket();
        mAccountInfo.setUsername(userName);
        loginPresenter.getEDongInfo(getActivity(), mAccountInfo);
        if (KeyStoreUtils.getMasterKey(getActivity()) != null &&
                KeyStoreUtils.getPrivateKey(getActivity()) != null) {
            ECashApplication.masterKey = KeyStoreUtils.getMasterKey(getActivity());
            ECashApplication.privateKey = KeyStoreUtils.getPrivateKey(getActivity());
        }
        if (accountInfo != null) {
            if (null == accountInfo.getLarge()) {
                WalletDatabase.updateAccountAvatar(mAccountInfo.getLarge(), accountInfo.getUsername());
            }
        }
    }

    @Override
    public void requestActiveAccount() {
        loginPresenter.requestOTPActiveAccount(getActivity(), accountInfo, pass);
    }
}
