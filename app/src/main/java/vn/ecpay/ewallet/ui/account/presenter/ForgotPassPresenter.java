package vn.ecpay.ewallet.ui.account.presenter;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassResponseData;
import vn.ecpay.ewallet.ui.account.view.ForgotPassView;

public interface ForgotPassPresenter extends Presenter<ForgotPassView> {
    void getOTPForgotPassword(String userName);

    void requestChangePass(ForgotPassResponseData forgotPassResponseData, String otp, String newPass);
}