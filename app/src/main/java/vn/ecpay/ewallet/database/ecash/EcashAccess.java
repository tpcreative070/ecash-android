//package vn.ecpay.ewallet.database.ecash;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.Query;
//
//import java.util.List;
//
//import vn.ecpay.ewallet.database.table.CashLogs;
//
//@Dao
//public interface EcashAccess {
//    @Insert
//    void insertOnlySingleCash(CashLogs cash);
//
//    @Insert
//    void insertMultipleCash(List<CashLogs> cashList);
//
//    @Query("SELECT * FROM CASH")
//    List<CashLogs> getAllCash();
//
//    @Query("SELECT MAX(id) FROM CASH")
//    int getMaxID();
//
//    @Query("SELECT MIN(id) FROM CASH")
//    int getMinID();
//
//    @Query("UPDATE CASH SET previousHash=:mPreviousHash WHERE id = :minID")
//    void update(String mPreviousHash, int minID);
//
//    @Query("SELECT * FROM CASH WHERE id=:maxID")
//    CashLogs getCashByMaxID(int maxID);
//
//    @Query("SELECT SUM(parValue) FROM CASH WHERE type LIKE :input")
//    int getTotalCash(String input);
//
//    @Query("SELECT DISTINCT id,userName,countryCode,issuerCode,decisionNo,serialNo,parValue,activeDate,expireDate,accSign,cycle,treSign,type,transactionSignature,previousHash  FROM CASH WHERE type LIKE :type AND serialNo IN (SELECT serialNo FROM CASH  GROUP BY serialNo HAVING COUNT(serialNo)%2) AND parValue =:money")
//    List<CashLogs> getListCashForMoney(String money, String type);
//
//}
//
