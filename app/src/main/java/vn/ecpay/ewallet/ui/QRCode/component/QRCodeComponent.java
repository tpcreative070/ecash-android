package vn.ecpay.ewallet.ui.QRCode.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.QRCode.fragment.ScannerQRCodeFragment;
import vn.ecpay.ewallet.ui.QRCode.module.QRCodeModule;

@Subcomponent(modules = QRCodeModule.class)
public interface QRCodeComponent {
    void inject(ScannerQRCodeFragment scannerQRCodeFragment);
}
