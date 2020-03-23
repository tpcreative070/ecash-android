package vn.ecpay.ewallet.ui.account.view;

import vn.ecpay.ewallet.common.base.ViewBase;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseDataEdong;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;

public interface RegisterView extends ViewBase {
    void registerSuccess(AccountInfo accountInfo, String privateKeyBase64, String publicKeyBase64);

    void onUserNameFail();

    void showDialogError(String err);

    void onIDNumberFail(String idNumberFail);

    void onPhoneNumberFail(String phoneNumberFail);

    void registerFail(String err);

    void retryOTPSuccess(AccountInfo accountInfo);

    void requestOtpErr(AccountInfo accountInfo, String err);

    void activeAccountSuccess(AccountInfo accountInfo);

    void loginSuccess(AccountInfo accountInfo);

    void getEDongInfoSuccess(AccountInfo accountInfo, ResponseDataEdong responseDataEdong);

    void onSyncContactSuccess();

    void onSyncContactFail(String err);

    void onOTPexpried();
}
