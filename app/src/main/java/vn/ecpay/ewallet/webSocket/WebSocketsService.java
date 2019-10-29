package vn.ecpay.ewallet.webSocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
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
import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.getPublicKeyCash.RequestGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseDataGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.webSocket.object.RequestReceived;
import vn.ecpay.ewallet.webSocket.object.ResponseCashMess;

public class WebSocketsService extends Service {
    private AccountInfo accountInfo;
    private int numberRequest;
    private String[][] deCryptECash;
    private String transactionSignature;
    private long totalMoney;
    private boolean isConnectSuccess;
    private boolean isQRCode;
    private ArrayList<QRCashTransfer> qrCashTransferList;
    private ResponseDataGetPublicKeyWallet responseGetPublicKeyWallet;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
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
            String userName = ECashApplication.getAccountInfo().getUsername();
            accountInfo = DatabaseUtil.getAccountInfo(userName, getApplicationContext());
            isQRCode = extras.getBoolean(Constant.IS_QR_CODE);
            if (isQRCode) {//cash to eCash by QR_code
                qrCashTransferList = new ArrayList<>();
                ResponseCashMess cashMess = (ResponseCashMess) extras.getSerializable(Constant.RESPONSE_CASH_MESS);
                if (cashMess != null) {
                    getPublicKeyWallet(cashMess);
                }
            } else {//cash to cash by
                int sl500 = extras.getInt(Constant.TOTAL_500);
                int sl200 = extras.getInt(Constant.TOTAL_200);
                int sl100 = extras.getInt(Constant.TOTAL_100);
                int sl50 = extras.getInt(Constant.TOTAL_50);
                int sl20 = extras.getInt(Constant.TOTAL_20);
                int sl10 = extras.getInt(Constant.TOTAL_10);
                String keyPublicReceiver = extras.getString(Constant.KEY_PUBLIC_RECEIVER);
                String walletReceiver = extras.getString(Constant.WALLET_RECEIVER);
                String contentSendMoney = extras.getString(Constant.CONTENT_SEND_MONEY);
                isConnectSuccess = false;

                OkHttpClient client = new OkHttpClient();
                String url = SocketUtil.getUrl(accountInfo, getApplicationContext()).replaceAll("%20", "+");
                Request requestCoinPrice = new Request.Builder().url(url).build();
                WebSocket webSocket = client.newWebSocket(requestCoinPrice, webSocketListener);
                client.dispatcher().executorService().shutdown();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (isConnectSuccess) {
                            webSocket.send(getObjectJsonSend(sl10, sl20, sl50, sl100, sl200, sl500,
                                    keyPublicReceiver, walletReceiver, contentSendMoney));
                            Log.e("send_money", "send money ok");
                        } else {
                            Log.e("connect_socket", "connect socket fail");
                            EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CONNECT_SOCKET_FAIL));
                        }
                    }
                }, 2000);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private String getObjectJsonSend(int sl10, int sl20, int sl50, int sl100, int sl200, int sl500,
                                     String keyPublicReceiver, String walletReceiver, String contentSendMoney) {
        WalletDatabase.getINSTANCE(getApplicationContext(), KeyStoreUtils.getMasterKey(getApplicationContext()));
        ArrayList<CashLogs> listCashSend = new ArrayList<>();
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
            ResponseCashMess responseMess = new ResponseCashMess();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(walletReceiver);
            responseMess.setTime(CommonUtils.getCurrentTime());
            responseMess.setType(Constant.TYPE_ECASH_TO_ECASH);
            responseMess.setContent(contentSendMoney);
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getIdSender(responseMess, getApplicationContext()));
            Gson gson = new Gson();
            String json = gson.toJson(responseMess);
            if (!json.isEmpty()) {
                DatabaseUtil.updateTransactionsLogAndCashOutDatabase(listCashSend, responseMess, getApplicationContext(), accountInfo.getUsername());
                EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
            }
            return json;
        }
        return Constant.STR_EMPTY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        Log.e("event", "service");
        if (event.getData().equals(Constant.UPDATE_ACCOUNT_LOGIN)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (ECashApplication.getAccountInfo() != null) {
                        AccountInfo dbAccountInfo = DatabaseUtil.getAccountInfo(ECashApplication.getAccountInfo().getUsername(), getApplicationContext());
                        if (dbAccountInfo != null) {
                            startSocket();
                        }
                    }
                }
            }, 500);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    public void startSocket() {
        Log.e("start_SK", "start_SK");
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getApplicationContext());
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, getApplicationContext()).replaceAll("%20", "+");
        Request requestCoinPrice = new Request.Builder().url(url).build();
        client.newWebSocket(requestCoinPrice, webSocketListener);
        client.dispatcher().executorService().shutdown();
    }

    //socket
    WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            isConnectSuccess = true;
            Log.e("onOpen", "onOpen");
        }

        @Override
        public void onMessage(WebSocket webSocket, String data) {
            Log.e("onMessage", data);
            ResponseCashMess responseMess = new Gson().fromJson(data, ResponseCashMess.class);
            if (responseMess != null) {
                RequestReceived requestReceived = new RequestReceived();
                requestReceived.setId(responseMess.getId());
                requestReceived.setReceiver(responseMess.getReceiver());
                requestReceived.setRefId(responseMess.getRefId());
                requestReceived.setType(Constant.TYPE_SEN_SOCKET);

                Gson gson = new Gson();
                String json = gson.toJson(requestReceived);
                Log.e("confirm", json);
                webSocket.send(json);
            }
            //save transaction log
            if (!DatabaseUtil.isTransactionLogExit(responseMess, getApplicationContext())) {
                DatabaseUtil.saveTransactionLog(responseMess, getApplicationContext());
                if (responseMess.getType().equals("CT")) {
                    if (responseMess.getCashEnc() != null) {
                        getPublicKeyWallet(responseMess);
                    }
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.d("onMessage", "MESSAGE: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            webSocket.cancel();
            Log.d("onClosing", "CLOSE: " + code + " " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.e("onClosed", reason);
            webSocket.close(1000, null);
            webSocket.cancel();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            isConnectSuccess = false;
            Log.e("onFailure", "SocKet_onFailure");
            t.printStackTrace();
        }
    };

    private void getPublicKeyWallet(ResponseCashMess responseMess) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_WALLET);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken(accountInfo));
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setWalletId(responseMess.getSender());
        requestGetPublicKeyWallet.setChannelSignature("");

        String alphabe = CommonUtils.getStringAlphabe(requestGetPublicKeyWallet);
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
                            if (CommonUtils.verifyData(responseMess, publicKeyWalletReceiver)) {
                                //deCrypt dong eCash
                                transactionSignature = responseMess.getId();
                                deCryptECash = CommonUtils.decrypEcash(responseMess.getCashEnc(), KeyStoreUtils.getPrivateKey(getApplicationContext()));
                                if (deCryptECash != null) {
                                    numberRequest = 0;
                                    checkArrayCash(responseMess);
                                } else {
                                    EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
                                }
                            } else {
                                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_VERIFY_CASH_FAIL));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyWallet> call, Throwable t) {
                Log.e("getPublicKey", "fail");
            }
        });
    }

    private void checkArrayCash(ResponseCashMess responseMess) {
        if (deCryptECash != null) {
            if (deCryptECash.length > 0) {
                if (numberRequest == deCryptECash.length) {
                    putNotificationMoneyChange(responseMess);
                    if (isQRCode) {
                        //put event updatePreviousCash data
                        qrCashTransferList.get(0).setTotalMoney(String.valueOf(totalMoney));
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_QR_CODE_SCAN_CASH_SUCCESS, qrCashTransferList));
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_MONEY_SOCKET));
                    } else {
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_MONEY_SOCKET));
                    }
                    totalMoney = 0;
                    numberRequest = 0;
                    deCryptECash = null;
                    return;
                }
                splitData(deCryptECash[numberRequest], responseMess);
            }
        }
    }

    private void putNotificationMoneyChange(ResponseCashMess responseMess) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "_channel_01";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, "notification", importance);
            mChannel.enableLights(true);

            Notification notification = new Notification.Builder(getApplicationContext(), id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("eCash")
                    .setContentText("Bạn đã được chuyển " + CommonUtils.formatPriceVND(totalMoney) + " từ tài khoản: " + responseMess.getSender())
                    .setAutoCancel(true)
                    .build();
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(1, notification);
            }
        }
    }

    private void splitData(String[] object, ResponseCashMess responseMess) {
        CashLogs cash = new CashLogs();
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

    private void getPublicKeyCashToCheck(CashLogs cash, ResponseCashMess responseMess) {
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

    private void checkVerifyCash(CashLogs cash, ResponseDataGetPublicKeyCash responseDataGetPublicKeyCash, ResponseCashMess responseMess) {
        if (CommonUtils.verifyCash(cash, responseDataGetPublicKeyCash.getDecisionTrekp(),
                responseDataGetPublicKeyCash.getDecisionAcckp())) {
            //xác thực đồng ecash ok => save cash
            //put info to result
            DatabaseUtil.saveCashToDB(cash, getApplicationContext(), accountInfo.getUsername());
            if (isQRCode) {
                qrCashTransferList.add(CommonUtils.getQrCashTransfer(cash, responseMess, responseGetPublicKeyWallet, true));
            }

            //next
            numberRequest = numberRequest + 1;
            checkArrayCash(responseMess);
        } else {
            //put info to result
            if (isQRCode) {
                qrCashTransferList.add(CommonUtils.getQrCashTransfer(cash, responseMess, responseGetPublicKeyWallet, false));
            }
            //lưu vào tien fake
            DatabaseUtil.SaveCashInvalidToDB(cash, getApplicationContext(), accountInfo.getUsername());
            //chạy thằng tiếp theo
            numberRequest = numberRequest + 1;
            checkArrayCash(responseMess);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
