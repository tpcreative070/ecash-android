package vn.ecpay.ewallet.ui.cashChange.presenter;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import java.util.List;

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
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashChange.RequestECashChange;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.edongToEcash.response.ResponseEdongToECash;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.RequestGetPublicKeyOrganizetion;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.ResponseGetPublickeyOrganization;
import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;

public class CashChangePresenterImpl implements CashChangePresenter {
    CashChangeView cashChangeView;
    @Inject
    ECashApplication application;

    @Inject
    public CashChangePresenterImpl() {
    }

    @Override
    public void setView(CashChangeView view) {
        this.cashChangeView = view;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewStart() {

    }

    @Override
    public void onViewResume() {

    }

    @Override
    public void onViewPause() {

    }

    @Override
    public void onViewStop() {

    }

    @Override
    public void onViewDestroy() {

    }

    @Override
    public void requestChangeCash(Context context, String cashEnc, List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestECashChange requestECashChange = new RequestECashChange();
        requestECashChange.setChannelCode(Constant.CHANNEL_CODE);
        requestECashChange.setFunctionCode(Constant.FUNCTION_CHANGE_CASH);
        requestECashChange.setCashEnc(cashEnc);
        requestECashChange.setQuantities(listQuality);
        requestECashChange.setReceiver(accountInfo.getWalletId());
        requestECashChange.setSender(String.valueOf(accountInfo.getWalletId()));
        requestECashChange.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestECashChange.setToken(CommonUtils.getToken(context));
        requestECashChange.setUsername(accountInfo.getUsername());
        requestECashChange.setValues(listValue);
        requestECashChange.setAuditNumber(CommonUtils.getAuditNumber());
        requestECashChange.setTime(CommonUtils.getCurrentTime());
        requestECashChange.setType(Constant.TYPE_CASH_EXCHANGE);

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestECashChange));
        requestECashChange.setChannelSignature(CommonUtils.generateSignature(dataSign));

        CommonUtils.logJson(requestECashChange);
        Call<ResponseEdongToECash> call = apiService.changeCash(requestECashChange);
        call.enqueue(new Callback<ResponseEdongToECash>() {
            @Override
            public void onResponse(Call<ResponseEdongToECash> call, Response<ResponseEdongToECash> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                CashInResponse responseData = response.body().getResponseData();
                                cashChangeView.changeCashSuccess(responseData);
                            } else {
                                cashChangeView.dismissLoading();
                                cashChangeView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            cashChangeView.dismissLoading();
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        cashChangeView.dismissLoading();
                        cashChangeView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    cashChangeView.dismissLoading();
                    cashChangeView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongToECash> call, Throwable t) {
                cashChangeView.dismissLoading();
           //     cashChangeView.showDialogError(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> requestChangeCash( context,  cashEnc,listQuality,  accountInfo,  listValue));
            }
        });
    }

    @Override
    public void getPublicKeyOrganization(FragmentActivity activity, AccountInfo accountInfo) {
        cashChangeView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestGetPublicKeyOrganizetion requestGetPublicKeyOrganizetion = new RequestGetPublicKeyOrganizetion();
        requestGetPublicKeyOrganizetion.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyOrganizetion.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_ORGANIZATION);
        requestGetPublicKeyOrganizetion.setIssuerCode(Constant.ISSUER_CODE);
        requestGetPublicKeyOrganizetion.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyOrganizetion.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyOrganizetion.setToken(CommonUtils.getToken(activity));
        requestGetPublicKeyOrganizetion.setUsername(accountInfo.getUsername());
        requestGetPublicKeyOrganizetion.setChannelSignature(Constant.STR_EMPTY);
        requestGetPublicKeyOrganizetion.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion));
        requestGetPublicKeyOrganizetion.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublickeyOrganization> call = apiService.getPublicKeyOrganization(requestGetPublicKeyOrganizetion);
        call.enqueue(new Callback<ResponseGetPublickeyOrganization>() {
            @Override
            public void onResponse(Call<ResponseGetPublickeyOrganization> call, Response<ResponseGetPublickeyOrganization> response) {
                cashChangeView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                cashChangeView.loadPublicKeyOrganizeSuccess(response.body().getResponseData().getIssuerKpValue());
                            } else {
                                cashChangeView.dismissLoading();
                                cashChangeView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            cashChangeView.dismissLoading();
                            CheckErrCodeUtil.errorMessage(activity, response.body().getResponseCode());
                        }
                    } else {
                        cashChangeView.dismissLoading();
                        cashChangeView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    cashChangeView.dismissLoading();
                    cashChangeView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublickeyOrganization> call, Throwable t) {
                cashChangeView.dismissLoading();
               // cashChangeView.showDialogError(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t,
                        () -> getPublicKeyOrganization( activity, accountInfo));
            }
        });
    }
}
