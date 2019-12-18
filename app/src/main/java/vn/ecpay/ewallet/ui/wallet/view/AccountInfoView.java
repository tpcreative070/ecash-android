package vn.ecpay.ewallet.ui.wallet.view;

import vn.ecpay.ewallet.common.base.ViewBase;

public interface AccountInfoView extends ViewBase {
    void onUpdateFail(String err);

    void showDialogError(String err);
}
