package vn.ecpay.ewallet.ui.cashChange.view;

import vn.ecpay.ewallet.common.base.ViewBase;

public interface CashChangeView extends ViewBase {
    void showDialogError(String err);

    void loadPublicKeyOrganizeSuccess(String issuerKpValue);
}
