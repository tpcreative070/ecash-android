package vn.ecpay.ewallet.ui.contact.presenter;

import android.content.Context;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.contact.view.AddContactView;

public interface AddContactPresenter extends Presenter<AddContactView> {
    void requestSearchByPhone(String edtTextSearch, AccountInfo accountInfo);

    void requestSearchWalletID(String walletId, AccountInfo accountInfo);
}
