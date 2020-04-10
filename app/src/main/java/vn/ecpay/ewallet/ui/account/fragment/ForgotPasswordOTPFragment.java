package vn.ecpay.ewallet.ui.account.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.request.ForgotPassOTPRequest;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassOTPResponse;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassResponseData;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.account.ForgotPasswordActivity;

public class ForgotPasswordOTPFragment extends ECashBaseFragment implements DialogUtil.OnResult {
    @BindView(R.id.edt_user_name)
    EditText edtUserName;
    @BindView(R.id.tv_error_user_name)
    TextView tvErrorUsername;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.btn_confirm)
    Button btConfirm;
    private AccountInfo accountInfo;
    private String userName;

    @Override
    protected int getLayoutResId() {
        return R.layout.forgot_password_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isHaveAccount()) {
            tvUserName.setVisibility(View.VISIBLE);
            edtUserName.setText(accountInfo.getUsername());
            edtUserName.setFocusable(false);
        } else {
            tvUserName.setVisibility(View.INVISIBLE);
            edtUserName.setFocusable(true);
            Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
        }
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    Utils.disableButtonConfirm(getBaseActivity(),btConfirm,false);
                }else{
                    Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
                }


            }
        });
    }

    public boolean isHaveAccount() {
        if (KeyStoreUtils.getMasterKey(getActivity()) != null) {
            List<AccountInfo> listAccount = DatabaseUtil.getAllAccountInfo(getContext());
            if (null != listAccount) {
                if (listAccount.size() > 0) {
                    accountInfo = listAccount.get(0);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ForgotPasswordActivity) getActivity()).updateTitle(getResources().getString(R.string.str_forgot_password));
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        tvErrorUsername.setText("");
        userName = edtUserName.getText().toString();
        if (userName.isEmpty()) {
            tvErrorUsername.setText( getResources().getString(R.string.err_user_name_null));
           // DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_user_name_null));
            return;
        }

        if (!CommonUtils.isValidateUserName(userName)) {
            tvErrorUsername.setText( getResources().getString(R.string.err_validate_user_name_fail));
            return;
        }
        showProgress();
        getOTPForgotPassword();
    }

    public void getOTPForgotPassword() {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getResources().getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        ForgotPassOTPRequest forgotPassOTPRequest = new ForgotPassOTPRequest();
        forgotPassOTPRequest.setChannelCode(Constant.CHANNEL_CODE);
        forgotPassOTPRequest.setFunctionCode(Constant.FUNCTION_FORGOT_PASS_OTP);
        forgotPassOTPRequest.setUsername(userName);
        forgotPassOTPRequest.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(forgotPassOTPRequest));
        forgotPassOTPRequest.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ForgotPassOTPResponse> call = apiService.getOTPForgotPass(forgotPassOTPRequest);
        call.enqueue(new Callback<ForgotPassOTPResponse>() {
            @Override
            public void onResponse(Call<ForgotPassOTPResponse> call, Response<ForgotPassOTPResponse> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            ForgotPassResponseData forgotPassOTPResponse = response.body().getResponseData();
                            getOTPSuccess(forgotPassOTPResponse);
                        } else {
                            CheckErrCodeUtil.errorMessage(getActivity(), response.body().getResponseCode());
                        }
                    } else {
                        showDialogError(getResources().getString(R.string.err_upload));
                    }
                } else {
                    showDialogError(getResources().getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ForgotPassOTPResponse> call, Throwable t) {
                dismissProgress();
               // showDialogError(getResources().getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t,ForgotPasswordOTPFragment.this);
            }
        });
    }

    private void getOTPSuccess(ForgotPassResponseData forgotPassResponseData) {
        ((ForgotPasswordActivity) getActivity()).addFragment(ForgotChangePassFragment.newInstance(forgotPassResponseData, userName), true);
    }

    @Override
    public void OnListenerOk() {
        getOTPForgotPassword();
    }

//    private void showDialogError(String err) {
//        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
//    }
}
