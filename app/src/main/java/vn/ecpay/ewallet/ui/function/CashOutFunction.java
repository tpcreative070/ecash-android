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
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
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
    private boolean isConnectSuccess = false;

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
                    ResponseMessSocket responseMessSocket = CommonUtils.getObjectJsonSendCashToCash(context, valuesList,
                            multiTransferList.get(i), contentSendMoney, i, typeSend, accountInfo);
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
        isConnectSuccess = false;
        this.cashOutListener = mCashOutListener;
        OkHttpClient client = new OkHttpClient();
        String url = SocketUtil.getUrl(accountInfo, context);
        Request requestCoinPrice = new Request.Builder().url(url).build();
        client.newWebSocket(requestCoinPrice, new WebSocketListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                isConnectSuccess = true;
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Gson gson = new Gson();
                        for (int i = 0; i < multiTransferList.size(); i++) {
                            String jsonSend = gson.toJson(CommonUtils.getObjectJsonSendCashToCash(context, valuesList,
                                    multiTransferList.get(i), contentSendMoney, i, typeSend, accountInfo));
                            webSocket.send(jsonSend);
                            Log.e("send_money", "send money ok");
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        cashOutListener.onCashOutSuccess();
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
                        webSocket.cancel();
                    }
                }.execute();
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @NotNull Response response) {
                if (!isConnectSuccess) {
                    EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CONNECT_SOCKET_FAIL));
                }
            }
        });
    }
}
