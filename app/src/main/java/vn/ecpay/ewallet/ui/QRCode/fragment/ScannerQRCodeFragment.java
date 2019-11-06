package vn.ecpay.ewallet.ui.QRCode.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contact.QRContact;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.QRCode.module.QRCodeModule;
import vn.ecpay.ewallet.ui.QRCode.presenter.QRCodePresenter;
import vn.ecpay.ewallet.ui.QRCode.view.QRCodeView;
import vn.ecpay.ewallet.ui.contact.AddContactActivity;
import vn.ecpay.ewallet.webSocket.WebSocketsService;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

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
    private ResponseMessSocket cashMess;
    private int typeScan;
    private AccountInfo accountInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_qrcode_scanner;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new QRCodeModule(this)).inject(this);
        qrCodePresenter.setView(this);
        qrCodePresenter.onViewCreate();
        cashMap = new HashMap<>();
        eCashSplit = new StringBuffer();
        mScannerView = new ZXingScannerView(getActivity());
        containerScanner.addView(mScannerView);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
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
        Gson gson = new Gson();
        try {
            QRScanBase qrScanBase = gson.fromJson(result.getText(), QRScanBase.class);
            if (CommonUtils.getTypeScan(qrScanBase) == Constant.IS_SCAN_CASH) {
                handleJsonResult(result.getText());
            } else {
                handleContact(result.getText());
            }
        } catch (JsonSyntaxException e) {
            ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> mScannerView.resumeCameraPreview(ScannerQRCodeFragment.this), 2000);
    }

    private void handleContact(String result) {
        Gson gson = new Gson();
        try {
            QRContact qrContact = gson.fromJson(result, QRContact.class);
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
                    DatabaseUtil.saveOnlySingleContact(getActivity(), contact);
                    Toast.makeText(getActivity(), getResources().getString(R.string.str_add_contact_success), Toast.LENGTH_LONG).show();
                }
            } else {
                ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
            }
        } catch (JsonSyntaxException e) {
            ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_qr_code_fail));
        }
    }

    private void handleJsonResult(String result) {
        Gson gson = new Gson();
        try {
            QRCodeSender qrCodeSender = gson.fromJson(result, QRCodeSender.class);
            if (qrCodeSender != null) {
                cashMap.put(qrCodeSender.getCycle(), qrCodeSender.getContent());
                String numberScan = cashMap.size() + "/" + qrCodeSender.getTotal();
                tvNumberScan.setText(getResources().getString(R.string.str_number_scan_qr_code, numberScan));
                if (cashMap.size() == qrCodeSender.getTotal()) {
                    showLoading();
                    for (int i = 1; i <= cashMap.size(); i++) {
                        eCashSplit.append(cashMap.get(i));
                    }
                    ResponseMessSocket cashMess = gson.fromJson(eCashSplit.toString(), ResponseMessSocket.class);
                    if (!DatabaseUtil.isTransactionLogExit(cashMess, getActivity())) {
                        DatabaseUtil.saveTransactionLog(cashMess, getActivity());
                    } else {
                        ((QRCodeActivity) getActivity()).showDialogError("QR Code đã được nhận");
                        return;
                    }
                    Intent intent = new Intent(getActivity(), WebSocketsService.class);
                    intent.putExtra(Constant.IS_QR_CODE, true);
                    intent.putExtra(Constant.RESPONSE_CASH_MESS, cashMess);
                    if (getActivity() != null) {
                        getActivity().startService(intent);
                    }
                }
            } else {
                ((QRCodeActivity) getActivity()).showDialogError("QR Code không hợp lệ");
            }
        } catch (JsonSyntaxException e) {
            ((QRCodeActivity) getActivity()).showDialogError("QR Code không hợp lệ");
        }
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.EVENT_QR_CODE_SCAN_CASH_SUCCESS)) {
            cashMap = new HashMap<>();
            tvNumberScan.setText(Constant.STR_EMPTY);
            dismissProgress();
            ArrayList<QRCashTransfer> qrCashTransfers = event.getQrCashTransfers();
            ((QRCodeActivity) getActivity()).addFragment(FragmentQRResult.newInstance(qrCashTransfers), true);
        }

        if (event.getData().equals(Constant.EVENT_VERIFY_CASH_FAIL)) {
            cashMap = new HashMap<>();
            tvNumberScan.setText(Constant.STR_EMPTY);
            dismissProgress();
            ((QRCodeActivity) getActivity()).showDialogError("Giao dịch được gửi cho tài khoản khác");
        }
        EventBus.getDefault().removeStickyEvent(event);
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
