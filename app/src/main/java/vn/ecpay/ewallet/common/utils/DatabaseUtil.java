package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CacheDataSocket_Database;
import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.Notification_Database;
import vn.ecpay.ewallet.database.table.Payment_DataBase;
import vn.ecpay.ewallet.database.table.TransactionLogQR_Database;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.model.BaseObject;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.account.cacheData.CacheData;
import vn.ecpay.ewallet.model.account.cacheData.CacheSocketData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.cashValue.response.Denomination;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.lixi.CashTemp;
import vn.ecpay.ewallet.model.notification.NotificationObj;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseDataUpdateMasterKey;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class DatabaseUtil {
    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void saveAccountInfo(AccountInfo accountInfo, Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.insertAccountInfoTask(accountInfo);
    }

    public static List<AccountInfo> getAllAccountInfo(Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        return WalletDatabase.getAllProfile();
    }

    public static void saveTransactionLogQR(QRCodeSender qrCodeSender, ResponseMessSocket responseMess, Context activity) {
        WalletDatabase.getINSTANCE(activity, ECashApplication.masterKey);
        TransactionLogQR_Database transactionLog = new TransactionLogQR_Database();
        transactionLog.setTransactionSignature(responseMess.getId());
        transactionLog.setTotal(qrCodeSender.getTotal());
        transactionLog.setValue(qrCodeSender.getContent());
        transactionLog.setSequence(qrCodeSender.getCycle());
        WalletDatabase.insertTransactionLogQRTask(transactionLog, Constant.STR_EMPTY);
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
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertTransactionLogTask(transactionLog);
    }

    public static void saveTransactionLog(CashInResponse cashInResponse, Context context) {
        TransactionLog_Database transactionLog = new TransactionLog_Database();
        transactionLog.setSenderAccountId(cashInResponse.getSender());
        transactionLog.setReceiverAccountId(String.valueOf(cashInResponse.getReceiver()));
        transactionLog.setType(cashInResponse.getType());
        transactionLog.setTime(String.valueOf(cashInResponse.getTime()));
        transactionLog.setContent(cashInResponse.getContent());
        transactionLog.setCashEnc(cashInResponse.getCashEnc());
        transactionLog.setTransactionSignature(cashInResponse.getId());
        transactionLog.setRefId(String.valueOf(cashInResponse.getRefId()));
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertTransactionLogTask(transactionLog, Constant.STR_EMPTY);
    }

    public static void saveTransactionLog(CacheSocketData cashInResponse, Context context) {
        TransactionLog_Database transactionLog = new TransactionLog_Database();
        transactionLog.setSenderAccountId(cashInResponse.getSender());
        transactionLog.setReceiverAccountId(String.valueOf(cashInResponse.getReceiver()));
        transactionLog.setType(cashInResponse.getType());
        transactionLog.setTime(String.valueOf(cashInResponse.getTime()));
        transactionLog.setContent(cashInResponse.getContent());
        transactionLog.setCashEnc(cashInResponse.getCashEnc());
        transactionLog.setTransactionSignature(cashInResponse.getId());
        transactionLog.setRefId(String.valueOf(cashInResponse.getRefId()));
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertTransactionLogTask(transactionLog, Constant.STR_EMPTY);
    }

    public static boolean isTransactionLogExit(String transactionSignature_id, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        TransactionLog_Database transactionLog = WalletDatabase.checkTransactionLogExit(transactionSignature_id);
        return null != transactionLog;
    }

    //check cash team exit
    public static boolean isCashTempExit(ResponseMessSocket responseMess, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        CashTemp cashTemp = WalletDatabase.checkCashTempExit(responseMess.getId());
        return null != cashTemp;
    }

    public static String getSignTransactionLog(TransactionLog_Database transactionLog) {
        String cashSign = transactionLog.getSenderAccountId()
                + transactionLog.getReceiverAccountId() + transactionLog.getType()
                + transactionLog.getTime() + transactionLog.getContent()
                + transactionLog.getCashEnc() + transactionLog.getTransactionSignature();
        byte[] dataSign = SHA256.hashSHA256(cashSign);
        return CommonUtils.generateSignature(dataSign, CommonUtils.getPrivateChannelKey());
    }

    public static void changeMasterKeyDatabase(Context context, String masterKey) {
        if (null != KeyStoreUtils.getMasterKey(context)) {
            WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
            WalletDatabase.changeKey(masterKey);
        }
    }

    public static boolean saveCashToDB(CashLogs_Database cash, Context context, String userName) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertCashTask(cash, userName);
        return true;
    }

    public static List<CashLogs_Database> getListCashForMoney(Context context, String value) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getListCashForMoney(value, Constant.STR_CASH_IN);
    }

    public static boolean SaveCashInvalidToDB(CashLogs_Database cash, Context context, String userName) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertCashInvalidTask(cash, userName);
        return true;
    }

    public static String getSignCash(CashLogs_Database cash) {
        String cashSign = cash.getCountryCode() + cash.getIssuerCode() + cash.getDecisionNo()
                + cash.getSerialNo() + cash.getParValue() + cash.getActiveDate() + cash.getExpireDate()
                + cash.getAccSign() + cash.getCycle() + cash.getTreSign() + cash.getType() + cash.getTransactionSignature();
        byte[] dataSign = SHA256.hashSHA256(cashSign);
        return CommonUtils.generateSignature(dataSign, CommonUtils.getPrivateChannelKey());
    }

    public static void updateTransactionsLogAndCashOutDatabase(ArrayList<CashLogs_Database> listCashSend,
                                                               ResponseMessSocket responseMess, Context context, String userName) {
        saveCashOut(responseMess.getId(), listCashSend, context, userName);
        saveTransactionLog(responseMess, context);
    }

    public static void saveCashOut(String transactionSignature, ArrayList<CashLogs_Database> listCashSend, Context context, String userName) {
        for (CashLogs_Database cashLogsDatabase : listCashSend) {
            cashLogsDatabase.setType(Constant.STR_CASH_OUT);
            cashLogsDatabase.setTransactionSignature(transactionSignature);
            saveCashToDB(cashLogsDatabase, context, userName);
        }
    }

    public static void saveListContact(Context context, ArrayList<Contact> listContact) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        for (int i = 0; i < listContact.size(); i++) {
            Contact contact = listContact.get(i);
            contact.setStatus(Constant.CONTACT_ON);
            contact.setFullName(getNameContact(context, listContact.get(i).getPhone()));
            if (!checkContactExit(context, String.valueOf(contact.getWalletId()))) {
                WalletDatabase.insertContactTask(contact);
            }
        }
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CONTACT));
    }

    public static void saveOnlySingleContact(Context context, Contact contact) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        contact.setStatus(Constant.CONTACT_ON);
        WalletDatabase.insertContactTask(contact);
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CONTACT));
    }

    private static String getNameContact(Context context, String phoneInput) {
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
        EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_CONTACT));
    }

    public static boolean checkContactExit(Context context, String walletId) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return null != WalletDatabase.checkContactExit(walletId);
    }

    public static Contact getCurrentContact(Context context, String walletId) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.checkContactExit(walletId);
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
        return WalletDatabase.getAllNotificationUnRead().size();
    }

    public static void deleteNotification(Context context, Long id) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.deleteNotification(id);
    }

    public static void updateNotificationRead(Context context, String read, Long id) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.updateNotificationRead(read, id);
    }

    public static List<CashTemp> getAllCashTemp(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllCashTemp();
    }

    public static void saveCashValue(Denomination cashValues, Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.insertCashValue(cashValues);
    }

    public static List<TransactionsHistoryModel> getListTransactionHistory(Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        return WalletDatabase.getListTransactionHistory();
    }

    public static void updateAccountLastAccessTime(ResponseDataUpdateMasterKey responseData, AccountInfo accountInfo, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.updateAccountLastAccessTime(responseData.getLastAccessTime(), accountInfo.getUsername());
    }

    public static void deleteAllCashValue(Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.deleteAllCashValue();
    }

    public static void saveCashTemp(CashTemp cashTemp, Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.insertCashTemp(cashTemp);
    }

    public static List<CashTemp> getAllLixiUnRead(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllLixiUnRead();
    }

    public static void updateStatusLixi(Context context, String status, int id) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.updateStatusLixi(status, id);
    }

    public static List<CashTotal> getAllCashTotal(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllCashTotal();
    }

    public static List<CashTotal> getAllCashValues(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllCashValues();
    }

    public static TransactionsHistoryModel getCurrentTransactionsHistory(Context context, String transactionSignature) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getCurrentTransactionsHistory(transactionSignature);
    }

    public static List<CacheData> getAllCacheData(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllCacheData();
    }

    public static void saveCacheData(CacheData_Database cacheData_database, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertOnlySingleCacheData(cacheData_database, Constant.STR_EMPTY);
    }

    public static void deleteCacheData(String transactionSignature, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.deleteCacheData(transactionSignature);
    }

    public static List<CacheSocketData> getAllCacheSocketData(Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        return WalletDatabase.getAllCacheSocketData();
    }

    public static void insertOnlySingleCacheSocketData(ResponseMessSocket responseMessSocket, Context context) {
        CacheDataSocket_Database cacheData_database = new CacheDataSocket_Database();
        cacheData_database.setCashEnc(responseMessSocket.getCashEnc());
        cacheData_database.setContent(responseMessSocket.getContent());
        cacheData_database.setId(responseMessSocket.getId());
        cacheData_database.setTime(responseMessSocket.getTime());
        cacheData_database.setType(responseMessSocket.getType());
        cacheData_database.setSender(responseMessSocket.getSender());
        cacheData_database.setReceiver(responseMessSocket.getReceiver());
        cacheData_database.setRefId(responseMessSocket.getRefId());

        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.insertOnlySingleCacheSocketData(cacheData_database);
    }

    public static void deleteCacheSocketData(String id, Context context) {
        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        WalletDatabase.deleteCacheSocketData(id);
    }

    public static boolean checkTransactionsLogs(Context context) {
        boolean isValid = true;
        TransactionLog_Database previousRecord;
        String mPreviousHash;

        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        List<TransactionLog_Database> transactionsList = WalletDatabase.getAllTransactionLog();
        if (transactionsList == null) return true;
        for (int i = 0; i < transactionsList.size(); i++) {
            if (i == 0) {//bản ghi đầu tiên
                previousRecord = transactionsList.get(transactionsList.size() - 1);
                mPreviousHash = getSignTransactionLog(previousRecord);
            } else {
                previousRecord = transactionsList.get(i - 1);
                mPreviousHash = getSignTransactionLog(previousRecord);
            }

            if (transactionsList.get(i).getPreviousHash().compareTo(mPreviousHash) != 0) {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    public static boolean checkCashLogs(Context context) {
        boolean isValid = true;
        CashLogs_Database previousRecord;
        String mPreviousHash;

        WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
        List<CashLogs_Database> cashList = WalletDatabase.getAllCash();
        if (cashList == null) return true;
        for (int i = 0; i < cashList.size(); i++) {
            if (i == 0) {//bản ghi đầu tiên
                previousRecord = cashList.get(cashList.size() - 1);
                mPreviousHash = getSignCash(previousRecord);
            } else {
                previousRecord = cashList.get(i - 1);
                mPreviousHash = getSignCash(previousRecord);
            }

            if (cashList.get(i).getPreviousHash().compareTo(mPreviousHash) != 0) {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    public static void insertPayment(Context context, final Payment_DataBase payment) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.insertPayment(payment);
    }

    public static Payment_DataBase getPayment(Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        return WalletDatabase.getPayment();
    }

    public static void deletePayment(Context context, int id) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.deletePayment(id);
    }

    public static void saveNotification(Context context, String title, String body) {
        Notification_Database notification = new Notification_Database();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setDate(CommonUtils.getCurrentTimeNotification());
        notification.setRead(Constant.ON);
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        WalletDatabase.insertNotificationTask(notification);
    }

    public static AccountInfo getAccountInfo(Context context) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        List<AccountInfo> accountInfoList = WalletDatabase.getAllProfile();
        if (null != accountInfoList) {
            if (accountInfoList.size() > 0) {
                return accountInfoList.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
