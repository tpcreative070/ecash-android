package vn.ecpay.ewallet.ui.function;

import android.content.Context;
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
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class CashOutSocketFunction {
    private AccountInfo accountInfo;
    private Context context;
    private int sl500, sl200, sl100, sl50, sl20, sl10;
    private String keyPublicReceiver;
    private String walletReceiver;
    private String contentSendMoney;

    public CashOutSocketFunction(Context context, int sl500, int sl200, int sl100, int sl50, int sl20, int sl10, String keyPublicReceiver, String walletReceiver, String contentSendMoney) {
        this.context = context;
        this.sl500 = sl500;
        this.sl200 = sl200;
        this.sl100 = sl100;
        this.sl50 = sl50;
        this.sl20 = sl20;
        this.sl10 = sl10;
        this.keyPublicReceiver = keyPublicReceiver;
        this.walletReceiver = walletReceiver;
        this.contentSendMoney = contentSendMoney;
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, context);
    }

    public void handleCashOutSocket() {
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, context).replaceAll("%20", "+");
        Request requestCoinPrice = new Request.Builder().url(url).build();
        client.newWebSocket(requestCoinPrice, new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                webSocket.send(getObjectJsonSend(sl10, sl20, sl50, sl100, sl200, sl500,
                        keyPublicReceiver, walletReceiver, contentSendMoney));
                Log.e("send_money", "send money ok");
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @NotNull Response response) {
                super.onFailure(webSocket, t, response);
                Log.e("connect_socket", "connect socket fail");
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CONNECT_SOCKET_FAIL));
            }
        });
        client.dispatcher().executorService().shutdown();
    }

    private String getObjectJsonSend(int sl10, int sl20, int sl50, int sl100, int sl200, int sl500,
                                     String keyPublicReceiver, String walletReceiver, String contentSendMoney) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();
        if (sl10 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("10000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl10; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (sl20 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("20000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl20; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl50 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("50000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl50; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl100 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("100000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl100; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (sl200 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("200000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl200; i++) {
                listCashSend.add(cashList.get(i));
            }
        }
        if (sl500 > 0) {
            List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney("500000", Constant.STR_CASH_IN);
            for (int i = 0; i < sl500; i++) {
                listCashSend.add(cashList.get(i));
            }
        }

        if (listCashSend.size() > 0) {
            String[][] cashArray = new String[listCashSend.size()][3];
            for (int i = 0; i < listCashSend.size(); i++) {
                CashLogs_Database cash = listCashSend.get(i);
                String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                cashArray[i] = moneyItem;
            }
            String encData = CommonUtils.getEncrypData(cashArray, keyPublicReceiver);
            ResponseMessSocket responseMess = new ResponseMessSocket();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(walletReceiver);
            responseMess.setTime(CommonUtils.getCurrentTime());
            responseMess.setType(Constant.TYPE_ECASH_TO_ECASH);
            responseMess.setContent(contentSendMoney);
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getIdSender(responseMess, context));
            Gson gson = new Gson();
            String json = gson.toJson(responseMess);
            if (!json.isEmpty()) {
                DatabaseUtil.updateTransactionsLogAndCashOutDatabase(listCashSend, responseMess, context, accountInfo.getUsername());
                EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
            }
            return json;
        }
        return Constant.STR_EMPTY;
    }
}
