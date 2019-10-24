package vn.ecpay.ewallet.ui.account.view;

import vn.ecpay.ewallet.common.base.ViewBase;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseDataEdong;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;

public interface LoginView extends ViewBase {
    void showDialogError(String responseMessage);

    void requestLoginSuccess(AccountInfo accountInfo);

    void requestActiveAccount();

    void requestOTPSuccess(AccountInfo accountInfo);

    void requestOTPFail(String err, AccountInfo accountInfo);

    void requestGetEDongInfoSuccess(ResponseDataEdong responseDataEdong, AccountInfo accountInfo);

    void activeAccountSuccess();
}
