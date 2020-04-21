package vn.ecpay.ewallet.webSocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.table.Payment_DataBase;
import vn.ecpay.ewallet.model.account.cacheData.CacheSocketData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.lixi.CashTemp;
import vn.ecpay.ewallet.model.payment.Payments;
import vn.ecpay.ewallet.ui.callbackListener.CashInSuccessListener;
import vn.ecpay.ewallet.ui.callbackListener.UpdateMasterKeyListener;
import vn.ecpay.ewallet.ui.cashIn.CashInActivity;
import vn.ecpay.ewallet.ui.function.CashInFunction;
import vn.ecpay.ewallet.ui.function.UpdateMasterKeyFunction;
import vn.ecpay.ewallet.webSocket.object.RequestReceived;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

import static vn.ecpay.ewallet.ECashApplication.getActivity;

public class WebSocketsService extends Service {
    private AccountInfo accountInfo;
    private boolean isConnectSuccess = false;
    private WebSocket webSocketLocal;
    private List<CacheSocketData> listResponseMessSockets;
    private boolean isRunning = false;

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
                    AccountInfo dbAccountInfo = DatabaseUtil.getAccountInfo(getApplicationContext());
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
                        AccountInfo dbAccountInfo = DatabaseUtil.getAccountInfo(getApplicationContext());
                        if (dbAccountInfo != null) {
                            startSocket();
                        }
                    }
                }
            }, 5000);
        }
        if (event.getData().equals(Constant.EVENT_PAYMENT_SUCCESS) || event.getData().equals(Constant.EVENT_SEND_REQUEST_PAYTO)) {
            Log.e("event", "EVENT_PAYMENT_SUCCESS or EVENT_SEND_REQUEST_PAYTO");
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (ECashApplication.getAccountInfo() != null) {
                    AccountInfo dbAccountInfo = DatabaseUtil.getAccountInfo(getApplicationContext());
                    if (dbAccountInfo != null) {
                        startSocket();
                    }
                }
            }, 500);
        }
        if (event.getData().equals(Constant.EVENT_CLOSE_SOCKET)) {
            stopSocket();
        }

        if (event.getData().equals(Constant.EVENT_VERIFY_CASH_FAIL)) {

        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    public void startSocket() {
        isRunning = false;
        Log.e("start_SK", "start_SK");
        accountInfo = DatabaseUtil.getAccountInfo(getApplicationContext());
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, getApplicationContext());
        Request requestCoinPrice = new Request.Builder().url(url).build();
        webSocketLocal = client.newWebSocket(requestCoinPrice, webSocketListener);
        client.dispatcher().executorService().shutdown();
    }

    private void stopSocket() {
        if (webSocketListener != null && webSocketLocal != null) {
            webSocketListener.onClosed(webSocketLocal, 1000, "stop");
        }
    }

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
                switch (responseMess.getType()) {
                    case Constant.TYPE_ECASH_TO_ECASH:
                    case Constant.TYPE_PAYTO:
                        DatabaseUtil.insertOnlySingleCacheSocketData(responseMess, getApplicationContext());
                        if (!isRunning) {
                            isRunning = true;
                            updateMasterKey();
                        }
                        break;
                    case Constant.TYPE_LIXI:
                        if (!DatabaseUtil.isCashTempExit(responseMess, getApplicationContext())) {
                            if (responseMess.getCashEnc() != null) {
                                Gson gson = new Gson();
                                String json = gson.toJson(responseMess);

                                CashTemp cashTemp = new CashTemp();
                                cashTemp.setContent(json);
                                cashTemp.setSenderAccountId(responseMess.getSender());
                                cashTemp.setTransactionSignature(responseMess.getId());
                                DatabaseUtil.saveCashTemp(cashTemp, getApplicationContext());
                                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_LIXI));
                                confirmMess(responseMess);
                            }
                        }
                        break;
                    case Constant.TYPE_SYNC_CONTACT:
                        webSocket.send(getJsonSend(responseMess));
                        DatabaseUtil.saveListContact(getApplicationContext(), responseMess.getContacts());
                        confirmMess(responseMess);
                        break;
                    case Constant.TYPE_CANCEL_CONTACT:
                        String walletIDContactCancel = responseMess.getSender();
                        DatabaseUtil.updateStatusContact(getApplicationContext(), Constant.CONTACT_OFF, Long.valueOf(walletIDContactCancel));
                        confirmMess(responseMess);
                        break;
                    case Constant.TYPE_TOPAY:
                        Payments topayResponse = new Gson().fromJson(data, Payments.class);
                        handlePaymentRequest(topayResponse);
                        confirmMess(responseMess);
                        //webSocket.send(getJsonSend(responseMess));
                        break;
                }
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            webSocket.cancel();
            Log.e("onClosing", "CLOSE: " + code + " " + reason);
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
            EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CONNECT_SOCKET_FAIL));
            Log.e("onFailure", "SocKet_onFailure");
            t.printStackTrace();
        }
    };

    private String getJsonSend(ResponseMessSocket responseMess) {
        RequestReceived requestReceived = new RequestReceived();
        requestReceived.setReceiver(responseMess.getReceiver());
        requestReceived.setRefId(responseMess.getRefId());
        requestReceived.setType(Constant.TYPE_SEN_SOCKET);

        Gson gson = new Gson();
        return gson.toJson(requestReceived);
    }

    private void updateMasterKey() {
        UpdateMasterKeyFunction updateMasterKeyFunction = new UpdateMasterKeyFunction(getApplicationContext());
        updateMasterKeyFunction.updateLastTimeAndMasterKey(new UpdateMasterKeyListener() {
            @Override
            public void onUpdateMasterSuccess() {
                syncData();
            }

            @Override
            public void onUpdateMasterFail(String code) {
                isRunning = false;
            }

            @Override
            public void onRequestTimeout() {
                isRunning = false;
            }
        });
    }

    private void syncData() {
        listResponseMessSockets = DatabaseUtil.getAllCacheSocketData(getApplicationContext());
        if (listResponseMessSockets.size() > 0) {
            handleListResponse();
        } else {
            EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_SUCCESS));
            isRunning = false;
        }
    }

    private void handleListResponse() {
        if (listResponseMessSockets.size() > 0) {
            if (!DatabaseUtil.isTransactionLogExit(listResponseMessSockets.get(0).getId(), getApplicationContext())) {
                if (listResponseMessSockets.get(0).getCashEnc() != null) {
                    CashInFunction cashInFunction = new CashInFunction(accountInfo, getApplicationContext(), listResponseMessSockets.get(0));
                    cashInFunction.handleCashIn(new CashInSuccessListener() {
                        @Override
                        public void onCashInSuccess(Long totalMoney) {
                            if (listResponseMessSockets.size() > 0) {
                                putNotificationMoneyChange(listResponseMessSockets.get(0), totalMoney);
                                confirmMess(listResponseMessSockets.get(0));
                                DatabaseUtil.deleteCacheSocketData(listResponseMessSockets.get(0).getId(), getApplicationContext());
                                listResponseMessSockets.remove(0);
                            }
                            handleListResponse();
                        }

                        @Override
                        public void onCashInFail() {
                            if (listResponseMessSockets.size() > 0) {
                                DatabaseUtil.deleteCacheSocketData(listResponseMessSockets.get(0).getId(), getApplicationContext());
                                listResponseMessSockets.remove(0);
                            }
                            handleListResponse();
                        }
                    });
                } else {
                    if (listResponseMessSockets.size() > 0) {
                        DatabaseUtil.deleteCacheSocketData(listResponseMessSockets.get(0).getId(), getApplicationContext());
                        listResponseMessSockets.remove(0);
                    }
                    handleListResponse();
                }
            } else {
                if (listResponseMessSockets.size() > 0) {
                    DatabaseUtil.deleteCacheSocketData(listResponseMessSockets.get(0).getId(), getApplicationContext());
                    confirmMess(listResponseMessSockets.get(0));
                    listResponseMessSockets.remove(0);
                }
                handleListResponse();
            }
        } else {
            syncData();
        }
    }

    private void putNotificationMoneyChange(CacheSocketData responseMess, Long totalMoney) {
        Contact contact = DatabaseUtil.getCurrentContact(getApplicationContext(), responseMess.getSender());
        String message = "";
        if (null != contact) {
            message = "Quý khách đã nhận được số tiền " + CommonUtils.formatPriceVND(totalMoney)
                    + " từ số ví: " + responseMess.getSender() + " (" + contact.getFullName() + ")";
        } else {
            message = "Quý khách đã nhận được số tiền " + CommonUtils.formatPriceVND(totalMoney)
                    + " từ số ví: " + responseMess.getSender();
        }
        DatabaseUtil.saveNotification(getApplicationContext(), "Thông báo biến động số dư", message);
        EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_NOTIFICATION));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "_channel_01";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, "notification", importance);
            mChannel.enableLights(true);

            Notification notification = new Notification.Builder(getApplicationContext(), id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Nhận tiền thành công")
                    .setContentText("Quý khách đã nhận được số tiền " + CommonUtils.formatPriceVND(totalMoney) + " từ số ví: " + responseMess.getSender())
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .build();
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(1, notification);
            }
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setContentTitle("Nhận tiền thành công");
            notification.setContentText(message);
            notification.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(1, notification.build());
        }
    }

    private void confirmMess(CacheSocketData responseMess) {
        Log.e("confirmMess", "confirmMess socket cache");
        RequestReceived requestReceived = new RequestReceived();
        requestReceived.setId(responseMess.getId());
        requestReceived.setReceiver(responseMess.getReceiver());
        requestReceived.setRefId(responseMess.getRefId());
        requestReceived.setType(Constant.TYPE_SEN_SOCKET);

        Gson gson = new Gson();
        String json = gson.toJson(requestReceived);
        webSocketLocal.send(json);
    }

    private void confirmMess(ResponseMessSocket responseMess) {
        Log.e("confirmMess", "confirmMess");
        RequestReceived requestReceived = new RequestReceived();
        requestReceived.setId(responseMess.getId());
        requestReceived.setReceiver(responseMess.getReceiver());
        requestReceived.setRefId(responseMess.getRefId());
        requestReceived.setType(Constant.TYPE_SEN_SOCKET);

        Gson gson = new Gson();
        String json = gson.toJson(requestReceived);
        webSocketLocal.send(json);
    }


    private void handlePaymentRequest(Payments payToRequest) {
        Payment_DataBase payment_dataBase = new Payment_DataBase();
        payment_dataBase.setSender(payToRequest.getSender());
        payment_dataBase.setTime(payToRequest.getTime());
        payment_dataBase.setType(payToRequest.getType());
        payment_dataBase.setContent(payToRequest.getContent());
        payment_dataBase.setSenderPublicKey(payToRequest.getSenderPublicKey());
        payment_dataBase.setTotalAmount(payToRequest.getTotalAmount());
        payment_dataBase.setChannelSignature(payToRequest.getChannelSignature());
        payment_dataBase.setFullName(payToRequest.getFullName());
        payment_dataBase.setToPay(true);

        DatabaseUtil.insertPayment(getApplicationContext(), payment_dataBase);
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_NEW_PAYMENT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.e("Soccket", "Socket server tèo rồi T_T");
        EventBus.getDefault().unregister(this);
    }
}
