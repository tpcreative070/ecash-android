package vn.ecpay.ewallet.ui.wallet.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import javax.inject.Inject;

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
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.changePass.RequestChangePassword;
import vn.ecpay.ewallet.model.changePass.response.ResponseChangePassword;

public class ChangePassActivity extends ECashBaseActivity {
    @BindView(R.id.edt_old_pass)
    EditText edtOldPass;
    @BindView(R.id.edt_new_pass)
    EditText edtNewPass;
    @BindView(R.id.edt_re_new_pass)
    EditText edtReNewPass;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String newPass, oldPass, reNewPass;
    private AccountInfo accountInfo;
    @Inject
    ECashApplication application;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountInfo = ECashApplication.getAccountInfo();
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.getMenu().clear();
            toolbarCenterText.setText(getString(R.string.str_change_pass));
            ivBack.setOnClickListener(v -> onBackPressed());
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        validateData();
    }

    private void validateData() {
        oldPass = edtOldPass.getText().toString();
        newPass = edtNewPass.getText().toString();
        reNewPass = edtReNewPass.getText().toString();

        if (oldPass.isEmpty()) {
            showDialogError(getString(R.string.err_old_pass_null));
            return;
        }

        if (newPass.isEmpty()) {
            showDialogError(getString(R.string.err_new_pass_null));
            return;
        }

        if (reNewPass.isEmpty()) {
            showDialogError(getString(R.string.err_renew_pass_null));
            return;
        }

        if (!CommonUtils.isValidatePass(newPass)) {
            showDialogError(getString(R.string.err_pass_bigger_six_char));
            return;
        }

        if (!newPass.equals(reNewPass)) {
            showDialogError(getString(R.string.err_pass_duplicate_fail));
            return;
        }

        if (oldPass.equals(newPass)) {
            showDialogError(getString(R.string.err_new_pass_must_other_old_pass));
            return;
        }
        requestChangePass();
    }

    private void requestChangePass() {
        showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestChangePassword requestChangePassword = new RequestChangePassword();
        requestChangePassword.setChannelCode(Constant.CHANNEL_CODE);
        requestChangePassword.setFunctionCode(Constant.FUNCTION_CHANGE_PASSWORD);
        requestChangePassword.setPassword(CommonUtils.encryptPassword(newPass));
        requestChangePassword.setSessionId(accountInfo.getSessionId());
        requestChangePassword.setToken(CommonUtils.encryptPassword(oldPass));
        requestChangePassword.setUsername(accountInfo.getUsername());
        requestChangePassword.setUserId(accountInfo.getUserId());
        requestChangePassword.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestChangePassword));
        requestChangePassword.setmChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseChangePassword> call = apiService.changePassword(requestChangePassword);
        call.enqueue(new Callback<ResponseChangePassword>() {
            @Override
            public void onResponse(Call<ResponseChangePassword> call, Response<ResponseChangePassword> response) {
                dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            ECashApplication.get(ChangePassActivity.this).showDialogChangePassSuccess(getString(R.string.str_change_pass_success));
                        } else if (response.body().getResponseCode().equals("3019")) {
                            showDialogError(getString(R.string.err_old_pass_invalid));
                        } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            application.checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            showDialogError(response.body().getResponseMessage());
                        }
                    } else {
                        showDialogError(getString(R.string.err_upload));
                    }
                } else {
                    showDialogError(getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseChangePassword> call, Throwable t) {
                dismissLoading();
                showDialogError(getString(R.string.err_upload));
            }
        });
    }
}
