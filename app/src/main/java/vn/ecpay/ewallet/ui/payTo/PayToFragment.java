package vn.ecpay.ewallet.ui.payTo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.network.CheckNetworkUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.ContentInputTextWatcher;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.NumberTextWatcher;
import vn.ecpay.ewallet.common.utils.Utils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.cashToCash.fragment.FragmentContactTransferCash;
import vn.ecpay.ewallet.ui.function.PayToFunction;
import vn.ecpay.ewallet.ui.callbackListener.MultiTransferListener;

@SuppressLint("ParcelCreator")
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
    @BindView(R.id.tv_ecash_number)
    TextView tvEcashNumber;
    @BindView(R.id.edt_amount)
    EditText edtAmount;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.iv_contact)
    ImageView ivContact;

    @BindView(R.id.tv_error_wallet_id)
    TextView tvErrorWallet;
    @BindView(R.id.tv_error_amount)
    TextView tvErrorAmount;
    @BindView(R.id.tv_error_content)
    TextView tvErrorContent;

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
        accountInfo = DatabaseUtil.getAccountInfo(getActivity());
        setData();
        edtAmount.addTextChangedListener(new NumberTextWatcher(getActivity(),edtAmount,edtContent,btnConfirm));
        edtContent.addTextChangedListener(new ContentInputTextWatcher(getActivity(),edtAmount,edtContent,btnConfirm));
    }
    private void setData(){
        Utils.disableButtonConfirm(getActivity(),btnConfirm,true);
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
    }
    private void updateBalance(){
        long numberCash = WalletDatabase.getAllCash(getActivity()).size();
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
       // EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_SCAN_CONTACT_PAYTO));
        Intent intentCashIn = new Intent(getActivity(), QRCodeActivity.class);
        intentCashIn.putExtra(Constant.EVENT_SCAN_CONTACT_PAYTO,Constant.EVENT_SCAN_CONTACT_PAYTO);
        startActivityForResult(intentCashIn,Constant.REQUEST_CONTACT_PAYTO);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
    private void validateData(){
        if(!CheckNetworkUtil.isConnected(getBaseActivity())){
            DialogUtil.getInstance().showDialogErrorConnectInternet(getActivity(), getString(R.string.str_error_connection_internet), new DialogUtil.OnResult() {
                @Override
                public void OnListenerOk() {
                    showProgress();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgress();
                            validateData();
                        }
                    },2000);
                }
            });
            return;
        }
        clearError();
        if (multiTransferList == null) {
            if (getActivity() != null)
               // showDialogError(getString(R.string.err_not_input_number_username));
            tvErrorWallet.setText(getString(R.string.err_not_input_number_username));
            return;
        }
        if (multiTransferList.size() == 0) {
            if (getActivity() != null)
              //  showDialogError(getString(R.string.err_not_input_number_username));
            tvErrorWallet.setText(getString(R.string.err_not_input_number_username));
            return;
        }
        long money=0;
        if (edtAmount.getText().toString().isEmpty()) {
            if (getActivity() != null)
              //  showDialogError(getString(R.string.err_anount_null));
            tvErrorAmount.setText(getString(R.string.err_anount_null));
            return;
        }
        if(edtAmount.getText().toString().length()>0){
             money =Long.parseLong(edtAmount.getText().toString().replace(".","").replace(",",""));
           // Log.e("money%1000 ",money%1000+"");
           // if(money<1000||money%1000!=0){
            if(money<Constant.AMOUNT_LIMITED_MIN||money>Constant.AMOUNT_LIMITED_MAX){
                //   showDialogError(getString(R.string.err_amount_does_not_exceed_twenty_million));
                tvErrorAmount.setText(getString(R.string.err_amount_does_not_exceed_twenty_million));
                return;
            }
            money = CommonUtils.validateMoney(money);
//            else if(money%1000!=0){//!CommonUtils.validateCashInput(money)
//                //  showDialogError(getString(R.string.err_amount_validate));
//                tvErrorAmount.setText(getString(R.string.err_amount_input_validate));
//                return;
//            }
        }
        if (edtContent.getText().toString().isEmpty()) {
            if (getActivity() != null)
              //  showDialogError(getString(R.string.err_dit_not_content));
            tvErrorContent.setText(getString(R.string.err_dit_not_content));
            return;
        }
        showProgress();///
        PayToFunction payToFuntion = new PayToFunction(getActivity(),money,multiTransferList,edtContent.getText().toString(),Constant.TYPE_TOPAY);
        payToFuntion.handlePayToSocket(this::PayToSuccess);
    }
    private void PayToSuccess() {
        dismissProgress();
        tvEcashNumber.setText("");
        edtContent.setText("");
        edtAmount.setText("");
        clearError();
        showDialogSendRequestOk();
    }
    private void clearError(){
        tvErrorWallet.setText("");
        tvErrorAmount.setText("");
        tvErrorContent.setText("");
    }
    protected void showDialogSendRequestOk() {
        restartSocket();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) return;
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_SEND_REQUEST_PAYTO));
            }
        }, 500);
        //showDialogSuccess(getString(R.string.str_transfer_success));
        DialogUtil.getInstance().showDialogConfirm(getActivity(), null,
                getString(R.string.str_send_request_success), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        //
                        // UsageEvents.Event
                    }

                    @Override
                    public void OnListenerCancel() {
                        getActivity().finish();
                    }
                });
    }

    private void gotoContact(){
        ((PayToActivity) getActivity()).addFragment(FragmentContactTransferCash.newInstance(this,true), true);
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
        Log.e("Payto  Fragment ",event.getData());
        if (event.getData().equals(Constant.EVENT_CONNECT_SOCKET_FAIL)) {
            dismissProgress();
           // showDialogError(getString(R.string.err_connect_socket_fail));
            DialogUtil.getInstance().showDialogErrorConnectInternet(getActivity(), getString(R.string.str_error_connection_internet), new DialogUtil.OnResult() {
                @Override
                public void OnListenerOk() {
                    showProgress();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgress();
                            validateData();
                        }
                    },2000);
                }
            });
        }
        if (event.getData().equals(Constant.EVENT_UPDATE_BALANCE)||event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)) {
            updateBalance();
        }
        EventBus.getDefault().removeStickyEvent(event);
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
        tvEcashNumber.setText(walletId.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    //   super.onActivityResult(requestCode, resultCode, data);
     //   Log.e("requestCode "+requestCode,"resultCode "+resultCode);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode ==Constant.REQUEST_CONTACT_PAYTO){
                try {
                    Contact contact =(Contact) data.getParcelableExtra(Constant.EVENT_SCAN_CONTACT_PAYTO);
                    if(contact!=null){
                        if(CommonUtils.checkWalletIDisMe(getActivity(),String.valueOf(contact.getWalletId()))){
                            showDialogError(getActivity().getString(R.string.str_error_you_cannot_pay_for_your_self));
                            return;
                        }
                        tvEcashNumber.setText(contact.getWalletId().toString());
                        multiTransferList = new ArrayList<>();
                        multiTransferList.add(contact);
                        Log.e("multiTransferList ",multiTransferList.size()+"");
                    }

                }catch (Exception e){

                }

           }
        }else{
            Log.e("requestCode ","cancel");
    }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
