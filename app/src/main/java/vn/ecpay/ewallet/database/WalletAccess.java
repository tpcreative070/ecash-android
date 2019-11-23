package vn.ecpay.ewallet.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

import vn.ecpay.ewallet.database.table.CashInvalid_Database;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.Contact_Database;
import vn.ecpay.ewallet.database.table.Decision_Database;
import vn.ecpay.ewallet.database.table.Notification_Database;
import vn.ecpay.ewallet.database.table.Profile_Database;
import vn.ecpay.ewallet.database.table.TransactionLogQR_Database;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.notification.NotificationObj;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;

@Dao
public interface WalletAccess {
    // todo Notification_Database---------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleNotification(Notification_Database notification);

    @Query("SELECT 0 as isCheck,title, body, read, id, date  FROM NOTIFICATION ORDER BY id DESC")
    List<NotificationObj> getAllNotification();

    @Query("SELECT 0 as isCheck,title, body, read, id, date  FROM NOTIFICATION WHERE read = 'on' ORDER BY id DESC")
    List<NotificationObj> getAllNotificationUnRead();

    @Query("DELETE From NOTIFICATION WHERE id = :idNotification")
    void deleteNotification(Long idNotification);

    @Query("DELETE From NOTIFICATION")
    void deleteAllNotification();

    @Query("UPDATE NOTIFICATION SET read=:read WHERE id = :id")
    void updateNotificationRead(String read, Long id);

    // todo Contact_Database---------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleContact(Contact_Database contact);

    @Query("SELECT * FROM CONTACTS where walletId != :myWalletId and status = '1'")
    List<Contact> getAllContact(String myWalletId);

    @Query("DELETE From CONTACTS WHERE walletId = :strWalletId")
    void deleteContact(Long strWalletId);

    @Query("SELECT * FROM CONTACTS Where phone LIKE  :name OR fullName like :name and walletId != :myWalletId  and status = '1'")
    List<Contact> getAllContactFilter(String name, Long myWalletId);

    @Query("UPDATE CONTACTS SET fullName=:name WHERE walletId = :walletId")
    void updateNameContact(String name, Long walletId);

    @Query("UPDATE CONTACTS SET status =:status WHERE walletId = :walletId")
    void updateStatusContact(int status, Long walletId);


    // todo Decision_Database--------------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleDecision(Decision_Database decision);

    @Query("SELECT * FROM DECISIONS_DIARY WHERE decisionNo LIKE :decisionNo")
    Decision_Database getDecisionNo(String decisionNo);

    // todo CashLogs_Database------------------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleCash(CashLogs_Database cash);

    @Insert
    void insertMultipleCash(List<CashLogs_Database> cashList);

    @Query("SELECT * FROM CASH_LOGS")
    List<CashLogs_Database> getAllCash();

    @Query("SELECT MAX(id) FROM CASH_LOGS")
    int getCashMaxID();

    @Query("SELECT MIN(id) FROM CASH_LOGS")
    int getCashMinID();

    @Query("UPDATE CASH_LOGS SET previousHash=:mPreviousHash WHERE id = :minID")
    void updatePreviousCash(String mPreviousHash, int minID);

    @Query("SELECT * FROM CASH_LOGS WHERE id=:maxID")
    CashLogs_Database getCashByMaxID(int maxID);

    @Query("SELECT SUM(parValue) FROM CASH_LOGS WHERE type LIKE :input")
    int getTotalCash(String input);

    @Query("SELECT id,userName,countryCode,issuerCode,decisionNo,serialNo,parValue,activeDate,expireDate,accSign,cycle,treSign,type,transactionSignature,previousHash  FROM CASH_LOGS WHERE type =:type AND (id + serialNo) IN (select max(id)+ serialNo from CASH_LOGS  group by serialNo having count(serialNo)%2 <> 0) AND parValue =:money")
    List<CashLogs_Database> getListCashForMoney(String money, String type);

    // todo Cash_Invalid----------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleCashInvalid(CashInvalid_Database cash);

    @Query("SELECT * FROM CASH_INVALID")
    List<CashLogs_Database> getAllCashInvalid();

    @Query("SELECT MAX(id) FROM CASH_INVALID")
    int getCashInvalidMaxID();

    @Query("SELECT MIN(id) FROM CASH_INVALID")
    int getCashInvalidMinID();

    @Query("UPDATE CASH_INVALID SET previousHash=:mPreviousHash WHERE id = :minID")
    void updatePreviousCashInvalid(String mPreviousHash, int minID);

    @Query("SELECT * FROM CASH_INVALID WHERE id=:maxID")
    CashLogs_Database getCashInvalidByMaxID(int maxID);

    @Query("SELECT SUM(parValue) FROM CASH_INVALID WHERE type LIKE :input")
    int getTotalCashInvalid(String input);

    // todo profile---------------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleUser(Profile_Database accountInfo);

