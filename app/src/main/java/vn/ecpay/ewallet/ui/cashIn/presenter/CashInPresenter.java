package vn.ecpay.ewallet.ui.cashIn.presenter;

import java.util.List;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.cashIn.view.CashInView;

public interface CashInPresenter extends Presenter<CashInView> {
    void transferMoneyEDongToECash(Long totalMoney, EdongInfo eDongInfoCashIn,
                                   List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue);

    void getEDongInfo(AccountInfo accountInfo);
}
