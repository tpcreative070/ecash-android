package vn.ecpay.ewallet.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.database.table.CashInvalid;
import vn.ecpay.ewallet.database.table.Contact;
import vn.ecpay.ewallet.database.table.Decision;
import vn.ecpay.ewallet.database.table.Profile;
import vn.ecpay.ewallet.database.table.TransactionLog;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.ContactTransferModel;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;

@Dao
public interface WalletAccess {
    //Contact---------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleContact(Contact contact);

    @Query("SELECT * FROM CONTACTS")
    List<ContactTransferModel> getAllContact();

    @Query("SELECT * FROM CONTACTS Where phone LIKE  :name OR fullName like :name")
    List<ContactTransferModel> getAllContactFilter(String name);

    //Decision--------------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleDecision(Decision decision);

    @Query("SELECT * FROM DECISIONS_DIARY WHERE decisionNo LIKE :decisionNo")
    Decision getDecisionNo(String decisionNo);

    //CashLogs------------------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleCash(CashLogs cash);

    @Insert
    void insertMultipleCash(List<CashLogs> cashList);

    @Query("SELECT * FROM CASH_LOGS")
    List<CashLogs> getAllCash();

    @Query("SELECT MAX(id) FROM CASH_LOGS")
    int getCashMaxID();

    @Query("SELECT MIN(id) FROM CASH_LOGS")
    int getCashMinID();

    @Query("UPDATE CASH_LOGS SET previousHash=:mPreviousHash WHERE id = :minID")
    void updatePreviousCash(String mPreviousHash, int minID);

    @Query("SELECT * FROM CASH_LOGS WHERE id=:maxID")
    CashLogs getCashByMaxID(int maxID);

    @Query("SELECT SUM(parValue) FROM CASH_LOGS WHERE type LIKE :input")
    int getTotalCash(String input);

    @Query("SELECT DISTINCT id,userName,countryCode,issuerCode,decisionNo,serialNo,parValue,activeDate,expireDate,accSign,cycle,treSign,type,transactionSignature,previousHash  FROM CASH_LOGS WHERE type LIKE :type AND serialNo IN (SELECT serialNo FROM CASH_LOGS  GROUP BY serialNo HAVING COUNT(serialNo)%2) AND parValue =:money")
    List<CashLogs> getListCashForMoney(String money, String type);

    //Cash_Invalid----------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleCashInvalid(CashInvalid cash);

    @Query("SELECT * FROM CASH_INVALID")
    List<CashLogs> getAllCashInvalid();

    @Query("SELECT MAX(id) FROM CASH_INVALID")
    int getCashInvalidMaxID();

    @Query("SELECT MIN(id) FROM CASH_INVALID")
    int getCashInvalidMinID();

    @Query("UPDATE CASH_INVALID SET previousHash=:mPreviousHash WHERE id = :minID")
    void updatePreviousCashInvalid(String mPreviousHash, int minID);

    @Query("SELECT * FROM CASH_INVALID WHERE id=:maxID")
    CashLogs getCashInvalidByMaxID(int maxID);

    @Query("SELECT SUM(parValue) FROM CASH_INVALID WHERE type LIKE :input")
    int getTotalCashInvalid(String input);

    //profile---------------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleUser(Profile accountInfo);

    @Insert
    void insertMultipleUser(List<Profile> userList);

    @Query("SELECT * FROM PROFILE WHERE username LIKE  :userName")
    AccountInfo fetchOneUserByUserId(String userName);

    @Query("SELECT * FROM PROFILE WHERE id =0")
    AccountInfo getAccountOfDevice();

    @Query("SELECT * FROM PROFILE")
    List<AccountInfo> getAllProfile();

    @Update
    void updateUser(Profile accountInfo);

    @Delete
    void deleteUser(Profile accountInfo);

    @Query("DELETE FROM PROFILE")
    void deleteAllData();

    //Transaction_log-------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleTransactionLog(TransactionLog transactionLog);

    @Query("SELECT * FROM TRANSACTIONS_LOGS")
    List<TransactionLog> getAllTransactionLog();

    @Query("SELECT MAX(id) FROM TRANSACTIONS_LOGS")
    int getMaxIDTransactionLog();

    @Query("SELECT MIN(id) FROM TRANSACTIONS_LOGS")
    int getMinIDTransactionLog();

    @Query("SELECT * FROM TRANSACTIONS_LOGS WHERE id=:maxID")
    TransactionLog getTransactionLogByMaxID(int maxID);

