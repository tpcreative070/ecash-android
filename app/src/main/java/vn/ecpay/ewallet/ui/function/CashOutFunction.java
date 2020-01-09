package vn.ecpay.ewallet.ui.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
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
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.interfaceListener.CashOutListener;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
import vn.ecpay.ewallet.webSocket.util.SocketUtil;

public class CashOutFunction {
    private AccountInfo accountInfo;
    private Context context;
    private String contentSendMoney;
    private List<Contact> multiTransferList;
    private List<CashTotal> valuesList;
    private String typeSend;
    private CashOutListener cashOutListener;

    public CashOutFunction(Context context, List<CashTotal> valuesList, List<Contact> multiTransferList, String content, String typeSend) {
        this.context = context;
        this.valuesList = valuesList;
        this.multiTransferList = multiTransferList;
        this.contentSendMoney = content;
        this.typeSend = typeSend;
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, context);
    }

    @SuppressLint("StaticFieldLeak")
    public void handleCashOutQRCode(CashOutListener mCashOutListener) {
        this.cashOutListener = mCashOutListener;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = 0; i < multiTransferList.size(); i++) {
                    String currentTime = CommonUtils.getCurrentTime();
                    Gson gson = new Gson();
                    ResponseMessSocket responseMessSocket = getObjectJsonSend(valuesList, multiTransferList.get(i), contentSendMoney, i);
                    String jsonCash = gson.toJson(responseMessSocket);
                    Contact contact = multiTransferList.get(i);
                    List<String> stringList = CommonUtils.getSplittedString(jsonCash, 1000);
                    ArrayList<QRCodeSender> codeSenderArrayList = new ArrayList<>();
                    if (stringList.size() > 0) {
                        for (int j = 0; j < stringList.size(); j++) {
                            QRCodeSender qrCodeSender = new QRCodeSender();
                            qrCodeSender.setCycle(j + 1);
                            qrCodeSender.setTotal(stringList.size());
                            qrCodeSender.setContent(stringList.get(j));
                            codeSenderArrayList.add(qrCodeSender);
                        }

                        //save image
                        if (codeSenderArrayList.size() > 0) {
                            for (int j = 0; j < codeSenderArrayList.size(); j++) {
                                Bitmap bitmap = CommonUtils.generateQRCode(gson.toJson(codeSenderArrayList.get(j)));
                                String root = Environment.getExternalStorageDirectory().toString();
                                File mFolder = new File(root + "/qr_image");
                                if (!mFolder.exists()) {
                                    mFolder.mkdir();
                                }
                                String imageName = contact.getWalletId() + "_" + currentTime + "_" + j + ".jpg";
                                File file = new File(mFolder, imageName);
                                if (file.exists())
                                    file.delete();
                                try {
                                    FileOutputStream out = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();
                                } catch (Exception e) {
                                    EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_FAIL));
                                    e.printStackTrace();
                                }
                            }
                            //save log
                            DatabaseUtil.saveTransactionLogQR(codeSenderArrayList, responseMessSocket, context);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                cashOutListener.onCashOutSuccess();
                EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
            }
        }.execute();
    }

    public void handleCashOutSocket(CashOutListener mCashOutListener) {
        this.cashOutListener = mCashOutListener;
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, context);
        Request requestCoinPrice = new Request.Builder().url(url).build();
        client.newWebSocket(requestCoinPrice, new WebSocketListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                Log.e("send_money", "send money ok");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Gson gson = new Gson();
                        for (int i = 0; i < multiTransferList.size(); i++) {
                            String jsonSend = gson.toJson(getObjectJsonSend(valuesList, multiTransferList.get(i), contentSendMoney, i));
                            webSocket.send(jsonSend);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        cashOutListener.onCashOutSuccess();
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
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

    private ResponseMessSocket getObjectJsonSend(List<CashTotal> valuesListAdapter, Contact contact, String contentSendMoney, int index) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();

        for (int i = 0; i < valuesListAdapter.size(); i++) {
            if (valuesListAdapter.get(i).getTotal() > 0) {
                List<CashLogs_Database> cashList = DatabaseUtil.getListCashForMoney(context, String.valueOf(valuesListAdapter.get(i).getParValue()));
                int totalCashSend = valuesListAdapter.get(i).getTotal();
                for (int j = 0; j < valuesListAdapter.get(i).getTotal(); j++) {
                    if (index > 0) {
                        int location = j + index * totalCashSend;
                        listCashSend.add(cashList.get(location));
                    } else {
                        listCashSend.add(cashList.get(j));
                    }
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
            String encData = CommonUtils.getEncrypData(cashArray, contact.getPublicKeyValue());
            ResponseMessSocket responseMess = new ResponseMessSocket();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(String.valueOf(contact.getWalletId()));
            responseMess.setTime(CommonUtils.getCurrentTime());
            responseMess.setType(typeSend);
            responseMess.setContent(contentSendMoney);
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getIdSender(responseMess, context));

            CommonUtils.logJson(responseMess);
            DatabaseUtil.updateTransactionsLogAndCashOutDatabase(listCashSend, responseMess, context, accountInfo.getUsername());
            return responseMess;
        }
        return null;
    }
}
