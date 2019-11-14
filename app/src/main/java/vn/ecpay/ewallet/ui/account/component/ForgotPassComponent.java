package vn.ecpay.ewallet.ui.account.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.account.fragment.ForgotChangePassFragment;
import vn.ecpay.ewallet.ui.account.module.ForgotPassModule;

@Subcomponent(modules = {
        ForgotPassModule.class
})
public interface ForgotPassComponent {
    void inject(ForgotChangePassFragment fragment);
}
