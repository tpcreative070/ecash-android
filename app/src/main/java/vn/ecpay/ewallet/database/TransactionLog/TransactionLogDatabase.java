package vn.ecpay.ewallet.database.TransactionLog;

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
import vn.ecpay.ewallet.database.table.TransactionLog;

@Database(entities = {TransactionLog.class}, version = 1, exportSchema = false)
public abstract class TransactionLogDatabase extends RoomDatabase {
    private static TransactionLogDatabase transactionLogDatabase;
    private static SafeHelperFactory factory;
    private static final Object sLock = new Object();

    public abstract TransactionLogAccess daoAccess();

    public static boolean changeKey(CharSequence newPass) {
        try {
            SafeHelperFactory.rekey(transactionLogDatabase.mDatabase, new SpannableStringBuilder(newPass));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @NotNull
    private static EditText getEditText(String newPass, Context context) {
        EditText editText = new EditText(context);
        editText.setText(newPass);
        return editText;
    }

    public static TransactionLogDatabase getINSTANCE(Context context, String pass) {
        if (pass != null) {
            if (transactionLogDatabase == null) {
                if (factory == null)
                    factory = SafeHelperFactory.fromUser(getEditText(pass, context).getText());
                transactionLogDatabase = Room.databaseBuilder(context, TransactionLogDatabase.class, Constant.DATABASE_NAME)
                        .openHelperFactory(factory)
                        .allowMainThreadQueries()
                        .build();
            }
            return transactionLogDatabase;
        }
        return null;
    }

    public static void insertTask(TransactionLog transactionLog) {
        TransactionLog mTransactionLog = new TransactionLog();
        mTransactionLog.setSenderAccountId(transactionLog.getSenderAccountId());
        mTransactionLog.setReceiverAccountId(transactionLog.getReceiverAccountId());
        mTransactionLog.setType(transactionLog.getType());
        mTransactionLog.setTime(transactionLog.getTime());
        mTransactionLog.setContent(transactionLog.getContent());
        mTransactionLog.setCashEnc(transactionLog.getCashEnc());
        mTransactionLog.setRefId(transactionLog.getRefId());
        mTransactionLog.setTransactionSignature(transactionLog.getTransactionSignature());
        mTransactionLog.setPreviousHash(transactionLog.getPreviousHash());
        insertTask(mTransactionLog, Constant.STR_EMPTY);
    }

    private static void insertTask(final TransactionLog transactionLog, String fake) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                transactionLogDatabase.daoAccess().insertOnlySingleTransactionLog(transactionLog);
                return null;
            }
        }.execute();
    }

    public static TransactionLog checkTransactionLogExit(String transactionSignature) {
        return transactionLogDatabase.daoAccess().checkTransactionLogExit(transactionSignature);
    }

    public static List<TransactionLog> getAllTransactionLog() {
        return transactionLogDatabase.daoAccess().getAllTransactionLog();
    }

    public static int getMaxIDCash() {
        return transactionLogDatabase.daoAccess().getMaxIDTransactionLog();
    }

    public static TransactionLog getTransactionLogByMaxID(int maxIDTransactionLog) {
        return transactionLogDatabase.daoAccess().getTransactionLogByMaxID(maxIDTransactionLog);
    }
}