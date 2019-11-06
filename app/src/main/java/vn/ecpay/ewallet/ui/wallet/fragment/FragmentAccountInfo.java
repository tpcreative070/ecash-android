package vn.ecpay.ewallet.ui.wallet.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.wallet.activity.MyQRCodeActivity;
import vn.ecpay.fragmentcommon.ui.widget.CircleImageView;

public class FragmentAccountInfo extends ECashBaseFragment {
    @BindView(R.id.iv_account)
    CircleImageView ivAccount;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sdt)
    TextView tvSdt;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_cmnd)
    TextView tvCmnd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private AccountInfo accountInfo;
    private ArrayList<EdongInfo> listEDongInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_account_info;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData();
    }

    private void setData() {
        WalletDatabase.getINSTANCE(getContext(), ECashApplication.masterKey);
        accountInfo = WalletDatabase.getAccountInfoTask(ECashApplication.getAccountInfo().getUsername());
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (accountInfo != null) {
            tvId.setText(String.valueOf(accountInfo.getWalletId()));
            tvName.setText(CommonUtils.getFullName(accountInfo));
            tvSdt.setText(accountInfo.getPersonMobilePhone());
            tvEmail.setText("");
            tvCmnd.setText(accountInfo.getIdNumber());
        } else {
            tvId.setText("");
            tvName.setText("");
            tvSdt.setText("");
            tvEmail.setText("");
            tvCmnd.setText("");
        }
    }

    @OnClick({R.id.iv_account, R.id.btn_login, R.id.layout_image_account, R.id.layout_name, R.id.layout_phone, R.id.layout_email, R.id.layout_cmnd, R.id.layout_qr_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_account:
                break;
            case R.id.btn_login:
                break;
            case R.id.layout_image_account:
                break;
            case R.id.layout_name:
                break;
            case R.id.layout_phone:
                break;
            case R.id.layout_email:
                break;
            case R.id.layout_cmnd:
                break;
            case R.id.layout_qr_code:
                Intent intent = new Intent(getActivity(), MyQRCodeActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.UPDATE_MONEY)) {
            setData();
        }
        if (event.getData().equals(Constant.UPDATE_ACCOUNT_LOGIN)) {
            setData();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }
}
