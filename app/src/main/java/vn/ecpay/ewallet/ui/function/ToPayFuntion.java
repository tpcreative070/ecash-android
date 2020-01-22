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
import vn.ecpay.ewallet.model.payment.Payments;
import vn.ecpay.ewallet.ui.interfaceListener.ToPayListener;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class ToPayFuntion {
    private AccountInfo accountInfo;
    private Context context;
    private ToPayListener toPayListener;
    private Payments payToRequest;
    private List<CashTotal> valuesList;
    private Contact contact;
    public ToPayFuntion(Context context, List<CashTotal> mValuesList, Contact contact, Payments payToRequest) {
        this.context = context;
        this.valuesList = mValuesList;
        this.contact = contact;
        this.payToRequest = payToRequest;
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, context);
    }

    public void handlePayToSocket(ToPayListener mtoPayListener){
        this.toPayListener =mtoPayListener;
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, context);
        Log.e("payto url ",url);
        Request requestCoinPrice = new Request.Builder().url(url).build();
        client.newWebSocket(requestCoinPrice, new WebSocketListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                Log.e("topay funtion", "topay funtion");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Gson gson = new Gson();
                        String jsonSend = gson.toJson(getObjectJsonSend(valuesList));
                        webSocket.send(jsonSend);
//                        for (int i = 0; i < multiTransferList.size(); i++) {
//                            String jsonSend = gson.toJson(getObjectJsonSend(valuesList, multiTransferList.get(i), i));
//                            webSocket.send(jsonSend);
//                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
//                        Log.e("response ",response.toString());
//                        Log.e("response ",response.body().toString());
                        toPayListener.onToPaySuccess();
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

    private ResponseMessSocket getObjectJsonSend(List<CashTotal> valuesList) {
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        for (int i = 0; i < valuesList.size(); i++) {
           Log.e("valuesListAdapter i ",valuesList.get(i).getTotal()+"");
            if (valuesList.get(i).getTotal() > 0) {
                List<CashLogs_Database> cashList = DatabaseUtil.getListCashForMoney(context, String.valueOf(valuesList.get(i).getParValue()));
                for (int j = 0; j < valuesList.get(i).getTotal(); j++) {
                    listCashSend.add(cashList.get(j));
                }
            }
        }
        if (listCashSend.size() > 0) {
            String[][] cashArray = new String[listCashSend.size()][3];
            for (int i = 0; i < listCashSend.size(); i++) {
                CashLogs_Database cash = listCashSend.get(i);
                String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                cashArray[i] = moneyItem;
            }
            String encData = CommonUtils.getEncrypData(cashArray, payToRequest.getSenderPublicKey());
            ResponseMessSocket responseMess = new ResponseMessSocket();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(String.valueOf(payToRequest.getSender()));
            responseMess.setTime(CommonUtils.getCurrentTime(Constant.FORMAT_DATE_TOPAY));
            responseMess.setType(Constant.TYPE_PAYTO);
            responseMess.setContent(payToRequest.getContent());
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getIdSender(responseMess, context));

            CommonUtils.logJson(responseMess);
            DatabaseUtil.updateTransactionsLogAndCashOutDatabase(listCashSend, responseMess, context, accountInfo.getUsername());
            return responseMess;
        }
        return null;
    }
}
