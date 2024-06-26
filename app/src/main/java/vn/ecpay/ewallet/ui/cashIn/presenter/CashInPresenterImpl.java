package vn.ecpay.ewallet.ui.cashIn.presenter;

import android.content.Context;

import com.google.gson.Gson;

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
import vn.ecpay.ewallet.model.account.getEdongInfo.RequestEdongInfo;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseEdongInfo;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.edongToEcash.RequestEdongToECash;
import vn.ecpay.ewallet.model.edongToEcash.response.ResponseEdongToECash;
import vn.ecpay.ewallet.ui.cashIn.view.CashInView;

public class CashInPresenterImpl implements CashInPresenter {
    private CashInView cashInView;
    @Inject
    ECashApplication application;

    @Inject
    public CashInPresenterImpl() {
    }

    @Override
    public void setView(CashInView view) {
        this.cashInView = view;
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
    public void transferMoneyEDongToECash(Context context,Long totalMoney, EdongInfo eDongInfoCashIn,
                                          List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue) {
        cashInView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestEdongToECash requestEdongToECash = new RequestEdongToECash();
        requestEdongToECash.setAmount(totalMoney);
        requestEdongToECash.setChannelCode(Constant.CHANNEL_CODE);
        requestEdongToECash.setCreditAccount(Constant.CREDIT_DEBIT_ACCOUNT);
        requestEdongToECash.setDebitAccount(eDongInfoCashIn.getAccountIdt());
        requestEdongToECash.setFunctionCode(Constant.FUNCTION_TRANSFER_EDONG_TO_ECASH);
        requestEdongToECash.setQuantities(listQuality);
        requestEdongToECash.setReceiver(String.valueOf(accountInfo.getWalletId()));
        requestEdongToECash.setSender(Constant.CREDIT_DEBIT_EWALLET);
        requestEdongToECash.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestEdongToECash.setTerminalId(accountInfo.getTerminalId());
        requestEdongToECash.setToken(CommonUtils.getToken(context));
        requestEdongToECash.setUsername(accountInfo.getUsername());
        requestEdongToECash.setValues(listValue);
        requestEdongToECash.setChannelSignature(Constant.STR_EMPTY);
        requestEdongToECash.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestEdongToECash));
        requestEdongToECash.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Gson gson = new Gson();
        String json = gson.toJson(requestEdongToECash);
        Call<ResponseEdongToECash> call = apiService.transferMoneyEdongToECash(requestEdongToECash);
        call.enqueue(new Callback<ResponseEdongToECash>() {
            @Override
            public void onResponse(Call<ResponseEdongToECash> call, Response<ResponseEdongToECash> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                CashInResponse responseData = response.body().getResponseData();
                                cashInView.transferMoneySuccess(responseData);
                            } else {
                                cashInView.dismissLoading();
                                cashInView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            cashInView.dismissLoading();
                            if(response.body().getResponseCode().equals(Constant.ERROR_CODE_4011)){
                                cashInView.showDialogErrorWithCloseAndContinue(context.getString(R.string.str_have_warning),context.getString(R.string.error_message_code_4011));
                            }else{
                                CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                            }

                        }
                    } else {
                        cashInView.dismissLoading();
                        cashInView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    cashInView.dismissLoading();
                    cashInView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongToECash> call, Throwable t) {
                cashInView.dismissLoading();
               // cashInView.showDialogError(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> transferMoneyEDongToECash( context, totalMoney,  eDongInfoCashIn, listQuality,  accountInfo, listValue));
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
                                cashInView.getEDongInfoSuccess();
                            }
                        } else {
                            cashInView.getEDongInfoSuccess();
                        }
                    } else {
                        cashInView.getEDongInfoSuccess();
                    }
                } else {
                    cashInView.getEDongInfoSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongInfo> call, Throwable t) {
                cashInView.getEDongInfoSuccess();
            }
        });
    }
}
