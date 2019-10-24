package vn.ecpay.ewallet.ui.cashIn.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.cashIn.CashInFragment;
import vn.ecpay.ewallet.ui.cashIn.module.CashInModule;

@Subcomponent(modules = {
        CashInModule.class
})
public interface CashInComponent {
    void inject(CashInFragment fragment);
}
