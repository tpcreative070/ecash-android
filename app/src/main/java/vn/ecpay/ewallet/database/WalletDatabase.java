package vn.ecpay.ewallet.database;

import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.widget.EditText;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.table.CacheDataSocket_Database;
import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.database.table.CashInvalid_Database;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.CashTemp_Database;
import vn.ecpay.ewallet.database.table.CashValues_Database;
import vn.ecpay.ewallet.database.table.Contact_Database;
import vn.ecpay.ewallet.database.table.Decision_Database;
import vn.ecpay.ewallet.database.table.IssuersDiary_Database;
import vn.ecpay.ewallet.database.table.Issuers_Database;
import vn.ecpay.ewallet.database.table.MerchantsDiary_Database;
import vn.ecpay.ewallet.database.table.Merchants_Database;
import vn.ecpay.ewallet.database.table.Notification_Database;
import vn.ecpay.ewallet.database.table.Payment_DataBase;
import vn.ecpay.ewallet.database.table.Profile_Database;
import vn.ecpay.ewallet.database.table.TransactionLogQR_Database;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.database.table.TransactionTimeOut_Database;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.cacheData.CacheData;
import vn.ecpay.ewallet.model.account.cacheData.CacheSocketData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.cashValue.response.Denomination;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.lixi.CashTemp;
import vn.ecpay.ewallet.model.notification.NotificationObj;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;

@Database(entities = {Contact_Database.class,
        CashLogs_Database.class,
        Decision_Database.class,
        Profile_Database.class,
        TransactionLog_Database.class,
        CashInvalid_Database.class,
        TransactionTimeOut_Database.class,
        TransactionLogQR_Database.class,
        Notification_Database.class,
        CashTemp_Database.class,
        CashValues_Database.class,
        Issuers_Database.class,
        IssuersDiary_Database.class,
        Merchants_Database.class,
        MerchantsDiary_Database.class,
        CacheData_Database.class,
        Payment_DataBase.class,
        CacheDataSocket_Database.class}, version = Constant.DATABASE_VERSION, exportSchema = false)
public abstract class WalletDatabase extends RoomDatabase {
    private static WalletDatabase walletDatabase;
    private static SafeHelperFactory factory;
    public static int numberRequest = 0;

    public abstract WalletAccess daoAccess();

    @NotNull
    private static EditText getEditText(String newPass, Context context) {
        EditText editText = new EditText(context);
        editText.setText(newPass);
        return editText;
    }

    public static void changeKey(CharSequence newPass) {
        try {
            SafeHelperFactory.rekey(walletDatabase.mDatabase, new SpannableStringBuilder(newPass));
            walletDatabase = null;
            factory = null;
        } catch (Exception ignored) {
        }
    }

    public static WalletDatabase getINSTANCE(Context context, String pass) {
        if (pass != null) {
            if (walletDatabase == null) {
                if (factory == null)
                    factory = SafeHelperFactory.fromUser(getEditText(pass, context).getText());
                walletDatabase = Room.databaseBuilder(context, WalletDatabase.class, Constant.DATABASE_NAME)
                        .openHelperFactory(factory)
                        .allowMainThreadQueries()
                        .build();
            }
            return walletDatabase;
        }
        return null;
    }

    public static void clearAllTable() {
        walletDatabase.clearAllTables();
    }

    // todo cache data---------------------------------------------------------------------------------------
    public static void insertOnlySingleCacheData(final CacheData_Database cacheData, String fake) {
        walletDatabase.daoAccess().insertOnlySingleCacheData(cacheData);
    }

    public static List<CacheData> getAllCacheData() {
        return walletDatabase.daoAccess().getAllCacheData();
    }

    public static void deleteCacheData(String transactionSignature) {
        walletDatabase.daoAccess().deleteCacheData(transactionSignature);
    }

    // todo cache data socket---------------------------------------------------------------------------------------
    public static void insertOnlySingleCacheSocketData(final CacheDataSocket_Database cacheData) {
        walletDatabase.daoAccess().insertOnlySingleCacheSocketData(cacheData);
    }

    public static List<CacheSocketData> getAllCacheSocketData() {
        return walletDatabase.daoAccess().getAllCacheSocketData();
    }

    public static void deleteCacheSocketData(String id) {
        walletDatabase.daoAccess().deleteCacheSocketData(id);
    }

    // todo notification---------------------------------------------------------------------------------------
    public static void insertNotificationTask(final Notification_Database notification) {
        walletDatabase.daoAccess().insertOnlySingleNotification(notification);
    }

    public static List<NotificationObj> getAllNotification() {
        return walletDatabase.daoAccess().getAllNotification();
    }

