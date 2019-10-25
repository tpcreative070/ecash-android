package vn.ecpay.ewallet.ui.cashIn.view;

import vn.ecpay.ewallet.common.base.BaseView;
import vn.ecpay.ewallet.model.edongToEcash.EDongToECash;

public interface CashInView extends BaseView {
    void showDialogError(String err);

    void transferMoneySuccess(EDongToECash eDongToECash, String id);
}
