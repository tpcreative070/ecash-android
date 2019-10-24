package vn.ecpay.ewallet.ui.wallet.view;

import vn.ecpay.ewallet.common.base.ViewBase;

public interface MyWalletView extends ViewBase {
    void showDialogError(String err);

    void onLogoutSuccess();

    void transferMoneyToCancel();

    void onCancelAccountSuccess();
}
