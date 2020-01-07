package vn.ecpay.ewallet.ui.account.presenter;

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
import vn.ecpay.ewallet.model.forgotPassword.changePass.request.ChangePassRequest;
import vn.ecpay.ewallet.model.forgotPassword.changePass.response.ChangePassResponse;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.request.ForgotPassOTPRequest;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassOTPResponse;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassResponseData;
import vn.ecpay.ewallet.ui.account.view.ForgotPassView;

public class ForgotPassPresenterImpl implements ForgotPassPresenter {

    private ForgotPassView forgotPassView;
    @Inject
    ECashApplication eCashApplication;

    @Inject
    public ForgotPassPresenterImpl() {
    }

    @Override
    public void setView(ForgotPassView view) {
        this.forgotPassView = view;
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
    public void getOTPForgotPassword(String userName) {
        forgotPassView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(eCashApplication.getResources().getString(R.string.api_base_url));
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
                forgotPassView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            ForgotPassResponseData forgotPassOTPResponse = response.body().getResponseData();
                            forgotPassView.getOTPSuccess(forgotPassOTPResponse);
                        } else {
                            forgotPassView.showDialogError(response.body().getResponseMessage());
                        }
                    } else {
                        forgotPassView.showDialogError(eCashApplication.getResources().getString(R.string.err_upload));
                    }
                } else {
                    forgotPassView.showDialogError(eCashApplication.getResources().getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ForgotPassOTPResponse> call, Throwable t) {
                forgotPassView.dismissLoading();
                forgotPassView.showDialogError(eCashApplication.getResources().getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void requestChangePass(ForgotPassResponseData forgotPassResponseData, String otp, String newPass) {
        forgotPassView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(eCashApplication.getResources().getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        ChangePassRequest changePassRequest = new ChangePassRequest();
        changePassRequest.setChannelCode(Constant.CHANNEL_CODE);
        changePassRequest.setFunctionCode(Constant.FUNCTION_CHANGE_PASS);
        changePassRequest.setOtpvalue(otp);
        changePassRequest.setPassword(CommonUtils.encryptPassword(newPass));
        changePassRequest.setTransactionCode(forgotPassResponseData.getTransactionCode());
        changePassRequest.setUserId(forgotPassResponseData.getUserId());
        changePassRequest.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(changePassRequest));
        changePassRequest.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ChangePassResponse> call = apiService.changePass(changePassRequest);
        call.enqueue(new Callback<ChangePassResponse>() {
            @Override
            public void onResponse(Call<ChangePassResponse> call, Response<ChangePassResponse> response) {
                forgotPassView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            forgotPassView.changePassSuccess();
                        } else if (response.body().getResponseCode().equals("3014") ||
                                response.body().getResponseCode().equals("0998")) {
                            forgotPassView.showDialogError(eCashApplication.getResources().getString(R.string.err_otp_fail));
                        } else {
                            forgotPassView.showDialogError(response.body().getResponseMessage());
                        }
                    } else {
                        forgotPassView.showDialogError(eCashApplication.getResources().getString(R.string.err_upload));
                    }
                } else {
                    forgotPassView.showDialogError(eCashApplication.getResources().getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ChangePassResponse> call, Throwable t) {
                forgotPassView.dismissLoading();
                forgotPassView.showDialogError(eCashApplication.getResources().getString(R.string.err_upload));
            }
        });
    }
}
