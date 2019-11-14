package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.TransactionLogQR_Database;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.notification.NotificationObj;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

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
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        return WalletDatabase.getAllProfile();
    }

    public static void saveTransactionLogQR(ArrayList<QRCodeSender> codeSenderArrayList, ResponseMessSocket responseMess, FragmentActivity activity) {
        WalletDatabase.getINSTANCE(activity, ECashApplication.masterKey);
        for (int i = 0; i < codeSenderArrayList.size(); i++) {
            TransactionLogQR_Database transactionLog = new TransactionLogQR_Database();
            transactionLog.setTransactionSignature(responseMess.getId());
            transactionLog.setTotal(codeSenderArrayList.get(i).getTotal());
            transactionLog.setValue(codeSenderArrayList.get(i).getContent());
            transactionLog.setSequence(codeSenderArrayList.get(i).getCycle());
            WalletDatabase.insertTransactionLogQRTask(transactionLog, Constant.STR_EMPTY);
        }
    }

    public static void saveTransactionLog(ResponseMessSocket responseMess, Context context) {
        TransactionLog_Database transactionLog = new TransactionLog_Database();
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
        WalletDatabase.insertTransactionLogTask(transactionLog);
    }

    public static void saveTransactionLog(TransactionLog_Database transactionLog, Context context) {
        transactionLog.setPreviousHash(getPreviousHashTransactionLog(transactionLog, context));

        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertTransactionLogTask(transactionLog, Constant.STR_EMPTY);
    }

    public static boolean isTransactionLogExit(ResponseMessSocket responseMess, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        TransactionLog_Database transactionLog = WalletDatabase.checkTransactionLogExit(responseMess.getId());
        return null != transactionLog;
    }

    //ma hoa mat xich transaction log
    public static String getPreviousHashTransactionLog(TransactionLog_Database transactionLog, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        List<TransactionLog_Database> transactionLogList = WalletDatabase.getAllTransactionLog();
        if (transactionLogList.size() > 0) {
            //get transaction_log max id
            TransactionLog_Database TransactionLogMax = WalletDatabase.getTransactionLogByMaxID(WalletDatabase.getMaxIDTransactionLog());

            //updatePrevious transaction_log min id => new PreviousHash
            WalletDatabase.updatePreviousTransactionLogMin(getSignTransactionLog(transactionLog), WalletDatabase.getMinIDTransactionLog());

            //ok
            return getSignTransactionLog(TransactionLogMax);
        } else {
            return getSignTransactionLog(transactionLog);
        }
    }

    //ma hoa mat xich dong tien
    public static String getPreviousHashCash(CashLogs_Database cash, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        List<CashLogs_Database> cashList = WalletDatabase.getAllCash();
        if (cashList.size() > 0) {
            //get cash max id
            CashLogs_Database cashMax = WalletDatabase.getCashByMaxID(WalletDatabase.getMaxIDCash());

            //updatePreviousCash min id => new PreviousHash
            WalletDatabase.updatePreviousCashMin(getSignCash(cash), WalletDatabase.getMinIDCash());

            //ok
            return getSignCash(cashMax);
        } else {
            return getSignCash(cash);
        }
    }

    public static String getSignTransactionLog(TransactionLog_Database transactionLog) {
        String cashSign = transactionLog.getSenderAccountId()
                + transactionLog.getReceiverAccountId() + transactionLog.getType()
                + transactionLog.getTime() + transactionLog.getContent()
                + transactionLog.getCashEnc() + transactionLog.getTransactionSignature();
        byte[] dataSign = SHA256.hashSHA256(cashSign);
        return CommonUtils.generateSignature(dataSign, Constant.STR_PRIVATE_KEY_CHANEL);
    }

    public static void changePassDatabase(Context context, String masterKey) {
        if (null != KeyStoreUtils.getMasterKey(context)) {
            WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
            WalletDatabase.changeKey(masterKey);
        }
    }

    public static boolean saveCashToDB(CashLogs_Database cash, Context context, String userName) {
        cash.setPreviousHash(getPreviousHashCash(cash, context));
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertCashTask(cash, userName);
        return true;
    }

    public static boolean SaveCashInvalidToDB(CashLogs_Database cash, Context context, String userName) {
        cash.setPreviousHash(getPreviousHashCash(cash, context));
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertCashInvalidTask(cash, userName);
        return true;
    }

    public static String getSignCash(CashLogs_Database cash) {
        String cashSign = cash.getCountryCode() + cash.getIssuerCode() + cash.getDecisionNo()
                + cash.getSerialNo() + cash.getParValue() + cash.getActiveDate() + cash.getExpireDate()
                + cash.getCycle() + cash.getAccSign() + cash.getTreSign();
        byte[] dataSign = SHA256.hashSHA256(cashSign);
        return CommonUtils.generateSignature(dataSign, Constant.STR_PRIVATE_KEY_CHANEL);
    }

    public static void updateTransactionsLogAndCashOutDatabase(ArrayList<CashLogs_Database> listCashSend, ResponseMessSocket responseMess, Context context, String userName) {
        saveTransactionLog(responseMess, context);
        for (int i = 0; i < listCashSend.size(); i++) {
            CashLogs_Database cash = listCashSend.get(i);
            cash.setType(Constant.STR_CASH_OUT);
            cash.setTransactionSignature(responseMess.getId());
            saveCashToDB(cash, context, userName);
        }
    }

    public static void saveListContact(Context context, ArrayList<Contact> listContact) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        for (int i = 0; i < listContact.size(); i++) {
            Contact contact = listContact.get(i);
            contact.setStatus(Constant.CONTACT_ON);
            contact.setFullName(getNameContact(context, listContact.get(i).getPhone()));
            WalletDatabase.insertContactTask(contact);
        }
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CONTACT));
    }

    public static void saveOnlySingleContact(Context context, Contact contact) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        contact.setStatus(Constant.CONTACT_ON);
        WalletDatabase.insertContactTask(contact);
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CONTACT));
    }


    public static String getNameContact(Context context, String phoneInput) {
        String[] projection = new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        assert phones != null;
        if (phones.getCount() > 0) {
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .replace("+84", "0")
                        .replace(" ", "");
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (CommonUtils.isValidatePhoneNumber(phoneNumber)) {
                    if (phoneNumber.equals(phoneInput)) {
                        return name;
                    }
                }
            }
        }
        return Constant.STR_EMPTY;
    }

    public static boolean deleteContact(Context context, Long walletId) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.deleteContact(walletId);
        return true;
    }

    public static boolean updateNameContact(Context context, String name, Long walletId) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.updateNameContact(name, walletId);
        return true;
    }

    public static void updateStatusContact(Context context, int status, Long walletId) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.updateStatusContact(status, walletId);
    }

    public static List<NotificationObj> getAllNotification(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllNotification();
    }

    public static void delAllNotification(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.deleteAllNotification();
    }

    public static int getSizeNotification(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllNotification().size();
    }

    public static void deleteNotification(Context context, Long id) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.deleteNotification(id);
    }

    public static void updateNotificationRead(Context context, String read, Long id) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.updateNotificationRead(read, id);
    }
}
