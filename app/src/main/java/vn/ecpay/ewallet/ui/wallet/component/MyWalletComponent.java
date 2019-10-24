package vn.ecpay.ewallet.ui.wallet.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.wallet.fragment.FragmentMyWallet;
import vn.ecpay.ewallet.ui.wallet.module.MyWalletModule;

@Subcomponent(modules = MyWalletModule.class)
public interface MyWalletComponent {
    void inject(FragmentMyWallet fragmentMyWallet);
}

