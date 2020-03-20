package vn.ecpay.ewallet.ui.cashIn.view;

import vn.ecpay.ewallet.common.base.BaseView;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;

public interface CashInView extends BaseView {
    void showDialogError(String err);
    void showDialogErrorWithCloseAndContinue(String title,String message);

    void transferMoneySuccess(CashInResponse eDongToECash);

    void getEDongInfoSuccess();
}