    @Query("SELECT * FROM TRANSACTIONS_LOGS WHERE transactionSignature Like :transactionSignature")
    TransactionLog checkTransactionLogExit(String transactionSignature);

    @Query("UPDATE TRANSACTIONS_LOGS SET previousHash=:previousHash WHERE id = :minID")
    void updatePreviousTransactionLogMin(String previousHash, int minID);

    @Query("SELECT 0 as isSection, IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') AS senderName, TRAN.senderAccountId, " +
            "IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverName, TRAN.receiverAccountId, " +
            "TRAN.type AS transactionType, TRAN.time  AS transactionDate , TRAN.content AS transactionContent,TRAN.transactionSignature, TRAN.cashEnc, " +
            "IFNULL((SELECT SUM(CASH_LOGS.parValue) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature), 0) AS transactionAmount, " +
            "IFNULL((SELECT MIN(CASH_LOGS.id) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature),'') AS cashLogType, " +
            "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverPhone, " +
            "IFNULL((SELECT COUNT(TIMEOUT.transactionSignature) FROM TRANSACTIONS_TIMEOUT AS TIMEOUT " +
            "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) AS transactionStatus FROM TRANSACTIONS_LOGS AS TRAN ")
    List<TransactionsHistoryModel> getAllTransactionsHistory();

    @Query("SELECT 0 as isSection, IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') AS senderName, TRAN.senderAccountId, " +
            "IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverName, TRAN.receiverAccountId, " +
            "TRAN.type AS transactionType, TRAN.time  AS transactionDate , TRAN.content AS transactionContent,TRAN.transactionSignature, TRAN.cashEnc, " +
            "IFNULL((SELECT SUM(CASH_LOGS.parValue) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature), 0) AS transactionAmount, " +
            "IFNULL((SELECT MIN(CASH_LOGS.id) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature),'') AS cashLogType, " +
            "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverPhone, " +
            "IFNULL((SELECT COUNT(TIMEOUT.transactionSignature) FROM TRANSACTIONS_TIMEOUT AS TIMEOUT " +
            "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) AS transactionStatus FROM TRANSACTIONS_LOGS AS TRAN " +
            "WHERE senderName like :key OR receiverName like :key OR TRAN.receiverAccountId like :key OR TRAN.receiverAccountId like :key OR TRAN.content like :key ")
    List<TransactionsHistoryModel> getAllTransactionsHistoryOnlyFilter(String key);

    @Query("select parValue, count(parValue) as validCount, 1 as status from CASH_LOGS where transactionSignature Like :transactionSignatureLog group by parValue union " +
            "select parValue,count(parValue) as validCount , 0 as status from CASH_INVALID where transactionSignature Like :transactionSignatureLog " +
            "group by parValue")
    List<CashLogTransaction> getAllCashByTransactionLog(String transactionSignatureLog);

    @Query("SELECT 0 as isSection, IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.senderAccountId), '') as senderName, TRAN.senderAccountId, " +
            "IFNULL((SELECT CONTACTS.fullName FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') as receiverName, TRAN.receiverAccountId, " +
            "TRAN.type AS transactionType, TRAN.time AS transactionDate, TRAN.content AS transactionContent, TRAN.transactionSignature, TRAN.cashEnc, " +
            "IFNULL((SELECT SUM(CASH_LOGS.parValue) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature), 0) as transactionAmount, " +
            "IFNULL((SELECT MIN(CASH_LOGS.id) FROM CASH_LOGS WHERE CASH_LOGS.transactionSignature = TRAN.transactionSignature),'') AS cashLogType, " +
            "IFNULL((SELECT CONTACTS.phone FROM CONTACTS WHERE CONTACTS.walletId = TRAN.receiverAccountId), '') AS receiverPhone, " +
            "IFNULL((SELECT COUNT(TIMEOUT.transactionSignature) FROM TRANSACTIONS_TIMEOUT as TIMEOUT " +
            "WHERE TIMEOUT.transactionSignature=TRAN.transactionSignature AND TIMEOUT.status=1), 0) as transactionStatus FROM TRANSACTIONS_LOGS as TRAN " +
            "WHERE substr(TRAN.time,1, 6) Like :date AND TRAN.Type LIKE :type AND transactionStatus LIKE :status")
    List<TransactionsHistoryModel> getAllTransactionsHistoryFilter(String date, String type, String status);

}
