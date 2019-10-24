package vn.ecpay.ewallet.ui.wallet.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.wallet.presenter.MyWalletPresenter;
import vn.ecpay.ewallet.ui.wallet.presenter.MyWalletPresenterImpl;

@Module
public class MyWalletModule {
    Fragment fragment;

    public MyWalletModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    MyWalletPresenter myWalletPresenter(MyWalletPresenterImpl impl) {
        return impl;
    }
}
