package vn.ecpay.ewallet.ui.billingQRCode;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.ui.payto.PayToActivity;

public class BillingQRCodeFragment extends ECashBaseFragment {
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_create_qr_code_to_pay;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onResume() {
        ((BillingQRCodeActivity) getActivity()).updateTitle(getString(R.string.str_create_a_bill_of_payment));
        super.onResume();
    }
}

