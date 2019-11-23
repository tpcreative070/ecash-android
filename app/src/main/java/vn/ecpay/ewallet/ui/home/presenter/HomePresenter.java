package vn.ecpay.ewallet.ui.home.presenter;

import android.content.Context;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.ResponseData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.home.view.HomeView;

public interface HomePresenter extends Presenter<HomeView> {
    void activeAccWalletInfo(AccountInfo accountInfo, ResponseData responseData, String otp, Context context);

    void syncContact(Context context, AccountInfo accountInfo);

    void getOTPActiveAccount(AccountInfo accountInfo, Context context);
}
