package vn.ecpay.ewallet.ui.wallet.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;

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
import vn.ecpay.ewallet.common.base.CircleImageView;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.account.updateInfo.request.RequestUpdateAccountInfo;
import vn.ecpay.ewallet.model.account.updateInfo.response.ResponseUpdateAccountInfo;

public class EditAccountInfoActivity extends ECashBaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_account)
    CircleImageView ivAccount;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.edt_cmnd)
    EditText edtCmnd;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    private AccountInfo accountInfo;
    private String name, address, cmnd, email;

    @Override
    protected int getLayoutResId() {
        return R.layout.edit_account_info_activity;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.getMenu().clear();
            toolbarCenterText.setText(getString(R.string.str_account_info));
            ivBack.setOnClickListener(v -> onBackPressed());
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        setData();
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        validateData();
    }

    private void validateData() {
        name = edtName.getText().toString();
        address = edtAddress.getText().toString();
        cmnd = edtCmnd.getText().toString();
        email = edtEmail.getText().toString();
        if (name.isEmpty()) {
            DialogUtil.getInstance().showDialogWarning(this, getString(R.string.err_user_name_null));
            return;
        }

        if (cmnd.isEmpty()) {
            DialogUtil.getInstance().showDialogWarning(this, getString(R.string.err_cmnd_null));
            return;
        }
        if (!email.isEmpty()) {
            if (!CommonUtils.isValidateEmail(email)) {
                DialogUtil.getInstance().showDialogWarning(this, getString(R.string.err_email_validate_fail));
                return;
            }
        }

        if (!CommonUtils.isValidateName(name)) {
            DialogUtil.getInstance().showDialogWarning(this, getString(R.string.err_validate_name_fail));
            return;
        }

        if (!CommonUtils.validatePassPort(cmnd)) {
            DialogUtil.getInstance().showDialogWarning(this, getString(R.string.err_validate_cmnd_fail));
            return;
        }

        updateAccountInfo();
    }

    private void setData() {
        WalletDatabase.getINSTANCE(this, ECashApplication.masterKey);
        accountInfo = WalletDatabase.getAccountInfoTask(ECashApplication.getAccountInfo().getUsername());
        if (accountInfo != null) {
            edtName.setText(CommonUtils.getFullName(accountInfo));
            edtAddress.setText(accountInfo.getPersonCurrentAddress());
            edtCmnd.setText(accountInfo.getIdNumber());
            edtEmail.setText(accountInfo.getPersonEmail());
            updateAvatar();
        }
    }

    private void updateAvatar() {
        if (ECashApplication.getAccountInfo().getLarge() == null) {
            ivAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar));
        } else {
            CommonUtils.loadAvatar(EditAccountInfoActivity.this, ivAccount, ECashApplication.getAccountInfo().getLarge());
        }
    }

    public void updateAccountInfo() {
        showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getResources().getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestUpdateAccountInfo requestUpdateAccountInfo = new RequestUpdateAccountInfo();
        requestUpdateAccountInfo.setChannelCode(Constant.CHANNEL_CODE);
        requestUpdateAccountInfo.setFunctionCode(Constant.FUNCTION_UPDATE_ACCOUNT_INFO);
        requestUpdateAccountInfo.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestUpdateAccountInfo.setToken(CommonUtils.getToken());
        requestUpdateAccountInfo.setUsername(accountInfo.getUsername());
        requestUpdateAccountInfo.setAuditNumber(CommonUtils.getAuditNumber());
        String[] separated = name.split(" ");
        if (separated.length == 1) {
            requestUpdateAccountInfo.setPersonFirstName(name);
            requestUpdateAccountInfo.setPersonMiddleName("");
            requestUpdateAccountInfo.setPersonLastName("");
        } else if (separated.length == 2) {
            requestUpdateAccountInfo.setPersonFirstName(separated[0]);
            requestUpdateAccountInfo.setPersonMiddleName("");
            requestUpdateAccountInfo.setPersonLastName(separated[1]);
        } else if (separated.length == 3) {
            requestUpdateAccountInfo.setPersonFirstName(separated[0]);
            requestUpdateAccountInfo.setPersonMiddleName(separated[1]);
            requestUpdateAccountInfo.setPersonLastName(separated[2]);
        } else {
            requestUpdateAccountInfo.setPersonFirstName(separated[0]);
            StringBuilder middleName = new StringBuilder();
            for (int i = 1; i < separated.length - 1; i++) {
                if (i == 1) {
                    middleName.append(separated[i]);
                }else {
                    middleName.append(" ").append(separated[i]);
                }
            }
            requestUpdateAccountInfo.setPersonMiddleName(middleName.toString());
            requestUpdateAccountInfo.setPersonLastName(separated[separated.length - 1]);
        }

        requestUpdateAccountInfo.setPersonCurrentAddress(address);
        requestUpdateAccountInfo.setPersonEmail(email);
        requestUpdateAccountInfo.setIdNumber(cmnd);

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestUpdateAccountInfo));
        requestUpdateAccountInfo.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseUpdateAccountInfo> call = apiService.updateAccountInfo(requestUpdateAccountInfo);
        call.enqueue(new Callback<ResponseUpdateAccountInfo>() {
            @Override
            public void onResponse(Call<ResponseUpdateAccountInfo> call, Response<ResponseUpdateAccountInfo> response) {
                dismissLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getResponseCode() != null) {
                            String code = response.body().getResponseCode();
                            if (code.equals(Constant.CODE_SUCCESS)) {
                                WalletDatabase.getINSTANCE(EditAccountInfoActivity.this, ECashApplication.masterKey);
                                WalletDatabase.updateAccountInfo(requestUpdateAccountInfo.getPersonFirstName(),
                                        requestUpdateAccountInfo.getPersonLastName(),
                                        requestUpdateAccountInfo.getPersonMiddleName(),
                                        requestUpdateAccountInfo.getIdNumber(),
                                        requestUpdateAccountInfo.getPersonCurrentAddress(),
                                        requestUpdateAccountInfo.getPersonEmail(), accountInfo.getUsername());
                                DialogUtil.getInstance().showDialogUpdateAccountInfo(EditAccountInfoActivity.this, Constant.STR_EMPTY, "Cập nhập thông tin tài khoản thành công", new DialogUtil.OnConfirm() {
                                    @Override
                                    public void OnListenerOk() {
                                        finish();
                                        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_ACCOUNT_INFO));
                                    }

                                    @Override
                                    public void OnListenerCancel() {
                                        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_ACCOUNT_INFO));

                                    }
                                });
                            } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                                ECashApplication.getInstance().checkSessionByErrorCode(response.body().getResponseCode());
                            } else {
                                showDialogError(response.body().getResponseMessage());
                            }
                        }
                    } else {
                        showDialogError(getResources().getString(R.string.err_upload));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateAccountInfo> call, Throwable t) {
                dismissLoading();
                showDialogError(getResources().getString(R.string.err_upload));
            }
        });
    }
}
