package vn.ecpay.ewallet.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.database.table.CashInvalid_Database;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.CashTemp_Database;
import vn.ecpay.ewallet.database.table.CashValues_Database;
import vn.ecpay.ewallet.database.table.Contact_Database;
import vn.ecpay.ewallet.database.table.Decision_Database;
import vn.ecpay.ewallet.database.table.Notification_Database;
import vn.ecpay.ewallet.database.table.Payment_DataBase;
import vn.ecpay.ewallet.database.table.Profile_Database;
import vn.ecpay.ewallet.database.table.TransactionLogQR_Database;
import vn.ecpay.ewallet.database.table.TransactionLog_Database;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.cacheData.CacheData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.lixi.CashTemp;
import vn.ecpay.ewallet.model.notification.NotificationObj;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;

@Dao
public interface WalletAccess {
    // todo Data_cache---------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleCacheData(CacheData_Database cacheData_database);

    @Query("SELECT *  FROM CACHE_DATA")
    List<CacheData> getAllCacheData();

    @Query("DELETE From CACHE_DATA WHERE transactionSignature = :mTransactionSignature")
    void deleteCacheData(String mTransactionSignature);

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

    // todo Cash_Value--------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCashValue(CashValues_Database cashValues);

    @Query("DELETE FROM CASH_VALUES")
    void deleteAllCashValue();

    @Query("SELECT id, parValue, 0 as total, 0 as totalDatabase FROM CASH_VALUES ORDER BY parValue ASC")
    List<CashTotal> getAllCashValue();

    // todo Cash_temp--------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCashTemp(CashTemp_Database cashTemp_database);

    @Query("SELECT * FROM CASH_TEMP")
    List<CashTemp> getAllCashTemp();

    @Query("SELECT * FROM CASH_TEMP WHERE transactionSignature = :transactionSignature")
    CashTemp checkCashTempExit(String transactionSignature);

    @Query("SELECT *  FROM CASH_TEMP WHERE status = 'CLOSE'")
    List<CashTemp> getAllLixiUnRead();

    @Query("UPDATE CASH_TEMP SET status=:status WHERE id = :id")
    void updateStatusLixi(String status, int id);

    // todo Contact_Database---------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleContact(Contact_Database contact);

    @Query("SELECT publicKeyValue, phone, walletId, fullName, status, 0 as isSection, 0 as isAddTransfer FROM CONTACTS where walletId <>:myWalletId and status = '1'")
    List<Contact> getAllContact(String myWalletId);

    @Query("DELETE From CONTACTS WHERE walletId = :strWalletId")
    void deleteContact(Long strWalletId);

    @Query("SELECT publicKeyValue, phone, walletId, fullName, status, 0 as isSection , 0 as isAddTransfer FROM CONTACTS Where (phone LIKE :name OR fullName like :name) AND walletId <>:myWalletId AND status = '1'")
    List<Contact> getAllContactFilter(String name, Long myWalletId);

    @Query("UPDATE CONTACTS SET fullName=:name WHERE walletId = :walletId")
    void updateNameContact(String name, Long walletId);

    @Query("UPDATE CONTACTS SET status =:status WHERE walletId = :walletId")
    void updateStatusContact(int status, Long walletId);

    @Query("SELECT publicKeyValue, phone, walletId, fullName, status, 0 as isSection, 0 as isAddTransfer FROM CONTACTS WHERE walletId = :strWalletId")
    Contact checkContactExit(String strWalletId);

    // todo Decision_Database--------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleDecision(Decision_Database decision);

    @Query("SELECT * FROM DECISIONS WHERE decisionNo LIKE :decisionNo")
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

    @Query("SELECT SUM(parValue) FROM CASH_LOGS WHERE type =:input")
    int getTotalCash(String input);

    @Query("SELECT id,countryCode,issuerCode,decisionNo,serialNo,parValue,activeDate,expireDate,accSign,cycle,treSign,type,transactionSignature,previousHash  FROM CASH_LOGS WHERE type =:type AND (id + serialNo) IN (select max(id)+ serialNo from CASH_LOGS  group by serialNo having count(serialNo)%2 <> 0) AND parValue =:money")
    List<CashLogs_Database> getListCashForMoney(String money, String type);

    @Query("SELECT DISTINCT parValue, count(parValue) as totalDatabase, 0 as total  FROM CASH_LOGS WHERE type ='in' AND (id + serialNo) IN (select max(id)+ serialNo from CASH_LOGS  group by serialNo having count(serialNo)%2 <> 0) group by parValue")
    List<CashTotal> getAllCashTotal();

