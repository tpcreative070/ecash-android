package vn.ecpay.ewallet.ui.callbackListener;

public interface UpdateMasterKeyListener {
    void onUpdateMasterSuccess();

    void onUpdateMasterFail(String code);

    void onRequestTimeout();
}
