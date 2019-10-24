package vn.ecpay.ewallet.ui.account.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.account.presenter.RegisterPresenter;
import vn.ecpay.ewallet.ui.account.presenter.RegisterPresenterImpl;

@Module
public class RegisterModule {
    Fragment fragment;

    public RegisterModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    RegisterPresenter registerPresenter(RegisterPresenterImpl impl) {
        return impl;
    }
}
