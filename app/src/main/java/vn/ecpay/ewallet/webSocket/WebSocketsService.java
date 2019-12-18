package vn.ecpay.ewallet.webSocket;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.ui.function.CashInFunction;
import vn.ecpay.ewallet.webSocket.object.RequestReceived;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class WebSocketsService extends Service {
    private AccountInfo accountInfo;
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
                            requestSearchWalletID(accountInfo, responseMess);
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

    public void requestSearchWalletID(AccountInfo accountInfo, ResponseMessSocket responseMess) {
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
                    if (response.body() != null)
                        if (response.body().getResponseCode() != null) {
                            if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                                ResponseDataGetPublicKeyWallet responseDataGetPublicKeyWallet = response.body().getResponseData();
                                Contact contact = new Contact();
                                contact.setFullName(CommonUtils.getFullName(responseDataGetPublicKeyWallet));
                                contact.setPhone(responseDataGetPublicKeyWallet.getPersonMobilePhone());
                                contact.setPublicKeyValue(responseDataGetPublicKeyWallet.getEcKpValue());
                                contact.setWalletId(Long.valueOf(responseMess.getSender()));
                                DatabaseUtil.saveOnlySingleContact(getApplicationContext(), contact);
                                getPublicKeyWallet(responseMess);
                            }
                        }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyWallet> call, Throwable t) {
            }
        });
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
                                    DatabaseUtil.saveTransactionLog(responseMess, getApplicationContext());
                                    confirmMess(responseMess);
                                    //deCrypt dong eCash
                                    CashInFunction cashInFunction = new CashInFunction(accountInfo, getApplicationContext(), responseMess);
                                    cashInFunction.handleCashSocket();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
