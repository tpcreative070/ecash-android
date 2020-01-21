package vn.ecpay.ewallet.ui.billingQRCode;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.QRCode.QRCodePayment;

public class BillingQRCodeActivity extends ECashBaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected int getLayoutResId() {
        return R.layout.cash_base_activity;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.getMenu().clear();
            toolbarCenterText.setText(getString(R.string.str_change_pass));
            ivBack.setOnClickListener(v -> onBackPressed());
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        getIntentData();
       //
    }
    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }

    public void updateTitle(String title) {
        toolbarCenterText.setText(title);
    }
    private void getIntentData(){
        Intent intent = getIntent();
        if(intent!=null){
            QRCodePayment qrCodePayment = (QRCodePayment) intent.getSerializableExtra(Constant.QR_CODE_TOPAY_MODEL);
            if(qrCodePayment!=null){
                addFragment( BillingQRCodeFragment.newInstance(qrCodePayment), false);
            }
        }
    }
}
