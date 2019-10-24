package vn.ecpay.ewallet.ui.account.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.account.fragment.FragmentLogin;
import vn.ecpay.ewallet.ui.account.module.LoginModule;

@Subcomponent(modules = {
        LoginModule.class
})
public interface LoginComponent {
    void inject(FragmentLogin fragment);
}
