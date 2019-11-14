package vn.ecpay.ewallet.ui.account.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassResponseData;
import vn.ecpay.ewallet.ui.account.ForgotPasswordActivity;
import vn.ecpay.ewallet.ui.account.module.ForgotPassModule;
import vn.ecpay.ewallet.ui.account.presenter.ForgotPassPresenter;
import vn.ecpay.ewallet.ui.account.view.ForgotPassView;

public class ForgotChangePassFragment extends ECashBaseFragment implements ForgotPassView {
    @BindView(R.id.edt_otp)
    EditText edtOtp;
    @BindView(R.id.edt_new_pass)
    EditText edtNewPass;
    @BindView(R.id.edt_re_new_pass)
    EditText edtReNewPass;
    private ForgotPassResponseData forgotPassResponseData;
    private String userName, otp, newPass, reNewPass;
    @Inject
    ForgotPassPresenter forgotPassPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.forgot_password_change_fragment;
    }

    public static ForgotChangePassFragment newInstance(ForgotPassResponseData forgotPassResponseData, String userName) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.FORGOT_PASS_TRANSFER_MODEL, forgotPassResponseData);
        args.putString(Constant.USER_NAME, userName);
        ForgotChangePassFragment fragment = new ForgotChangePassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.forgotPassResponseData = (ForgotPassResponseData) bundle.getSerializable(Constant.FORGOT_PASS_TRANSFER_MODEL);
            this.userName = bundle.getString(Constant.USER_NAME);
        } else {
            ((ForgotPasswordActivity) Objects.requireNonNull(getActivity())).onBackPressed();
        }
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new ForgotPassModule(this)).inject(this);
        forgotPassPresenter.setView(this);
        forgotPassPresenter.onViewCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ForgotPasswordActivity) getActivity()).updateTitle(getResources().getString(R.string.str_restore_pass));
    }

    @OnClick({R.id.btn_send_again_code, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send_again_code:
                forgotPassPresenter.getOTPForgotPassword(userName);
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void validateData() {
        otp = edtOtp.getText().toString();
        newPass = edtNewPass.getText().toString();
        reNewPass = edtReNewPass.getText().toString();
        if (userName.isEmpty()) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_user_name_null));
            return;
        }
        if (otp.isEmpty()) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_input_otp_null));
            return;
        }
        if (newPass.isEmpty()) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_pass_null));
            return;
        }
        if (reNewPass.isEmpty()) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_repass_null));
            return;
        }

        if (!CommonUtils.isValidatePass(newPass)) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_pass_bigger_six_char));
            return;
        }

        if (!newPass.equals(reNewPass)) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_pass_duplicate_fail));
            return;
        }

        if (!CommonUtils.isValidateUserName(userName)) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_validate_user_name_fail));
            return;
        }
        if (null == forgotPassResponseData) {
            ((ForgotPasswordActivity) getActivity()).showDialogError(getString(R.string.err_upload));
        }
        forgotPassPresenter.requestChangePass(forgotPassResponseData, otp, newPass);
    }

    @Override
    public void showDialogError(String err) {
        ((ForgotPasswordActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void changePassSuccess() {
        ECashApplication.get(Objects.requireNonNull(getActivity())).showDialogChangePassSuccess(getString(R.string.str_change_pass_success));
    }

    @Override
    public void getOTPSuccess(ForgotPassResponseData forgotPassOTPResponse) {
        Toast.makeText(getActivity(), getResources().getString(R.string.str_send_otp_success), Toast.LENGTH_LONG).show();
        this.forgotPassResponseData = forgotPassOTPResponse;
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
