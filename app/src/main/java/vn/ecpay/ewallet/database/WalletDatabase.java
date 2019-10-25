package vn.ecpay.ewallet.database;

import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.widget.EditText;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.commonsware.cwac.saferoom.SafeHelperFactory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.database.table.CashInvalid;
import vn.ecpay.ewallet.database.table.Contact;
import vn.ecpay.ewallet.database.table.Decision;
import vn.ecpay.ewallet.database.table.Profile;
import vn.ecpay.ewallet.database.table.TransactionLog;
import vn.ecpay.ewallet.database.table.TransactionTimeOut;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.getPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.contactTransfer.ContactTransferModel;

@Database(entities = {Contact.class,
        CashLogs.class,
        Decision.class,
        Profile.class,
        TransactionLog.class,
        CashInvalid.class,
        TransactionTimeOut.class}, version = 1, exportSchema = false)
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

    //contact---------------------------------------------------------------------------------------
    public static void insertContactTask(ResponseDataGetPublicKeyWallet mContact) {
        Contact contact = new Contact();
        contact.setWalletId(mContact.getmWalletId());
        contact.setFullName(CommonUtils.getFullName(mContact));
        contact.setPublicKeyValue(mContact.getEcKpValue());
        contact.setPhone(mContact.getPersonMobilePhone());
        insertContactTask(contact, Constant.STR_EMPTY);
    }

    private static void insertContactTask(final Contact contact, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleContact(contact);
                return null;
            }
        }.execute();
    }

    public static List<ContactTransferModel> getListContact() {
        return walletDatabase.daoAccess().getAllContact();
    }

    public static List<ContactTransferModel> getListContactFilter(String filter) {
        return walletDatabase.daoAccess().getAllContactFilter(filter);
    }


    //cash------------------------------------------------------------------------------------------
    public static void insertCashTask(CashLogs cash, String userName) {
        CashLogs mCash = new CashLogs();
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

    private static void insertCashTask(final CashLogs cash) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleCash(cash);
                return null;
            }
        }.execute();
    }

    public static List<CashLogs> getAllCash() {
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

    public static CashLogs getCashByMaxID(int maxID) {
        return walletDatabase.daoAccess().getCashByMaxID(maxID);
    }

    public static int getTotalCash(String type) {
        try {
            return walletDatabase.daoAccess().getTotalCash(type);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getTotalMoney(String money, String type) {
        return walletDatabase.daoAccess().getListCashForMoney(money, type).size();
    }

    public static List<CashLogs> getListCashForMoney(String money, String type) {
        return walletDatabase.daoAccess().getListCashForMoney(money, type);
    }

    //Cash_Invalid----------------------------------------------------------------------------------
    public static void insertCashInvalidTask(CashLogs cash, String userName) {
        CashInvalid mCash = new CashInvalid();
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

    private static void insertCashInvalidTask(final CashInvalid cash) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleCashInvalid(cash);
                return null;
            }
        }.execute();
    }

    //Decision--------------------------------------------------------------------------------------
    public static void insertDecisionTask(Decision decision) {
        Decision mDecision = new Decision();
        mDecision.setDecisionNo(decision.getDecisionNo());
        mDecision.setAccountPublicKeyValue(decision.getAccountPublicKeyValue());
        mDecision.setTreasurePublicKeyValue(decision.getTreasurePublicKeyValue());
        insertDecisionTask(mDecision, Constant.STR_EMPTY);
    }

    private static void insertDecisionTask(final Decision decision, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleDecision(decision);
                return null;
            }
        }.execute();
    }

    public static Decision getDecisionNo(String decisionNo) {
        return walletDatabase.daoAccess().getDecisionNo(decisionNo);
    }

    //profile---------------------------------------------------------------------------------------
    public static void insertAccountInfoTask(AccountInfo accountInfo) {
        Profile user = new Profile();
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
        insertAccountInfoTask(user, Constant.STR_EMPTY);
    }

    private static void insertAccountInfoTask(final Profile accountInfo, String fake) {
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

    public static void deleteAllData() {
        walletDatabase.daoAccess().deleteAllData();
    }

    //transaction_log-------------------------------------------------------------------------------
    public static void insertTransactionLogTask(TransactionLog transactionLog) {
        TransactionLog mTransactionLog = new TransactionLog();
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

    public static void insertTransactionLogTask(final TransactionLog transactionLog, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                walletDatabase.daoAccess().insertOnlySingleTransactionLog(transactionLog);
                return null;
            }
        }.execute();
    }

    public static TransactionLog checkTransactionLogExit(String transactionSignature) {
        return walletDatabase.daoAccess().checkTransactionLogExit(transactionSignature);
    }

    public static List<TransactionLog> getAllTransactionLog() {
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

    public static TransactionLog getTransactionLogByMaxID(int maxIDTransactionLog) {
        return walletDatabase.daoAccess().getTransactionLogByMaxID(maxIDTransactionLog);
    }
}