    public static List<NotificationObj> getAllNotificationUnRead() {
        return walletDatabase.daoAccess().getAllNotificationUnRead();
    }

    public static void deleteAllNotification() {
        walletDatabase.daoAccess().deleteAllNotification();
    }

    public static void deleteNotification(Long id) {
        walletDatabase.daoAccess().deleteNotification(id);
    }

    public static void updateNotificationRead(String read, Long id) {
        walletDatabase.daoAccess().updateNotificationRead(read, id);
    }


    // todo Cash_Value-------------------------------------------------------------------------------
    public static void insertCashValue(Denomination cashValues) {
        CashValues_Database cashValues_database = new CashValues_Database();
        cashValues_database.setParValue(cashValues.getValue());
        walletDatabase.daoAccess().insertCashValue(cashValues_database);
    }

    public static void deleteAllCashValue() {
        walletDatabase.daoAccess().deleteAllCashValue();
    }

    public static List<CashTotal> getAllCashValues() {
        return walletDatabase.daoAccess().getAllCashValue();
    }

    public static List<CashTotal> getAllCashTotal() {
        return walletDatabase.daoAccess().getAllCashTotal();
    }


    // todo Cash_Temp-------------------------------------------------------------------------------

    public static void insertCashTemp(CashTemp cashTemp) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                CashTemp_Database temp_database = new CashTemp_Database();
                temp_database.setSenderAccountId(cashTemp.getSenderAccountId());
                temp_database.setStatus(Constant.CLOSE);
                temp_database.setContent(cashTemp.getContent());
                temp_database.setReceiveDate(cashTemp.getReceiveDate());
                temp_database.setTransactionSignature(cashTemp.getTransactionSignature());
                temp_database.setReceiveDate(CommonUtils.getCurrentTime());
                walletDatabase.daoAccess().insertCashTemp(temp_database);
                return null;
            }
        }.execute();
    }

    public static List<CashTemp> getAllCashTemp() {
        return walletDatabase.daoAccess().getAllCashTemp();
    }

    public static List<CashTemp> getAllLixiUnRead() {
        return walletDatabase.daoAccess().getAllLixiUnRead();
    }

    public static void updateStatusLixi(String status, int id) {
        walletDatabase.daoAccess().updateStatusLixi(status, id);
    }


    // todo contact---------------------------------------------------------------------------------------
    public static void insertContactTask(final Contact mContact) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Contact_Database contact = new Contact_Database();
                contact.setWalletId(mContact.getWalletId());
                contact.setFullName(mContact.getFullName());
                contact.setPublicKeyValue(mContact.getPublicKeyValue());
                contact.setPhone(mContact.getPhone());
                contact.setStatus(mContact.getStatus());
                walletDatabase.daoAccess().insertOnlySingleContact(contact);
                return null;
            }
        }.execute();
    }

    public static List<Contact> getListContact(String walletId) {
        return walletDatabase.daoAccess().getAllContact(walletId);
    }

    public static List<Contact> getListContactFilter(String filter, Long walletId) {
        return walletDatabase.daoAccess().getAllContactFilter(filter, walletId);
    }

    public static void deleteContact(Long walletId) {
        walletDatabase.daoAccess().deleteContact(walletId);
    }

    public static void updateNameContact(String name, Long walletId) {
        walletDatabase.daoAccess().updateNameContact(name, walletId);
    }

    public static void updateStatusContact(int status, Long walletId) {
        walletDatabase.daoAccess().updateStatusContact(status, walletId);
    }

    public static Contact checkContactExit(String strWalletId) {
        return walletDatabase.daoAccess().checkContactExit(strWalletId);
    }


    // todo cash------------------------------------------------------------------------------------------
    public static void insertCashTask(Context context, CashLogs_Database cash, String userName) {
        CashLogs_Database mCash = new CashLogs_Database();
        mCash.setCountryCode(cash.getCountryCode());
        mCash.setIssuerCode(cash.getIssuerCode());
        mCash.setDecisionNo(cash.getDecisionNo());
        mCash.setSerialNo(cash.getSerialNo());
        mCash.setParValue(cash.getParValue());
        mCash.setActiveDate(cash.getActiveDate());
        mCash.setExpireDate(cash.getExpireDate());
        mCash.setCycle(cash.getCycle());
        mCash.setTreSign(cash.getTreSign());
        mCash.setAccSign(cash.getAccSign());
        mCash.setType(cash.getType());
        mCash.setTransactionSignature(cash.getTransactionSignature());
        insertCashTask(context, mCash);
    }

    private static void insertCashTask(Context context, final CashLogs_Database cash) {
        numberRequest = numberRequest + 1;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cash.setPreviousHash(getPreviousHashCash(context, cash));
                walletDatabase.daoAccess().insertOnlySingleCash(cash);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                numberRequest = numberRequest - 1;
            }
        }.execute();
    }

    private static String getPreviousHashCash(Context context, CashLogs_Database cash) {
        List<CashLogs_Database> cashList = getAllCash(context);
        if (cashList.size() > 0) {
            //get cash max id
            CashLogs_Database cashMax = getCashByMaxID(context, getMaxIDCash(context));

            //updatePreviousCash min id => new PreviousHash
            updatePreviousCashMin(context, DatabaseUtil.getSignCash(cash), getMinIDCash(context));

            //ok
            return DatabaseUtil.getSignCash(cashMax);
        } else {
            return DatabaseUtil.getSignCash(cash);
        }
    }

    public static List<CashLogs_Database> getAllCash(Context context) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getAllCash();
    }

    public static int getMaxIDCash(Context context) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getCashMaxID();
    }

    public static int getMinIDCash(Context context) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getCashMinID();
    }

    public static void updatePreviousCashMin(Context context, String previousHash, int minID) {
        getINSTANCE(context, ECashApplication.masterKey);
        walletDatabase.daoAccess().updatePreviousCash(previousHash, minID);
    }

    public static CashLogs_Database getCashByMaxID(Context context, int maxID) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getCashByMaxID(maxID);
    }

    public static int getTotalCash(String type) {
        return walletDatabase.daoAccess().getTotalCash(type);
    }

    public static List<CashLogs_Database> getListCashForMoney(String money, String type) {
        return walletDatabase.daoAccess().getListCashForMoney(money, type);
    }

    // todo Cash_Invalid----------------------------------------------------------------------------------
    public static void insertCashInvalidTask(Context context, CashLogs_Database cashLogs_database, String userName) {
        CashInvalid_Database cashInvalid_database = new CashInvalid_Database();
        cashInvalid_database.setUserName(userName);
        cashInvalid_database.setCountryCode(cashLogs_database.getCountryCode());
        cashInvalid_database.setIssuerCode(cashLogs_database.getIssuerCode());
        cashInvalid_database.setDecisionNo(cashLogs_database.getDecisionNo());
        cashInvalid_database.setSerialNo(cashLogs_database.getSerialNo());
        cashInvalid_database.setParValue(cashLogs_database.getParValue());
        cashInvalid_database.setActiveDate(cashLogs_database.getActiveDate());
        cashInvalid_database.setExpireDate(cashLogs_database.getExpireDate());
        cashInvalid_database.setCycle(cashLogs_database.getCycle());
        cashInvalid_database.setTreSign(cashLogs_database.getTreSign());
        cashInvalid_database.setAccSign(cashLogs_database.getAccSign());
        cashInvalid_database.setType(cashLogs_database.getType());
        cashInvalid_database.setTransactionSignature(cashLogs_database.getTransactionSignature());
        insertCashInvalidTask(context, cashInvalid_database, cashLogs_database);
    }

    private static void insertCashInvalidTask(Context context, final CashInvalid_Database cash, CashLogs_Database cashLogs_database) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cash.setPreviousHash(getPreviousHashCash(context, cashLogs_database));
                walletDatabase.daoAccess().insertOnlySingleCashInvalid(cash);
                return null;
            }
        }.execute();
    }

    // todo Decision_Database--------------------------------------------------------------------------------------
    public static void insertDecisionTask(Decision_Database decision) {
        Decision_Database mDecision = new Decision_Database();
        mDecision.setDecisionNo(decision.getDecisionNo());
        mDecision.setAccountPublicKeyValue(decision.getAccountPublicKeyValue());
        mDecision.setTreasurePublicKeyValue(decision.getTreasurePublicKeyValue());
        insertDecisionTask(mDecision, Constant.STR_EMPTY);
    }

    private static void insertDecisionTask(final Decision_Database decision, String fake) {
        walletDatabase.daoAccess().insertOnlySingleDecision(decision);
    }

    public static Decision_Database getDecisionNo(String decisionNo) {
        return walletDatabase.daoAccess().getDecisionNo(decisionNo);
    }

    // todo profile---------------------------------------------------------------------------------------
    public static void insertAccountInfoTask(AccountInfo accountInfo) {
        Profile_Database user = new Profile_Database();
        user.setWalletId(accountInfo.getWalletId());
        user.setKeyPublicAlias(accountInfo.getKeyPublicAlias());
        user.setEcKeyPublicValue(accountInfo.getEcKeyPublicValue());
        user.setAccountIdt(accountInfo.getAccountIdt());
        user.setChannelCode(accountInfo.getChannelCode());
        user.setChannelSignature(accountInfo.getChannelSignature());
        user.setChannelId(accountInfo.getChannelId());
        user.setDateCreated(accountInfo.getDateCreated());
        user.setFunctionCode(accountInfo.getFunctionCode());
        user.setUsername(accountInfo.getUsername());
        user.setPersonMobilePhone(accountInfo.getPersonMobilePhone());
        user.setPersonLastName(accountInfo.getPersonLastName());
        user.setPersonMiddleName(accountInfo.getPersonMiddleName());
        user.setPersonFirstName(accountInfo.getPersonFirstName());
        user.setNickname(accountInfo.getNickname());
        user.setIdNumber(accountInfo.getIdNumber());
        user.setTerminalId(accountInfo.getTerminalId());
        user.setTerminalInfo(accountInfo.getTerminalInfo());
        user.setCustomerId(accountInfo.getCustomerId());
        user.setFunctionId(accountInfo.getFunctionId());
        user.setGroupId(accountInfo.getGroupId());
        user.setPassword(CommonUtils.getToken(accountInfo));
        user.setUserId(accountInfo.getUserId());
        user.setPersonEmail(accountInfo.getPersonEmail());
        user.setPersonCurrentAddress(accountInfo.getPersonCurrentAddress());
        user.setLarge(accountInfo.getLarge());
        user.setLastAccessTime(accountInfo.getLastAccessTime());
        insertAccountInfoTask(user, Constant.STR_EMPTY);
    }

    private static void insertAccountInfoTask(final Profile_Database accountInfo, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleUser(accountInfo);
                return null;
            }
        }.execute();
    }

    public static List<AccountInfo> getAllProfile() {
        try {
            return walletDatabase.daoAccess().getAllProfile();
        } catch (Exception e) {
            return null;
        }
    }

    public static void updateAccountInfo(String fistName, String lastName, String middleName, String idNumber,
                                         String address, String email, String userName) {
        walletDatabase.daoAccess().updateAccountInfo(fistName, lastName, middleName, idNumber,
                address, email, userName);
    }

    public static void updateFullAccountInfo(String fistName, String lastName, String middleName, String idNumber,
                                             String address, String email, String bIconLarge, String sessionId, String userName, String token) {
        walletDatabase.daoAccess().updateFullAccountInfo(fistName, lastName, middleName, idNumber,
                address, email, bIconLarge, sessionId, userName, token);
    }

    public static void updateAccountAvatar(String bIconLarge, String userName) {
        walletDatabase.daoAccess().updateAvatar(bIconLarge, userName);
    }

    public static void updateAccountLastAccessTime(String time, String userName) {
        walletDatabase.daoAccess().updateAccessTime(time, userName);
    }

    // todo transaction_log_QR_code-------------------------------------------------------------------------------
    public static void insertTransactionLogQRTask(final TransactionLogQR_Database transactionLog, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleTransactionLogQR(transactionLog);
                return null;
            }
        }.execute();
    }

    public static List<QRCodeSender> getAllTransactionLogQR(String transactionSignature) {
        return walletDatabase.daoAccess().getAllTransactionLogQR(transactionSignature);
    }


    // todo transaction_log-------------------------------------------------------------------------------
    public static void insertTransactionLogTask(Context context, TransactionLog_Database transactionLog) {
        TransactionLog_Database mTransactionLog = new TransactionLog_Database();
        mTransactionLog.setSenderAccountId(transactionLog.getSenderAccountId());
        mTransactionLog.setReceiverAccountId(transactionLog.getReceiverAccountId());
        mTransactionLog.setType(transactionLog.getType());
        mTransactionLog.setTime(transactionLog.getTime());
        mTransactionLog.setContent(transactionLog.getContent());
        mTransactionLog.setCashEnc(transactionLog.getCashEnc());
        mTransactionLog.setRefId(transactionLog.getRefId());
        mTransactionLog.setTransactionSignature(transactionLog.getTransactionSignature());
        insertTransactionLogTask(context, mTransactionLog, Constant.STR_EMPTY);
    }

    public static void insertTransactionLogTask(Context context, final TransactionLog_Database transactionLog, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                transactionLog.setPreviousHash(getPreviousHashTransactionLog(context, transactionLog));
                walletDatabase.daoAccess().insertOnlySingleTransactionLog(transactionLog);
                return null;
            }
        }.execute();
    }

    //ma hoa mat xich transaction log
    private static String getPreviousHashTransactionLog(Context context, TransactionLog_Database transactionLog) {
        List<TransactionLog_Database> transactionLogList = getAllTransactionLog(context);
        if (transactionLogList.size() > 0) {
            //get transaction_log max id
            TransactionLog_Database TransactionLogMax = getTransactionLogByMaxID(context, getMaxIDTransactionLog(context));

            //updatePrevious transaction_log min id => new PreviousHash
            updatePreviousTransactionLogMin(context, DatabaseUtil.getSignTransactionLog(transactionLog), getMinIDTransactionLog(context));

            //ok
            return DatabaseUtil.getSignTransactionLog(TransactionLogMax);
        } else {
            return DatabaseUtil.getSignTransactionLog(transactionLog);
        }
    }

    public static TransactionLog_Database checkTransactionLogExit(Context context, String transactionSignature) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().checkTransactionLogExit(transactionSignature);
    }

    public static CashTemp checkCashTempExit(Context context, String transactionSignature) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().checkCashTempExit(transactionSignature);
    }

    public static List<TransactionLog_Database> getAllTransactionLog(Context context) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getAllTransactionLog();
    }

    private static int getMaxIDTransactionLog(Context context) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getMaxIDTransactionLog();
    }

    private static int getMinIDTransactionLog(Context context) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getMinIDTransactionLog();
    }

    private static void updatePreviousTransactionLogMin(Context context, String previousHash, int minID) {
        getINSTANCE(context, ECashApplication.masterKey);
        walletDatabase.daoAccess().updatePreviousTransactionLogMin(previousHash, minID);
    }

    private static TransactionLog_Database getTransactionLogByMaxID(Context context, int maxIDTransactionLog) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getTransactionLogByMaxID(maxIDTransactionLog);
    }

    public static List<TransactionsHistoryModel> getListTransactionHistory(Context context) {
        getINSTANCE(context, ECashApplication.masterKey);
        return walletDatabase.daoAccess().getAllTransactionsHistory();
    }

    public static TransactionsHistoryModel getCurrentTransactionsHistory(String transactionSignature) {
        return walletDatabase.daoAccess().getCurrentTransactionsHistory(transactionSignature);
    }

    public static List<TransactionsHistoryModel> getListTransactionHistoryFilter(String filter) {
        return walletDatabase.daoAccess().getAllTransactionsHistoryOnlyFilter(filter);
    }

    public static List<CashLogTransaction> getAllCashByTransactionLog(String filter) {
        return walletDatabase.daoAccess().getAllCashByTransactionLog(filter);
    }

    public static List<CashLogTransaction> getAllCashByTransactionLogByType(String filter, String type) {
        return walletDatabase.daoAccess().getAllCashByTransactionLogByType(filter, type);
    }

    public static List<TransactionsHistoryModel> getAllTransactionsHistoryFilter(String date, String type, String status) {
        String strTransactionsHistoryQuery = "SELECT 0 as isSection, IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') as senderName, TRAN.senderAccountId, " +
                "IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') as receiverName, TRAN.receiverAccountId, " +
                "TRAN.type AS transactionType, TRAN.time AS transactionDate, TRAN.content AS transactionContent, TRAN.transactionSignature, TRAN.cashEnc, " +
                "IFNULL((SELECT SUM(CASH_LOGS.parValue) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature), 0) as transactionAmount, " +
                "IFNULL((SELECT DISTINCT CASH_LOGS.type FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature),'') AS cashLogType, " +
                "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverPhone, " +
                "IFNULL((SELECT COUNT(TIMEOUT.transactionSignature) FROM TRANSACTIONS_TIMEOUT as TIMEOUT " +
                "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) as transactionStatus FROM TRANSACTIONS_LOGS as TRAN WHERE 1=1 ";
        if (date != null) {
            strTransactionsHistoryQuery += String.format("AND substr(TRAN.time,1, 6) = '%s' ", date);
        }
        if (type != null) {
            strTransactionsHistoryQuery += String.format("AND TRAN.Type = '%s' ", type);
        }
        if (status != null) {
            strTransactionsHistoryQuery += String.format("AND transactionStatus = %s ", status);
        }
        strTransactionsHistoryQuery += "ORDER BY TRAN.id DESC";

        return walletDatabase.daoAccess().getAllTransactionsHistoryFilter(new SimpleSQLiteQuery(strTransactionsHistoryQuery));
    }

    public static void insertPayment(final Payment_DataBase payment) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySinglePayment(payment);
                return null;
            }
        }.execute();
    }

    public static Payment_DataBase getPayment() {
        return walletDatabase.daoAccess().getSinglePayment();
    }

    public static void deletePayment(int id) {
        walletDatabase.daoAccess().deletePayment(id);
    }

}
