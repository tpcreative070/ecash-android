package vn.ecpay.ewallet.ui.function;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.cacheData.CacheData;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.ui.callbackListener.CashInSuccessListener;


public class SyncCashService extends Service {
    private boolean isRunning = false;
    private List<CacheData> listResponseMessSockets;
    private AccountInfo accountInfo;

    private boolean changeCashPayment = false;

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
        //     Log.e("B ", event.getData());
        if (event.getData().equals(Constant.EVENT_UPDATE_CASH_IN)) {
            if (!isRunning) {
                isRunning = true;
                accountInfo = DatabaseUtil.getAccountInfo(getApplicationContext());
                syncData();
            }
        }
        if (event.getData().equals(Constant.EVENT_CASH_IN_CHANGE)) {
            changeCashPayment = true;
            syncData();
        }

        if (event.getData().equals(Constant.EVENT_CASH_OUT_MONEY)) {
            accountInfo = DatabaseUtil.getAccountInfo(getApplicationContext());
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
            isRunning = false;
            if (!changeCashPayment) {
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_SUCCESS));
            } else {
                changeCashPayment = false;
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_PAYTO));
            }
        }
    }

    private void handleListResponse() {
        if (listResponseMessSockets.size() > 0) {
            CacheData cacheData = listResponseMessSockets.get(0);
            CashInResponse responseMess = new Gson().fromJson(cacheData.getResponseData(), CashInResponse.class);
            responseMess.setId(cacheData.getTransactionSignature());
            if (!DatabaseUtil.isTransactionLogExit(cacheData.getTransactionSignature(), getApplicationContext())) {
                if (responseMess.getCashEnc() != null) {
                    CashInFunction cashInFunction = new CashInFunction(responseMess, accountInfo, getApplicationContext());
                    cashInFunction.handleCash(new CashInSuccessListener() {
                        @Override
                        public void onCashInSuccess(Long TotalMoney) {
                            DatabaseUtil.deleteCacheData(cacheData.getTransactionSignature(), getApplicationContext());
                            listResponseMessSockets.remove(0);
                            handleListResponse();
                        }

                        @Override
                        public void onCashInFail() {
                            listResponseMessSockets.remove(0);
                            handleListResponse();
                        }
                    });
                } else {
                    listResponseMessSockets.remove(0);
                    DatabaseUtil.deleteCacheData(cacheData.getTransactionSignature(), getApplicationContext());
                    handleListResponse();
                }
            } else {
                listResponseMessSockets.remove(0);
                DatabaseUtil.deleteCacheData(cacheData.getTransactionSignature(), getApplicationContext());
                handleListResponse();
            }
        } else {
            syncData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
