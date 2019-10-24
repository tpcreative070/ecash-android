package vn.ecpay.ewallet.ui.cashToCash.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.cashToCash.fragment.CashToCashFragment;
import vn.ecpay.ewallet.ui.cashToCash.module.CashToCashModule;

@Subcomponent(modules = CashToCashModule.class)
public interface CashToCashComponent {
    void inject(CashToCashFragment cashToCashFragment);
}
