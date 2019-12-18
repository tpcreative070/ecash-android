package vn.ecpay.ewallet.ui.cashOut;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eccrypto.ECashCrypto;
import vn.ecpay.ewallet.common.eccrypto.EllipticCurve;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.ecashToEdong.RequestEcashToEdong;
import vn.ecpay.ewallet.model.ecashToEdong.ResponseECashToEdong;
import vn.ecpay.ewallet.ui.cashOut.module.CashOutModule;
import vn.ecpay.ewallet.ui.cashOut.presenter.CashOutPresenter;
import vn.ecpay.ewallet.ui.cashOut.view.CashOutView;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class CashOutFragment extends ECashBaseFragment implements CashOutView {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.tv_eDong_wallet)
    TextView tvEDongWallet;
    @BindView(R.id.layout_eDong)
    RelativeLayout layoutEDong;
    @BindView(R.id.tv_over_edong)
    TextView tvOverEdong;
    @BindView(R.id.tv_edong)
    TextView tvEdong;
    @BindView(R.id.layout_eDong_send)
    RelativeLayout layoutEDongSend;
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
    @BindView(R.id.tv_total_200)
    TextView tvTotal200;
    @BindView(R.id.iv_up_200)
    ImageView ivUp200;
    @BindView(R.id.tv_100)
    TextView tv100;
    @BindView(R.id.tv_sl_100)
    TextView tvSl100;
    @BindView(R.id.iv_down_100)
    ImageView ivDown100;
    @BindView(R.id.tv_total_100)
    TextView tvTotal100;
    @BindView(R.id.iv_up_100)
    ImageView ivUp100;
    @BindView(R.id.tv_50)
    TextView tv50;
    @BindView(R.id.tv_sl_50)
    TextView tvSl50;
    @BindView(R.id.iv_down_50)
    ImageView ivDown50;
    @BindView(R.id.tv_total_50)
    TextView tvTotal50;
    @BindView(R.id.iv_up_50)
    ImageView ivUp50;
    @BindView(R.id.tv_20)
    TextView tv20;
    @BindView(R.id.tv_sl_20)
    TextView tvSl20;
    @BindView(R.id.iv_down_20)
    ImageView ivDown20;
    @BindView(R.id.tv_total_20)
    TextView tvTotal20;
    @BindView(R.id.iv_up_20)
    ImageView ivUp20;
    @BindView(R.id.tv_10)
    TextView tv10;
    @BindView(R.id.tv_sl_10)
    TextView tvSl10;
    @BindView(R.id.iv_down_10)
    ImageView ivDown10;
    @BindView(R.id.tv_total_10)
    TextView tvTotal10;
    @BindView(R.id.iv_up_10)
    ImageView ivUp10;
    @BindView(R.id.tv_total_send)
    TextView tvTotalOut;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.layout_500)
    RelativeLayout layout500;
    @BindView(R.id.layout_200)
    RelativeLayout layout200;
    @BindView(R.id.layout_100)
    RelativeLayout layout100;
    @BindView(R.id.layout_50)
    RelativeLayout layout50;
    @BindView(R.id.layout_20)
    RelativeLayout layout20;
    @BindView(R.id.layout_10)
    RelativeLayout layout10;

    private int total500 = 0, total200 = 0, total100 = 0, total50 = 0, total20 = 0, total10 = 0;
    private int slDatabase500, slDatabase200, slDatabase100, slDatabase50, slDatabase20, slDatabase10;
    private AccountInfo accountInfo;
    private ArrayList<EdongInfo> listEDongInfo;
    private EdongInfo edongInfo;
    private long balance;
    private long totalMoney;
    private String encData;
    private String refId;
    private String publicKeyOrganization;
    private ResponseMessSocket responseMess;
    private ArrayList<CashLogs_Database> listCashSend;
    private byte elementSplit = '$';
    @Inject
    CashOutPresenter cashOutPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cash_out_pickup;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashOutModule(this)).inject(this);
        cashOutPresenter.setView(this);
        cashOutPresenter.onViewCreate();
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        listEDongInfo = ECashApplication.getListEDongInfo();
        setData();
        getMoneyDatabase();
        updateQualityOut();
        updateQualityMoney();
        updateTotalMoneySend();
        cashOutPresenter.getPublicKeyOrganization(getActivity(), accountInfo);
    }

    private void setData() {
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));

        if (listEDongInfo.size() > 0) {
            edongInfo = listEDongInfo.get(0);
            tvEDongWallet.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
            tvOverEdong.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEdong(listEDongInfo.get(0).getUsableBalance())));
            tvEdong.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
        }
    }

    private void showDialogEDong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.str_chose_edong_account));
        String[] eDong = new String[listEDongInfo.size()];
        for (int i = 0; i < listEDongInfo.size(); i++) {
            eDong[i] = String.valueOf(listEDongInfo.get(i).getAccountIdt());
        }

        builder.setItems(eDong, (dialog, which) -> {
            tvEDongWallet.setText(String.valueOf(listEDongInfo.get(which).getAccountIdt()));
            tvOverEdong.setText(String.valueOf(listEDongInfo.get(which).getUsableBalance()));
            edongInfo = listEDongInfo.get(which);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogCashOutOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                getString(R.string.str_dialog_cash_out_success, CommonUtils.formatPriceVND(totalMoney), String.valueOf(edongInfo.getAccountIdt())), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        total500 = 0;
                        total200 = 0;
                        total100 = 0;
                        total50 = 0;
                        total20 = 0;
                        total10 = 0;
                        getMoneyDatabase();
                        updateTotalMoneySend();
                        updateQualityOut();
                        updateBalance();
                    }

                    @Override
                    public void OnListenerCancel() {
                        if (getActivity() != null)
                            getActivity().finish();
                    }
                });
    }

    private void updateBalance() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (listEDongInfo.size() > 0) {
            for (int i = 0; i < listEDongInfo.size(); i++) {
                if (listEDongInfo.get(i).getAccountIdt().equals(edongInfo.getAccountIdt())) {
                    tvOverEdong.setText(CommonUtils.formatPriceVND(listEDongInfo.get(i).getUsableBalance()));
                    tvEdong.setText(String.valueOf(listEDongInfo.get(i).getAccountIdt()));
                }
            }
        } else {
            tvEdong.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
            tvOverEdong.setText(CommonUtils.formatPriceVND(CommonUtils.getMoneyEdong(listEDongInfo.get(0).getUsableBalance())));
        }
    }

    private void getMoneyDatabase() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        slDatabase500 = WalletDatabase.getTotalMoney("500000", Constant.STR_CASH_IN);
        slDatabase200 = WalletDatabase.getTotalMoney("200000", Constant.STR_CASH_IN);
        slDatabase100 = WalletDatabase.getTotalMoney("100000", Constant.STR_CASH_IN);
        slDatabase50 = WalletDatabase.getTotalMoney("50000", Constant.STR_CASH_IN);
        slDatabase20 = WalletDatabase.getTotalMoney("20000", Constant.STR_CASH_IN);
        slDatabase10 = WalletDatabase.getTotalMoney("10000", Constant.STR_CASH_IN);
        if (slDatabase500 > 0) {
            layout500.setVisibility(View.VISIBLE);
        } else {
            layout500.setVisibility(View.GONE);
        }

        if (slDatabase200 > 0) {
            layout200.setVisibility(View.VISIBLE);
        } else {
            layout200.setVisibility(View.GONE);
        }

        if (slDatabase100 > 0) {
            layout100.setVisibility(View.VISIBLE);
        } else {
            layout100.setVisibility(View.GONE);
        }

        if (slDatabase50 > 0) {
            layout50.setVisibility(View.VISIBLE);
        } else {
            layout50.setVisibility(View.GONE);
        }

        if (slDatabase20 > 0) {
            layout20.setVisibility(View.VISIBLE);
        } else {
            layout20.setVisibility(View.GONE);
        }

        if (slDatabase10 > 0) {
            layout10.setVisibility(View.VISIBLE);
        } else {
            layout10.setVisibility(View.GONE);
        }

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
        tvTotalOut.setText(CommonUtils.formatPriceVND(totalMoney));
    }

    @Override
    public void onResume() {
        ((CashOutActivity) getActivity()).updateTitle(getString(R.string.str_cash_out));
        super.onResume();
    }

    @OnClick({R.id.layout_eDong, R.id.layout_eDong_send, R.id.iv_down_500, R.id.iv_up_500, R.id.iv_down_200, R.id.iv_up_200, R.id.iv_down_100, R.id.iv_up_100, R.id.iv_down_50, R.id.iv_up_50, R.id.iv_down_20, R.id.iv_up_20, R.id.iv_down_10, R.id.iv_up_10, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
            case R.id.layout_eDong:
                if (listEDongInfo.size() > 1) {
                    showDialogEDong();
                }
                break;

            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void updateAllData() {
        updateQualityOut();
        updateQualityMoney();
        updateTotalMoneySend();
    }

    private void validateData() {
        if (totalMoney == 0) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_chose_money));
            return;
        }
        if (null == publicKeyOrganization) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_get_public_key_organize));
            if (getActivity() != null)
                getActivity().onBackPressed();
            return;
        }
        AsyncTaskCash asyncTaskCash = new AsyncTaskCash();
        asyncTaskCash.execute();
    }

    private class AsyncTaskCash extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getCashEncrypt();
            return null;
        }
    }

    private void getCashEncrypt() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        listCashSend = new ArrayList<>();
        if (total10 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("10000", Constant.STR_CASH_IN);
            for (int i = 0; i < total10; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (total20 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("20000", Constant.STR_CASH_IN);
            for (int i = 0; i < total20; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (total50 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("50000", Constant.STR_CASH_IN);
            for (int i = 0; i < total50; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (total100 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("100000", Constant.STR_CASH_IN);
            for (int i = 0; i < total100; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (total200 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("200000", Constant.STR_CASH_IN);
            for (int i = 0; i < total200; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (total500 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("500000", Constant.STR_CASH_IN);
            for (int i = 0; i < total500; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (listCashSend.size() > 0) {
            String[][] cashSendArray = new String[listCashSend.size()][3];
            for (int i = 0; i < listCashSend.size(); i++) {
                CashLogs_Database cash = listCashSend.get(i);
                String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                cashSendArray[i] = moneyItem;
            }

            encData = getEncrypData(cashSendArray, publicKeyOrganization);
            responseMess = new ResponseMessSocket();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(String.valueOf(accountInfo.getWalletId()));
            responseMess.setTime(CommonUtils.getCurrentTime());
            responseMess.setType(Constant.TYPE_SEND_ECASH_TO_EDONG);
            responseMess.setContent(Constant.STR_EMPTY);
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getIdSender(responseMess, getActivity()));
            refId = getIdSender(responseMess);
            responseMess.setRefId(refId);
            if (refId.isEmpty() || encData.isEmpty()) {
                dismissProgress();
                DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_upload));
                return;
            }
            cashOutPresenter.sendECashToEDong(encData, refId, totalMoney, edongInfo, accountInfo);
        }
    }

    private String getIdSender(ResponseMessSocket responseMess) {
        byte[] dataSign = SHA256.hashSHA256(getSignBodySender(responseMess));
        return CommonUtils.generateSignature(dataSign, KeyStoreUtils.getPrivateKey(getActivity()));
    }

    private String getSignBodySender(ResponseMessSocket responseMess) {
        return responseMess.getSender() + responseMess.getReceiver() + responseMess.getTime() + responseMess.getType()
                + responseMess.getContent() + responseMess.getCashEnc();
    }

    private String getEncrypData(String[][] cashArray, String publicKyReceiver) {
        byte[][] blockEnc;
        try {
            EllipticCurve ec = EllipticCurve.getSecp256k1();
            byte[] keyPublic = Base64.decode(publicKyReceiver, Base64.DEFAULT);
            ECPublicKeyParameters publicKeyParameters = ec.getPublicKeyParameters(keyPublic);
            blockEnc = ECashCrypto.encryptV2(ec, publicKeyParameters, cashArray);
        } catch (IOException e) {
            e.printStackTrace();
            return Constant.STR_EMPTY;
        }

        String encData = (Base64.encodeToString(blockEnc[0], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[1], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[2], Base64.DEFAULT)).replaceAll("\n", "");
        return encData;
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
    public void loadPublicKeyOrganizeSuccess(String publicKeyOrganization) {
        this.publicKeyOrganization = publicKeyOrganization;
    }

    @Override
    public void showDialogError(String err) {
        DialogUtil.getInstance().showDialogWarning(getActivity(), err);
    }

    @Override
    public void sendECashToEDongSuccess() {
        AsyncTaskSendSuccess asyncTaskSendSuccess = new AsyncTaskSendSuccess();
        asyncTaskSendSuccess.execute();

    }

    private class AsyncTaskSendSuccess extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseUtil.updateTransactionsLogAndCashOutDatabase(listCashSend, responseMess, getActivity(), accountInfo.getUsername());
            EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
            cashOutPresenter.getEDongInfo(accountInfo);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showDialogCashOutOk();
        }
    }
}
