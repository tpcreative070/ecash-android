package vn.ecpay.ewallet.ui.cashToCash.presenter;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.cashToCash.view.CashToCashView;

public interface CashToCashPresenter extends Presenter<CashToCashView> {
    void getPublicKeyWallet(AccountInfo accountInfo, String idWallet);

}
