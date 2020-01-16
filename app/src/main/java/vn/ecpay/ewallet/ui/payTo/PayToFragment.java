package vn.ecpay.ewallet.ui.payTo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.cashToCash.fragment.FragmentContactTransferCash;
import vn.ecpay.ewallet.ui.function.PayToFuntion;
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
    @BindView(R.id.tv_ecash_number)
    TextView tvEcashNumber;
    @BindView(R.id.edt_amount)
    EditText edtAmount;
    @BindView(R.id.edt_content)
    EditText edtContent;
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
       // EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_SCAN_CONTACT_PAYTO));
        Intent intentCashIn = new Intent(getActivity(), QRCodeActivity.class);
        intentCashIn.putExtra(Constant.EVENT_SCAN_CONTACT_PAYTO,Constant.EVENT_SCAN_CONTACT_PAYTO);
        startActivityForResult(intentCashIn,Constant.REQUEST_CONTACT_PAYTO);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
    private void validateData(){// TODO
      //  showDialogNewPayment("150000","1213244");
        if (multiTransferList != null) {
            if (multiTransferList.size() == 0) {
                if (getActivity() != null)
                    showDialogError(getString(R.string.err_not_input_number_username));
                return;
            }
        }
//        else if(tvEcashNumber.getText().toString().length()>0){
//            multiTransferList = new ArrayList<>();
//            Contact contact = new Contact();
//            contact.setWalletId(Long.parseLong(tvEcashNumber.getText().toString()));
//            multiTransferList.add(contact);
//        }
        else {
            if (getActivity() != null)
                showDialogError(getString(R.string.err_not_input_number_username));
            return;
        }
        if (edtAmount.getText().toString().isEmpty()) {
            if (getActivity() != null)
                showDialogError(getString(R.string.err_anount_null));
            return;
        }
        if(edtAmount.getText().toString().length()>0){
            Long money =Long.parseLong(edtAmount.getText().toString());
           // Log.e("money%1000 ",money%1000+"");
            if(money<1000||money%1000!=0){
                showDialogError(getString(R.string.err_amount_validate));
                return;
            }
        }
        if (edtContent.getText().toString().isEmpty()) {
            if (getActivity() != null)
                showDialogError(getString(R.string.err_dit_not_content));
            return;
        }
        showProgress();///
        PayToFuntion payToFuntion = new PayToFuntion(getActivity(),Long.parseLong(edtAmount.getText().toString()),multiTransferList,edtContent.getText().toString(),Constant.TYPE_TOPAY);
        payToFuntion.handlePayToSocket(this::PayToSuccess);
    }
    private void PayToSuccess() {
        dismissProgress();
        //EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
    }
    private void gotoContact(){
        ((PayToActivity) getActivity()).addFragment(FragmentContactTransferCash.newInstance(this), true);
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

        if (event.getData().equals(Constant.PAYTO_SUCCESS)) {
            dismissProgress();
            Toast.makeText(getActivity(),"Event payto Success",Toast.LENGTH_LONG).show();
        }

        if (event.getData().equals(Constant.EVENT_CONNECT_SOCKET_FAIL)) {
            dismissProgress();
            showDialogError(getString(R.string.err_connect_socket_fail));
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
                        tvEcashNumber.setText(contact.getWalletId().toString());
                        if(multiTransferList!=null&&multiTransferList.size()>0){
                            multiTransferList.clear();
                        }
                        multiTransferList.add(contact);
                    }

                }catch (Exception e){

                }

           }
        }else{
            Log.e("requestCode ","cancel");
    }

    }

}
