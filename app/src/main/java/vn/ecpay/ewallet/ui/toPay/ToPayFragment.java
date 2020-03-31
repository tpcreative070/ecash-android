package vn.ecpay.ewallet.ui.toPay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.ContentInputTextWatcher;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.NumberTextWatcher;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.QRCode.QRCodePayment;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.billingQRCode.BillingQRCodeActivity;

import static vn.ecpay.ewallet.common.utils.Constant.QR_TO_PAY;

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

    @BindView(R.id.tv_error_wallet_id)
    TextView tvErrorWallet;
    @BindView(R.id.tv_error_amount)
    TextView tvErrorAmount;
    @BindView(R.id.tv_error_content)
    TextView tvErrorContent;

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
        edtAmount.addTextChangedListener(new NumberTextWatcher(getActivity(),edtAmount,edtContent,btnConfirm));
        edtContent.addTextChangedListener(new ContentInputTextWatcher(getActivity(),edtAmount,edtContent,btnConfirm));
    }
    @Override
    public void onResume() {
        toolbarCenterText.setText(getString(R.string.str_create_a_bill_of_payment));
        super.onResume();
    }
    private void setData(){
        Utils.disableButtonConfirm(getActivity(),btnConfirm,true);
        viewContact.setVisibility(View.GONE);
        viewQRCode.setVisibility(View.GONE);
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
    }
    private void updateBalance(){
        long numberCash = WalletDatabase.getAllCash().size();
        if (WalletDatabase.numberRequest == 0 && numberCash > 0) {
            if (getActivity() != null)
                getActivity().runOnUiThread(() -> {
                    WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
                    balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
                    tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
                });
        }else{
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    updateBalance();
                }
            }, 1000);
        }
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
        clearError();
        if (edtAmount.getText().toString().isEmpty()) {
            if (getActivity() != null)
             //   showDialogError(getString(R.string.err_anount_null));
            tvErrorAmount.setText(getString(R.string.err_anount_null));
            return;
        }
        if(edtAmount.getText().toString().length()>0){
            Long money =Long.parseLong(edtAmount.getText().toString().replace(".","").replace(",",""));
            // Log.e("money%1000 ",money%1000+"");
            if(money<1000||money%1000!=0){
              //  showDialogError(getString(R.string.err_amount_validate));
                tvErrorAmount.setText(getString(R.string.err_amount_validate));
                return;
            }else if(money>Constant.AMOUNT_LIMITED){
             //   showDialogError(getString(R.string.err_amount_does_not_exceed_twenty_million));
                tvErrorAmount.setText(getString(R.string.err_amount_does_not_exceed_twenty_million));
                return;
            }
        }
        if (edtContent.getText().toString().isEmpty()) {
            if (getActivity() != null)
                showDialogError(getString(R.string.err_dit_not_content));
            tvErrorContent.setText(getString(R.string.err_dit_not_content));
            return;
        }
        String userName = ECashApplication.getAccountInfo().getUsername();
        String amount =edtAmount.getText().toString().replace(".","").replace(",","");
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        QRCodePayment qrPayment =new QRCodePayment();
        qrPayment.setSender(String.valueOf(accountInfo.getWalletId()));
        qrPayment.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        qrPayment.setType(Constant.TYPE_TOPAY);
        qrPayment.setContent(edtContent.getText().toString());
        qrPayment.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        qrPayment.setTotalAmount(amount);
        //qrPayment.setFullName(CommonUtils.getFullName(accountInfo));

        QRCodePayment channelSignature = new QRCodePayment();
        channelSignature.setContent(edtContent.getText().toString());
        channelSignature.setSender(String.valueOf(accountInfo.getWalletId()));
        channelSignature.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        channelSignature.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        channelSignature.setTotalAmount(amount);
        channelSignature.setType(Constant.TYPE_TOPAY);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(channelSignature));

        qrPayment.setChannelSignature(CommonUtils.generateSignature(dataSign));

        crateQRCode(qrPayment);
    }
    private void clearError(){
        tvErrorWallet.setText("");
        tvErrorAmount.setText("");
        tvErrorContent.setText("");
    }
    private void crateQRCode(QRCodePayment qrCodePayment){
        if (getActivity() != null) {
            Gson gson = new Gson();
            QRScanBase qrScanBase = new QRScanBase();
            qrScanBase.setType(QR_TO_PAY);
            qrScanBase.setContent(gson.toJson(qrCodePayment));
            Intent intent = new Intent(getActivity(), BillingQRCodeActivity.class);
            intent.putExtra(Constant.QR_CODE_TOPAY_MODEL,qrScanBase);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        //Log.e("event topay ",event.getData());
        if (event.getData().equals(Constant.EVENT_UPDATE_BALANCE)||event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)) {
            updateBalance();
        }
        EventBus.getDefault().removeStickyEvent(event);
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
}
