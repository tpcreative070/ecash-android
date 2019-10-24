package vn.ecpay.ewallet.ui.cashIn.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.cashIn.presenter.CashInPresenter;
import vn.ecpay.ewallet.ui.cashIn.presenter.CashInPresenterImpl;

@Module
public class CashInModule {
    Fragment fragment;

    public CashInModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    CashInPresenter cashInPresenter(CashInPresenterImpl impl) {
        return impl;
    }
}
