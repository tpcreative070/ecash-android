package vn.ecpay.ewallet.ui.account.presenter;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.view.LoginView;

public interface LoginPresenter extends Presenter<LoginView> {
    void requestLogin(AccountInfo accountInfo, String userName, String pass);

    void getEDongInfo(AccountInfo accountInfo);

    void requestOTPActiveAccount(AccountInfo accountInfo, String pass);

    void activeAccount(AccountInfo accountInfo, String otp);
}
