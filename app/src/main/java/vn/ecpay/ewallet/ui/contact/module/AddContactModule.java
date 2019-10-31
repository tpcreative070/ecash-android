package vn.ecpay.ewallet.ui.contact.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.contact.presenter.AddContactPresenter;
import vn.ecpay.ewallet.ui.contact.presenter.AddContactPresenterImpl;

@Module
public class AddContactModule {
    Fragment fragment;

    public AddContactModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    AddContactPresenter addContactPresenter(AddContactPresenterImpl impl) {
        return impl;
    }
}
