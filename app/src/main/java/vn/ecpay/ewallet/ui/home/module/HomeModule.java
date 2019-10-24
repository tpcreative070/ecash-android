package vn.ecpay.ewallet.ui.home.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.home.presenter.HomePresenter;
import vn.ecpay.ewallet.ui.home.presenter.HomePresenterImpl;

@Module
public class HomeModule {
    Fragment fragment;

    public HomeModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    HomePresenter homePresenter(HomePresenterImpl impl) {
        return impl;
    }
}
