package vn.ecpay.ewallet.ui.function;

import android.content.Context;
import android.util.Base64;

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
import vn.ecpay.ewallet.common.eccrypto.AES;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.RequestUpdateMasterKey;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseDataUpdateMasterKey;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseUpdateMasterKey;
import vn.ecpay.ewallet.ui.callbackListener.UpdateMasterKeyListener;

public class UpdateMasterKeyFunction {
    private Context activity;
    @Inject
    ECashApplication application;

    public UpdateMasterKeyFunction(Context activity) {
        this.activity = activity;
    }

    public void updateLastTimeAndMasterKey(boolean loading,UpdateMasterKeyListener updateMasterKeyListener) {
        showLoading(loading);
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(activity);
        if (null == accountInfo) {
            updateMasterKeyListener.onUpdateMasterFail("xxx");
        }
        String masterKey = KeyStoreUtils.getMasterKey(activity);

        Retrofit retrofit = RetroClientApi.getRetrofitClient(activity.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestUpdateMasterKey requestUpdateMasterKey = new RequestUpdateMasterKey();
        requestUpdateMasterKey.setChannelCode(Constant.CHANNEL_CODE);
        requestUpdateMasterKey.setFunctionCode(Constant.FUNCTION_UPDATE_MASTER_KEY);
        requestUpdateMasterKey.setSessionId(CommonUtils.getSessionId(activity));
        requestUpdateMasterKey.setUsername(DatabaseUtil.getAccountInfo(activity).getUsername());
        requestUpdateMasterKey.setToken(CommonUtils.getToken(activity));
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
                        String code = response.body().getResponseCode();
                        if (code.equals(Constant.CODE_SUCCESS)) {
                            DatabaseUtil.updateAccountLastAccessTime(responseData, accountInfo, activity);
                            ECashApplication.masterKey = responseData.getMasterKey();
                            DatabaseUtil.changeMasterKeyDatabase(activity, responseData.getMasterKey());
                            KeyStoreUtils.saveMasterKey(responseData.getMasterKey(), activity);
                            updateMasterKeyListener.onUpdateMasterSuccess();
                        } else if (code.equals(Constant.ERROR_CODE_LAST_TIME_INVALID)) {
                            updateLastTimeAndMasterKeyTimeOut(updateMasterKeyListener);
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
                if (activity instanceof ECashBaseActivity) {
                    ((ECashBaseActivity) activity).dismissLoading();
                }
                ECashApplication.getInstance().showErrorConnection(t, () -> updateLastTimeAndMasterKey(loading, updateMasterKeyListener));
            }
        });
    }

    private void updateLastTimeAndMasterKeyTimeOut(UpdateMasterKeyListener updateMasterKeyListener) {
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(activity);
        if (null == accountInfo) {
            updateMasterKeyListener.onUpdateMasterFail("xxx");
        }
        String repalce = CommonUtils.getSessionId(activity).replace("-", "");
        String ss = repalce + repalce;
        byte[] masterKey = AES.encrypt(KeyStoreUtils.getMasterKey(activity).getBytes(), CommonUtils.hexStringToByteArray(ss));
        byte[] time = AES.encrypt(accountInfo.getLastAccessTime().getBytes(), CommonUtils.hexStringToByteArray(ss));

        Retrofit retrofit = RetroClientApi.getRetrofitClient(activity.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestUpdateMasterKey requestUpdateMasterKey = new RequestUpdateMasterKey();
        requestUpdateMasterKey.setChannelCode(Constant.CHANNEL_CODE);
        requestUpdateMasterKey.setFunctionCode(Constant.FUNCTION_UPDATE_MASTER_KEY_TIME_OUT);
        requestUpdateMasterKey.setSessionId(CommonUtils.getSessionId(activity));
        requestUpdateMasterKey.setUsername(DatabaseUtil.getAccountInfo(activity).getUsername());
        requestUpdateMasterKey.setToken(CommonUtils.getToken(activity));
        requestUpdateMasterKey.setAuditNumber(CommonUtils.getAuditNumber());
        requestUpdateMasterKey.setLastAccessTimeEnc(Base64.encodeToString(time, Base64.DEFAULT).replace("\n", ""));
        requestUpdateMasterKey.setMasterKeyEnc(Base64.encodeToString(masterKey, Base64.DEFAULT).replace("\n", ""));
        requestUpdateMasterKey.setTerminalId(CommonUtils.getIMEI(activity));
        requestUpdateMasterKey.setWalletId(String.valueOf(accountInfo.getWalletId()));

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestUpdateMasterKey));
        requestUpdateMasterKey.setChannelSignature(CommonUtils.generateSignature(dataSign));

        CommonUtils.logJson(requestUpdateMasterKey);
        Call<ResponseUpdateMasterKey> call = apiService.updateLastTimeAndMasterKeyTimeOut(requestUpdateMasterKey);
        call.enqueue(new Callback<ResponseUpdateMasterKey>() {
            @Override
            public void onResponse(Call<ResponseUpdateMasterKey> call, Response<ResponseUpdateMasterKey> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        ResponseDataUpdateMasterKey responseData = response.body().getResponseData();
                        String code = response.body().getResponseCode();
                        if (code.equals(Constant.CODE_SUCCESS)) {
                            String time = CommonUtils.decrypTimeAndMasterKey(responseData.getLastAccessTimeEnc(), KeyStoreUtils.getPrivateKey(activity));
                            String masterKey = CommonUtils.decrypTimeAndMasterKey(responseData.getMasterKeyEnc(), KeyStoreUtils.getPrivateKey(activity));

                            responseData.setLastAccessTime(time);
                            responseData.setMasterKey(masterKey);

                            DatabaseUtil.updateAccountLastAccessTime(responseData, accountInfo, activity);
                            ECashApplication.masterKey = responseData.getMasterKey();
                            DatabaseUtil.changeMasterKeyDatabase(activity, responseData.getMasterKey());
                            KeyStoreUtils.saveMasterKey(responseData.getMasterKey(), activity);
                            updateMasterKeyListener.onUpdateMasterSuccess();
                        }else {
                            showLoading(false);
                            updateMasterKeyListener.onUpdateMasterFail(response.body().getResponseCode());
                        }
                    } else {
                        showLoading(false);
                        updateMasterKeyListener.onUpdateMasterFail("error");
                    }
                } else {
                    showLoading(false);
                    updateMasterKeyListener.onUpdateMasterFail("error");
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateMasterKey> call, Throwable t) {
                updateMasterKeyListener.onRequestTimeout();
                if (activity instanceof ECashBaseActivity) {
                    ((ECashBaseActivity) activity).dismissLoading();
                }
                ECashApplication.getInstance().showErrorConnection(t, () -> updateLastTimeAndMasterKeyTimeOut(updateMasterKeyListener));
            }
        });
    }
    private void showLoading(boolean show){
        if(activity instanceof ECashBaseActivity){
            if(show){
                ((ECashBaseActivity) activity).showLoading();
            }else{
                ((ECashBaseActivity) activity).dismissLoading();
            }
        }
    }
}
