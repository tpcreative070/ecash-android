package vn.ecpay.ewallet.ui.lixi;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.ui.cashToCash.fragment.CashToCashFragment;

public class SendLixiFragment extends CashToCashFragment {
    @Override
    protected int getLayoutResId() {
        return R.layout.send_lixi_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void updateType() {
        toolbarCenterText.setText(getResources().getString(R.string.str_send_lixi));
        typeSend = Constant.TYPE_LIXI;
    }

    @Override
    protected void showDialogSendOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                "Gửi lì xì thành công", new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        setData();
                        updateTotalMoney();
                    }

                    @Override
                    public void OnListenerCancel() {
                        getActivity().finish();
                    }
                });
    }
}
