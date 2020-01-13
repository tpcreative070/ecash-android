package vn.ecpay.ewallet.ui.contact.view;

import vn.ecpay.ewallet.common.base.ViewBase;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone.ResponseDataGetWalletByPhone;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;

public interface AddContactView extends ViewBase {
    void getWalletFail(String err);

    void onSearchByWalletSuccess(ResponseDataGetPublicKeyWallet responseData);

    void onSearchByPhoneSuccess(ResponseDataGetWalletByPhone responseData);

    void onSyncContactSuccess();
}
