package vn.ecpay.ewallet.ui.account.presenter;

import android.content.Context;

import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.view.RegisterView;
import vn.ecpay.ewallet.common.base.Presenter;

public interface RegisterPresenter extends Presenter<RegisterView> {
    void checkUSerNameAccount(String userName);

    void checkIdAndPhoneNumberAccount(String idNumber, String phone);

    void requestRegister(Context context, String userName, String CMND, String pass, String name, String phone);

    void retryOTP(AccountInfo accountInfo);

    void activeAccount(AccountInfo accountInfo, String otp);

    void loginAccount(AccountInfo accountInfo);

    void getEDongInfo(AccountInfo accountInfo);
}
