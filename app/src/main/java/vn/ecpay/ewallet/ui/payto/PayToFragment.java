package vn.ecpay.ewallet.ui.payto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.cashToCash.fragment.CashToCashFragment;
import vn.ecpay.ewallet.ui.cashToCash.fragment.FragmentContactTransferCash;
import vn.ecpay.ewallet.ui.interfaceListener.MultiTransferListener;

public class PayToFragment extends ECashBaseFragment implements MultiTransferListener {
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
    @BindView(R.id.edt_ecash_number)
    EditText edtEcashNumber;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.iv_qr_code)
    ImageView ivQRCode;
    @BindView(R.id.iv_contact)
    ImageView ivContact;

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private List<Contact> multiTransferList;
    private long balance;
    private AccountInfo accountInfo;
    public static PayToFragment newInstance(ArrayList<Contact> listContactTransfer) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constant.CONTACT_TRANSFER_MODEL, listContactTransfer);
        PayToFragment fragment = new PayToFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_payto;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.multiTransferList = bundle.getParcelableArrayList(Constant.CONTACT_TRANSFER_MODEL);
            if (null != multiTransferList) {
                if (multiTransferList.size() > 0) {
                    updateWalletSend();
                }
            }
        }
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        setData();
    }
    private void setData(){
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
    }

    @Override
    public void onResume() {
        toolbarCenterText.setText(String.format(getString(R.string.str_payment_request)," "));
        super.onResume();
    }
    @OnClick({R.id.iv_back,R.id.iv_qr_code,R.id.iv_contact, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_qr_code:
                gotoScanQRCode();
                break;
            case R.id.iv_contact:
                gotoContact();
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }
    private void gotoScanQRCode(){
        Intent intentCashIn = new Intent(getActivity(), QRCodeActivity.class);
        startActivity(intentCashIn);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void validateData(){// TODO
        showDialogNewPayment("150000","1213244");
    }
    private void gotoContact(){
        ((PayToActivity) getActivity()).addFragment(FragmentContactTransferCash.newInstance(this), true);
    }

    @Override
    public void onMultiTransfer(ArrayList<Contact> contactList) {
        multiTransferList =contactList;
        updateWalletSend();
    }
    private void updateWalletSend() {
        StringBuilder walletId = new StringBuilder();
        for (int i = 0; i < multiTransferList.size(); i++) {
            if (i == 0) {
                walletId.append(multiTransferList.get(i).getWalletId());
            } else {
                walletId.append("; ").append(multiTransferList.get(i).getWalletId());
            }
        }
        edtEcashNumber.setText(walletId.toString());
    }
}
