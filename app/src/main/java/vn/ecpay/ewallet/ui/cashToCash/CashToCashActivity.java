package vn.ecpay.ewallet.ui.cashToCash;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.fragment.CashToCashFragment;

public class CashToCashActivity extends ECashBaseActivity implements Serializable {
    private ArrayList<Contact> listContactTransfer;

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
        Intent intent = getIntent();
        Bundle content = intent.getExtras();
        if (null != content) {
            listContactTransfer = content.getParcelableArrayList(Constant.CONTACT_TRANSFER_MODEL);
            addFragment(CashToCashFragment.newInstance(listContactTransfer), true);
        } else {
            addFragment(new CashToCashFragment(), false);
        }
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }
}
