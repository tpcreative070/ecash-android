package vn.ecpay.ewallet.ui.cashChange.presenter;

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
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
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
    public void requestChangeCash(List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue) {
//        cashChangeView.showLoading();
//        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
//        APIService apiService = retrofit.create(APIService.class);
//        RequestGetPublicKeyOrganizetion requestGetPublicKeyOrganizetion = new RequestGetPublicKeyOrganizetion();
//        requestGetPublicKeyOrganizetion.setChannelCode(Constant.CHANNEL_CODE);
//        requestGetPublicKeyOrganizetion.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_ORGANIZATION);
//        requestGetPublicKeyOrganizetion.setIssuerCode(Constant.ISSUER_CODE);
//        requestGetPublicKeyOrganizetion.setSessionId(ECashApplication.getAccountInfo().getSessionId());
//        requestGetPublicKeyOrganizetion.setTerminalId(accountInfo.getTerminalId());
//        requestGetPublicKeyOrganizetion.setToken(CommonUtils.getToken(accountInfo));
//        requestGetPublicKeyOrganizetion.setUsername(accountInfo.getUsername());
//        requestGetPublicKeyOrganizetion.setChannelSignature(Constant.STR_EMPTY);
//
//        String alphabe = CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion);
//        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion));
//        requestGetPublicKeyOrganizetion.setChannelSignature(CommonUtils.generateSignature(dataSign));
//
//        Call<ResponseGetPublickeyOrganization> call = apiService.getPublicKeyOrganization(requestGetPublicKeyOrganizetion);
//        call.enqueue(new Callback<ResponseGetPublickeyOrganization>() {
//            @Override
//            public void onResponse(Call<ResponseGetPublickeyOrganization> call, Response<ResponseGetPublickeyOrganization> response) {
//                cashChangeView.dismissLoading();
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    if (response.body().getResponseCode() != null) {
//                        application.checkSessionByErrorCode(response.body().getResponseCode());
//                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
//                            cashOutView.loadPublicKeyOrganizeSuccess(response.body().getResponseData().getIssuerKpValue());
//                        } else {
//                            cashOutView.showDialogError(response.body().getResponseMessage());
//                        }
//                    }
//                } else {
//                    cashOutView.showDialogError(application.getString(R.string.err_upload));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseGetPublickeyOrganization> call, Throwable t) {
//                cashChangeView.dismissLoading();
//                cashChangeView.showDialogError(application.getString(R.string.err_upload));
//            }
//        });
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
        requestGetPublicKeyOrganizetion.setToken(CommonUtils.getToken(accountInfo));
        requestGetPublicKeyOrganizetion.setUsername(accountInfo.getUsername());
        requestGetPublicKeyOrganizetion.setChannelSignature(Constant.STR_EMPTY);

        String alphabe = CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion));
        requestGetPublicKeyOrganizetion.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublickeyOrganization> call = apiService.getPublicKeyOrganization(requestGetPublicKeyOrganizetion);
        call.enqueue(new Callback<ResponseGetPublickeyOrganization>() {
            @Override
            public void onResponse(Call<ResponseGetPublickeyOrganization> call, Response<ResponseGetPublickeyOrganization> response) {
                cashChangeView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        application.checkSessionByErrorCode(response.body().getResponseCode());
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            cashChangeView.loadPublicKeyOrganizeSuccess(response.body().getResponseData().getIssuerKpValue());
                        } else {
                            cashChangeView.showDialogError(response.body().getResponseMessage());
                        }
                    }
                } else {
                    cashChangeView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublickeyOrganization> call, Throwable t) {
                cashChangeView.dismissLoading();
                cashChangeView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }
}
