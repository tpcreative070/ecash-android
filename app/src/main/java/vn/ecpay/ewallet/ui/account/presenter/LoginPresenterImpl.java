package vn.ecpay.ewallet.ui.account.presenter;

import android.util.Log;

import com.google.gson.Gson;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.OTP.RequestGetOTP;
import vn.ecpay.ewallet.model.OTP.response.ResponseGetOTP;
import vn.ecpay.ewallet.model.account.active.RequestActiveAccount;
import vn.ecpay.ewallet.model.account.active.ResponseActiveAccount;
import vn.ecpay.ewallet.model.account.getEdongInfo.RequestEdongInfo;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseDataEdong;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseEdongInfo;
import vn.ecpay.ewallet.model.account.login.RequestLogin;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.ResponseLoginAfterRegister;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.view.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;
    @Inject
    ECashApplication application;

    @Inject
    public LoginPresenterImpl() {
    }

    @Override
    public void setView(LoginView view) {
        this.loginView = view;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewStart() {

    }

    @Override
    public void onViewResume() {

    }

    @Override
    public void onViewPause() {

    }

    @Override
    public void onViewStop() {

    }

    @Override
    public void onViewDestroy() {

    }

    @Override
    public void requestLogin(AccountInfo accountInfo, String userName, String pass) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setChannelCode(Constant.CHANNEL_CODE);
        requestLogin.setFunctionCode(Constant.FUNCTION_LOGIN);
        requestLogin.setUsername(userName);
        requestLogin.setToken(CommonUtils.encryptPassword(pass));
        requestLogin.setUuid(Constant.STR_EMPTY);
        requestLogin.setChannelSignature(Constant.STR_EMPTY);
        requestLogin.setTerminalId(Constant.STR_EMPTY);
        requestLogin.setTransactionId(Constant.STR_EMPTY);
        requestLogin.setAuditNumber(CommonUtils.getAuditNumber());

        String alphabe = CommonUtils.getStringAlphabe(requestLogin);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestLogin));
        requestLogin.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(requestLogin);
        Call<ResponseLoginAfterRegister> call = apiService.login(requestLogin);
        call.enqueue(new Callback<ResponseLoginAfterRegister>() {
            @Override
            public void onResponse(Call<ResponseLoginAfterRegister> call, Response<ResponseLoginAfterRegister> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            loginView.requestLoginSuccess(response.body().getAccountInfo());
                        } else if (response.body().getResponseCode().equals("3077")) {
                            //must save account to active account
                            if (accountInfo != null) {
                                loginView.requestActiveAccount();
                            } else {
                                loginView.dismissLoading();
                                loginView.showDialogError(response.body().getResponseMessage());
                            }
                        } else if (response.body().getResponseCode().equals("3035")) {
                            loginView.dismissLoading();
                            loginView.showDialogError(application.getString(R.string.err_user_not_exit));
                        } else {
                            loginView.dismissLoading();
                            loginView.showDialogError(response.body().getResponseMessage());
                        }
                    }
                } else {
                    loginView.dismissLoading();
                    loginView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginAfterRegister> call, Throwable t) {
                loginView.dismissLoading();
                loginView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void getEDongInfo(AccountInfo accountInfo) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestEdongInfo requestEdongInfo = new RequestEdongInfo();
        requestEdongInfo.setAuditNumber(CommonUtils.getAuditNumber());
        requestEdongInfo.setChannelCode(Constant.CHANNEL_CODE);
        requestEdongInfo.setFunctionCode(Constant.FUNCTION_GET_EDONG_INFO);
        requestEdongInfo.setSessionId(accountInfo.getSessionId());
        requestEdongInfo.setToken(CommonUtils.getToken(accountInfo));
        requestEdongInfo.setUsername(accountInfo.getUsername());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestEdongInfo));
        requestEdongInfo.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(requestEdongInfo);
        Call<ResponseEdongInfo> call = apiService.getEdongInfo(requestEdongInfo);
        call.enqueue(new Callback<ResponseEdongInfo>() {
            @Override
            public void onResponse(Call<ResponseEdongInfo> call, Response<ResponseEdongInfo> response) {
                loginView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            ResponseDataEdong responseDataEdong = response.body().getResponseData();
                            accountInfo.setWalletId(responseDataEdong.getWaletId());
                            loginView.requestGetEDongInfoSuccess(responseDataEdong, accountInfo);
                        } else {
                            loginView.showDialogError(response.body().getResponseMessage());
                        }
                    } else {
                        loginView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    loginView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongInfo> call, Throwable t) {
                loginView.dismissLoading();
                loginView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void requestOTPActiveAccount(AccountInfo accountInfo, String pass) {
        loginView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetOTP requestGetOTP = new RequestGetOTP();
        requestGetOTP.setChannelCode(Constant.CHANNEL_CODE);
        requestGetOTP.setFunctionCode(Constant.FUNCTION_GET_OTP);
        requestGetOTP.setToken(CommonUtils.encryptPassword(pass));
        requestGetOTP.setUsername(accountInfo.getUsername());
        requestGetOTP.setWalletId(String.valueOf(accountInfo.getWalletId()));
        requestGetOTP.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetOTP));
        requestGetOTP.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(requestGetOTP);
        Call<ResponseGetOTP> call = apiService.getOTP(requestGetOTP);
        call.enqueue(new Callback<ResponseGetOTP>() {
            @Override
            public void onResponse(Call<ResponseGetOTP> call, Response<ResponseGetOTP> response) {
                loginView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            accountInfo.setTransactionCode(String.valueOf(response.body().getResponseData().getTransactionCode()));
                            loginView.requestOTPSuccess(accountInfo);
                        } else if (response.body().getResponseCode().equals("3015")) {
                            //quá thời hạn gửi OTP
                            loginView.showDialogError(response.body().getResponseMessage());
                        } else {
                            loginView.showDialogError(response.body().getResponseMessage());
                        }
                    } else {
                        loginView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    loginView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOTP> call, Throwable t) {
                loginView.dismissLoading();
                loginView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }
    @Override
    public void activeAccount(AccountInfo accountInfo, String otp) {
        loginView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestActiveAccount requestActiveAccount = new RequestActiveAccount();
        requestActiveAccount.setAccountIdt(String.valueOf(accountInfo.getAccountIdt()));
        requestActiveAccount.setChannelCode(Constant.CHANNEL_CODE);
        requestActiveAccount.setCustomerId(String.valueOf(accountInfo.getCustomerId()));
        requestActiveAccount.setIdNumber(accountInfo.getIdNumber());
        requestActiveAccount.setUserId(accountInfo.getUserId());
        requestActiveAccount.setFunctionCode(Constant.FUNCTION_ACTIVE_ACCOUNT);
        requestActiveAccount.setOtpvalue(otp);
        requestActiveAccount.setWalletId(accountInfo.getWalletId());
        requestActiveAccount.setTransactionCode(accountInfo.getTransactionCode());
        requestActiveAccount.setAuditNumber(CommonUtils.getAuditNumber());

        String alphabe = CommonUtils.getStringAlphabe(requestActiveAccount);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestActiveAccount));
        requestActiveAccount.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(requestActiveAccount);
        Call<ResponseActiveAccount> call = apiService.activeAccount(requestActiveAccount);
        call.enqueue(new Callback<ResponseActiveAccount>() {
            @Override
            public void onResponse(Call<ResponseActiveAccount> call, Response<ResponseActiveAccount> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            loginView.activeAccountSuccess();
                        } else if (response.body().getResponseCode().equals("3014") ||
                                response.body().getResponseCode().equals("0998")) {
                            loginView.dismissLoading();
                            loginView.requestOTPFail(application.getString(R.string.err_otp_input_fail), accountInfo);
                        } else {
                            loginView.dismissLoading();
                            loginView.showDialogError(response.body().getResponseMessage());
                        }
                    } else {
                        loginView.dismissLoading();
                        loginView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    loginView.dismissLoading();
                    loginView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseActiveAccount> call, Throwable t) {
                loginView.dismissLoading();
                loginView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }
}
