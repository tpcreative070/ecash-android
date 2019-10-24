//package vn.ecpay.ewallet.database.profile;
//
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.text.Editable;
//import android.text.SpannableStringBuilder;
//import android.widget.EditText;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.sqlite.db.SupportSQLiteDatabase;
//import androidx.sqlite.db.SupportSQLiteOpenHelper;
//
//import com.commonsware.cwac.saferoom.SafeHelperFactory;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
//import vn.ecpay.ewallet.ECashApplication;
//import vn.ecpay.ewallet.common.utils.CommonUtils;
//import vn.ecpay.ewallet.database.object.Profile;
//import vn.ecpay.ewallet.model.account.register_response.AccountInfo;
//import vn.ecpay.ewallet.common.utils.Constant;
//
//@Database(entities = {Profile.class}, version = 1, exportSchema = false)
//public abstract class UserDatabase extends RoomDatabase {
//    private static UserDatabase userDatabase;
//    private static SafeHelperFactory factory;
//    private static final String DATABASE_NAME = "profile_database";
//
//    public abstract ProfileAccess daoAccess();
//
//    public static boolean changeKey(CharSequence newPass) {
//        try {
//            SafeHelperFactory.rekey(userDatabase.mDatabase, new SpannableStringBuilder(newPass));
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @NotNull
//    private static EditText getEditText(String newPass, Context context) {
//        EditText editText = new EditText(context);
//        editText.setText(newPass);
//        return editText;
//    }
//
//    public static UserDatabase getINSTANCE(Context context, String pass) {
//        if (pass != null) {
//            if (userDatabase == null) {
//                if (factory == null)
//                    factory = SafeHelperFactory.fromUser(getEditText(pass, context).getText());
//                userDatabase = Room.databaseBuilder(context, UserDatabase.class, Constant.DATABASE_NAME)
//                        .openHelperFactory(factory)
//                        .allowMainThreadQueries()
//                        .build();
//            }
//            return userDatabase;
//        }
//        return null;
//    }
//
//    public static void destroyInstance() {
//        userDatabase = null;
//    }
//
//    public static void insertCashTask(AccountInfo accountInfo) {
//        Profile user = new Profile();
//        user.setWalletId(accountInfo.getWalletId());
//        user.setKeyPublicAlias(accountInfo.getKeyPublicAlias());
//        user.setEcKeyPublicValue(accountInfo.getEcKeyPublicValue());
//        user.setAccountIdt(accountInfo.getAccountIdt());
//        user.setChannelCode(accountInfo.getChannelCode());
//        user.setChannelSignature(accountInfo.getChannelSignature());
//        user.setChannelId(accountInfo.getChannelId());
//        user.setDateCreated(accountInfo.getDateCreated());
//        user.setFunctionCode(accountInfo.getFunctionCode());
//        user.setUsername(accountInfo.getUsername());
//        user.setPersonMobilePhone(accountInfo.getPersonMobilePhone());
//        user.setPersonLastName(accountInfo.getPersonLastName());
//        user.setPersonMiddleName(accountInfo.getPersonMiddleName());
//        user.setPersonFirstName(accountInfo.getPersonFirstName());
//        user.setNickname(accountInfo.getNickname());
//        user.setIdNumber(accountInfo.getIdNumber());
//        user.setTerminalId(accountInfo.getTerminalId());
//        user.setTerminalInfo(accountInfo.getTerminalInfo());
//        user.setCustomerId(accountInfo.getCustomerId());
//        user.setFunctionId(accountInfo.getFunctionId());
//        user.setGroupId(accountInfo.getGroupId());
//        user.setPassword(CommonUtils.getToken(accountInfo));
//        user.setUserId(accountInfo.getUserId());
//        insertCashTask(user, Constant.STR_EMPTY);
//    }
//
//    private static void insertCashTask(final Profile accountInfo, String fake) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                userDatabase.daoAccess().insertOnlySingleUser(accountInfo);
//                return null;
//            }
//        }.execute();
//    }
//
//    public static AccountInfo getAccountInfoTask(String username) {
//        try {
//            return userDatabase.daoAccess().fetchOneUserByUserId(username);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static List<AccountInfo> getAllProfile() {
//        try {
//            return userDatabase.daoAccess().getAllProfile();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static void updateAccount(Profile profile) {
//        userDatabase.daoAccess().updateUser(profile);
//    }
//
//    public static void deleteAllData() {
//        userDatabase.daoAccess().deleteAllData();
//    }
//}
