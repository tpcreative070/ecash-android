package vn.ecpay.ewallet.ui.QRCode.presenter;

import javax.inject.Inject;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.ui.QRCode.view.QRCodeView;

public class QRCodePresenterImpl implements QRCodePresenter {
    private QRCodeView qrCodeView;
    private int numberRequest;
    @Inject
    ECashApplication application;

    @Inject
    public QRCodePresenterImpl() {
    }

    @Override
    public void setView(QRCodeView view) {
        qrCodeView = view;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewStart() {

    }

    @Override
    public void onViewResume() {

    }

    @Override
    public void onViewPause() {

    }

    @Override
    public void onViewStop() {

    }

    @Override
    public void onViewDestroy() {

    }
}
