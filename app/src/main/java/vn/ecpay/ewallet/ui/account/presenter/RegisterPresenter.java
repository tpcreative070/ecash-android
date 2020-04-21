package vn.ecpay.ewallet.ui.account.presenter;

import android.content.Context;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.view.RegisterView;

public interface RegisterPresenter extends Presenter<RegisterView> {
    void checkUSerNameAccount(String userName);

    void checkIdAndPhoneNumberAccount(String idNumber, String phone);

    void requestRegister(Context context, String userName, String CMND, String pass, String name, String phone);

    void retryOTP(Context context,AccountInfo accountInfo);

    void activeAccount(Context context,AccountInfo accountInfo, String otp);

    void loginAccount(Context context,AccountInfo accountInfo);

    void getEDongInfo(Context context,AccountInfo accountInfo);

    void syncContact(Context context, AccountInfo mAccountInfo);
}
