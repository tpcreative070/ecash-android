package vn.ecpay.ewallet.ui.cashOut.presenter;

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
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
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
    public void getPublicKeyOrganization(Context context, AccountInfo accountInfo, boolean isValidate) {
        cashOutView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestGetPublicKeyOrganizetion requestGetPublicKeyOrganizetion = new RequestGetPublicKeyOrganizetion();
        requestGetPublicKeyOrganizetion.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyOrganizetion.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_ORGANIZATION);
        requestGetPublicKeyOrganizetion.setIssuerCode(Constant.ISSUER_CODE);
        requestGetPublicKeyOrganizetion.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyOrganizetion.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyOrganizetion.setToken(CommonUtils.getToken(context));
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
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                if (isValidate) {
                                    cashOutView.loadPublicKeyOrganizeSuccessForValidate(response.body().getResponseData().getIssuerKpValue());
                                } else {
                                    cashOutView.loadPublicKeyOrganizeSuccess(response.body().getResponseData().getIssuerKpValue());
                                }
                            } else {
                                cashOutView.dismissLoading();
                                cashOutView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            cashOutView.dismissLoading();
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        cashOutView.dismissLoading();
                        cashOutView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    cashOutView.dismissLoading();
                    cashOutView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublickeyOrganization> call, Throwable t) {
                cashOutView.dismissLoading();
                ECashApplication.getInstance().showErrorConnection(t, () -> getPublicKeyOrganization( context,  accountInfo,  isValidate));
            }
        });
    }

    @Override
    public void sendECashToEDong(Context context, String encData, String idSender, long totalMoney, EdongInfo edongInfo, AccountInfo accountInfo) {
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
        requestEcashToEdong.setReceiver(Constant.CREDIT_DEBIT_EWALLET);
        requestEcashToEdong.setSender(String.valueOf(accountInfo.getWalletId()));
        requestEcashToEdong.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestEcashToEdong.setTerminalId(accountInfo.getTerminalId());
        requestEcashToEdong.setTime(CommonUtils.getCurrentTime());
        requestEcashToEdong.setToken(CommonUtils.getToken(context));
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
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                cashOutView.sendECashToEDongSuccess();
                            } else {
                                cashOutView.dismissLoading();
                                cashOutView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            cashOutView.dismissLoading();
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        cashOutView.dismissLoading();
                        cashOutView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    cashOutView.dismissLoading();
                    cashOutView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseECashToEdong> call, Throwable t) {
                cashOutView.dismissLoading();
                // cashOutView.showDialogError(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> sendECashToEDong( context,  encData,  idSender,  totalMoney,  edongInfo,  accountInfo));
            }
        });
    }

    @Override
    public void getEDongInfo(AccountInfo accountInfo, Context context) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestEdongInfo requestEdongInfo = new RequestEdongInfo();
        requestEdongInfo.setAuditNumber(CommonUtils.getAuditNumber());
        requestEdongInfo.setChannelCode(Constant.CHANNEL_CODE);
        requestEdongInfo.setFunctionCode(Constant.FUNCTION_GET_EDONG_INFO);
        requestEdongInfo.setSessionId(accountInfo.getSessionId());
        requestEdongInfo.setToken(CommonUtils.getToken(context));
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
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (response.body().getResponseData().getListAcc().size() > 0) {
                                ECashApplication.setListEDongInfo(response.body().getResponseData().getListAcc());
                                cashOutView.getEDongInfoSuccess();
                            }
                        } else {
                            cashOutView.dismissLoading();
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        cashOutView.dismissLoading();
                        cashOutView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    cashOutView.dismissLoading();
                    cashOutView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongInfo> call, Throwable t) {
                cashOutView.getEDongInfoSuccess();
            }
        });
    }
}
