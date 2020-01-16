package vn.ecpay.ewallet.ui.toPay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.QRCode.QRToPay;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.toPay.RequestToPay;
import vn.ecpay.ewallet.ui.billingQRCode.BillingQRCodeActivity;

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
    @BindView(R.id.tv_ecash_number)
    TextView edtEcashNumber;
    @BindView(R.id.edt_amount)
    EditText edtAmount;
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
        if (edtAmount.getText().toString().isEmpty()) {
            if (getActivity() != null)
                showDialogError(getString(R.string.err_anount_null));
            return;
        }
        if (edtContent.getText().toString().isEmpty()) {
            if (getActivity() != null)
                showDialogError(getString(R.string.err_dit_not_content));
            return;
        }
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        QRToPay qrToPay =new QRToPay();
        qrToPay.setSender(String.valueOf(accountInfo.getWalletId()));
        qrToPay.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        qrToPay.setType(Constant.TYPE_TOPAY);
        qrToPay.setContent(edtContent.getText().toString());
        qrToPay.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        qrToPay.setTotalAmount(edtAmount.getText().toString());

        RequestToPay requestToPay = new RequestToPay();
        requestToPay.setContent(edtContent.getText().toString());
        requestToPay.setSender(String.valueOf(accountInfo.getWalletId()));
        requestToPay.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        requestToPay.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        requestToPay.setTotalAmount(edtAmount.getText().toString());
        requestToPay.setType(Constant.TYPE_TOPAY);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestToPay));

        qrToPay.setChannelSignature(CommonUtils.generateSignature(dataSign));

        crateQRCode(qrToPay);
    }
    private void crateQRCode(QRToPay qrToPay){
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), BillingQRCodeActivity.class);
            intent.putExtra(Constant.QR_CODE_TOPAY_MODEL,qrToPay);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
