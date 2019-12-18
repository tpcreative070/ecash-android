package vn.ecpay.ewallet.ui.function;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class CashInFunction {
    private CashInResponse eDongToECashResponse;
    private AccountInfo accountInfo;
    private String[][] deCryptECash;
    private Context context;
    private ResponseMessSocket responseMessSocket;
    private String transactionSignature;

    public CashInFunction(CashInResponse eDongToECashResponse, AccountInfo accountInfo, Context context) {
        this.eDongToECashResponse = eDongToECashResponse;
        this.accountInfo = accountInfo;
        this.context = context;
        this.transactionSignature = eDongToECashResponse.getId();
    }

    public CashInFunction(AccountInfo accountInfo, Context context, ResponseMessSocket responseMessSocket) {
        this.accountInfo = accountInfo;
        this.context = context;
        this.responseMessSocket = responseMessSocket;
        this.transactionSignature = responseMessSocket.getId();
    }

    public void handleCashSocket() {
        deCryptECash = CommonUtils.decrypEcash(responseMessSocket.getCashEnc(), KeyStoreUtils.getPrivateKey(context));
        checkArrayCash();
    }

    public void handleCash() {
        deCryptECash = CommonUtils.decrypEcash(eDongToECashResponse.getCashEnc(), KeyStoreUtils.getPrivateKey(context));
        checkArrayCash();
    }

    private void checkArrayCash() {
        if (null != deCryptECash) {
            if (deCryptECash.length > 0) {
                AsyncTaskCash asynctaskCash = new AsyncTaskCash();
                asynctaskCash.execute(deCryptECash);
            }
        }
    }

    private class AsyncTaskCash extends AsyncTask<String[][], Void, Void> {
        @Override
        protected Void doInBackground(String[][]... params) {
            String[][] deCryptArray = params[0];
            for (String[] object : deCryptArray) {
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
                cash.setTransactionSignature(transactionSignature);
                WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
                Decision_Database decision = WalletDatabase.getDecisionNo(item[2]);
                if (decision != null) {
                    checkVerifyCash(cash, decision.getTreasurePublicKeyValue(), decision.getAccountPublicKeyValue());
                } else {
                    getPublicKeyCashToCheck(cash, item[2]);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_MONEY));
        }
    }

    private void getPublicKeyCashToCheck(CashLogs_Database cash, String decisionNo) {
        Log.e("getPublicKeyCashToCheck", "ahihi");
        Retrofit retrofit = RetroClientApi.getRetrofitClient(context.getResources().getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyCash requestGetPublicKeyCash = new RequestGetPublicKeyCash();
        requestGetPublicKeyCash.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyCash.setDecisionCode(cash.getDecisionNo());
        requestGetPublicKeyCash.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_CASH);
        requestGetPublicKeyCash.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyCash.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyCash.setToken(CommonUtils.getToken());
        requestGetPublicKeyCash.setUsername(accountInfo.getUsername());
        requestGetPublicKeyCash.setAuditNumber(CommonUtils.getAuditNumber());
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
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyCash> call, Throwable t) {
            }
        });
    }

    private void saveDecision(String decisionNo, ResponseDataGetPublicKeyCash responseDataGetPublicKeyCash) {
        Decision_Database decision = new Decision_Database();
        decision.setDecisionNo(decisionNo);
        decision.setTreasurePublicKeyValue(responseDataGetPublicKeyCash.getDecisionTrekp());
        decision.setAccountPublicKeyValue(responseDataGetPublicKeyCash.getDecisionAcckp());
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertDecisionTask(decision);
    }

    private void checkVerifyCash(CashLogs_Database cash, String decisionTrekp, String decisionAcckp) {
        if (CommonUtils.verifyCash(cash, decisionTrekp, decisionAcckp)) {
            //xác thực đồng ecash ok => save cash
            DatabaseUtil.saveCashToDB(cash, context, accountInfo.getUsername());
        } else {
            //lưu vào tien fake
            DatabaseUtil.SaveCashInvalidToDB(cash, context, accountInfo.getUsername());
            //todo nothing
        }
    }
}
