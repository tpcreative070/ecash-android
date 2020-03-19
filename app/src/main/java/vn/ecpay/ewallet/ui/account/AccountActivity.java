package vn.ecpay.ewallet.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.ui.account.fragment.FragmentLogin;

public class AccountActivity extends ECashBaseActivity {
    private boolean isTimeOut;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_account;
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
            isTimeOut = content.getBoolean(Constant.IS_SESSION_TIMEOUT);
            addFragment(FragmentLogin.newInstance(isTimeOut), false);
        } else {
            addFragment(FragmentLogin.newInstance(false), false);
        }
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout_login);
    }
}
