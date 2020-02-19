package vn.ecpay.ewallet.ui.QRCode.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.Result;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.model.QRCode.QRCodePayment;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contact.QRContact;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.lixi.CashTemp;
import vn.ecpay.ewallet.model.payment.Payments;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.QRCode.module.QRCodeModule;
import vn.ecpay.ewallet.ui.QRCode.presenter.QRCodePresenter;
import vn.ecpay.ewallet.ui.QRCode.view.QRCodeView;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

import static vn.ecpay.ewallet.common.utils.Constant.QR_CONTACT;
import static vn.ecpay.ewallet.common.utils.Constant.QR_TO_PAY;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_EDONG_TO_ECASH;

public class ScannerQRCodeFragment extends ECashBaseFragment implements ZXingScannerView.ResultHandler, QRCodeView {
    @BindView(R.id.layout_scan_qr)
    LinearLayout layoutScanQr;
    @BindView(R.id.layout_qr_code)
    LinearLayout layoutQrCode;
    @BindView(R.id.layout_flash)
    LinearLayout layoutFlash;
    @BindView(R.id.layout_image)
    LinearLayout layoutImage;
    @BindView(R.id.tv_number_scan)
    TextView tvNumberScan;
    private ZXingScannerView mScannerView;
    @BindView(R.id.containerScanner)
    FrameLayout containerScanner;
    @BindView(R.id.progress)
    ProgressBar progress;
    private HashMap<Integer, String> cashMap;
    private StringBuffer eCashSplit;
    @Inject
    QRCodePresenter qrCodePresenter;
    private AccountInfo accountInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_qrcode_scanner;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new QRCodeModule(this)).inject(this);
        qrCodePresenter.setView(this);
        qrCodePresenter.onViewCreate();
        eCashSplit = new StringBuffer();
        mScannerView = new ZXingScannerView(getActivity());
        containerScanner.addView(mScannerView);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        cashMap = new HashMap<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.startCamera();
        Handler handler = new Handler();
        handler.postDelayed(() -> progress.setVisibility(View.GONE), 1000);
        mScannerView.setResultHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.e("result ",result.toString());
        Gson gson = new Gson();
        Log.e("gson ",gson.toJson(result));
        try {
            QRScanBase qrScanBase = gson.fromJson(result.getText(), QRScanBase.class);
            Log.e("qrScanBase ",gson.toJson(qrScanBase));
            if(qrScanBase!=null){
                if (qrScanBase.getType() != null) {
                    switch (qrScanBase.getType()) {
                        case QR_CONTACT:
                            if (((QRCodeActivity) Objects.requireNonNull(getActivity())).isScanQRCodePayTo()) {
                                handleContactWithPayTo(result.getText());
                            } else {
                                handleContact(result.getText());
                            }
                            break;
                        case QR_TO_PAY:
                            handleQRCodeToPay(result.getText());
                            break;
                        default:
                            dismissProgress();
                            if (getActivity() != null)
                                ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
                            break;
                    }
                } else {
                    if (null != qrScanBase.getContent()) {
                        handleCash(result.getText());
                    } else {
                        dismissProgress();
                        if (getActivity() != null)
                            ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
                    }
                }
            }else{
                if (getActivity() != null)
                    ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
            }

        } catch (JsonSyntaxException e) {
            Log.e("e ",e.getLocalizedMessage());
            Log.e("e ",e.getMessage());
            dismissProgress();
            if (getActivity() != null)
                ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> mScannerView.resumeCameraPreview(ScannerQRCodeFragment.this), 2000);
    }

    private void handleContact(String result) {
        Gson gson = new Gson();

        try {
            QRScanBase qrScanBase = gson.fromJson(result, QRScanBase.class);
            if(qrScanBase!=null&&qrScanBase.getContent()!=null){
               // QRContact qrContact = gson.fromJson(result, QRContact.class);
                QRContact qrContact = gson.fromJson(qrScanBase.getContent(), QRContact.class);
                if (qrContact != null) {
                    Contact contact = new Contact();
                    contact.setPublicKeyValue(qrContact.getPublicKey());
                    contact.setFullName(qrContact.getFullname());
                    contact.setPhone(qrContact.getPersonMobiPhone());
                    contact.setTerminalInfo(qrContact.getTerminalInfo());
                    contact.setWalletId(qrContact.getWalletId());

                    if (contact.getWalletId().equals(accountInfo.getWalletId())) {
                        ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_add_contact_conflict));
                    } else {
                        // todo: can using funtion checkContactExist(contact)
                        List<Contact> listContact = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
                        for (int i = 0; i < listContact.size(); i++) {
                            if (listContact.get(i).getWalletId().equals(contact.getWalletId())) {
                                ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_add_contact_duplicate));
                                return;
                            }
                        }
                        DatabaseUtil.saveOnlySingleContact(getActivity(), contact);
                        Toast.makeText(getActivity(), getResources().getString(R.string.str_add_contact_success), Toast.LENGTH_LONG).show();
                    }
                } else {
                    ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
                }
            }
            else {
                ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
            }
        } catch (JsonSyntaxException e) {
            ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
        }
    }

    private void handleCash(String result) {
     //    Log.e("result",result);
        Gson gson = new Gson();
        try {
           // QRCodeSender qrCodeSender = new QRCodeSender();
             QRCodeSender qrCodeSender = gson.fromJson(result, QRCodeSender.class);
             if(qrCodeSender!=null){
            //  Log.e("scan_qr_code",qrCodeSender.toString());
                cashMap.put(qrCodeSender.getCycle(), qrCodeSender.getContent());
                String numberScan = cashMap.size() + "/" + qrCodeSender.getTotal();
                tvNumberScan.setText(getResources().getString(R.string.str_number_scan_qr_code, numberScan));
                if (cashMap.size() == qrCodeSender.getTotal()) {
                    showProgress();
                    for (int i = 1; i <= cashMap.size(); i++) {
                        eCashSplit.append(cashMap.get(i));
                    }
                    ResponseMessSocket responseMess = gson.fromJson(eCashSplit.toString(), ResponseMessSocket.class);
                    if (!responseMess.getReceiver().equals(String.valueOf(accountInfo.getWalletId()))) {
                        restartScan();
                        if (getActivity() != null)
                            ((QRCodeActivity) getActivity()).showDialogError("Giao dịch được gửi cho tài khoản khác");
                        return;
                    }
                    if (responseMess.getType().equals(Constant.TYPE_LIXI)) {
                        if (!DatabaseUtil.isCashTempExit(responseMess, getActivity())) {
                            if (responseMess.getCashEnc() != null) {
                                String json = gson.toJson(responseMess);
                                CashTemp cashTemp = new CashTemp();
                                cashTemp.setContent(json);
                                cashTemp.setSenderAccountId(responseMess.getSender());
                                cashTemp.setTransactionSignature(responseMess.getId());
                                DatabaseUtil.saveCashTemp(cashTemp, getActivity());
                                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_LIXI));
                                restartScan();
                                Toast.makeText(getActivity(), getResources().getString(R.string.str_take_lixi_success), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            dismissProgress();
                            restartScan();
                            if (getActivity() != null)
                                ((QRCodeActivity) getActivity()).showDialogError("QR Code đã được nhận");
                        }
                    } else {
                        if (!DatabaseUtil.isTransactionLogExit(responseMess, getActivity())) {
                            handleCashIn(responseMess);
                        } else {
                            restartScan();
                            if (getActivity() != null) {
                                ((QRCodeActivity) getActivity()).showDialogError("QR Code đã được nhận");
                            }

                        }
                    }
                }
            } else {
                dismissProgress();
                if (null != getActivity())
                    ((QRCodeActivity) getActivity()).showDialogError("QR Code không hợp lệ");
            }
        } catch (JsonSyntaxException e) {
            dismissProgress();
            if (null != getActivity())
                ((QRCodeActivity) getActivity()).showDialogError("QR Code không hợp lệ");
        }
    }

    private void handleQRCodeToPay(String result){
        try {
            Gson gson = new Gson();
            QRScanBase qrScanBase = gson.fromJson(result, QRScanBase.class);
            if(qrScanBase!=null&&qrScanBase.getContent()!=null){
                QRCodePayment qrCodePayment=gson.fromJson(qrScanBase.getContent(), QRCodePayment.class);
                if(qrCodePayment!=null){
                    Payments payment = new Payments(qrCodePayment);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constant.SCAN_QR_TOPAY, payment);
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().finish();
                    dismissLoading();
                }else{
                    ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
                }
            }else {
                ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
            }
        }catch (Exception e){
            ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
        }

    }
    private void handleContactWithPayTo(String result) {
        try {
            Gson gson = new Gson();
            QRScanBase qrScanBase = gson.fromJson(result, QRScanBase.class);
            if(qrScanBase!=null&&qrScanBase.getContent()!=null){
                // QRContact qrContact = gson.fromJson(result, QRContact.class);
                QRContact qrContact = gson.fromJson(qrScanBase.getContent(), QRContact.class);
                if (qrContact != null) {
                    Contact contact = new Contact();
                    contact.setPublicKeyValue(qrContact.getPublicKey());
                    contact.setFullName(qrContact.getFullname());
                    contact.setPhone(qrContact.getPersonMobiPhone());
                    contact.setTerminalInfo(qrContact.getTerminalInfo());
                    contact.setWalletId(qrContact.getWalletId());
                    if (!checkContactExist(contact)) {
                        DatabaseUtil.saveOnlySingleContact(getActivity(), contact);
                    }
                    ((QRCodeActivity) getActivity()).checkPayTo(contact);
                } else {
                    ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
                }
            }

        } catch (JsonSyntaxException e) {
            ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
        }
    }

    private boolean checkContactExist(Contact contact) {
        List<Contact> listContact = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
        if (listContact != null && listContact.size() > 0) {
            for (int i = 0; i < listContact.size(); i++) {
                if (listContact.get(i).getWalletId().equals(contact.getWalletId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String transactionSignatureCashInQR;

    private void handleCashIn(ResponseMessSocket responseMess) {
        transactionSignatureCashInQR = responseMess.getId();
        Gson gson = new Gson();
        String jsonCashInResponse = gson.toJson(responseMess);
        CacheData_Database cacheData_database = new CacheData_Database();
        cacheData_database.setTransactionSignature(responseMess.getId());
        cacheData_database.setResponseData(jsonCashInResponse);
        cacheData_database.setType(TYPE_SEND_EDONG_TO_ECASH);
        DatabaseUtil.saveCacheData(cacheData_database, getActivity());
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CASH_IN));
    }

    private void restartScan() {
        cashMap.clear();
        eCashSplit.delete(0, eCashSplit.length());
        tvNumberScan.setText(Constant.STR_EMPTY);
        dismissProgress();
    }

    @OnClick({R.id.layout_scan_qr, R.id.layout_qr_code, R.id.layout_flash, R.id.layout_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_scan_qr:
                break;
            case R.id.layout_qr_code:
                break;
            case R.id.layout_flash:
                break;
            case R.id.layout_image:
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

    @SuppressLint("UseSparseArrays")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.EVENT_VERIFY_CASH_FAIL)) {
            cashMap.clear();
            eCashSplit.delete(0, eCashSplit.length());
            tvNumberScan.setText(Constant.STR_EMPTY);
            dismissProgress();
            ((QRCodeActivity) getActivity()).showDialogError("Giao dịch được gửi cho tài khoản khác");
        }
        if (event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> onCashInSuccessListener());
                }
            }, 500);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void onCashInSuccessListener() {
        restartScan();
        TransactionsHistoryModel transactionsHistoryModel = DatabaseUtil.getCurrentTransactionsHistory(getActivity(), transactionSignatureCashInQR);
        if (getActivity() != null)
            ((QRCodeActivity) getActivity()).addFragment(FragmentQRResult.newInstance(transactionsHistoryModel), true);
    }

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }
}
