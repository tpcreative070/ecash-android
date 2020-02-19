package vn.ecpay.ewallet.ui.function;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
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
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.cacheData.CacheData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;


public class SyncCashService extends Service {
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
        Gson g = new Gson();
        Log.e("B ", event.getData());
        if (event.getData().equals(Constant.EVENT_UPDATE_CASH_IN)) {
            if (!isRunning) {
                isRunning = true;
                String userName = ECashApplication.getAccountInfo().getUsername();
                accountInfo = DatabaseUtil.getAccountInfo(userName, getApplicationContext());
                syncData();
            }
        }
        if (event.getData().equals(Constant.EVENT_CASH_IN_CHANGE)) {
            syncData();
            EVENT_CASH_IN_CHANGE = "EVENT_CASH_IN_PAYTO";
        }

        if (event.getData().equals(Constant.EVENT_CASH_OUT_MONEY)) {
            String userName = ECashApplication.getAccountInfo().getUsername();
            accountInfo = DatabaseUtil.getAccountInfo(userName, getApplicationContext());
            cashOutData(event);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @SuppressLint("StaticFieldLeak")
    private void cashOutData(EventDataChange event) {
        if (isRunning) {
            cashOutData(event);
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    isRunning = true;
                    DatabaseUtil.updateTransactionsLogAndCashOutDatabase(event.getListCashSend(), event.getResponseMess(),
                            getApplicationContext(), accountInfo.getUsername());
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    isRunning = false;
                    EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
                }
            }.execute();
        }
    }

    private void syncData() {
        listResponseMessSockets = DatabaseUtil.getAllCacheData(getApplicationContext());
        if (listResponseMessSockets.size() > 0) {
            handleListResponse();
        } else {
            syncData();
        }
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
            if (EVENT_CASH_IN_CHANGE.length() == 0) {
                Log.e("E ","E");
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_SUCCESS));

            } else {
                EVENT_CASH_IN_CHANGE = "";
                Log.e("C ","C");
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_PAYTO));
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}