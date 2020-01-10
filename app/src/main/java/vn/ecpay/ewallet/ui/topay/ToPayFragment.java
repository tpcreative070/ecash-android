package vn.ecpay.ewallet.ui.topay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.billingQRCode.BillingQRCodeActivity;
import vn.ecpay.ewallet.ui.wallet.activity.MyQRCodeActivity;

public class ToPayFragment extends ECashBaseFragment {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;

    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.view_contact)
    View viewContact;
    @BindView(R.id.edt_ecash_number)
    EditText edtEcashNumber;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.view_qr_code)
    View viewQRCode;
    @BindView(R.id.iv_qr_code)
    ImageView ivQRCode;


    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private long balance;
    private long totalMoney;
    private AccountInfo accountInfo;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_payto;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        setData();
    }
    @Override
    public void onResume() {
        toolbarCenterText.setText(getString(R.string.str_create_a_bill_of_payment));
        super.onResume();
    }
    private void setData(){
        viewContact.setVisibility(View.GONE);
        viewQRCode.setVisibility(View.GONE);
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
    }
    @OnClick({R.id.iv_back,R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }
    private void validateData(){
        //TODO:
        crateQRCode();
    }
    private void crateQRCode(){// todo: push object
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), BillingQRCodeActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
