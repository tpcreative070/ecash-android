package vn.ecpay.ewallet.database.ecash;

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

import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.database.table.Cash;

@Database(entities = {Cash.class}, version = 1, exportSchema = false)
public abstract class CashDatabase extends RoomDatabase {
    private static CashDatabase cashDatabase;
    private static SafeHelperFactory factory;

    public abstract EcashAccess daoAccess();

    @NotNull
    private static EditText getEditText(String newPass, Context context) {
        EditText editText = new EditText(context);
        editText.setText(newPass);
        return editText;
    }

    public static boolean changeKey(CharSequence newPass) {
        try {
            SafeHelperFactory.rekey(cashDatabase.mDatabase, new SpannableStringBuilder(newPass));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static CashDatabase getINSTANCE(Context context, String pass) {
        if (pass != null) {
            if (cashDatabase == null) {
                if (factory == null)
                    factory = SafeHelperFactory.fromUser(getEditText(pass, context).getText());
                cashDatabase = Room.databaseBuilder(context, CashDatabase.class, Constant.DATABASE_NAME)
                        .openHelperFactory(factory)
                        .allowMainThreadQueries()
                        .build();
            }
            return cashDatabase;
        }
        return null;
    }

    public static void insertTask(Cash cash, String userName) {
        Cash mCash = new Cash();
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
        insertTask(mCash);
    }

    private static void insertTask(final Cash cash) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cashDatabase.daoAccess().insertOnlySingleCash(cash);
                return null;
            }
        }.execute();
    }

    public static List<Cash> getAllCash() {
        try {
            return cashDatabase.daoAccess().getAllCash();
        } catch (Exception e) {
            return null;
        }
    }

    public static int getMaxIDCash() {
        return cashDatabase.daoAccess().getMaxID();
    }

    public static int getMinIDCash() {
        return cashDatabase.daoAccess().getMinID();
    }

    public static void updatePreviousHashMin(String previousHash, int minID) {
        cashDatabase.daoAccess().update(previousHash, minID);
    }

    public static Cash getCashByMaxID(int maxID) {
        return cashDatabase.daoAccess().getCashByMaxID(maxID);
    }

    public static int getTotalCash(String type) {
        try {
            return cashDatabase.daoAccess().getTotalCash(type);
        }catch (Exception e){
            return 0;
        }
    }

    public static int getTotalMoney(String money, String type) {
        return cashDatabase.daoAccess().getListCashForMoney(money, type).size();
    }

    public static List<Cash> getListCashForMoney(String money, String type) {
        return cashDatabase.daoAccess().getListCashForMoney(money, type);
    }
}
