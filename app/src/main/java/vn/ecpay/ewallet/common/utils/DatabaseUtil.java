package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.database.table.TransactionLog;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.getPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.webSocket.object.ResponseCashMess;

public class DatabaseUtil {
    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void saveAccountInfo(AccountInfo accountInfo, Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.insertAccountInfoTask(accountInfo);
    }

    public static AccountInfo getAccountInfo(String userName, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAccountInfoTask(userName);
    }

    public static List<AccountInfo> getAllAccountInfo(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllProfile();
    }

    public static void saveTransactionLog(ResponseCashMess responseMess, Context context) {
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setSenderAccountId(responseMess.getSender());
        transactionLog.setReceiverAccountId(responseMess.getReceiver());
        transactionLog.setType(responseMess.getType());
        transactionLog.setTime(responseMess.getTime());
        transactionLog.setContent(responseMess.getContent());
        transactionLog.setCashEnc(responseMess.getCashEnc());
        transactionLog.setTransactionSignature(responseMess.getId());
        transactionLog.setRefId(responseMess.getRefId());
        transactionLog.setPreviousHash(getPreviousHashTransactionLog(transactionLog, context));

        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertTransactionLogTask(transactionLog, Constant.STR_EMPTY);
    }

    public static void saveTransactionLog(TransactionLog transactionLog, Context context) {
        transactionLog.setPreviousHash(getPreviousHashTransactionLog(transactionLog, context));

        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertTransactionLogTask(transactionLog, Constant.STR_EMPTY);
    }

    public static boolean isTransactionLogExit(ResponseCashMess responseMess, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        TransactionLog transactionLog = WalletDatabase.checkTransactionLogExit(responseMess.getId());
        if (transactionLog != null) {
            return true;
        }
        return false;
    }

    //ma hoa mat xich transaction log
    public static String getPreviousHashTransactionLog(TransactionLog transactionLog, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        List<TransactionLog> transactionLogList = WalletDatabase.getAllTransactionLog();
        if (transactionLogList.size() > 0) {
            //get transaction_log max id
            TransactionLog TransactionLogMax = WalletDatabase.getTransactionLogByMaxID(WalletDatabase.getMaxIDTransactionLog());

            //updatePrevious transaction_log min id => new PreviousHash
            WalletDatabase.updatePreviousTransactionLogMin(getSignTransactionLog(transactionLog), WalletDatabase.getMinIDTransactionLog());

            //ok
            return getSignTransactionLog(TransactionLogMax);
        } else {
            return getSignTransactionLog(transactionLog);
        }
    }

    //ma hoa mat xich dong tien
    public static String getPreviousHashCash(CashLogs cash, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        List<CashLogs> cashList = WalletDatabase.getAllCash();
        if (cashList.size() > 0) {
            //get cash max id
            CashLogs cashMax = WalletDatabase.getCashByMaxID(WalletDatabase.getMaxIDCash());

            //updatePreviousCash min id => new PreviousHash
            WalletDatabase.updatePreviousCashMin(getSignCash(cash), WalletDatabase.getMinIDCash());

            //ok
            return getSignCash(cashMax);
        } else {
            return getSignCash(cash);
        }
    }

    public static String getSignTransactionLog(TransactionLog transactionLog) {
        String cashSign = transactionLog.getSenderAccountId()
                + transactionLog.getReceiverAccountId() + transactionLog.getType()
                + transactionLog.getTime() + transactionLog.getContent()
                + transactionLog.getCashEnc() + transactionLog.getTransactionSignature();
        byte[] dataSign = SHA256.hashSHA256(cashSign);
        return CommonUtils.generateSignature(dataSign, Constant.STR_PRIVATE_KEY_CHANEL);
    }

    public static void changePassDatabase(Context context, String masterKey) {
        if (KeyStoreUtils.getMasterKey(context) != null) {
            WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
            WalletDatabase.changeKey(masterKey);
        }
    }

    public static boolean SaveCashToDB(CashLogs cash, Context context, String userName) {
        cash.setPreviousHash(getPreviousHashCash(cash, context));
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertCashTask(cash, userName);
        return true;
    }

    public static boolean SaveCashInvalidToDB(CashLogs cash, Context context, String userName) {
        cash.setPreviousHash(getPreviousHashCash(cash, context));
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertCashInvalidTask(cash, userName);
        return true;
    }

    public static String getSignCash(CashLogs cash) {
        String cashSign = cash.getCountryCode() + cash.getIssuerCode() + cash.getDecisionNo()
                + cash.getSerialNo() + cash.getParValue() + cash.getActiveDate() + cash.getExpireDate()
                + cash.getCycle() + cash.getAccSign() + cash.getTreSign();
        byte[] dataSign = SHA256.hashSHA256(cashSign);
        return CommonUtils.generateSignature(dataSign, Constant.STR_PRIVATE_KEY_CHANEL);
    }

    public static void updateDatabase(ArrayList<CashLogs> listCashSend, ResponseCashMess responseMess, Context context, String userName) {
        //save trasaction log
        saveTransactionLog(responseMess, context);
        //save to cash log
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        for (int i = 0; i < listCashSend.size(); i++) {
            CashLogs cash = listCashSend.get(i);
            cash.setType(Constant.STR_CASH_OUT);
            cash.setTransactionSignature(responseMess.getId());
            WalletDatabase.insertCashTask(cash, userName);
        }
    }

    public static void saveContact(Context context, ResponseDataGetPublicKeyWallet dataGetPublicKeyWallet) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertContactTask(dataGetPublicKeyWallet);
    }
}
