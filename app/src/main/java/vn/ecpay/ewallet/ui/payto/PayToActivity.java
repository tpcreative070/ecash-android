package vn.ecpay.ewallet.ui.payto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.fragment.CashToCashFragment;

public class PayToActivity extends ECashBaseActivity {
    private ArrayList<Contact> listContactTransfer;
    @BindView(R.id.fragment_layout)
    FrameLayout fragmentLayout;
    @Override
    protected int getLayoutResId() {
        return R.layout.lixi_activity;
    }

    @Override
    protected void setupActivityComponent() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle content = intent.getExtras();
        if (null != content) {
            listContactTransfer = content.getParcelableArrayList(Constant.CONTACT_TRANSFER_MODEL);
            addFragment(PayToFragment.newInstance(listContactTransfer), true);
        }
        addFragment(new PayToFragment(), false);
    }
    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }

}
