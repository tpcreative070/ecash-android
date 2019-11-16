package vn.ecpay.ewallet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.ArrayList;

import vn.ecpay.ewallet.common.dependencyInjection.ApplicationComponent;
import vn.ecpay.ewallet.common.dependencyInjection.ApplicationModule;
import vn.ecpay.ewallet.common.dependencyInjection.DaggerApplicationComponent;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.AccountActivity;

public class ECashApplication extends Application {
    private static ApplicationComponent applicationComponent;
    public static AccountInfo accountInfo;
    public static ArrayList<EdongInfo> listEDongInfo;
    public static String privateKey;
    public static String masterKey;
    private static ECashApplication sInstance;
    private Gson mGSon;

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
    }

    private void initApp() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static ECashApplication get(Context context) {
        return (ECashApplication) context.getApplicationContext();
    }

    public void checkSessionByErrorCode(String error_code) {
        switch (error_code) {
            case "3043":
                showDialogSwitchLogin(getString(R.string.err_end_of_the_session));
                break;
            default:
                break;
        }

    }

    public void showDialogSwitchLogin(final String messenger) {
        if (DialogUtil.getInstance().isShowing()) {
            DialogUtil.getInstance().dismissDialog();
        }
        DialogUtil.getInstance().showDialogConfirm(getActivity(), messenger, new DialogUtil.OnConfirm() {
            @Override
            public void OnListenerOk() {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra(Constant.IS_SESSION_TIMEOUT,true);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void OnListenerCancel() {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    public void showDialogChangePassSuccess(final String messenger) {
        if (DialogUtil.getInstance().isShowing()) {
            DialogUtil.getInstance().dismissDialog();
        }
        DialogUtil.getInstance().showDialogChangePassSuccess(getActivity(),
                getResources().getString(R.string.str_dialog_notification_title), messenger, new DialogUtil.OnResult() {
                    @Override
                    public void OnListenerOk() {
                        Intent intent = new Intent(getActivity(), AccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
    }
}
