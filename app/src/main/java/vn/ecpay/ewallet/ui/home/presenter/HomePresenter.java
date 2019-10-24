package vn.ecpay.ewallet.ui.home.presenter;

import android.content.Context;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.home.view.HomeView;

public interface HomePresenter extends Presenter<HomeView> {
    void activeAccWalletInfo(AccountInfo accountInfo, Context context);
}
