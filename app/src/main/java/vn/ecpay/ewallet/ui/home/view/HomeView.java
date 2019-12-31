package vn.ecpay.ewallet.ui.home.view;

import java.util.List;

import vn.ecpay.ewallet.common.base.BaseView;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.ResponseData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.response.Denomination;

public interface HomeView extends BaseView {
    void onActiveAccountSuccess(AccountInfo mAccountInfo);

    void showDialogError(String err);

    void onSyncContactSuccess();

    void onSyncContactFail(String responseMessage);

    void onGetOTPActiveAccountSuccess(ResponseData transactionCode);

    void requestOTPFail(String code, ResponseData responseCode);

    void getCashValuesSuccess(List<Denomination> cashValuesList);
}
