package vn.ecpay.ewallet.ui.callbackListener;

public interface CashInSuccessListener {
    void onCashInSuccess(Long totalMoney);

    void onCashInFail();
}
