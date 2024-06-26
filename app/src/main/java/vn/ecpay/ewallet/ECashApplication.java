package vn.ecpay.ewallet;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import vn.ecpay.ewallet.common.dependencyInjection.ApplicationComponent;
import vn.ecpay.ewallet.common.dependencyInjection.ApplicationModule;
import vn.ecpay.ewallet.common.dependencyInjection.DaggerApplicationComponent;
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.common.utils.CheckErrorConnectionUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.ErrorStatusConnectionModel;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.notification.TokenFCMObj;
import vn.ecpay.ewallet.ui.account.AccountActivity;

public class ECashApplication extends Application {
    private static ApplicationComponent applicationComponent;
    public static AccountInfo accountInfo;
    public static ArrayList<EdongInfo> listEDongInfo;
    public static String privateKey;
    public static String masterKey;
    public static String FBToken;
    private static ECashApplication sInstance;
    private Gson mGSon;
    public static boolean isChangeDataBase = false;
    public static boolean isCancelAccount = false;
    public static Long lastTimeAddContact;

    public static boolean isChangeDataBase() {
        return isChangeDataBase;
    }

    public static void setIsChangeDataBase(boolean isChangeDataBase) {
        ECashApplication.isChangeDataBase = isChangeDataBase;
    }

    public static ECashApplication getInstance() {
        return sInstance;
    }

    public Gson getGSon() {
        return mGSon;
    }

    public static AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public static void setAccountInfo(AccountInfo mAccountInfo) {
        accountInfo = mAccountInfo;
    }

    public static ArrayList<EdongInfo> getListEDongInfo() {
        return listEDongInfo;
    }

    public static void setListEDongInfo(ArrayList<EdongInfo> mInfoEdongList) {
        ECashApplication.listEDongInfo = mInfoEdongList;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mGSon = new Gson();
        initApp();
        InitTokenFCMDB();
    }

    private void initApp() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    static AppCompatActivity activity;

    public static AppCompatActivity getActivity() {
        return activity;
    }

    public boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public static ECashApplication get(Context context) {
        return (ECashApplication) context.getApplicationContext();
    }

    private void InitTokenFCMDB() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    FBToken = Objects.requireNonNull(task.getResult()).getToken();
                    Log.e("FBToken", FBToken);
                    if (FBToken.isEmpty()) return;
                    if (!SharedPrefs.getInstance().get(SharedPrefs.channelKp, String.class).isEmpty() &&
                            !SharedPrefs.getInstance().get(SharedPrefs.clientKs, String.class).isEmpty() &&
                            !SharedPrefs.getInstance().get(SharedPrefs.clientKp, String.class).isEmpty()) {
                        return;
                    }

                    SharedPreferences prefs = this.getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                    String strDate = sdf.format(c.getTime());

                    TokenFCMObj myToken = new TokenFCMObj();
                    myToken.setApp_name(Constant.app_name);
                    myToken.setCreated_date(strDate);
                    myToken.setStatus("0");
                    myToken.setTerminal_id(prefs.getString(Constant.DEVICE_IMEI, null));
                    myToken.setTerminal_info(CommonUtils.getModelName());
                    myToken.setToken(FBToken);
                    myToken.setChannel_code("MB001");

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("firebase_token");
                    String id = mDatabase.push().getKey();
                    if (null != id)
                        mDatabase.child(id).setValue(myToken);
                });

    }

    public static void showDialogSwitchLogin(final String messenger) {
        if (DialogUtil.getInstance().isShowing()) {
            DialogUtil.getInstance().dismissDialog();
        }
        DialogUtil.getInstance().showDialogSessionTimeO(getActivity(), messenger, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra(Constant.IS_SESSION_TIMEOUT, true);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void showErrorConnection(Throwable t, DialogUtil.OnResult onResult) {
        ErrorStatusConnectionModel errorConnect = new CheckErrorConnectionUtils().error(getActivity(), t);
        DialogUtil.getInstance().showDialogErrorConnectInternet(getActivity(), errorConnect.getMessage(), onResult);
    }
}
