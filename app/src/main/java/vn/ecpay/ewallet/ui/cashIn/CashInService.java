package vn.ecpay.ewallet.ui.cashIn;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.Decision_Database;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.getPublicKeyCash.RequestGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseDataGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseGetPublicKeyCash;

public class CashInService extends Service {
    private int numberRequest = 0;
    private CashInResponse eDongToECashResponse;
    private String[][] deCryptECash;
    private AccountInfo accountInfo;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras;
        try {
            extras = intent.getExtras();
        } catch (NullPointerException e) {
            extras = null;
        }
        if (null != extras) {
            eDongToECashResponse = (CashInResponse) extras.getSerializable(Constant.EDONG_TO_ECASH);
            accountInfo = (AccountInfo) extras.getSerializable(Constant.ACCOUNT_INFO);
            if (eDongToECashResponse != null) {
                deCryptECash = CommonUtils.decrypEcash(eDongToECashResponse.getCashEnc(), KeyStoreUtils.getPrivateKey(getApplicationContext()));
                checkArrayCash();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkArrayCash() {
        if (null != deCryptECash) {
            if (deCryptECash.length > 0) {
                if (numberRequest == deCryptECash.length) {
                    EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_MONEY));
                    numberRequest = 0;
                    stopSelf();
                    return;
                }
                AsyncTaskCash asynctaskCash = new AsyncTaskCash();
                asynctaskCash.execute(deCryptECash[numberRequest]);
            }
        }
    }

    private class AsyncTaskCash extends AsyncTask<String[], Void, String> {

        @Override
        protected String doInBackground(String[]... strings) {
            String[] object = strings[0];
            CashLogs_Database cash = new CashLogs_Database();
            cash.setAccSign(object[1].replaceAll("\n", ""));
            cash.setTreSign(object[2].replaceAll("\n", ""));

            String[] item = object[0].split(";");
            cash.setCountryCode(item[0]);
            cash.setIssuerCode(item[1]);
            cash.setDecisionNo(item[2]);
            cash.setSerialNo(item[3]);
            cash.setParValue(Integer.valueOf(item[4]));
            cash.setActiveDate(item[5]);
            cash.setExpireDate(item[6]);
            cash.setCycle(Integer.valueOf(item[7]));
            cash.setType(Constant.STR_CASH_IN);
            cash.setTransactionSignature(eDongToECashResponse.getId());
            WalletDatabase.getINSTANCE(getApplicationContext(), ECashApplication.masterKey);
            Decision_Database decision = WalletDatabase.getDecisionNo(item[2]);
            if (decision != null) {
                checkVerifyCash(cash, decision.getTreasurePublicKeyValue(), decision.getAccountPublicKeyValue());
            } else {
                getPublicKeyCashToCheck(cash, item[2]);
            }
            return null;
        }
    }

    private void getPublicKeyCashToCheck(CashLogs_Database cash, String decisionNo) {
        Log.e("getPublicKeyCashToCheck", "ahihi");
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyCash requestGetPublicKeyCash = new RequestGetPublicKeyCash();
        requestGetPublicKeyCash.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyCash.setDecisionCode(cash.getDecisionNo());
        requestGetPublicKeyCash.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_CASH);
        requestGetPublicKeyCash.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyCash.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyCash.setToken(CommonUtils.getToken(accountInfo));
        requestGetPublicKeyCash.setUsername(accountInfo.getUsername());
        requestGetPublicKeyCash.setChannelSignature(Constant.STR_EMPTY);

        String alphabe = CommonUtils.getStringAlphabe(requestGetPublicKeyCash);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyCash));
        requestGetPublicKeyCash.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyCash> call = apiService.getPublicKeyCash(requestGetPublicKeyCash);
        call.enqueue(new Callback<ResponseGetPublicKeyCash>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyCash> call, retrofit2.Response<ResponseGetPublicKeyCash> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            ResponseDataGetPublicKeyCash responseGetPublicKeyCash = response.body().getResponseData();
                            saveDecision(decisionNo, responseGetPublicKeyCash);
                            checkVerifyCash(cash, responseGetPublicKeyCash.getDecisionTrekp(),
                                    responseGetPublicKeyCash.getDecisionAcckp());
                        } else {
                            numberRequest = numberRequest + 1;
                            checkArrayCash();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyCash> call, Throwable t) {
                numberRequest = numberRequest + 1;
                checkArrayCash();
            }
        });
    }

    private void saveDecision(String decisionNo, ResponseDataGetPublicKeyCash responseDataGetPublicKeyCash) {
        Decision_Database decision = new Decision_Database();
        decision.setDecisionNo(decisionNo);
        decision.setTreasurePublicKeyValue(responseDataGetPublicKeyCash.getDecisionTrekp());
        decision.setAccountPublicKeyValue(responseDataGetPublicKeyCash.getDecisionAcckp());
        WalletDatabase.getINSTANCE(getApplicationContext(), ECashApplication.masterKey);
        WalletDatabase.insertDecisionTask(decision);
    }

    private void checkVerifyCash(CashLogs_Database cash, String decisionTrekp, String decisionAcckp) {
        if (CommonUtils.verifyCash(cash, decisionTrekp, decisionAcckp)) {
            //xác thực đồng ecash ok => save cash
            DatabaseUtil.saveCashToDB(cash, getApplicationContext(), accountInfo.getUsername());
            numberRequest = numberRequest + 1;
            checkArrayCash();
        } else {
            //lưu vào tien fake
            //todo nothing
            numberRequest = numberRequest + 1;
            checkArrayCash();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
