package vn.ecpay.ewallet.ui.QRCode.module;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ui.QRCode.presenter.QRCodePresenter;
import vn.ecpay.ewallet.ui.QRCode.presenter.QRCodePresenterImpl;

@Module
public class QRCodeModule {
    Fragment fragment;

    public QRCodeModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    QRCodePresenter qrCodePresenter(QRCodePresenterImpl impl) {
        return impl;
    }
}
