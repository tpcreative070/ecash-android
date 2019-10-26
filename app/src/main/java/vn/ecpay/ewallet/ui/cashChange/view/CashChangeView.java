package vn.ecpay.ewallet.ui.cashChange.view;

import vn.ecpay.ewallet.common.base.ViewBase;
import vn.ecpay.ewallet.model.cashChange.response.ResponseCashChangeData;

public interface CashChangeView extends ViewBase {
    void showDialogError(String err);

    void loadPublicKeyOrganizeSuccess(String issuerKpValue);

    void changeCashSuccess(ResponseCashChangeData responseData);
}
