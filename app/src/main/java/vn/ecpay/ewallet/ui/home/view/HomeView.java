package vn.ecpay.ewallet.ui.home.view;

import vn.ecpay.ewallet.common.base.BaseView;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;

public interface HomeView extends BaseView {
    void onActiveAccountSuccess(AccountInfo mAccountInfo);

    void showDialogError(String err);

    void onSyncContactSuccess();

    void onSyncContactFail(String responseMessage);
}
