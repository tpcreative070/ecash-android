package vn.ecpay.ewallet.ui.account.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.account.presenter.LoginPresenter;
import vn.ecpay.ewallet.ui.account.presenter.LoginPresenterImpl;

@Module
public class LoginModule {
    Fragment fragment;

    public LoginModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    LoginPresenter loginPresenter(LoginPresenterImpl impl) {
        return impl;
    }
}
