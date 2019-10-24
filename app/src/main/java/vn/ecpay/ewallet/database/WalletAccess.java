package vn.ecpay.ewallet.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vn.ecpay.ewallet.database.table.Cash;
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

    //Cash------------------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleCash(Cash cash);

    @Insert
    void insertMultipleCash(List<Cash> cashList);

    @Query("SELECT * FROM CASH")
    List<Cash> getAllCash();

    @Query("SELECT MAX(id) FROM CASH")
    int getCashMaxID();

    @Query("SELECT MIN(id) FROM CASH")
    int getCashMinID();

    @Query("UPDATE CASH SET previousHash=:mPreviousHash WHERE id = :minID")
    void updatePreviousCash(String mPreviousHash, int minID);

    @Query("SELECT * FROM CASH WHERE id=:maxID")
    Cash getCashByMaxID(int maxID);

    @Query("SELECT SUM(parValue) FROM CASH WHERE type LIKE :input")
    int getTotalCash(String input);

    @Query("SELECT DISTINCT id,userName,countryCode,issuerCode,decisionNo,serialNo,parValue,activeDate,expireDate,accSign,cycle,treSign,type,transactionSignature,previousHash  FROM CASH WHERE type LIKE :type AND serialNo IN (SELECT serialNo FROM CASH  GROUP BY serialNo HAVING COUNT(serialNo)%2) AND parValue =:money")
    List<Cash> getListCashForMoney(String money, String type);

    //Cash_Invalid----------------------------------------------------------------------------------
    @Insert
    void insertOnlySingleCashInvalid(CashInvalid cash);

    @Query("SELECT * FROM CASH_INVALID")
    List<Cash> getAllCashInvalid();

    @Query("SELECT MAX(id) FROM CASH_INVALID")
    int getCashInvalidMaxID();

    @Query("SELECT MIN(id) FROM CASH_INVALID")
    int getCashInvalidMinID();

    @Query("UPDATE CASH_INVALID SET previousHash=:mPreviousHash WHERE id = :minID")
    void updatePreviousCashInvalid(String mPreviousHash, int minID);

    @Query("SELECT * FROM CASH_INVALID WHERE id=:maxID")
    Cash getCashInvalidByMaxID(int maxID);

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

    @Query("SELECT * FROM TRANSACTION_LOG")
    List<TransactionLog> getAllTransactionLog();

    @Query("SELECT MAX(id) FROM TRANSACTION_LOG")
    int getMaxIDTransactionLog();

    @Query("SELECT MIN(id) FROM TRANSACTION_LOG")
    int getMinIDTransactionLog();

    @Query("SELECT * FROM TRANSACTION_LOG WHERE id=:maxID")
    TransactionLog getTransactionLogByMaxID(int maxID);

    @Query("SELECT * FROM TRANSACTION_LOG WHERE transactionSignature Like :transactionSignature")
    TransactionLog checkTransactionLogExit(String transactionSignature);

    @Query("UPDATE CASH SET previousHash=:previousHash WHERE id = :minID")
    void updatePreviousTransactionLogMin(String previousHash, int minID);
}
