package vn.ecpay.ewallet.ui.billingQRCode;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;

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
    @OnClick({R.id.view_share,R.id.view_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_share:
                showToas();
                break;
            case R.id.view_download:
                showToas();
                break;
        }
    }
    private void showToas(){
        Toast.makeText(getActivity(),"Đang xử lý",Toast.LENGTH_LONG).show();
    }
}

