package vn.ecpay.ewallet.ui.account.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.request.ForgotPassOTPRequest;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassOTPResponse;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassResponseData;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.account.ForgotPasswordActivity;

public class ForgotPasswordOTPFragment extends ECashBaseFragment {
    @BindView(R.id.edt_user_name)
    EditText edtUserName;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
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
        }
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
        userName = edtUserName.getText().toString();
        if (userName.isEmpty()) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_user_name_null));
            return;
        }

        if (!CommonUtils.isValidateUserName(userName)) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_validate_user_name_fail));
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
                            showDialogError(response.body().getResponseMessage());
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
                showDialogError(getResources().getString(R.string.err_upload));
            }
        });
    }

    private void getOTPSuccess(ForgotPassResponseData forgotPassResponseData) {
        Toast.makeText(getActivity(), getResources().getString(R.string.str_send_otp_success), Toast.LENGTH_LONG).show();
        ((ForgotPasswordActivity) getActivity()).addFragment(ForgotChangePassFragment.newInstance(forgotPassResponseData, userName), true);
    }

    private void showDialogError(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }
}
