package vn.ecpay.ewallet.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.contact.fragment.FragmentAddContact;

public class AddContactActivity extends ECashBaseActivity {
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;

    @Override
    protected int getLayoutResId() {
        return R.layout.contact_activity;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(new FragmentAddContact(), true);
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }

    public void updateTitle(String title) {
        toolbarCenterText.setText(title);
    }

    @OnClick({R.id.iv_back, R.id.iv_scan_contact})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_scan_contact:
                Intent intent = new Intent(AddContactActivity.this, QRCodeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }
}
