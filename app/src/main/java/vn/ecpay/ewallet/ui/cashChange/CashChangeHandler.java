package vn.ecpay.ewallet.ui.cashChange;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

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
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashChange.RequestECashChange;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.edongToEcash.response.ResponseEdongToECash;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.RequestGetPublicKeyOrganizetion;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.ResponseGetPublickeyOrganization;
import vn.ecpay.ewallet.ui.cashChange.component.CashChangeSuccess;
import vn.ecpay.ewallet.ui.cashChange.component.PublicKeyOrganization;

public class CashChangeHandler {
  private ECashBaseActivity activity;
    private ECashApplication application;
    private  String publicKeyOrganization ="";
    public CashChangeHandler(ECashApplication application,ECashBaseActivity activity){
        this.application =application;
        this.activity =activity;
    }
    public String getPublicKeyOrganization(AccountInfo accountInfo, PublicKeyOrganization publicKey){
        publicKeyOrganization ="";
        activity.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(activity.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestGetPublicKeyOrganizetion requestGetPublicKeyOrganizetion = new RequestGetPublicKeyOrganizetion();
        requestGetPublicKeyOrganizetion.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyOrganizetion.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_ORGANIZATION);
        requestGetPublicKeyOrganizetion.setIssuerCode(Constant.ISSUER_CODE);
        requestGetPublicKeyOrganizetion.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyOrganizetion.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyOrganizetion.setToken(CommonUtils.getToken());
        requestGetPublicKeyOrganizetion.setUsername(accountInfo.getUsername());
        requestGetPublicKeyOrganizetion.setChannelSignature(Constant.STR_EMPTY);
        requestGetPublicKeyOrganizetion.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion));
        requestGetPublicKeyOrganizetion.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublickeyOrganization> call = apiService.getPublicKeyOrganization(requestGetPublicKeyOrganizetion);
        call.enqueue(new Callback<ResponseGetPublickeyOrganization>() {
            @Override
            public void onResponse(Call<ResponseGetPublickeyOrganization> call, Response<ResponseGetPublickeyOrganization> response) {
                activity.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            publicKeyOrganization= response.body().getResponseData().getIssuerKpValue();
                            publicKey.getPublicKeyOrganization(publicKeyOrganization);
                        }else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            application.checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            activity.showDialogError(response.body().getResponseMessage());
                        }
                    }
                } else {
                    activity.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublickeyOrganization> call, Throwable t) {
                activity.dismissLoading();
                activity.showDialogError(application.getString(R.string.err_upload));
            }
        });
        return publicKeyOrganization;
    }
    public void requestChangeCash(String cashEnc, List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue, CashChangeSuccess cashChangeSuccess){
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestECashChange requestECashChange = new RequestECashChange();
        requestECashChange.setChannelCode(Constant.CHANNEL_CODE);
        requestECashChange.setFunctionCode(Constant.FUNCTION_CHANGE_CASH);
        requestECashChange.setCashEnc(cashEnc);
        requestECashChange.setQuantities(listQuality);
        requestECashChange.setReceiver(accountInfo.getWalletId());
        requestECashChange.setSender(Constant.ISSUER_CODE);
        requestECashChange.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestECashChange.setToken(CommonUtils.getToken());
        requestECashChange.setUsername(accountInfo.getUsername());
        requestECashChange.setValues(listValue);
        requestECashChange.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestECashChange));
        requestECashChange.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseEdongToECash> call = apiService.changeCash(requestECashChange);
        call.enqueue(new Callback<ResponseEdongToECash>() {
            @Override
            public void onResponse(Call<ResponseEdongToECash> call, Response<ResponseEdongToECash> response) {
                if (response.isSuccessful()) {
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                CashInResponse responseData = response.body().getResponseData();
                                cashChangeSuccess.changeCashSuccess(responseData);
                            } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                                activity.dismissLoading();
                                application.checkSessionByErrorCode(response.body().getResponseCode());
                            } else {
                                activity.dismissLoading();
                                activity.showDialogError(response.body().getResponseMessage());
                            }
                        } else {
                            activity.dismissLoading();
                            activity.showDialogError(response.body().getResponseMessage());
                        }
                    }
                } else {
                    activity.dismissLoading();
                    activity.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongToECash> call, Throwable t) {
                activity.dismissLoading();
                activity.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }
}
