package vn.ecpay.ewallet.ui.cashChange.presenter;

import javax.inject.Inject;

import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;

public class CashChangePresenterImpl implements CashChangePresenter {

    CashChangeView cashChangeView;

    @Inject
    public CashChangePresenterImpl() {
    }

    @Override
    public void setView(CashChangeView view) {
        this.cashChangeView = view;
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
