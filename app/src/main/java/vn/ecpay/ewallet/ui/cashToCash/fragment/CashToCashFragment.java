package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cash.getPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.contactTransfer.ContactTransferModel;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.module.CashToCashModule;
import vn.ecpay.ewallet.ui.cashToCash.presenter.CashToCashPresenter;
import vn.ecpay.ewallet.ui.cashToCash.view.CashToCashView;
import vn.ecpay.ewallet.webSocket.WebSocketsService;
import vn.ecpay.ewallet.webSocket.object.ResponseCashMess;

public class CashToCashFragment extends ECashBaseFragment implements CashToCashView, ContactTransferListener {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverECash;
    @BindView(R.id.edt_stk_cash)
    EditText edtStkCash;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.tv_500)
    TextView tv500;
    @BindView(R.id.tv_sl_500)
    TextView tvSl500;
    @BindView(R.id.iv_down_500)
    ImageView ivDown500;
    @BindView(R.id.tv_total_500)
    TextView tvTotal500;
    @BindView(R.id.iv_up_500)
    ImageView ivUp500;
    @BindView(R.id.tv_200)
    TextView tv200;
    @BindView(R.id.tv_sl_200)
    TextView tvSl200;
    @BindView(R.id.iv_down_200)
    ImageView ivDown200;
    @BindView(R.id.iv_up_200)
    ImageView ivUp200;
    @BindView(R.id.tv_100)
    TextView tv100;
    @BindView(R.id.tv_sl_100)
    TextView tvSl100;
    @BindView(R.id.iv_down_100)
    ImageView ivDown100;
    @BindView(R.id.iv_up_100)
    ImageView ivUp100;
    @BindView(R.id.tv_50)
    TextView tv50;
    @BindView(R.id.tv_sl_50)
    TextView tvSl50;
    @BindView(R.id.iv_down_50)
    ImageView ivDown50;
    @BindView(R.id.iv_up_50)
    ImageView ivUp50;
    @BindView(R.id.tv_20)
    TextView tv20;
    @BindView(R.id.tv_sl_20)
    TextView tvSl20;
    @BindView(R.id.iv_down_20)
    ImageView ivDown20;
    @BindView(R.id.iv_up_20)
    ImageView ivUp20;
    @BindView(R.id.tv_10)
    TextView tv10;
    @BindView(R.id.tv_sl_10)
    TextView tvSl10;
    @BindView(R.id.iv_down_10)
    ImageView ivDown10;
    @BindView(R.id.iv_up_10)
    ImageView ivUp10;
    @BindView(R.id.tv_total_send)
    TextView tvTotalSend;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_total_200)
    TextView tvTotal200;
    @BindView(R.id.tv_total_100)
    TextView tvTotal100;
    @BindView(R.id.tv_total_50)
    TextView tvTotal50;
    @BindView(R.id.tv_total_20)
    TextView tvTotal20;
    @BindView(R.id.tv_total_10)
    TextView tvTotal10;
    @BindView(R.id.sw_qr_code)
    Switch swQrCode;
    private AccountInfo accountInfo;
    private String publicKeyWalletReceiver;

    private String walletId, content;
    private long balance;
    private int total500 = 0, total200 = 0, total100 = 0, total50 = 0, total20 = 0, total10 = 0;
    private int slDatabase500, slDatabase200, slDatabase100, slDatabase50, slDatabase20, slDatabase10;
    private long totalMoney = 0;
    private ResponseDataGetPublicKeyWallet dataGetPublicKey;
    private ContactTransferModel transferModel;

    @Inject
    CashToCashPresenter cashToCashPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_transfer_money_pick;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = ECashApplication.getAccountInfo().getUsername();
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashToCashModule(this)).inject(this);
        cashToCashPresenter.setView(this);
        cashToCashPresenter.onViewCreate();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        edtStkCash.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!edtStkCash.getText().toString().isEmpty()) {
                    if (edtStkCash.getText().toString().equals(accountInfo.getWalletId())) {
                        ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_duplicate_wallet_id));
                        return;
                    }
                    cashToCashPresenter.getPublicKeyWallet(accountInfo, edtStkCash.getText().toString());
                }
            }
        });
        setData();
        getMoneyDatabase();
        updateQualityMoney();
        updateQualityOut();
        updateTotalMoneySend();
    }

    private void setData() {
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverECash.setText(CommonUtils.formatPriceVND(balance));
    }

    private void getMoneyDatabase() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        slDatabase500 = WalletDatabase.getTotalMoney("500000", Constant.STR_CASH_IN);
        slDatabase200 = WalletDatabase.getTotalMoney("200000", Constant.STR_CASH_IN);
        slDatabase100 = WalletDatabase.getTotalMoney("100000", Constant.STR_CASH_IN);
        slDatabase50 = WalletDatabase.getTotalMoney("50000", Constant.STR_CASH_IN);
        slDatabase20 = WalletDatabase.getTotalMoney("20000", Constant.STR_CASH_IN);
        slDatabase10 = WalletDatabase.getTotalMoney("10000", Constant.STR_CASH_IN);
    }

    private void updateQualityOut() {
        tvTotal500.setText(String.valueOf(total500));
        tvTotal200.setText(String.valueOf(total200));
        tvTotal100.setText(String.valueOf(total100));
        tvTotal50.setText(String.valueOf(total50));
        tvTotal20.setText(String.valueOf(total20));
        tvTotal10.setText(String.valueOf(total10));
    }

    private void updateQualityMoney() {
        tvSl500.setText(getActivity().getString(R.string.str_money, String.valueOf(slDatabase500)));
        tvSl200.setText(getActivity().getString(R.string.str_money, String.valueOf(slDatabase200)));
        tvSl100.setText(getActivity().getString(R.string.str_money, String.valueOf(slDatabase100)));
        tvSl50.setText(getActivity().getString(R.string.str_money, String.valueOf(slDatabase50)));
        tvSl20.setText(getActivity().getString(R.string.str_money, String.valueOf(slDatabase20)));
        tvSl10.setText(getActivity().getString(R.string.str_money, String.valueOf(slDatabase10)));
    }

    private void updateTotalMoneySend() {
        totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
        tvTotalSend.setText(CommonUtils.formatPriceVND(totalMoney));
    }

    @Override
    public void onResume() {
        ((CashToCashActivity) getActivity()).updateTitle(getResources().getString(R.string.str_transfer));
        super.onResume();
    }

    private void updateAllData() {
        updateQualityOut();
        updateQualityMoney();
        updateTotalMoneySend();
    }

    @OnClick({R.id.iv_contact, R.id.iv_down_500, R.id.iv_up_500, R.id.iv_down_200, R.id.iv_up_200, R.id.iv_down_100, R.id.iv_up_100, R.id.iv_down_50, R.id.iv_up_50, R.id.iv_down_20, R.id.iv_up_20, R.id.iv_down_10, R.id.iv_up_10, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_contact:
                ((CashToCashActivity) getActivity()).addFragment(FragmentContactTransferCash.newInstance(this), true);
                break;
            case R.id.iv_down_500:
                if (total500 > 0) {
                    total500 = total500 - 1;
                    slDatabase500 = slDatabase500 + 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_500:
                if (slDatabase500 > 0) {
                    total500 = total500 + 1;
                    slDatabase500 = slDatabase500 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_down_200:
                if (total200 > 0) {
                    total200 = total200 - 1;
                    slDatabase200 = slDatabase200 + 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_200:
                if (slDatabase200 > 0) {
                    total200 = total200 + 1;
                    slDatabase200 = slDatabase200 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_down_100:
                if (total100 > 0) {
                    total100 = total100 - 1;
                    slDatabase100 = slDatabase100 + 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_100:
                if (slDatabase100 > 0) {
                    total100 = total100 + 1;
                    slDatabase100 = slDatabase100 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_down_50:
                if (total50 > 0) {
                    total50 = total50 - 1;
                    slDatabase50 = slDatabase50 + 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_50:
                if (slDatabase50 > 0) {
                    total50 = total50 + 1;
                    slDatabase50 = slDatabase50 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_down_20:
                if (total20 > 0) {
                    total20 = total20 - 1;
                    slDatabase20 = slDatabase20 + 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_20:
                if (slDatabase20 > 0) {
                    total20 = total20 + 1;
                    slDatabase20 = slDatabase20 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_down_10:
                if (total10 > 0) {
                    total10 = total10 - 1;
                    slDatabase10 = slDatabase10 + 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_10:
                if (slDatabase10 > 0) {
                    total10 = total10 + 1;
                    slDatabase10 = slDatabase10 - 1;
                }
                updateAllData();
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void validateData() {
        walletId = edtStkCash.getText().toString();
        content = edtContent.getText().toString();
        if (walletId.isEmpty()) {
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_not_input_number_username));
            return;
        }
        if (content.isEmpty()) {
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_dit_not_content));
            return;
        }
        if (walletId.equals(String.valueOf(accountInfo.getWalletId()))) {
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_duplicate_wallet_id));
            return;
        }

        if (totalMoney == 0) {
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_dit_not_money_transfer));
            return;
        }

        if (publicKeyWalletReceiver.equals(Constant.STR_EMPTY)) {
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_dit_not_get_key_transfer));
            return;
        }
        if (swQrCode.isChecked()) {
            if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                showLoading();
                getListCashSend();
            }
        } else {
            showLoading();
            startService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getListCashSend();
                }
            }
            default:
                break;
        }
    }

    private void getListCashSend() {
        if (!isExternalStorageWritable()) {
            dismissLoading();
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_store_image));
            return;
        }
        String jsonCash = getObjectJsonSend(total10, total20, total50, total100, total200, total500,
                publicKeyWalletReceiver, edtStkCash.getText().toString(), edtContent.getText().toString());
        List<String> stringList = CommonUtils.getSplittedString(jsonCash, 1000);
        ArrayList<QRCodeSender> codeSenderArrayList = new ArrayList<>();
        if (stringList.size() > 0) {
            for (int i = 0; i < stringList.size(); i++) {
                QRCodeSender qrCodeSender = new QRCodeSender();
                qrCodeSender.setCycle(i + 1);
                qrCodeSender.setTotal(stringList.size());
                qrCodeSender.setContent(stringList.get(i));
                codeSenderArrayList.add(qrCodeSender);
            }
        }
        if (codeSenderArrayList.size() > 0) {
            DatabaseUtil.saveContact(getActivity(), dataGetPublicKey);
            DatabaseUtil.updateDatabase(listCashSend, responseMess, getActivity(), accountInfo.getUsername());
            saveImageQRCode(codeSenderArrayList);
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private void saveImageQRCode(ArrayList<QRCodeSender> qrCodeSender) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = 0; i < qrCodeSender.size(); i++) {
                    Gson gson = new Gson();
                    Bitmap bitmap = generateQRCode(gson.toJson(qrCodeSender.get(i)));
                    String root = Environment.getExternalStorageDirectory().toString();
                    File mFolder = new File(root + "/qr_image");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    String imageName = walletId + "_" + CommonUtils.getAuditNumber() + "_" + i + ".jpg";
                    File file = new File(mFolder, imageName);
                    if (file.exists())
                        file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoading();
                showDialogSendOk();
            }
        }.execute();
    }

    private Bitmap generateQRCode(String value) {
        int WIDTH = 400;
        Writer writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = writer.encode(value, BarcodeFormat.QR_CODE, WIDTH, WIDTH);
            Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < 400; i++) {
                for (int j = 0; j < 400; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK
                            : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<CashLogs> listCashSend;
    private ResponseCashMess responseMess;

    private String getObjectJsonSend(int sl10, int sl20, int sl50, int sl100, int sl200, int sl500,
                                     String keyPublicReceiver, String walletReceiver, String contentSendMoney) {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        listCashSend = new ArrayList<>();
        if (sl10 > 0) {
            List<CashLogs> cashList = WalletDatabase.getListCashForMoney("10000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl10; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (sl20 > 0) {
            List<CashLogs> cashList = WalletDatabase.getListCashForMoney("20000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl20; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl50 > 0) {
            List<CashLogs> cashList = WalletDatabase.getListCashForMoney("50000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl50; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl100 > 0) {
            List<CashLogs> cashList = WalletDatabase.getListCashForMoney("100000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl100; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl200 > 0) {
            List<CashLogs> cashList = WalletDatabase.getListCashForMoney("200000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl200; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (sl500 > 0) {
            List<CashLogs> cashList = WalletDatabase.getListCashForMoney("500000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl500; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (listCashSend.size() > 0) {
            String[][] cashArray = new String[listCashSend.size()][3];
            for (int i = 0; i < listCashSend.size(); i++) {
                CashLogs cash = listCashSend.get(i);
                String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                cashArray[i] = moneyItem;
            }
            String encData = CommonUtils.getEncrypData(cashArray, keyPublicReceiver);
            responseMess = new ResponseCashMess();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(walletReceiver);
            responseMess.setTime(CommonUtils.getCurrentTime());
            responseMess.setType(Constant.TYPE_SEND_MONEY);
            responseMess.setContent(contentSendMoney);
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getIdSender(responseMess, getActivity()));
            Gson gson = new Gson();
            return gson.toJson(responseMess);
        }
        return Constant.STR_EMPTY;
    }

    private void startService() {
        Intent intent = new Intent(getActivity(), WebSocketsService.class);
        intent.putExtra(Constant.IS_QR_CODE, false);
        intent.putExtra(Constant.TOTAL_500, total500);
        intent.putExtra(Constant.TOTAL_200, total200);
        intent.putExtra(Constant.TOTAL_100, total100);
        intent.putExtra(Constant.TOTAL_50, total50);
        intent.putExtra(Constant.TOTAL_20, total20);
        intent.putExtra(Constant.TOTAL_10, total10);
        intent.putExtra(Constant.KEY_PUBLIC_RECEIVER, publicKeyWalletReceiver);
        intent.putExtra(Constant.WALLET_RECEIVER, edtStkCash.getText().toString());
        intent.putExtra(Constant.CONTENT_SEND_MONEY, edtContent.getText().toString());
        if (getActivity() != null) {
            getActivity().startService(intent);
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
        if (event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)) {
            dismissLoading();
            showDialogSendOk();
        }

        if (event.getData().equals(Constant.EVENT_CONNECT_SOCKET_FAIL)) {
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_connect_socket_fail));
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void showDialogSendOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                getString(R.string.str_dialog_cash_to_cash_success, CommonUtils.formatPriceVND(totalMoney), edtStkCash.getText().toString()), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        total500 = 0;
                        total200 = 0;
                        total100 = 0;
                        total50 = 0;
                        total20 = 0;
                        total10 = 0;
                        setData();
                        getMoneyDatabase();
                        updateQualityMoney();
                        updateQualityOut();
                        updateTotalMoneySend();
                    }

                    @Override
                    public void OnListenerCancel() {
                        getActivity().finish();
                    }
                });
    }

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }

    @Override
    public void getPublicKeyWalletSuccess(ResponseDataGetPublicKeyWallet responseDataGetPublicKeyWallet) {
        this.dataGetPublicKey = responseDataGetPublicKeyWallet;
        dataGetPublicKey.setmWalletId(Long.valueOf(edtStkCash.getText().toString()));
        publicKeyWalletReceiver = responseDataGetPublicKeyWallet.getEcKpValue();
    }

    @Override
    public void getPublicKeyWalletFail(String err) {
        publicKeyWalletReceiver = Constant.STR_EMPTY;
        try {
            ((CashToCashActivity) Objects.requireNonNull(getActivity())).showDialogError(err);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void itemClick(ContactTransferModel mTransferModel) {
        if (mTransferModel != null) {
            this.transferModel = mTransferModel;
            edtStkCash.setText(String.valueOf(transferModel.getWalletId()));
            publicKeyWalletReceiver = transferModel.getPublicKeyValue();
        }
    }
}
