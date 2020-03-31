package vn.ecpay.ewallet.ui.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

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
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.changePass.RequestChangePassword;
import vn.ecpay.ewallet.model.changePass.response.ResponseChangePassword;
import vn.ecpay.ewallet.ui.account.AccountActivity;

public class  ChangePassActivity extends ECashBaseActivity {
    @BindView(R.id.edt_old_pass)
    EditText edtOldPass;
    @BindView(R.id.tv_error_old_pass)
    TextView tvErrorOldPass;

    @BindView(R.id.edt_new_pass)
    EditText edtNewPass;
    @BindView(R.id.tv_error_new_pass)
    TextView tvErrorNewPass;

    @BindView(R.id.edt_re_new_pass)
    EditText edtReNewPass;
    @BindView(R.id.tv_error_re_new_pass)
    TextView tvErrorReNewPass;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_confirm)
    Button btConfirm;

    private String newPass;
    private String oldPass;
    private AccountInfo accountInfo;

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
        Utils.disableButtonConfirm(getBaseActivity(),btConfirm,true);
        addTextChange();
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        tvErrorOldPass.setText("");
        tvErrorNewPass.setText("");
        tvErrorReNewPass.setText("");
        oldPass = edtOldPass.getText().toString();
        newPass = edtNewPass.getText().toString();
        String reNewPass = edtReNewPass.getText().toString();

        if (oldPass.isEmpty()) {
            tvErrorOldPass.setText(getString(R.string.err_old_pass_null));
            return;
        }

        if (newPass.isEmpty()) {
            tvErrorNewPass.setText(getString(R.string.err_new_pass_null));
            return;
        }

        if (reNewPass.isEmpty()) {
            tvErrorReNewPass.setText(getString(R.string.err_renew_pass_null));
            return;
        }

        if (!CommonUtils.isValidatePass(newPass)) {
            tvErrorNewPass.setText(getString(R.string.err_pass_bigger_six_char));
            return;
        }

        if (!newPass.equals(reNewPass)) {
            tvErrorNewPass.setText(getString(R.string.err_pass_duplicate_fail));
            tvErrorReNewPass.setText(getString(R.string.err_pass_duplicate_fail));
            return;
        }

        if (oldPass.equals(newPass)) {
            tvErrorOldPass.setText(getString(R.string.err_new_pass_must_other_old_pass));
            return;
        }
        showLoading();
        requestChangePass();
    }

    private void addTextChange(){
        edtOldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    if(edtNewPass.getText().length()>0&&edtNewPass.getText().length()>0){
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
                    if(edtOldPass.getText().length()>0&&edtReNewPass.getText().length()>0){
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
                    if(edtOldPass.getText().length()>0&&edtNewPass.getText().length()>0){
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

    private void requestChangePass() {
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
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                onChangePassSuccess();
                            } else {
                                showDialogError(getResources().getString(R.string.err_upload));
                            }
                        } else if (response.body().getResponseCode().equals(Constant.ERROR_CODE_3019)) {
                            showDialogError(getString(R.string.err_old_pass_invalid));
                        } else {
                            CheckErrCodeUtil.errorMessage(getApplicationContext(), response.body().getResponseCode());
                        }
                    } else {
                        showDialogError(getResources().getString(R.string.err_upload));
                    }
                } else {
                    showDialogError(getResources().getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseChangePassword> call, Throwable t) {
                dismissLoading();
                ECashApplication.getInstance().showErrorConnection(t);
            }
        });
    }

    private void onChangePassSuccess() {
        DialogUtil.getInstance().showDialogChangePassSuccess(ChangePassActivity.this,
                getString(R.string.str_change_pass_success), () -> {
                    Intent intent = new Intent(this, AccountActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                });
    }
}
