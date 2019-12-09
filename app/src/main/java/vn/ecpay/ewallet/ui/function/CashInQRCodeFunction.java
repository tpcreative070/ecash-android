package vn.ecpay.ewallet.ui.function;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

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
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.getPublicKeyCash.RequestGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseDataGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class CashInQRCodeFunction {
    private Context context;
    private AccountInfo accountInfo;
    private ArrayList<QRCashTransfer> qrCashTransferList;
    private ResponseMessSocket cashMess;
    private ResponseDataGetPublicKeyWallet responseGetPublicKeyWallet;
    private String transactionSignature;
    private String[][] deCryptECash;
    private int numberRequest;
    private long totalMoney;

    public CashInQRCodeFunction(Context context, ResponseMessSocket cashMess) {
        this.context = context;
        this.cashMess = cashMess;
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, context);
    }

    public void handleCashInQRCode() {
        qrCashTransferList = new ArrayList<>();
        if (cashMess != null) {
            getPublicKeyWallet(cashMess);
        }
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
                            responseGetPublicKeyWallet = response.body().getResponseData();
                            String publicKeyWalletReceiver = responseGetPublicKeyWallet.getEcKpValue();
                            if (null != publicKeyWalletReceiver) {
                                if (CommonUtils.verifyData(responseMess, publicKeyWalletReceiver)) {
                                    //deCrypt dong eCash
                                    transactionSignature = responseMess.getId();
                                    deCryptECash = CommonUtils.decrypEcash(responseMess.getCashEnc(), KeyStoreUtils.getPrivateKey(context));
                                    if (deCryptECash != null) {
                                        DatabaseUtil.saveTransactionLog(responseMess, context);
                                        numberRequest = 0;
                                        checkArrayCash(responseMess);
                                    } else {
                                        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
                                    }
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

    private void checkArrayCash(ResponseMessSocket responseMess) {
        if (deCryptECash != null) {
            if (deCryptECash.length > 0) {
                if (numberRequest == deCryptECash.length) {
                    qrCashTransferList.get(0).setTotalMoney(String.valueOf(totalMoney));
                    EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_QR_CODE_SCAN_CASH_SUCCESS, qrCashTransferList));
                    EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_MONEY_SOCKET));

                    totalMoney = 0;
                    numberRequest = 0;
                    deCryptECash = null;
                    return;
                }
                splitData(deCryptECash[numberRequest], responseMess);
            }
        }
    }

    private void splitData(String[] object, ResponseMessSocket responseMess) {
        CashLogs_Database cash = new CashLogs_Database();
        cash.setAccSign(object[1].replaceAll("\n", ""));
        cash.setTreSign(object[2].replaceAll("\n", ""));

        String[] item = object[0].split(";");
        totalMoney = totalMoney + Integer.valueOf(item[4]);
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
        getPublicKeyCashToCheck(cash, responseMess);
    }

    private void getPublicKeyCashToCheck(CashLogs_Database cash, ResponseMessSocket responseMess) {
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
        requestGetPublicKeyCash.setChannelSignature(Constant.STR_EMPTY);
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
                            checkVerifyCash(cash, responseGetPublicKeyCash, responseMess);
                        } else {
                            numberRequest = numberRequest + 1;
                            checkArrayCash(responseMess);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyCash> call, Throwable t) {
                numberRequest = numberRequest + 1;
                checkArrayCash(responseMess);
            }
        });
    }

    private void checkVerifyCash(CashLogs_Database cash, ResponseDataGetPublicKeyCash responseDataGetPublicKeyCash, ResponseMessSocket responseMess) {
        if (CommonUtils.verifyCash(cash, responseDataGetPublicKeyCash.getDecisionTrekp(),
                responseDataGetPublicKeyCash.getDecisionAcckp())) {
            //xác thực đồng ecash ok => save cash
            DatabaseUtil.saveCashToDB(cash, context, accountInfo.getUsername());

            //put info to result
            qrCashTransferList.add(CommonUtils.getQrCashTransfer(cash, responseMess, responseGetPublicKeyWallet, true));

            //next
            numberRequest = numberRequest + 1;
            checkArrayCash(responseMess);
        } else {
            //put info to result
            qrCashTransferList.add(CommonUtils.getQrCashTransfer(cash, responseMess, responseGetPublicKeyWallet, false));

            //lưu vào tien fake
            DatabaseUtil.SaveCashInvalidToDB(cash, context, accountInfo.getUsername());

            //chạy thằng tiếp theo
            numberRequest = numberRequest + 1;
            checkArrayCash(responseMess);
        }
    }

}
