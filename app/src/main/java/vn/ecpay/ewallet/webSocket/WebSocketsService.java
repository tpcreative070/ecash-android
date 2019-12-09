package vn.ecpay.ewallet.webSocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.getPublicKeyCash.RequestGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseDataGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.webSocket.object.RequestReceived;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class WebSocketsService extends Service {
    private AccountInfo accountInfo;
    private int numberRequest;
    private String[][] deCryptECash;
    private String transactionSignature;
    private long totalMoney;
    private boolean isConnectSuccess;
    private ResponseDataGetPublicKeyWallet responseGetPublicKeyWallet;
    private WebSocket webSocketLocal;

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
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.UPDATE_ACCOUNT_LOGIN)) {
            Log.e("event", "service_UPDATE_ACCOUNT_LOGIN");
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (ECashApplication.getAccountInfo() != null) {
                    AccountInfo dbAccountInfo = DatabaseUtil.getAccountInfo(ECashApplication.getAccountInfo().getUsername(), getApplicationContext());
                    if (dbAccountInfo != null) {
                        startSocket();
                    }
                }
            }, 500);
        }

        if (event.getData().equals(Constant.EVENT_NETWORK_CHANGE)) {
            Log.e("event", "service_EVENT_NETWORK_CHANGE");
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (!isConnectSuccess) {
                    if (ECashApplication.getAccountInfo() != null) {
                        AccountInfo dbAccountInfo = DatabaseUtil.getAccountInfo(ECashApplication.getAccountInfo().getUsername(), getApplicationContext());
                        if (dbAccountInfo != null) {
                            startSocket();
                        }
                    }
                }
            }, 2000);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    public void startSocket() {
        Log.e("start_SK", "start_SK");
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getApplicationContext());
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, getApplicationContext());
        Request requestCoinPrice = new Request.Builder().url(url).build();
        webSocketLocal = client.newWebSocket(requestCoinPrice, webSocketListener);
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
            ResponseMessSocket responseMess = new Gson().fromJson(data, ResponseMessSocket.class);
            if (responseMess != null) {
                if (responseMess.getType().equals(Constant.TYPE_ECASH_TO_ECASH)) {
                    if (!DatabaseUtil.isTransactionLogExit(responseMess, getApplicationContext())) {
                        if (responseMess.getCashEnc() != null) {
                            getPublicKeyWallet(responseMess);
                        }
                    }
                } else if (responseMess.getType().equals(Constant.TYPE_SYNC_CONTACT)) {
                    RequestReceived requestReceived = new RequestReceived();
                    requestReceived.setReceiver(responseMess.getReceiver());
                    requestReceived.setRefId(responseMess.getRefId());
                    requestReceived.setType(Constant.TYPE_SEN_SOCKET);

                    Gson gson = new Gson();
                    String json = gson.toJson(requestReceived);
                    webSocket.send(json);

                    //SAVE CONTACT TO DATABASE
                    DatabaseUtil.saveListContact(getApplicationContext(), responseMess.getContacts());
                } else if (responseMess.getType().equals(Constant.TYPE_CANCEL_CONTACT)) {
                    String walletIDContactCancel = responseMess.getSender();
                    DatabaseUtil.updateStatusContact(getApplicationContext(), Constant.CONTACT_OFF, Long.valueOf(walletIDContactCancel));
                }
            }
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

    private void confirmMess(ResponseMessSocket responseMess) {
        RequestReceived requestReceived = new RequestReceived();
        requestReceived.setId(responseMess.getId());
        requestReceived.setReceiver(responseMess.getReceiver());
        requestReceived.setRefId(responseMess.getRefId());
        requestReceived.setType(Constant.TYPE_SEN_SOCKET);

        Gson gson = new Gson();
        String json = gson.toJson(requestReceived);
        webSocketLocal.send(json);
    }

    private void getPublicKeyWallet(ResponseMessSocket responseMess) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getString(R.string.api_base_url));
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
                            responseGetPublicKeyWallet = response.body().getResponseData();
                            String publicKeyWalletReceiver = responseGetPublicKeyWallet.getEcKpValue();
                            if (null != publicKeyWalletReceiver) {
                                if (CommonUtils.verifyData(responseMess, publicKeyWalletReceiver)) {
                                    //deCrypt dong eCash
                                    transactionSignature = responseMess.getId();
                                    deCryptECash = CommonUtils.decrypEcash(responseMess.getCashEnc(), KeyStoreUtils.getPrivateKey(getApplicationContext()));
                                    if (deCryptECash != null) {
                                        DatabaseUtil.saveTransactionLog(responseMess, getApplicationContext());
                                        numberRequest = 0;
                                        checkArrayCash(responseMess);
                                        confirmMess(responseMess);
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
                    putNotificationMoneyChange(responseMess);
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

    private void putNotificationMoneyChange(ResponseMessSocket responseMess) {
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
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getString(R.string.api_base_url));
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
            DatabaseUtil.saveCashToDB(cash, getApplicationContext(), accountInfo.getUsername());

            //next
            numberRequest = numberRequest + 1;
            checkArrayCash(responseMess);
        } else {
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
