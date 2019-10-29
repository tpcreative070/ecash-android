package vn.ecpay.ewallet.ui.cashChange.view;

import vn.ecpay.ewallet.common.base.ViewBase;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;

public interface CashChangeView extends ViewBase {
    void showDialogError(String err);

    void loadPublicKeyOrganizeSuccess(String issuerKpValue);

    void changeCashSuccess(CashInResponse responseData);
}
