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
import vn.ecpay.ewallet.model.payTo.PayToRequest;
import vn.ecpay.ewallet.model.toPay.ToPayChannelSignature;
import vn.ecpay.ewallet.ui.interfaceListener.PayToListener;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class PayToFuntion {
    private AccountInfo accountInfo;
    private Context context;
    private String content;
    private List<Contact> multiTransferList;
    private long totalAmount;
    private String type;
    private PayToListener payToListener;

    public PayToFuntion(Context context, long total, List<Contact> multiTransferList, String content, String type) {
        this.context = context;
        this.totalAmount = total;
        this.multiTransferList = multiTransferList;
        this.content = content;
        this.type = type;
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, context);
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
                        //  String jsonSend = "{\"cashEnc\":\"BIvmEiduhswp0R0AlPzaEqupTn7hD4+SmTUmpj89aPVYaHJX28wC7zPLiXizTMepIiMgl1K28Icu5bWECT/9vv8\\u003d$BDTalEsI1OzxIPp5aXzW7yKpPOmZYi5r8PtUsIZsDkBJX8Cdz623fRu/UWXMao0tM902npUSSvj+WRgBCfjy8X8\\u003d$c1830M69ysBKcCqiz2tCxJUmpKczrtVqrUjGaOYaKX77ch7XgQeseS0QpuV/O4sMPf5VCV7QzdzZVZ8/AMyAMfh4t5HUMonJRB8KhMxxg/kmEZffpkdJ47TWHEByDn+rAbBkU3UZVl70yg7uDdPYqbdYQAum6wYm5hVCu0HZmKpqn6IiL55IsYX5egu2ItFVIks+nPbjeW+KgYL9Wf0zgKb/FW7pIviH6zf7jFq2Pbb8eEga3COoyisdFZBfWWl8jNgKAs2VEtifFYd1KIuWQg\\u003d\\u003d\",\"content\":\"ccc\",\"id\":\"MEQCIGgZoeeKKW852v5+5c8S1Eh3skttCWXNedslzyDM5gHpAiAxubAxdWsgaKsDiqA79Q7dH0vhYx0lDX2AxX4ovpExrw\\u003d\\u003d\",\"receiver\":\"8347724315\",\"sender\":\"8344937050\",\"time\":\"20200117130748\",\"type\":\"YT\"}";
                          // String jsonSend = "{\"channelSignature\":\"MEUCIQCWTNLqKbXyoqQJo29shr4V+DhNzM5wSyeWpw+n5zHF3QIgIc28R/NteIXhsBV1MQvFUPQZYTXPunzG3i5Lj+IHnCw\\u003d\",\"content\":\"qqqq\",\"receiver\":\"8347724315\",\"sender\":\"8344937050\",\"senderPublicKey\":\"BNgnkABeFITGUGZj8DBlPRyJ3ttnVw6q+UbYD/Du/a5QWNO+nUXEGeuTNubfpOrAphIpNaGDtWimSO+pFmpy3KM\\u003d\",\"time\":\"20200117 11:11:47\",\"totalAmount\":\"4000\",\"type\":\"CT\"}}";
                            webSocket.send(jsonSend);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
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

    private PayToRequest getObjectJsonSend( Contact contact, int index) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));

        PayToRequest payToRequest = new PayToRequest();
        payToRequest.setSender(String.valueOf(accountInfo.getWalletId()));
        payToRequest.setReceiver(String.valueOf(contact.getWalletId()));
        payToRequest.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        payToRequest.setType(type);
        payToRequest.setContent(content);
        payToRequest.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        payToRequest.setTotalAmount(String.valueOf(totalAmount));
        payToRequest.setFullName(CommonUtils.getFullName(accountInfo));

        ToPayChannelSignature requestToPay = new ToPayChannelSignature();
        requestToPay.setContent(content);
        requestToPay.setReceiver(String.valueOf(contact.getWalletId()));
        requestToPay.setSender(String.valueOf(accountInfo.getWalletId()));
        requestToPay.setSenderPublicKey(accountInfo.getEcKeyPublicValue());
        requestToPay.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
        requestToPay.setTotalAmount(String.valueOf(totalAmount));
        requestToPay.setType(type);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestToPay));

        payToRequest.setChannelSignature(CommonUtils.generateSignature(dataSign));

        CommonUtils.logJson(payToRequest);
        return payToRequest;
    }
}
