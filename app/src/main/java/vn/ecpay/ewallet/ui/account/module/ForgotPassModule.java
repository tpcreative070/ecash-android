package vn.ecpay.ewallet.ui.account.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.account.presenter.ForgotPassPresenter;
import vn.ecpay.ewallet.ui.account.presenter.ForgotPassPresenterImpl;

@Module
public class ForgotPassModule {
    Fragment fragment;

    public ForgotPassModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    ForgotPassPresenter forgotPassPresenter(ForgotPassPresenterImpl impl) {
        return impl;
    }
}
