package vn.ecpay.ewallet.ui.function;

import android.annotation.SuppressLint;
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
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.getPublicKeyCash.RequestGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseDataGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.ui.interfaceListener.CashInSuccessListener;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

import static vn.ecpay.ewallet.common.utils.Constant.TYPE_CASH_EXCHANGE;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_EDONG_TO_ECASH;

public class CashInFunction {
    private CashInResponse cashInResponse;
    private AccountInfo accountInfo;
    private String[][] deCryptECash;
    private Context context;
    private ResponseMessSocket responseMessSocket;
    private String transactionSignature;
    private CashInSuccessListener cashInSuccessListener;

    public CashInFunction(CashInResponse cashInResponse, AccountInfo accountInfo, Context context) {
        this.cashInResponse = cashInResponse;
        this.accountInfo = accountInfo;
        this.context = context;
        this.transactionSignature = cashInResponse.getId();
    }

    public CashInFunction(AccountInfo accountInfo, Context context, ResponseMessSocket responseMessSocket) {
        this.accountInfo = accountInfo;
        this.context = context;
        this.responseMessSocket = responseMessSocket;
        this.transactionSignature = responseMessSocket.getId();
    }

    public void handleCashIn(CashInSuccessListener mCashInSuccessListener) {
        this.cashInSuccessListener = mCashInSuccessListener;
        if (!DatabaseUtil.checkContactExit(context, responseMessSocket.getSender())) {
            requestSearchWalletID(accountInfo, responseMessSocket);
        } else {
            Contact contact = DatabaseUtil.getCurrentContact(context, responseMessSocket.getSender());
            if (contact.getPublicKeyValue().isEmpty()) {
                getPublicKeyWallet(responseMessSocket);
            } else {
                if (CommonUtils.verifyData(responseMessSocket, DatabaseUtil.getCurrentContact(context, responseMessSocket.getSender()).getPublicKeyValue())) {
                    DatabaseUtil.saveTransactionLog(responseMessSocket, context);
                    deCryptECash = CommonUtils.decrypEcash(responseMessSocket.getCashEnc(), KeyStoreUtils.getPrivateKey(context));
                    checkArrayCash();
                } else {
                    EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
                }
            }
        }
    }

    public void handleCash(CashInSuccessListener mCashInSuccessListener) {
        this.cashInSuccessListener = mCashInSuccessListener;
        deCryptECash = CommonUtils.decrypEcash(cashInResponse.getCashEnc(), KeyStoreUtils.getPrivateKey(context));
        DatabaseUtil.saveTransactionLog(cashInResponse, context);
        checkArrayCash();
    }

    private void requestSearchWalletID(AccountInfo accountInfo, ResponseMessSocket responseMess) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(context.getResources().getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_WALLET);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken());
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setWalletId(responseMess.getSender());
        requestGetPublicKeyWallet.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyWallet));
        requestGetPublicKeyWallet.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyWallet> call = apiService.getPublicKeyWallet(requestGetPublicKeyWallet);
        call.enqueue(new Callback<ResponseGetPublicKeyWallet>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyWallet> call, retrofit2.Response<ResponseGetPublicKeyWallet> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (response.body().getResponseCode() != null) {
                            if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                                ResponseDataGetPublicKeyWallet responseDataGetPublicKeyWallet = response.body().getResponseData();
                                Contact contact = new Contact();
                                contact.setFullName(CommonUtils.getFullName(responseDataGetPublicKeyWallet));
                                contact.setPhone(responseDataGetPublicKeyWallet.getPersonMobilePhone());
                                contact.setPublicKeyValue(responseDataGetPublicKeyWallet.getEcKpValue());
                                contact.setWalletId(Long.valueOf(responseMess.getSender()));
                                DatabaseUtil.saveOnlySingleContact(context, contact);
                            }
                        }
                }
                getPublicKeyWallet(responseMess);
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyWallet> call, Throwable t) {
                getPublicKeyWallet(responseMess);
            }
        });
    }

    private void getPublicKeyWallet(ResponseMessSocket responseMess) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(context.getResources().getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_WALLET);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken());
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setWalletId(responseMess.getSender());
        requestGetPublicKeyWallet.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyWallet));
        requestGetPublicKeyWallet.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyWallet> call = apiService.getPublicKeyWallet(requestGetPublicKeyWallet);
        call.enqueue(new Callback<ResponseGetPublicKeyWallet>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyWallet> call, retrofit2.Response<ResponseGetPublicKeyWallet> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            ResponseDataGetPublicKeyWallet responseGetPublicKeyWallet = response.body().getResponseData();
                            String publicKeyWalletReceiver = responseGetPublicKeyWallet.getEcKpValue();
                            if (null != publicKeyWalletReceiver) {
                                if (CommonUtils.verifyData(responseMess, publicKeyWalletReceiver)) {
                                    DatabaseUtil.saveTransactionLog(responseMess, context);
                                    deCryptECash = CommonUtils.decrypEcash(responseMessSocket.getCashEnc(), KeyStoreUtils.getPrivateKey(context));
                                    checkArrayCash();
                                } else {
                                    EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
                                }
                            } else {
                                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
                            }
                        } else {
                            EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyWallet> call, Throwable t) {
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
            }
        });
    }

    private void checkArrayCash() {
        if (null != deCryptECash) {
            if (deCryptECash.length > 0) {
                AsyncTaskCash asynctaskCash = new AsyncTaskCash();
                asynctaskCash.execute(deCryptECash);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
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
            if (null != cashInSuccessListener) {
                cashInSuccessListener.onCashInSuccess();
            }
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
            if(accountInfo.getUsername()==null){
                accountInfo = CommonUtils.getAccountByUserName(context);
            }
            DatabaseUtil.saveCashToDB(cash, context, accountInfo.getUsername());
        } else {
            //lưu vào tien fake
            DatabaseUtil.SaveCashInvalidToDB(cash, context, accountInfo.getUsername());
            //todo nothing
        }
    }
}
