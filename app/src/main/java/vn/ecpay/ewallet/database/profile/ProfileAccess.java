//package vn.ecpay.ewallet.database.profile;
//
//
//import androidx.room.Dao;
//import androidx.room.Delete;
//import androidx.room.Insert;
//import androidx.room.Query;
//import androidx.room.Update;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import vn.ecpay.ewallet.database.object.Profile;
//import vn.ecpay.ewallet.model.account.register_response.AccountInfo;
//
//@Dao
//public interface ProfileAccess {
//    @Insert
//    void insertOnlySingleUser(Profile accountInfo);
//
//    @Insert
//    void insertMultipleUser(List<Profile> userList);
//
//    @Query("SELECT * FROM PROFILE WHERE username LIKE  :userName")
//    AccountInfo fetchOneUserByUserId(String userName);
//
//    @Query("SELECT * FROM PROFILE WHERE id =0")
//    AccountInfo getAccountOfDevice();
//
//    @Query("SELECT * FROM PROFILE")
//    List<AccountInfo> getAllProfile();
//
//    @Update
//    void updateUser(Profile accountInfo);
//
//    @Delete
//    void deleteUser(Profile accountInfo);
//
//    @Query("DELETE FROM PROFILE")
//    void deleteAllData();
//}
