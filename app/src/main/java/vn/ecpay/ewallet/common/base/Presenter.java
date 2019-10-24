package vn.ecpay.ewallet.common.base;


public interface Presenter<T extends ViewBase> {

    void setView(T view);

    void onViewCreate();

    void onViewStart();

    void onViewResume();

    void onViewPause();

    void onViewStop();

    void onViewDestroy();
}