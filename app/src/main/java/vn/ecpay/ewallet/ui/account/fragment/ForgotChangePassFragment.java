package vn.ecpay.ewallet.ui.account.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassResponseData;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.account.ForgotPasswordActivity;
import vn.ecpay.ewallet.ui.account.module.ForgotPassModule;
import vn.ecpay.ewallet.ui.account.presenter.ForgotPassPresenter;
import vn.ecpay.ewallet.ui.account.view.ForgotPassView;

public class ForgotChangePassFragment extends ECashBaseFragment implements ForgotPassView {
    @BindView(R.id.edt_otp)
    EditText edtOtp;
    @BindView(R.id.tv_error_otp)
    TextView tvErrorOTP;


    @BindView(R.id.edt_new_pass)
    EditText edtNewPass;
    @BindView(R.id.tv_error_new_pass)
    TextView tvErrorNewPass;

    @BindView(R.id.edt_re_new_pass)
    EditText edtReNewPass;
    @BindView(R.id.tv_error_re_new_pass)
    TextView tvErrorReNewPass;

    @BindView(R.id.btn_confirm)
    Button btConfirm;


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
            if (getActivity() != null)
                getActivity().onBackPressed();
        }
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new ForgotPassModule(this)).inject(this);
        forgotPassPresenter.setView(this);
        forgotPassPresenter.onViewCreate();
        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
        addTextChange();
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
                tvErrorOTP.setText("");
                forgotPassPresenter.getOTPForgotPassword(userName, getActivity());
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void validateData() {
        tvErrorOTP.setText("");
        tvErrorNewPass.setText("");
        tvErrorReNewPass.setText("");
        otp = edtOtp.getText().toString();
        newPass = edtNewPass.getText().toString();
        reNewPass = edtReNewPass.getText().toString();
        if (userName.isEmpty()) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_user_name_null));
            return;
        }
        if (otp.isEmpty()) {
            tvErrorOTP.setText(getResources().getString(R.string.err_input_otp_null));
        return;
        }
        if (newPass.isEmpty()) {
            tvErrorNewPass.setText(getResources().getString(R.string.err_pass_null));
            return;
        }
        if (reNewPass.isEmpty()) {
            tvErrorReNewPass.setText(getResources().getString(R.string.err_repass_null));
            return;
        }

        if (!CommonUtils.isValidatePass(newPass)) {
            tvErrorNewPass.setText(getResources().getString(R.string.err_pass_bigger_six_char));
            tvErrorReNewPass.setText(getResources().getString(R.string.err_pass_bigger_six_char));
            return;
        }

        if (!newPass.equals(reNewPass)) {
           // tvErrorNewPass.setText(getResources().getString(R.string.err_pass_duplicate_fail));
            tvErrorReNewPass.setText(getResources().getString(R.string.err_pass_duplicate_fail));
            return;
        }

        if (!CommonUtils.isValidateUserName(userName)) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_validate_user_name_fail));
            return;
        }
        if (null == forgotPassResponseData) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_upload));
        }
        forgotPassPresenter.requestChangePass(forgotPassResponseData, otp, newPass, getActivity(),tvErrorOTP);
    }

    @Override
    public void showDialogError(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @Override
    public void changePassSuccess() {
        DialogUtil.getInstance().showDialogFogotPassSuccess(getActivity(), "", getResources().getString(R.string.str_restore_pass_success), new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void getOTPSuccess(ForgotPassResponseData forgotPassOTPResponse) {
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
    private void addTextChange(){
        edtOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    if(edtNewPass.getText().length()>0&&edtReNewPass.getText().length()>0){
                        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,false);
                    }else{
                        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
                    }
                }else{
                    Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
                }

            }
        });
        edtNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    if(edtOtp.getText().length()>0&&edtReNewPass.getText().length()>0){
                        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,false);
                    }else{
                        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
                    }
                }else{
                    Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
                }

            }
        });
        edtReNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    if(edtOtp.getText().length()>0&&edtNewPass.getText().length()>0){
                        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,false);
                    }else{
                        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
                    }
                }else{
                    Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
                }
            }
        });

    }
}
