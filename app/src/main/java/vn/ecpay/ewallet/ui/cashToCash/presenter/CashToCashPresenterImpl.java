package vn.ecpay.ewallet.ui.cashToCash.presenter;

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
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.ui.cashToCash.view.CashToCashView;

public class CashToCashPresenterImpl implements CashToCashPresenter {
    private CashToCashView cashToCashView;
    @Inject
    ECashApplication application;

    @Inject
    public CashToCashPresenterImpl() {
    }

    @Override
    public void setView(CashToCashView view) {
        this.cashToCashView = view;
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
    public void getPublicKeyWallet(AccountInfo accountInfo, String idWallet) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_WALLET);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken());
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setWalletId(idWallet);
        requestGetPublicKeyWallet.setChannelSignature(Constant.STR_EMPTY);

        String alphabe = CommonUtils.getStringAlphabe(requestGetPublicKeyWallet);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyWallet));
        requestGetPublicKeyWallet.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyWallet> call = apiService.getPublicKeyWallet(requestGetPublicKeyWallet);
        call.enqueue(new Callback<ResponseGetPublicKeyWallet>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyWallet> call, Response<ResponseGetPublicKeyWallet> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        application.checkSessionByErrorCode(response.body().getResponseCode());
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            cashToCashView.getPublicKeyWalletSuccess(response.body().getResponseData());
                        } else {
                            cashToCashView.getPublicKeyWalletFail(response.body().getResponseMessage());
                        }
                    }
                } else {
                    cashToCashView.getPublicKeyWalletFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyWallet> call, Throwable t) {
                cashToCashView.getPublicKeyWalletFail(application.getString(R.string.err_upload));
            }
        });
    }
}
