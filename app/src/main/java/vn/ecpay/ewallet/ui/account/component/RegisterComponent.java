package vn.ecpay.ewallet.ui.account.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.account.fragment.FragmentRegister;
import vn.ecpay.ewallet.ui.account.module.RegisterModule;

@Subcomponent(modules = RegisterModule.class)
public interface RegisterComponent {
    void inject(FragmentRegister fragment);
}
