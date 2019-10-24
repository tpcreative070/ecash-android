package vn.ecpay.ewallet.ui.cashOut.presenter;

import android.content.Context;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.cashOut.view.CashOutView;

public interface CashOutPresenter extends Presenter<CashOutView> {
    void getPublicKeyOrganization(Context context, AccountInfo accountInfo);

    void sendECashToEDong(String encData, String idSender, long totalMoney, EdongInfo edongInfo, AccountInfo accountInfo);

    void getEDongInfo(AccountInfo accountInfo);
}