    //todo Cash_Invalid----------------------------------------------------------------------------------
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

    @Query("SELECT * FROM PROFILE WHERE username =:userName")
    AccountInfo fetchOneUserByUserId(String userName);

    @Query("SELECT * FROM PROFILE WHERE id =0")
    AccountInfo getAccountOfDevice();

    @Query("SELECT * FROM PROFILE")
    List<AccountInfo> getAllProfile();

    @Query("UPDATE PROFILE SET personFirstName=:fistName, personLastName=:lastName, personMiddleName=:middleName,idNumber=:idNumber,personCurrentAddress=:address, personEmail=:email WHERE username =:userName")
    void updateAccountInfo(String fistName, String lastName, String middleName, String idNumber,
                           String address, String email, String userName);

    @Query("UPDATE PROFILE SET large=:bIconLarge WHERE username =:userName")
    void updateAvatar(String bIconLarge, String userName);

    @Query("UPDATE PROFILE SET lastAccessTime=:time WHERE username =:userName")
    void updateAccessTime(String time, String userName);

    @Update
    void updateUser(Profile_Database accountInfo);

    @Delete
    void deleteUser(Profile_Database accountInfo);

    @Query("DELETE FROM PROFILE")
    void deleteAllData();

    // todo Transaction_log_QRCode------------------------------------------------------------------
    @Insert
    void insertOnlySingleTransactionLogQR(TransactionLogQR_Database transactionLogQR);

    @Query("SELECT value  as mContent, sequence as mCycle,total as mTotal FROM TRANSACTION_QR WHERE transactionSignature = :transactionSignature")
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
            "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') AS senderPhone, " +
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
            "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) AS transactionStatus FROM TRANSACTIONS_LOGS AS TRAN WHERE TRAN.transactionSignature =:transactionSignature")
    TransactionsHistoryModel getCurrentTransactionsHistory(String transactionSignature);

    @Query("SELECT 0 as isSection, IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') AS senderName, TRAN.senderAccountId, " +
            "IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverName, TRAN.receiverAccountId, " +
            "TRAN.type AS transactionType, TRAN.time  AS transactionDate , TRAN.content AS transactionContent,TRAN.transactionSignature, TRAN.cashEnc, " +
            "IFNULL((SELECT SUM(CASH_LOGS.parValue) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature), 0) AS transactionAmount, " +
            "IFNULL((SELECT DISTINCT CASH_LOGS.type FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature),'') AS cashLogType, " +
            "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverPhone, " +
            "IFNULL((SELECT COUNT(TIMEOUT.transactionSignature) FROM TRANSACTIONS_TIMEOUT AS TIMEOUT " +
            "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) AS transactionStatus FROM TRANSACTIONS_LOGS AS TRAN " +
            "WHERE (senderName like :key AND (transactionType='CT' OR transactionType='TT')) OR (receiverName like :key AND cashLogType='out' AND (transactionType='CT' OR transactionType='TT')) OR TRAN.receiverAccountId like :key OR TRAN.receiverAccountId like :key OR TRAN.content like :key ORDER BY TRAN.id DESC")
    List<TransactionsHistoryModel> getAllTransactionsHistoryOnlyFilter(String key);

    @Query("select parValue, count(parValue) as validCount, 1 as status from CASH_LOGS where transactionSignature = :transactionSignatureLog group by parValue union " +
            "select parValue,count(parValue) as validCount , 0 as status from CASH_INVALID where transactionSignature = :transactionSignatureLog " +
            "group by parValue")
    List<CashLogTransaction> getAllCashByTransactionLog(String transactionSignatureLog);

    @Query("select parValue, count(parValue) as validCount, 1 as status from CASH_LOGS where type = :typeMoney AND transactionSignature = :transactionSignatureLog group by parValue union " +
            "select parValue,count(parValue) as validCount , 0 as status from CASH_INVALID where transactionSignature = :transactionSignatureLog " +
            "group by parValue")
    List<CashLogTransaction> getAllCashByTransactionLogByType(String transactionSignatureLog, String typeMoney);

    @RawQuery
    List<TransactionsHistoryModel> getAllTransactionsHistoryFilter(SimpleSQLiteQuery strQuery);

    // todo payment---------------------------------------------------------------------------------------
    @Insert
    void insertOnlySinglePayment(Payment_DataBase payment_dataBase);

    @Delete
    void deletePayment(Payment_DataBase payment_dataBase);

    @Query("DELETE From PAYMENTS WHERE id = :id")
    void deletePayment(int id);

    @Query("SELECT * FROM PAYMENTS LIMIT 1")
    Payment_DataBase getSinglePayment();
}
