package vn.ecpay.ewallet.ui.cashChange.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.cashChange.presenter.CashChangePresenter;
import vn.ecpay.ewallet.ui.cashChange.presenter.CashChangePresenterImpl;

@Module
public class CashChangeModule {
    Fragment fragment;

    public CashChangeModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    CashChangePresenter cashChangePresenter(CashChangePresenterImpl impl) {
        return impl;
    }
}
