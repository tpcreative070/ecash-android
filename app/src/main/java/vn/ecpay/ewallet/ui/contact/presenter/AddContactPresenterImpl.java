package vn.ecpay.ewallet.ui.contact.presenter;

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
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone.ResponseGetPublicKeyByPhone;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.ui.contact.view.AddContactView;

public class AddContactPresenterImpl implements AddContactPresenter {
    AddContactView addContactView;

    @Inject
    ECashApplication application;

    @Inject
    public AddContactPresenterImpl() {
    }

    @Override
    public void setView(AddContactView view) {
        this.addContactView = view;
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
    public void requestSearchByPhone(String phoneNumber, AccountInfo accountInfo, Context context) {
        addContactView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_BY_PHONE);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken(context));
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setPersonMobilePhone(phoneNumber);
        requestGetPublicKeyWallet.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyWallet));
        requestGetPublicKeyWallet.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyByPhone> call = apiService.getPublicKeyWalletByPhone(requestGetPublicKeyWallet);
        call.enqueue(new Callback<ResponseGetPublicKeyByPhone>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyByPhone> call, Response<ResponseGetPublicKeyByPhone> response) {
                addContactView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                addContactView.onSearchByPhoneSuccess(response.body().getResponseData());
                            } else {
                                addContactView.getWalletFail(application.getString(R.string.err_upload));
                            }
                        } else {
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        addContactView.getWalletFail(application.getString(R.string.err_upload));
                    }
                } else {
                    addContactView.getWalletFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyByPhone> call, Throwable t) {
                addContactView.dismissLoading();
               // addContactView.getWalletFail(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> requestSearchByPhone( phoneNumber,  accountInfo,  context));
            }
        });
    }

    @Override
    public void requestSearchWalletID(String walletId, AccountInfo accountInfo, Context context) {
        addContactView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_WALLET);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken(context));
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setWalletId(walletId);
        requestGetPublicKeyWallet.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyWallet));
        requestGetPublicKeyWallet.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyWallet> call = apiService.getPublicKeyWallet(requestGetPublicKeyWallet);
        call.enqueue(new Callback<ResponseGetPublicKeyWallet>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyWallet> call, Response<ResponseGetPublicKeyWallet> response) {
                addContactView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                addContactView.onSearchByWalletSuccess(response.body().getResponseData());
                            } else {
                                addContactView.getWalletFail(application.getString(R.string.err_upload));
                            }
                        } else {
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        addContactView.getWalletFail(application.getString(R.string.err_upload));
                    }
                } else {
                    addContactView.getWalletFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyWallet> call, Throwable t) {
                addContactView.dismissLoading();
               // addContactView.getWalletFail(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> requestSearchWalletID( walletId,  accountInfo, context));
            }
        });
    }
}
