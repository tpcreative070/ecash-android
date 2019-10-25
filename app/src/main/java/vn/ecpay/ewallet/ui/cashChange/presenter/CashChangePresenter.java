package vn.ecpay.ewallet.ui.cashChange.presenter;

import androidx.fragment.app.FragmentActivity;

import java.util.List;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;

public interface CashChangePresenter extends Presenter<CashChangeView> {
    void requestChangeCash(List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue);

    void getPublicKeyOrganization(FragmentActivity activity, AccountInfo accountInfo);
}
