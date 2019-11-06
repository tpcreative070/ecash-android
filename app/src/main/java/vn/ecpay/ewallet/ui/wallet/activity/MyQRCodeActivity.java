package vn.ecpay.ewallet.ui.wallet.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.ui.wallet.fragment.FragmentMyQRCode;

public class MyQRCodeActivity extends ECashBaseActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.getMenu().clear();
            toolbarCenterText.setText(getString(R.string.str_change_pass));
            ivBack.setOnClickListener(v -> onBackPressed());
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        addFragment(new FragmentMyQRCode(), false);
    }


    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }

    public void updateTitle(String title) {
        toolbarCenterText.setText(title);
    }
}
