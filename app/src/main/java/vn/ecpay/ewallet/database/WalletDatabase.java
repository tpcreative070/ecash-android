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

import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
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
import vn.ecpay.ewallet.database.table.Profile_Database;
import vn.ecpay.ewallet.database.table.TransactionLogQR_Database;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.database.table.TransactionTimeOut_Database;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
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
        MerchantsDiary_Database.class}, version = Constant.DATABASE_VERSION, exportSchema = false)
public abstract class WalletDatabase extends RoomDatabase {
    private static WalletDatabase walletDatabase;
    private static SafeHelperFactory factory;

    public abstract WalletAccess daoAccess();

    @NotNull
    private static EditText getEditText(String newPass, Context context) {
        EditText editText = new EditText(context);
        editText.setText(newPass);
        return editText;
    }

    public static boolean changeKey(CharSequence newPass) {
        try {
            SafeHelperFactory.rekey(walletDatabase.mDatabase, new SpannableStringBuilder(newPass));
            walletDatabase = null;
            factory = null;
            return true;
        } catch (Exception e) {
            return false;
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

    // todo notification---------------------------------------------------------------------------------------
    public static void insertNotificationTask(final Notification_Database notification, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleNotification(notification);
                return null;
            }
        }.execute();
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                CashValues_Database cashValues_database = new CashValues_Database();
                cashValues_database.setParValue(cashValues.getValue());
                walletDatabase.daoAccess().insertCashValue(cashValues_database);
                return null;
            }
        }.execute();
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
    public static void insertCashTask(CashLogs_Database cash, String userName) {
        CashLogs_Database mCash = new CashLogs_Database();
        mCash.setUserName(userName);
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
        mCash.setPreviousHash(cash.getPreviousHash());
        insertCashTask(mCash);
    }

    private static void insertCashTask(final CashLogs_Database cash) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleCash(cash);
                return null;
            }
        }.execute();
    }

    public static List<CashLogs_Database> getAllCash() {
        try {
            return walletDatabase.daoAccess().getAllCash();
        } catch (Exception e) {
            return null;
        }
    }

    public static int getMaxIDCash() {
        return walletDatabase.daoAccess().getCashMaxID();
    }

    public static int getMinIDCash() {
        return walletDatabase.daoAccess().getCashMinID();
    }

    public static void updatePreviousCashMin(String previousHash, int minID) {
        walletDatabase.daoAccess().updatePreviousCash(previousHash, minID);
    }

    public static CashLogs_Database getCashByMaxID(int maxID) {
        return walletDatabase.daoAccess().getCashByMaxID(maxID);
    }

    public static int getTotalCash(String type) {
        try {
            return walletDatabase.daoAccess().getTotalCash(type);
        } catch (Exception e) {
            return 0;
        }
    }

    public static List<CashLogs_Database> getListCashForMoney(String money, String type) {
        return walletDatabase.daoAccess().getListCashForMoney(money, type);
    }

    // todo Cash_Invalid----------------------------------------------------------------------------------
    public static void insertCashInvalidTask(CashLogs_Database cash, String userName) {
        CashInvalid_Database mCash = new CashInvalid_Database();
        mCash.setUserName(userName);
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
        mCash.setPreviousHash(cash.getPreviousHash());
        insertCashInvalidTask(mCash);
    }

    private static void insertCashInvalidTask(final CashInvalid_Database cash) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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

    public static AccountInfo getAccountInfoTask(String username) {
        try {
            return walletDatabase.daoAccess().fetchOneUserByUserId(username);
        } catch (Exception e) {
            return null;
        }
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

    public static void updateAccountAvatar(String bIconLarge, String userName) {
        walletDatabase.daoAccess().updateAvatar(bIconLarge, userName);
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
    public static void insertTransactionLogTask(TransactionLog_Database transactionLog) {
        TransactionLog_Database mTransactionLog = new TransactionLog_Database();
        mTransactionLog.setSenderAccountId(transactionLog.getSenderAccountId());
        mTransactionLog.setReceiverAccountId(transactionLog.getReceiverAccountId());
        mTransactionLog.setType(transactionLog.getType());
        mTransactionLog.setTime(transactionLog.getTime());
        mTransactionLog.setContent(transactionLog.getContent());
        mTransactionLog.setCashEnc(transactionLog.getCashEnc());
        mTransactionLog.setRefId(transactionLog.getRefId());
        mTransactionLog.setTransactionSignature(transactionLog.getTransactionSignature());
        mTransactionLog.setPreviousHash(transactionLog.getPreviousHash());
        insertTransactionLogTask(mTransactionLog, Constant.STR_EMPTY);
    }

    public static void insertTransactionLogTask(final TransactionLog_Database transactionLog, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleTransactionLog(transactionLog);
                return null;
            }
        }.execute();
    }

    public static TransactionLog_Database checkTransactionLogExit(String transactionSignature) {
        return walletDatabase.daoAccess().checkTransactionLogExit(transactionSignature);
    }

    public static CashTemp checkCashTempExit(String transactionSignature) {
        return walletDatabase.daoAccess().checkCashTempExit(transactionSignature);
    }

    public static List<TransactionLog_Database> getAllTransactionLog() {
        return walletDatabase.daoAccess().getAllTransactionLog();
    }

    public static int getMaxIDTransactionLog() {
        return walletDatabase.daoAccess().getMaxIDTransactionLog();
    }

    public static int getMinIDTransactionLog() {
        return walletDatabase.daoAccess().getMinIDTransactionLog();
    }

    public static void updatePreviousTransactionLogMin(String previousHash, int minID) {
        walletDatabase.daoAccess().updatePreviousTransactionLogMin(previousHash, minID);
    }

    public static TransactionLog_Database getTransactionLogByMaxID(int maxIDTransactionLog) {
        return walletDatabase.daoAccess().getTransactionLogByMaxID(maxIDTransactionLog);
    }

    public static List<TransactionsHistoryModel> getListTransactionHistory() {
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
}
