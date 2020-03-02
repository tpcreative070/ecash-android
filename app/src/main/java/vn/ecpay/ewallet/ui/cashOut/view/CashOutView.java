package vn.ecpay.ewallet.ui.cashOut.view;

import vn.ecpay.ewallet.common.base.BaseView;

public interface CashOutView extends BaseView {
    void loadPublicKeyOrganizeSuccess(String publicKeyOrganization);

    void showDialogError(String err);

    void sendECashToEDongSuccess();

    void getEDongInfoSuccess();
}
