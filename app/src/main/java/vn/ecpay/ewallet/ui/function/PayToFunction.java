package vn.ecpay.ewallet.ui.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.payment.Payments;
import vn.ecpay.ewallet.ui.callbackListener.PayToListener;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class PayToFunction {
    private AccountInfo accountInfo;
    private Context context;
    private String content;
    private List<Contact> multiTransferList;
    private long totalAmount;
    private String type;
    private PayToListener payToListener;

    public PayToFunction(Context context, long total, List<Contact> multiTransferList, String content, String type) {
        this.context = context;
        this.totalAmount = total;
        this.multiTransferList = multiTransferList;
        this.content = content;
        this.type = type;
        accountInfo = DatabaseUtil.getAccountInfo(context);
    }

    public void handlePayToSocket(PayToListener payToListener){
        this.payToListener =payToListener;
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, context);
       Log.e("payto url ",url);
        Request requestCoinPrice = new Request.Builder().url(url).build();
        client.newWebSocket(requestCoinPrice, new WebSocketListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                Log.e("payto funtion", "payto funtion");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Gson gson = new Gson();
                        for (int i = 0; i < multiTransferList.size(); i++) {
                            String jsonSend = gson.toJson(getObjectJsonSend(multiTransferList.get(i), i));
                            webSocket.send(jsonSend);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
//                        Log.e("response ",response.toString());
//                        Log.e("response ",response.body().);
                        payToListener.onPayToSuccess();
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.PAYTO_SUCCESS));
                    }
                }.execute();
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @NotNull Response response) {
                Log.e("connect_socket", "connect socket fail");
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CONNECT_SOCKET_FAIL));
            }
        });
        client.dispatcher().executorService().shutdown();
    }

    private Payments getObjectJsonSend(Contact contact, int index) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));

        Payments payToRequest = new Payments();
        payToRequest.setSender(String.valueOf(accountInfo.getWalletId()));
        payToRequest.setReceiver(String.valueOf(contact.getWalletId()));
        payToRequest.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        payToRequest.setType(type);
        payToRequest.setContent(content);
        payToRequest.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        payToRequest.setTotalAmount(String.valueOf(totalAmount));
       // payToRequest.setFullName(CommonUtils.getFullName(accountInfo));

        Payments channelSignature = new Payments();
        channelSignature.setContent(content);
        channelSignature.setReceiver(String.valueOf(contact.getWalletId()));
        channelSignature.setSender(String.valueOf(accountInfo.getWalletId()));
        channelSignature.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        channelSignature.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        channelSignature.setTotalAmount(String.valueOf(totalAmount));
        channelSignature.setType(type);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(channelSignature));

        payToRequest.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(payToRequest);
        return payToRequest;
    }
}
