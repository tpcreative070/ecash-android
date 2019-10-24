package vn.ecpay.ewallet.common.dependencyInjection;

import javax.inject.Singleton;

import dagger.Component;
import vn.ecpay.ewallet.ui.QRCode.component.QRCodeComponent;
import vn.ecpay.ewallet.ui.QRCode.module.QRCodeModule;
import vn.ecpay.ewallet.ui.account.component.LoginComponent;
import vn.ecpay.ewallet.ui.account.component.RegisterComponent;
import vn.ecpay.ewallet.ui.account.module.LoginModule;
import vn.ecpay.ewallet.ui.account.module.RegisterModule;
import vn.ecpay.ewallet.ui.cashIn.component.CashInComponent;
import vn.ecpay.ewallet.ui.cashIn.module.CashInModule;
import vn.ecpay.ewallet.ui.cashOut.component.CashOutComponent;
import vn.ecpay.ewallet.ui.cashOut.module.CashOutModule;
import vn.ecpay.ewallet.ui.cashToCash.component.CashToCashComponent;
import vn.ecpay.ewallet.ui.cashToCash.module.CashToCashModule;
import vn.ecpay.ewallet.ui.home.component.HomeComponent;
import vn.ecpay.ewallet.ui.home.module.HomeModule;
import vn.ecpay.ewallet.ui.wallet.component.MyWalletComponent;
import vn.ecpay.ewallet.ui.wallet.module.MyWalletModule;

@Singleton
@Component(
        modules = {
                ApplicationModule.class
        }
)
public interface ApplicationComponent {
    LoginComponent plus(LoginModule loginModule);

    CashInComponent plus(CashInModule cashInModule);

    HomeComponent plus(HomeModule homeModule);

    CashOutComponent plus(CashOutModule cashOutModule);

    CashToCashComponent plus(CashToCashModule cashToCashModule);

    RegisterComponent plus(RegisterModule registerModule);

    QRCodeComponent plus(QRCodeModule qrCodeModule);

    MyWalletComponent plus(MyWalletModule myWalletModule);
}
