//package vn.ecpay.ewallet.database.Decision;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.text.SpannableStringBuilder;
//import android.widget.EditText;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//
//import com.commonsware.cwac.saferoom.SafeHelperFactory;
//
//import org.jetbrains.annotations.NotNull;
//
//import vn.ecpay.ewallet.common.utils.Constant;
//import vn.ecpay.ewallet.database.object.Decision;
//
//@Database(entities = {Decision.class}, version = 1, exportSchema = false)
//public abstract class DecisionDatabase extends RoomDatabase {
//    private static DecisionDatabase decisionDatabase;
//    private static SafeHelperFactory factory;
//
//    public abstract DecisionAccess daoAccess();
//
//    @NotNull
//    private static EditText getEditText(String newPass, Context context) {
//        EditText editText = new EditText(context);
//        editText.setText(newPass);
//        return editText;
//    }
//
//    public static boolean changeKey(CharSequence newPass) {
//        try {
//            SafeHelperFactory.rekey(decisionDatabase.mDatabase, new SpannableStringBuilder(newPass));
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public static DecisionDatabase getINSTANCE(Context context, String pass) {
//        if (pass != null) {
//            if (decisionDatabase == null) {
//                if (factory == null)
//                    factory = SafeHelperFactory.fromUser(getEditText(pass, context).getText());
//                decisionDatabase = Room.databaseBuilder(context, DecisionDatabase.class, Constant.DATABASE_NAME)
//                        .openHelperFactory(factory)
//                        .allowMainThreadQueries()
//                        .build();
//            }
//            return decisionDatabase;
//        }
//        return null;
//    }
//
//    public static void insertTask(Decision decision) {
//        Decision mDecision = new Decision();
//        mDecision.setDecisionNo(decision.getDecisionNo());
//        mDecision.setAccountPublicKeyValue(decision.getAccountPublicKeyValue());
//        mDecision.setTreasurePublicKeyValue(decision.getTreasurePublicKeyValue());
//        insertTask(mDecision, Constant.STR_EMPTY);
//    }
//
//    private static void insertTask(final Decision decision, String fake) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                decisionDatabase.daoAccess().insertOnlySingleDecision(decision);
//                return null;
//            }
//        }.execute();
//    }
//
//    public static Decision getDecisionNo(String decisionNo) {
//        return decisionDatabase.daoAccess().getDecisionNo(decisionNo);
//    }
//}
