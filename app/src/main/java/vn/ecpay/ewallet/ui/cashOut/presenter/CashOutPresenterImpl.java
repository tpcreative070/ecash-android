package vn.ecpay.ewallet.ui.cashOut.presenter;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

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
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.account.getEdongInfo.RequestEdongInfo;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseEdongInfo;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.ecashToEdong.RequestEcashToEdong;
import vn.ecpay.ewallet.model.ecashToEdong.ResponseECashToEdong;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.RequestGetPublicKeyOrganizetion;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.ResponseGetPublickeyOrganization;
import vn.ecpay.ewallet.ui.cashOut.view.CashOutView;

public class CashOutPresenterImpl implements CashOutPresenter {
    private CashOutView cashOutView;
    @Inject
    ECashApplication application;

    @Inject
    public CashOutPresenterImpl() {
    }

    @Override
    public void setView(CashOutView view) {
        this.cashOutView = view;
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
    public void getPublicKeyOrganization(Context context, AccountInfo accountInfo) {
        cashOutView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestGetPublicKeyOrganizetion requestGetPublicKeyOrganizetion = new RequestGetPublicKeyOrganizetion();
        requestGetPublicKeyOrganizetion.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyOrganizetion.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_ORGANIZATION);
        requestGetPublicKeyOrganizetion.setIssuerCode(Constant.ISSUER_CODE);
        requestGetPublicKeyOrganizetion.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyOrganizetion.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyOrganizetion.setToken(CommonUtils.getToken());
        requestGetPublicKeyOrganizetion.setUsername(accountInfo.getUsername());
        requestGetPublicKeyOrganizetion.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion));
        requestGetPublicKeyOrganizetion.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublickeyOrganization> call = apiService.getPublicKeyOrganization(requestGetPublicKeyOrganizetion);
        call.enqueue(new Callback<ResponseGetPublickeyOrganization>() {
            @Override
            public void onResponse(Call<ResponseGetPublickeyOrganization> call, Response<ResponseGetPublickeyOrganization> response) {
                cashOutView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            cashOutView.loadPublicKeyOrganizeSuccess(response.body().getResponseData().getIssuerKpValue());
                        } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            cashOutView.dismissLoading();
                            application.checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            cashOutView.dismissLoading();
                            cashOutView.showDialogError(response.body().getResponseMessage());
                        }
                    }
                } else {
                    cashOutView.dismissLoading();
                    cashOutView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublickeyOrganization> call, Throwable t) {
                cashOutView.dismissLoading();
                cashOutView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void sendECashToEDong(String encData, String idSender, long totalMoney, EdongInfo edongInfo, AccountInfo accountInfo) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestEcashToEdong requestEcashToEdong = new RequestEcashToEdong();
        requestEcashToEdong.setAmount(totalMoney);
        requestEcashToEdong.setCashEnc(encData);
        requestEcashToEdong.setChannelCode(Constant.CHANNEL_CODE);
        requestEcashToEdong.setContent("");
        requestEcashToEdong.setCreditAccount(edongInfo.getAccountIdt());
        requestEcashToEdong.setDebitAccount(Constant.CREDIT_DEBIT_ACCOUNT);
        requestEcashToEdong.setFunctionCode(Constant.FUNCTION_TRANSFER_ECASH_TO_EDONG);
        requestEcashToEdong.setId(idSender);
        requestEcashToEdong.setReceiver(String.valueOf(accountInfo.getWalletId()));
        requestEcashToEdong.setSender(String.valueOf(accountInfo.getWalletId()));
        requestEcashToEdong.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestEcashToEdong.setTerminalId(accountInfo.getTerminalId());
        requestEcashToEdong.setTime(CommonUtils.getCurrentTime());
        requestEcashToEdong.setToken(CommonUtils.getToken());
        requestEcashToEdong.setType(Constant.TYPE_SEND_ECASH_TO_EDONG);
        requestEcashToEdong.setUsername(accountInfo.getUsername());
        requestEcashToEdong.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestEcashToEdong));
        requestEcashToEdong.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(requestEcashToEdong);
        Call<ResponseECashToEdong> call = apiService.transferMoneyECashToEDong(requestEcashToEdong);
        call.enqueue(new Callback<ResponseECashToEdong>() {
            @Override
            public void onResponse(Call<ResponseECashToEdong> call, Response<ResponseECashToEdong> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            cashOutView.sendECashToEDongSuccess();
                        } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            cashOutView.dismissLoading();
                            application.checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            cashOutView.dismissLoading();
                            cashOutView.showDialogError(response.body().getResponseMessage());
                        }
                    }
                } else {
                    cashOutView.dismissLoading();
                    cashOutView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseECashToEdong> call, Throwable t) {
                cashOutView.dismissLoading();
                cashOutView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void getEDongInfo(AccountInfo accountInfo) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestEdongInfo requestEdongInfo = new RequestEdongInfo();
        requestEdongInfo.setAuditNumber(CommonUtils.getAuditNumber());
        requestEdongInfo.setChannelCode(Constant.CHANNEL_CODE);
        requestEdongInfo.setFunctionCode(Constant.FUNCTION_GET_EDONG_INFO);
        requestEdongInfo.setSessionId(accountInfo.getSessionId());
        requestEdongInfo.setToken(CommonUtils.getToken());
        requestEdongInfo.setUsername(accountInfo.getUsername());

        String alphabe = CommonUtils.getStringAlphabe(requestEdongInfo);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestEdongInfo));
        requestEdongInfo.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseEdongInfo> call = apiService.getEdongInfo(requestEdongInfo);
        call.enqueue(new Callback<ResponseEdongInfo>() {
            @Override
            public void onResponse(Call<ResponseEdongInfo> call, Response<ResponseEdongInfo> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (response.body().getResponseData().getListAcc().size() > 0) {
                                ECashApplication.setListEDongInfo(response.body().getResponseData().getListAcc());
                            }
                        } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            cashOutView.dismissLoading();
                            application.checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            cashOutView.dismissLoading();
                            cashOutView.showDialogError(response.body().getResponseMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongInfo> call, Throwable t) {
                cashOutView.dismissLoading();
                cashOutView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }
}
