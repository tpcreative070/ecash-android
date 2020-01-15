package vn.ecpay.ewallet.ui.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.interfaceListener.CashOutListener;
import vn.ecpay.ewallet.ui.interfaceListener.PayToListener;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class PayToFuntion {
    private AccountInfo accountInfo;
    private Context context;
    private String contentSendMoney;
    private List<Contact> multiTransferList;
    private long total;
    private String typeSend;
    private PayToListener payToListener;

    public PayToFuntion(Context context, long total, List<Contact> multiTransferList, String content, String typeSend) {
        this.context = context;
        this.total = total;
        this.multiTransferList = multiTransferList;
        this.contentSendMoney = content;
        this.typeSend = typeSend;
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
                Log.e("payto", "payto ok");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Gson gson = new Gson();
                        for (int i = 0; i < multiTransferList.size(); i++) {
                            String jsonSend = gson.toJson(getObjectJsonSend(total, multiTransferList.get(i), contentSendMoney, i));
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

    private ResponseMessSocket getObjectJsonSend(long total, Contact contact, String contentSendMoney, int index) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();

        // todo: review CashOutFuntion
        return null;
    }
}
