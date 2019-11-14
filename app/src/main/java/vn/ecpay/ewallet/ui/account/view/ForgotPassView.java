package vn.ecpay.ewallet.ui.account.view;

import vn.ecpay.ewallet.common.base.ViewBase;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassResponseData;

public interface ForgotPassView extends ViewBase {
    void getOTPSuccess(ForgotPassResponseData forgotPassOTPResponse);

    void showDialogError(String err);

    void changePassSuccess();
}