    @Insert
    void insertMultipleUser(List<Profile_Database> userList);

    @Query("SELECT * FROM PROFILE WHERE username LIKE  :userName")
    AccountInfo fetchOneUserByUserId(String userName);

    @Query("SELECT * FROM PROFILE WHERE id =0")
    AccountInfo getAccountOfDevice();

    @Query("SELECT * FROM PROFILE")
    List<AccountInfo> getAllProfile();

    @Update
    void updateUser(Profile_Database accountInfo);

    @Delete
    void deleteUser(Profile_Database accountInfo);

    @Query("DELETE FROM PROFILE")
    void deleteAllData();

    // todo Transaction_log_QRCode------------------------------------------------------------------
    @Insert
    void insertOnlySingleTransactionLogQR(TransactionLogQR_Database transactionLogQR);

    @Query("SELECT value  as mContent, sequence as mCycle,total as mTotal   FROM TRANSACTION_QR WHERE transactionSignature = :transactionSignature")
    List<QRCodeSender> getAllTransactionLogQR(String transactionSignature);

    // todo Transaction_log--------------------------------------------------------------------------
    @Insert
    void insertOnlySingleTransactionLog(TransactionLog_Database transactionLog);

    @Query("SELECT * FROM TRANSACTIONS_LOGS")
    List<TransactionLog_Database> getAllTransactionLog();

    @Query("SELECT MAX(id) FROM TRANSACTIONS_LOGS")
    int getMaxIDTransactionLog();

    @Query("SELECT MIN(id) FROM TRANSACTIONS_LOGS")
    int getMinIDTransactionLog();

    @Query("SELECT * FROM TRANSACTIONS_LOGS WHERE id=:maxID")
    TransactionLog_Database getTransactionLogByMaxID(int maxID);

    @Query("SELECT * FROM TRANSACTIONS_LOGS WHERE transactionSignature = :transactionSignature")
    TransactionLog_Database checkTransactionLogExit(String transactionSignature);

    @Query("UPDATE TRANSACTIONS_LOGS SET previousHash=:previousHash WHERE id = :minID")
    void updatePreviousTransactionLogMin(String previousHash, int minID);

    @Query("SELECT 0 as isSection, IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') AS senderName, TRAN.senderAccountId, " +
            "IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverName, TRAN.receiverAccountId, " +
            "TRAN.type AS transactionType, TRAN.time  AS transactionDate , TRAN.content AS transactionContent,TRAN.transactionSignature, TRAN.cashEnc, " +
            "IFNULL((SELECT SUM(CASH_LOGS.parValue) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature), 0) AS transactionAmount, " +
            "IFNULL((SELECT DISTINCT CASH_LOGS.type FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature),'') AS cashLogType, " +
            "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverPhone, " +
            "IFNULL((SELECT COUNT(TIMEOUT.transactionSignature) FROM TRANSACTIONS_TIMEOUT AS TIMEOUT " +
            "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) AS transactionStatus FROM TRANSACTIONS_LOGS AS TRAN ORDER BY TRAN.id DESC")
    List<TransactionsHistoryModel> getAllTransactionsHistory();

    @Query("SELECT 0 as isSection, IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') AS senderName, TRAN.senderAccountId, " +
            "IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverName, TRAN.receiverAccountId, " +
            "TRAN.type AS transactionType, TRAN.time  AS transactionDate , TRAN.content AS transactionContent,TRAN.transactionSignature, TRAN.cashEnc, " +
            "IFNULL((SELECT SUM(CASH_LOGS.parValue) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature), 0) AS transactionAmount, " +
            "IFNULL((SELECT DISTINCT CASH_LOGS.type FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature),'') AS cashLogType, " +
            "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverPhone, " +
            "IFNULL((SELECT COUNT(TIMEOUT.transactionSignature) FROM TRANSACTIONS_TIMEOUT AS TIMEOUT " +
            "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) AS transactionStatus FROM TRANSACTIONS_LOGS AS TRAN " +
            "WHERE senderName like :key OR receiverName like :key OR TRAN.receiverAccountId like :key OR TRAN.receiverAccountId like :key OR TRAN.content like :key ORDER BY TRAN.id DESC")
    List<TransactionsHistoryModel> getAllTransactionsHistoryOnlyFilter(String key);

    @Query("select parValue, count(parValue) as validCount, 1 as status from CASH_LOGS where transactionSignature = :transactionSignatureLog group by parValue union " +
            "select parValue,count(parValue) as validCount , 0 as status from CASH_INVALID where transactionSignature = :transactionSignatureLog " +
            "group by parValue")
    List<CashLogTransaction> getAllCashByTransactionLog(String transactionSignatureLog);

    @RawQuery
    List<TransactionsHistoryModel> getAllTransactionsHistoryFilter(SimpleSQLiteQuery strQuery);

}
