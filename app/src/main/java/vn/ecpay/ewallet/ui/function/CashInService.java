package vn.ecpay.ewallet.ui.function;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.cacheData.CacheData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;

public class CashInService extends Service {
    private boolean isRunning = false;
    private List<CacheData> listResponseMessSockets;
    private AccountInfo accountInfo;

    private String EVENT_CASH_IN_CHANGE ="";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
        listResponseMessSockets = new ArrayList<>();
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        Log.e("event 1 ",event.toString());
        if (event.getData().equals(Constant.EVENT_UPDATE_CASH_IN)) {
            if (!isRunning) {
                syncData();
            }
        } if(event.getData().equals(Constant.EVENT_CASH_IN_CHANGE)){
            EVENT_CASH_IN_CHANGE ="EVENT_CASH_IN_CHANGE";
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void syncData() {
        isRunning = true;
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getApplicationContext());
        listResponseMessSockets = DatabaseUtil.getAllCacheData(getApplicationContext());
        handleListResponse();
    }

    private void handleListResponse() {
        if (listResponseMessSockets.size() > 0) {
            CashInResponse responseMess = new Gson().fromJson(listResponseMessSockets.get(0).getResponseData(), CashInResponse.class);
            if (!DatabaseUtil.isTransactionLogExit(responseMess, getApplicationContext())) {
                if (responseMess.getCashEnc() != null) {
                    CashInFunction cashInFunction = new CashInFunction(responseMess, accountInfo, getApplicationContext());
                    cashInFunction.handleCash(() -> {
                        DatabaseUtil.deleteCacheData(responseMess.getId(), getApplicationContext());
                        listResponseMessSockets.remove(0);
                        handleListResponse();
                    });
                } else {
                    listResponseMessSockets.remove(0);
                    DatabaseUtil.deleteCacheData(responseMess.getId(), getApplicationContext());
                    handleListResponse();
                }
            } else {
                listResponseMessSockets.remove(0);
                DatabaseUtil.deleteCacheData(responseMess.getId(), getApplicationContext());
                handleListResponse();
            }
        } else {
            isRunning = false;
            //Log.e("EVENT_CASH_IN_PAYTO ",EVENT_CASH_IN_CHANGE);
            if(EVENT_CASH_IN_CHANGE.length()==0){
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_SUCCESS));

            }else{

                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_PAYTO));
                EVENT_CASH_IN_CHANGE="";
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
