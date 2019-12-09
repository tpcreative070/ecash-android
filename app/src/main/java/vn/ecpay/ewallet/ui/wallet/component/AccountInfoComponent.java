package vn.ecpay.ewallet.ui.wallet.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.wallet.fragment.FragmentAccountInfo;
import vn.ecpay.ewallet.ui.wallet.module.AccountInfoModule;

@Subcomponent(modules = AccountInfoModule.class)
public interface AccountInfoComponent {
    void inject(FragmentAccountInfo fragmentAccountInfo);
}

