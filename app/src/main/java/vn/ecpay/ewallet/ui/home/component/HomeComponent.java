package vn.ecpay.ewallet.ui.home.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.home.HomeFragment;
import vn.ecpay.ewallet.ui.home.module.HomeModule;

@Subcomponent(modules = {
        HomeModule.class
})
public interface HomeComponent {
    void inject(HomeFragment fragment);
}
