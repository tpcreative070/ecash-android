package vn.ecpay.ewallet.ui.cashOut.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.cashOut.presenter.CashOutPresenter;
import vn.ecpay.ewallet.ui.cashOut.presenter.CashOutPresenterImpl;

@Module
public class CashOutModule {
    Fragment fragment;

    public CashOutModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    CashOutPresenter cashOutPresenter(CashOutPresenterImpl impl) {
        return impl;
    }
}
