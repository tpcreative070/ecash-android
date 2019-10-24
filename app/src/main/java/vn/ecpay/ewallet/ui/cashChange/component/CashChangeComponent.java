package vn.ecpay.ewallet.ui.cashChange.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.cashChange.fragment.CashChangeFragment;
import vn.ecpay.ewallet.ui.cashChange.module.CashChangeModule;

@Subcomponent(modules = CashChangeModule.class)
public interface CashChangeComponent {
    void inject(CashChangeFragment cashChangeFragment);
}
