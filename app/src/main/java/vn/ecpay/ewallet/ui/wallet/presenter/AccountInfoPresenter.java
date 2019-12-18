package vn.ecpay.ewallet.ui.wallet.presenter;

import android.content.Context;

import vn.ecpay.ewallet.common.base.Presenter;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.wallet.view.AccountInfoView;

public interface AccountInfoPresenter extends Presenter<AccountInfoView> {
    void onChooseImage(Context context, int requestCode);

    void onCaptureImage(Context activity, int requestCode);

    void onEvent(EventDataChange event, AccountInfo accountInfo);
}
