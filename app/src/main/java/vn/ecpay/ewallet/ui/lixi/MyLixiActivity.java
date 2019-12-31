package vn.ecpay.ewallet.ui.lixi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;

public class MyLixiActivity extends ECashBaseActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.lixi_activity;
    }

    @Override
    protected void setupActivityComponent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(new MyLixiFragment(), false);
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }
}