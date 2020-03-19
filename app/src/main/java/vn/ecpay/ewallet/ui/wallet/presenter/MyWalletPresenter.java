package vn.ecpay.ewallet.ui.wallet.presenter;

import android.content.Context;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.wallet.view.MyWalletView;

public interface MyWalletPresenter extends Presenter<MyWalletView> {
    void logout(AccountInfo accountInfo);

    void validateCancelAccount(long balance, AccountInfo accountInfo, Context context);

    void cancelAccount(AccountInfo accountInfo, Context context);
}
