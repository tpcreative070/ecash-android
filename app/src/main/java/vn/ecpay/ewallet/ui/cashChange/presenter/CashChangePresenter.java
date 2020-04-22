package vn.ecpay.ewallet.ui.cashChange.presenter;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import java.util.List;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;

public interface CashChangePresenter extends Presenter<CashChangeView> {
    void requestChangeCash(Context context,String cashEnc, List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue, long amount);

    void getPublicKeyOrganization(FragmentActivity activity, AccountInfo accountInfo);
}
