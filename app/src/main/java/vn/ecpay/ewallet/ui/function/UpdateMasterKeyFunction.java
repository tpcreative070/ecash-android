package vn.ecpay.ewallet.ui.function;

import android.content.Context;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.RequestUpdateMasterKey;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseDataUpdateMasterKey;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseUpdateMasterKey;
import vn.ecpay.ewallet.ui.callbackListener.UpdateMasterKeyListener;

public class UpdateMasterKeyFunction {
    private Context context;
    @Inject
    ECashApplication application;

    public UpdateMasterKeyFunction(Context context) {
        this.context = context;
    }

    public void updateLastTimeAndMasterKey(UpdateMasterKeyListener updateMasterKeyListener) {
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(ECashApplication.getAccountInfo().getUsername(), context);
        if (null == accountInfo) {
            updateMasterKeyListener.onUpdateMasterFail();
        }
        String masterKey = KeyStoreUtils.getMasterKey(context);

        Retrofit retrofit = RetroClientApi.getRetrofitClient(context.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestUpdateMasterKey requestUpdateMasterKey = new RequestUpdateMasterKey();
        requestUpdateMasterKey.setChannelCode(Constant.CHANNEL_CODE);
        requestUpdateMasterKey.setFunctionCode(Constant.FUNCTION_UPDATE_MASTER_KEY);
        requestUpdateMasterKey.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestUpdateMasterKey.setUsername(ECashApplication.getAccountInfo().getUsername());
        requestUpdateMasterKey.setToken(CommonUtils.getToken(ECashApplication.getAccountInfo()));
        requestUpdateMasterKey.setAuditNumber(CommonUtils.getAuditNumber());
        requestUpdateMasterKey.setLastAccessTime(accountInfo.getLastAccessTime());
        requestUpdateMasterKey.setMasterKey(masterKey);
        requestUpdateMasterKey.setTerminalId(CommonUtils.getIMEI(context));
        requestUpdateMasterKey.setWalletId(String.valueOf(accountInfo.getWalletId()));

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestUpdateMasterKey));
        requestUpdateMasterKey.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseUpdateMasterKey> call = apiService.updateLastTimeAndMasterKey(requestUpdateMasterKey);
        call.enqueue(new Callback<ResponseUpdateMasterKey>() {
            @Override
            public void onResponse(Call<ResponseUpdateMasterKey> call, Response<ResponseUpdateMasterKey> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        ResponseDataUpdateMasterKey responseData = response.body().getResponseData();
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            WalletDatabase.updateAccountLastAccessTime(responseData.getLastAccessTime(), accountInfo.getUsername());
                            ECashApplication.masterKey = responseData.getMasterKey();
                            DatabaseUtil.changeMasterKeyDatabase(context, responseData.getMasterKey());
                            KeyStoreUtils.saveMasterKey(responseData.getMasterKey(), context);
                            updateMasterKeyListener.onUpdateMasterSuccess();
                        } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            ECashApplication.getInstance().checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            updateMasterKeyListener.onUpdateMasterFail();
                        }
                    } else {
                        updateMasterKeyListener.onRequestTimeout();
                    }
                } else {
                    updateMasterKeyListener.onRequestTimeout();
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateMasterKey> call, Throwable t) {
                updateMasterKeyListener.onRequestTimeout();
            }
        });
    }
}
