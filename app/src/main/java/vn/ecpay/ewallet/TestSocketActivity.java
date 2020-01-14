//package vn.ecpay.ewallet;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Base64;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//
//import com.google.gson.Gson;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//import okhttp3.OkHttpClient;
//import okhttp3.Response;
//import okhttp3.WebSocket;
//import okhttp3.WebSocketListener;
//import okio.ByteString;
//import vn.ecpay.ewallet.common.base.ECashBaseActivity;
//import vn.ecpay.ewallet.common.eccrypto.ECashCrypto;
//import vn.ecpay.ewallet.common.eccrypto.SHA256;
//import vn.ecpay.ewallet.common.eccrypto.Test;
//import vn.ecpay.ewallet.common.keystore.KSDeCrypt;
//import vn.ecpay.ewallet.common.keystore.KSEnCrypt;
//import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
//import vn.ecpay.ewallet.common.utils.CommonUtils;
//import vn.ecpay.ewallet.common.utils.Constant;
//import vn.ecpay.ewallet.database.WalletDatabase;
//import vn.ecpay.ewallet.database.table.CashLogs_Database;
//import vn.ecpay.ewallet.database.table.TransactionLog_Database;
//import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
//import vn.ecpay.ewallet.webSocket.genenSignature.ChangeSignature;
//import vn.ecpay.ewallet.webSocket.genenSignature.WalletSignature;
//import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;
//
//public class TestSocketActivity extends ECashBaseActivity {
//    @BindView(R.id.start)
//    Button start;
//    @BindView(R.id.output)
//    TextView output;
//
//    WebSocketListener webSocketListenerCoinPrice = new WebSocketListener() {
//        @Override
//        public void onOpen(WebSocket webSocket, Response response) {
//            Log.e("onOpen", "onOpen");
//        }
//
//        @Override
//        public void onMessage(WebSocket webSocket, String text) {
//            Log.e("onMessage", "onOpen");
//        }
//
//        @Override
//        public void onMessage(WebSocket webSocket, ByteString bytes) {
//            Log.d("onMessage", "MESSAGE: " + bytes.hex());
//        }
//
//        @Override
//        public void onClosing(WebSocket webSocket, int code, String reason) {
//            webSocket.close(1000, null);
//            webSocket.cancel();
//            Log.d("onClosing", "CLOSE: " + code + " " + reason);
//        }
//
//        @Override
//        public void onClosed(WebSocket webSocket, int code, String reason) {
//            //TODO: stuff
//        }
//
//        @Override
//        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
//            //TODO: stuff
//        }
//    };
//
//    @Override
//    protected int getLayoutResId() {
//        return R.layout.test_socket;
//    }
//
//    @Override
//    protected void setupActivityComponent() {
//
//    }
//
//    String url = "ws://10.10.32.102:8010/sync?channelCode=MB001&username=vinh6&waletId=8393228427&functionCode=EC00027&terminalId=358240051111110&terminalInfo=Android%20SDK%20built%20for%20x86&auditNumber=123456789&sessionId=44bf2fde-024c-48d7-b2d2-8456f48d097e&channelSignature=MEUCIQC39mVyFLVczCCuhWyh%2BGilx4fLjPR1T6FshNQh7GBnmwIgYP4Alyc7EXmPb7cBHDOWWrn%2F8nzIkJFX6fUtSa5cN3M%3D&walletSignature=MEQCIALk%2BNUYFDSv2x6zDrq8OP%2FzqHZ8djb64r6t4xBKj7dgAiA5xgEn7HEMzjD%2FOoS5FhJvU5%2FSaWKwwzvGhPYfnO7VcQ%3D%3D";
//    static final byte elementSplit = ';';//59
//
//    @OnClick(R.id.start)
//    public void onViewClicked() {
//        OkHttpClient clientCoinPrice = new OkHttpClient();
////        Request requestCoinPrice = new Request.Builder().url("wss://echo.websocket.org").build();
////        Request requestCoinPrice = new Request.Builder().url(url).build();
////        clientCoinPrice.newWebSocket(requestCoinPrice, webSocketListenerCoinPrice);
////        clientCoinPrice.dispatcher().executorService().shutdown();
//
////        startService(new Intent(this, WebSocketsService.class));
//
////        insertData();
////        updatePreviousCash();
//
//
//        //test save ecash
////        saveEcash();
////        showData();
//
////        saveTransactionLog();
//////
////        final Handler handler = new Handler();
////        handler.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                Decision_Database.getINSTANCE(getApplicationContext());
////                List<CashLogs_Database> transactionLogList = Decision_Database.getAllCash();
////
////
////                WalletDatabase.getINSTANCE(getApplicationContext());
////                List<TransactionLog_Database> transactionLogList1 = WalletDatabase.getAllTransactionLog();
////                Log.e("ahiih", "jiji");
////            }
////        }, 1000);
//
//        getCurentTime();
//    }
//
//    private String getCurentTime(){
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat(Constant.FORMAT_DATE_SEND_CASH);
//        Log.e("time", df.format(c.getTime()));
//        return df.format(c.getTime());
//    }
//
//    private void saveTransactionLog() {
//        TransactionLog_Database transactionLog = new TransactionLog_Database();
//        transactionLog.setSenderAccountId("responseMess.getSender()");
//        transactionLog.setSenderAccountId("responseMess.getReceiver()");
//        transactionLog.setType("responseMess.getType()");
//        transactionLog.setTime("responseMess.getTime()");
//        transactionLog.setContent("responseMess.getContent()");
//        transactionLog.setCashEnc("responseMess.getCashEnc()");
//        transactionLog.setTransactionSignature("responseMess.getId()");
//        transactionLog.setRefId("responseMess.getRefId()");
//        transactionLog.setPreviousHash("getPreviousHashTransactionLog(transactionLog)");
//
//        WalletDatabase.getINSTANCE(getApplicationContext(), KeyStoreUtils.getMasterKey(getApplicationContext()));
//        WalletDatabase.insertTransactionLogTask(transactionLog);
//    }
//
//
//    private ResponseMessSocket getObject() {
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String strDate = sdf.format(c.getTime());
//
//        ResponseMessSocket responseMess = new ResponseMessSocket();
//        responseMess.setSender("8314175251");
//        responseMess.setReceiver("8314175251");
//        responseMess.setTime(strDate);
//        responseMess.setType("CT");
//        responseMess.setContent("ahihi");
//        responseMess.setId("MEQCIClxc69wXWSu2MUzDCYqtTVGU+ZTQo3ucGRNlTGwE3zzAiBgIrHUqijkzYiTkJk5c7kdq7GA0IcirTpFio3oa/yVDw==");
//        responseMess.setCashEnc("BKTQfV9XdIt7/onz5dEQrj41rd/xzCZY7wRSvPEpvO+C1AP1GH8QtVqhtnlvlUr0okNSSKRwi+eUwRgadc8W3WE=$BIKet0twFXqXpw7DDQ0iXW/2vBmgiOI7kN3DI6ZFn65K7me8EfQzry7V4wXwxmChEL866+3ubU1IOQ9GIAD7P/g=$ZuA5RIYkzTovqu7ROkaXfvbJAS8loskcUOgE23PR+6xbyS+rO39KxbOaSJH1KdkJ+SX4jkY7OR3ou6HMW+XKfoTUvZUB7w/qNK+PiMe3wyGg+XGWYVK+12tET/wb3HcISDO07W631bZuwYtwcBsPI32/A8ClfTdnu6sOqqrq26fsAfTuHehGbAfn+P1Urdbw38rrVNcBPML0Fl8NUvfKz0gGfOHhqJ7aGvzDSnnx0Eky3MvzbSSLxcrfZEViw2WL3aLVXGt6a6GO7OhzTB84tuJOcdGPHDx8oP8qsbK6TMz5vTrWmWhtJ+zLgKF1bTz8qFBmtK4i5YducBMNZhOVlIVoC0kanu/THHn0kVr9lVMVf7jGpXj1qRVRa1vKISuU8/N5+eIfcQT/OPoNtjIEDakrZBSC9x7QAkUXMyF6qpS8yco8tbxU/4LmvDdKeLrFrF1b1aeut/BchDMZiJTEDN4vCDJ9MQjypcSt2+Yky+mU6JVrrGWIGKRkVw2kdpMDVxTg6boI5ffY5oKsZfys7q39TcGjftB22XZX4i9vfgYoqMc1ZBds/GalXfvUSg698tK/aLuWplnOMFATTJailw==");
//        return responseMess;
//    }
//
//    private void saveEcash() {
//
//        WalletDatabase.getINSTANCE(getApplicationContext(), KeyStoreUtils.getPrivateKey(getApplicationContext()));
//        List<CashLogs_Database> cashList = WalletDatabase.getAllCash();
//
////        UserDatabase.getINSTANCE(getApplicationContext(), KeyStoreUtils.getPrivateKey(getApplicationContext()));
////        UserDatabase.getAllProfile();
//
//        WalletDatabase.getINSTANCE(getApplicationContext(), KeyStoreUtils.getPrivateKey(getApplicationContext()));
//        List<TransactionLog_Database> transactionLogList = WalletDatabase.getAllTransactionLog();
//
//        Log.e("deCryptEcash", "deCryptEcash");
////        ResponseMessSocket responseMess = getObject();
////        if (responseMess.getCashEnc() != null) {
////            String[][] deCryptEcash = CommonUtils.decrypEcash(responseMess.getCashEnc(),
////                    Constant.STR_PRIVATE_KEY_CHANEL);
////            Log.e("deCryptEcash", "deCryptEcash");
////            if (deCryptEcash != null) {
//////                CommonUtils.saveCashToDB(deCryptEcash, getApplicationContext(), "userName", responseMess.getId());
////                Log.e("saveCashToDB", "saveCashToDB");
////            }
////        }
//
//    }
//
//    private void insertData() {
//        CashLogs_Database mCash = new CashLogs_Database();
//        mCash.setUserName("userName");
//        mCash.setCountryCode("cash.getCountryCode()");
//        mCash.setIssuerCode("cash.getIssuerCode()");
//        mCash.setDecisionNo("cash.getDecisionNo()");
//        mCash.setSerialNo("");
//        mCash.setParValue(235);
//        mCash.setActiveDate("cash.getActiveDate()");
//        mCash.setExpireDate("cash.getExpireDate()");
//        mCash.setCycle(2345);
//        mCash.setTreSign("cash.getTreSign()");
//        mCash.setAccSign("cash.getAccSign()");
//        mCash.setType("cash.getType()");
//        mCash.setTransactionSignature("cash.getTransactionSignature()");
//        mCash.setPreviousHash("cash.getPreviousHashCash()");
//
//        WalletDatabase.getINSTANCE(this, KeyStoreUtils.getPrivateKey(getApplicationContext()));
//        WalletDatabase.insertCashTask(mCash, "ahihi");
//    }
//
//    private void showData() {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                WalletDatabase.getINSTANCE(getApplicationContext(), KeyStoreUtils.getPrivateKey(getApplicationContext()));
//                List<CashLogs_Database> cashList = WalletDatabase.getAllCash();
//                Log.e("cash", String.valueOf(cashList.size()));
//            }
//        }, 3000);
//
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    private void getCash() {
//        String[] s0 = {"VN;GPB;GPB01001;2716791183927;500000;20181220;20501220;2",
//                "MEUCIQCq18K/LHBxKqcpY1Q6wcqSheZKt/GjrMJbmypxazlVxwIgL+UYif0Fkbm7mdH/92oJj3UQsj/m0fVMoiXhSmia3+E=",
//                "MEUCIQCY2mEltt1BY0sKQUZ6xCswSjha5iW+15Pw0kQygiXZPAIgMw1FlAb8tPoLcSG4lKOBvfkAiF1aoRO00o8YZpOV20s="};
//        String[][] cashArray = {s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0};
//        byte[][] blockEnc = new byte[0][];
//        try {
//            blockEnc = ECashCrypto.encryptV2(Test.ec, Test.kp, cashArray);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String encData = (Base64.encodeToString(blockEnc[0], Base64.DEFAULT) + (char) elementSplit
//                + Base64.encodeToString(blockEnc[1], Base64.DEFAULT) + (char) elementSplit
//                + Base64.encodeToString(blockEnc[2], Base64.DEFAULT)).replaceAll("\n", "");
//
//        //---------------------------
//        String[] listEncData = encData.split(";");
//        byte[] zero = Base64.decode(listEncData[0], Base64.DEFAULT);
//        byte[] one = Base64.decode(listEncData[1], Base64.DEFAULT);
//        byte[] tow = Base64.decode(listEncData[2], Base64.DEFAULT);
//
//        byte[][] result = {zero, one, tow};
//
//        try {
//            String[][] ok = ECashCrypto.decryptV2(Test.ec, Test.ks, blockEnc);
//
//            String[][] okResult = ECashCrypto.decryptV2(Test.ec, Test.ks, result);
//
//            Log.e("ahiih", "ahihi");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        long t3 = System.currentTimeMillis();
//
//    }
//
//    private String getUrl(AccountInfo accountInfo) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("ws")
//                .encodedAuthority("10.10.32.102:8010")
//                .appendPath("sync")
//                .appendQueryParameter("channelCode", Constant.CHANNEL_CODE)
//                .appendQueryParameter("username", accountInfo.getUsername())
//                .appendQueryParameter("waletId", String.valueOf(accountInfo.getWalletId()))
//                .appendQueryParameter("functionCode", "EC00027")
//                .appendQueryParameter("terminalId", accountInfo.getTerminalId())
//                .appendQueryParameter("terminalInfo", accountInfo.getTerminalInfo())
//                .appendQueryParameter("auditNumber", CommonUtils.getAuditNumber())
//                .appendQueryParameter("sessionId", accountInfo.getSessionId())
//                .appendQueryParameter("channelSignature", getChannelSignature(accountInfo))
//                .appendQueryParameter("walletSignature", getWalletSignature(accountInfo));
//        String url = builder.build().toString();
//        return url;
//    }
//
//    private String getChannelSignature(AccountInfo accountInfo) {
//        ChangeSignature changeSignature = new ChangeSignature();
//        changeSignature.setAuditNumber(CommonUtils.getAuditNumber());
//        changeSignature.setChannelCode(Constant.CHANNEL_CODE);
//        changeSignature.setFunctionCode(Constant.FUNCTION_WEB_SOCKET);
//        changeSignature.setSessionId(accountInfo.getSessionId());
//        changeSignature.setTerminalId(accountInfo.getTerminalId());
//        changeSignature.setTerminalInfo(accountInfo.getTerminalInfo());
//        changeSignature.setUsername(accountInfo.getUsername());
//        changeSignature.setWalletId(String.valueOf(accountInfo.getWalletId()));
//
//        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(changeSignature));
//        return CommonUtils.generateSignature(dataSign);
//    }
//
//    private String getWalletSignature(AccountInfo accountInfo) {
//        WalletSignature walletSignature = new WalletSignature();
//        walletSignature.setTerminalId(accountInfo.getTerminalId());
//        walletSignature.setTerminalInfo(accountInfo.getTerminalInfo());
//        walletSignature.setWalletId(String.valueOf(accountInfo.getWalletId()));
//
//        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(walletSignature));
//        return CommonUtils.generateSignature(dataSign, KeyStoreUtils.getPrivateKey(this));
//    }
//
//    private KSEnCrypt mEncrypt;
//
//    private void getInstanceFromPreference() {
//        // get instance save in share preference
//        Gson gson = new Gson();
//        SharedPreferences prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
//        String jsEncrypt = prefs.getString(Constant.INSTANCE_KS_ENCRYPT_PRIVATE, null);
//        mEncrypt = gson.fromJson(jsEncrypt, KSEnCrypt.class);
//        KSDeCrypt ksDeCrypt = KSDeCrypt.createInstance();
//        // get private key from KeyStore
//        String mPriKeyStored = ksDeCrypt.getKey(Constant.WALLET_ALIAS_PRIVATE_KEY,
//                mEncrypt.getEncryption(), mEncrypt.getIv());
//        Log.e("key", mPriKeyStored);
//    }
//}