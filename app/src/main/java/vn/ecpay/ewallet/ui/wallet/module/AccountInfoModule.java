package vn.ecpay.ewallet.ui.wallet.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.wallet.presenter.AccountInfoPresenter;
import vn.ecpay.ewallet.ui.wallet.presenter.AccountInfoPresenterImpl;

@Module
public class AccountInfoModule {
    Fragment fragment;

    public AccountInfoModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    AccountInfoPresenter accountInfoPresenter(AccountInfoPresenterImpl impl) {
        return impl;
    }
}

