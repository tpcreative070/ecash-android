package vn.ecpay.ewallet.ui.cashToCash.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.cashToCash.presenter.CashToCashPresenter;
import vn.ecpay.ewallet.ui.cashToCash.presenter.CashToCashPresenterImpl;

@Module
public class CashToCashModule {
    Fragment fragment;

    public CashToCashModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    CashToCashPresenter cashToCashPresenter(CashToCashPresenterImpl impl) {
        return impl;
    }
}
