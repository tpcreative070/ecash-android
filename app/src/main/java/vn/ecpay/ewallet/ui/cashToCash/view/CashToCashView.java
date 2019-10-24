package vn.ecpay.ewallet.ui.cashToCash.view;

import vn.ecpay.ewallet.common.base.BaseView;
import vn.ecpay.ewallet.model.cash.getPublicKeyWallet.ResponseDataGetPublicKeyWallet;

public interface CashToCashView extends BaseView {
    void getPublicKeyWalletSuccess(ResponseDataGetPublicKeyWallet ecKpValue);

    void getPublicKeyWalletFail(String err);
}
