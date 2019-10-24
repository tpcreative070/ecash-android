package vn.ecpay.ewallet.ui.cashOut.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.cashOut.CashOutFragment;
import vn.ecpay.ewallet.ui.cashOut.module.CashOutModule;

@Subcomponent(modules = CashOutModule.class)
public interface CashOutComponent {
    void inject(CashOutFragment cashOutFragment);
}
