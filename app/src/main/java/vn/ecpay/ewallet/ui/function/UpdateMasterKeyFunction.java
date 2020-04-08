package vn.ecpay.ewallet.ui.function;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.RequestUpdateMasterKey;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseDataUpdateMasterKey;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseUpdateMasterKey;
import vn.ecpay.ewallet.ui.callbackListener.UpdateMasterKeyListener;

public class UpdateMasterKeyFunction {
    private AppCompatActivity activity;
    @Inject
    ECashApplication application;

    public UpdateMasterKeyFunction(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void updateLastTimeAndMasterKey(UpdateMasterKeyListener updateMasterKeyListener) {
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(ECashApplication.getAccountInfo().getUsername(), activity);
        if (null == accountInfo) {
            updateMasterKeyListener.onUpdateMasterFail("xxx");
        }
        String masterKey = KeyStoreUtils.getMasterKey(activity);

        Retrofit retrofit = RetroClientApi.getRetrofitClient(activity.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestUpdateMasterKey requestUpdateMasterKey = new RequestUpdateMasterKey();
        requestUpdateMasterKey.setChannelCode(Constant.CHANNEL_CODE);
        requestUpdateMasterKey.setFunctionCode(Constant.FUNCTION_UPDATE_MASTER_KEY);
        requestUpdateMasterKey.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestUpdateMasterKey.setUsername(ECashApplication.getAccountInfo().getUsername());
        requestUpdateMasterKey.setToken(CommonUtils.getToken());
        requestUpdateMasterKey.setAuditNumber(CommonUtils.getAuditNumber());
        requestUpdateMasterKey.setLastAccessTime(accountInfo.getLastAccessTime());
        requestUpdateMasterKey.setMasterKey(masterKey);
        requestUpdateMasterKey.setTerminalId(CommonUtils.getIMEI(activity));
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
                            DatabaseUtil.changeMasterKeyDatabase(activity, responseData.getMasterKey());
                            KeyStoreUtils.saveMasterKey(responseData.getMasterKey(), activity);
                            updateMasterKeyListener.onUpdateMasterSuccess();
                        } else {
                            updateMasterKeyListener.onUpdateMasterFail(response.body().getResponseCode());
                        }
                    } else {
                        updateMasterKeyListener.onUpdateMasterFail("error");
                    }
                } else {
                    updateMasterKeyListener.onUpdateMasterFail("error");
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateMasterKey> call, Throwable t) {
                updateMasterKeyListener.onRequestTimeout();
                if(activity instanceof ECashBaseActivity){
                    ((ECashBaseActivity) activity).dismissLoading();
                }
                ECashApplication.getInstance().showErrorConnection(t, () -> updateLastTimeAndMasterKey(updateMasterKeyListener));
            }
        });
    }
}
