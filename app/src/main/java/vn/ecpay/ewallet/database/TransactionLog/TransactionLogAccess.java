package vn.ecpay.ewallet.database.TransactionLog;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import vn.ecpay.ewallet.database.table.TransactionLog;

@Dao
public interface TransactionLogAccess {
    @Insert
    void insertOnlySingleTransactionLog(TransactionLog transactionLog);

    @Query("SELECT * FROM TRANSACTION_LOG")
    List<TransactionLog> getAllTransactionLog();

    @Query("SELECT MAX(id) FROM TRANSACTION_LOG")
    int getMaxIDTransactionLog();

    @Query("SELECT * FROM TRANSACTION_LOG WHERE id=:maxID")
    TransactionLog getTransactionLogByMaxID(int maxID);

    @Query("SELECT * FROM TRANSACTION_LOG WHERE transactionSignature Like :transactionSignature")
    TransactionLog checkTransactionLogExit(String transactionSignature);
}
