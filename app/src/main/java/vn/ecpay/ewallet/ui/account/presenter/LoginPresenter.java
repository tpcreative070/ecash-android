package vn.ecpay.ewallet.ui.account.presenter;

import android.content.Context;
import android.widget.TextView;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.view.LoginView;

public interface LoginPresenter extends Presenter<LoginView> {
    void requestLogin(Context context, AccountInfo accountInfo, String userName, String pass, TextView tvError);

    void getEDongInfo(Context context,AccountInfo accountInfo);

    void requestOTPActiveAccount(Context context,AccountInfo accountInfo, String pass);

    void activeAccount(Context context,AccountInfo accountInfo, String otp);
}
