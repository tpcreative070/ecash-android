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

    @Query("SELECT * FROM TRANSACTION_LOGS")
    List<TransactionLog> getAllTransactionLog();

    @Query("SELECT MAX(id) FROM TRANSACTION_LOGS")
    int getMaxIDTransactionLog();

    @Query("SELECT MIN(id) FROM TRANSACTION_LOGS")
    int getMinIDTransactionLog();

    @Query("SELECT * FROM TRANSACTION_LOGS WHERE id=:maxID")
    TransactionLog getTransactionLogByMaxID(int maxID);

    @Query("SELECT * FROM TRANSACTION_LOGS WHERE transactionSignature Like :transactionSignature")
    TransactionLog checkTransactionLogExit(String transactionSignature);

    @Query("UPDATE TRANSACTION_LOGS SET previousHash=:previousHash WHERE id = :minID")
    void updatePreviousTransactionLogMin(String previousHash, int minID);
}
