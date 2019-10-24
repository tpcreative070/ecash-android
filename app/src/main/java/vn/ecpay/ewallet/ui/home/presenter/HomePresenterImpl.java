package vn.ecpay.ewallet.ui.home.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.EllipticCurve;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.RequestGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.ResponseGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.home.view.HomeView;

public class HomePresenterImpl implements HomePresenter {
    private HomeView homeView;
    @Inject
    ECashApplication application;

    @Inject
    public HomePresenterImpl() {
    }

    @Override
    public void setView(HomeView view) {
        this.homeView = view;
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
    public void activeAccWalletInfo(AccountInfo accountInfo, Context context) {
        homeView.showLoading();
        EllipticCurve ec = EllipticCurve.getSecp256k1();
        ECPrivateKeyParameters ks = ec.generatePrivateKeyParameters();
        ECPublicKeyParameters kp = ec.getPublicKeyParameters(ks);

        byte[] mPriKey = ks.getD().toByteArray();
        byte[] mPubKey = kp.getQ().getEncoded(false);
        String privateKeyBase64 = Base64.encodeToString(mPriKey, Base64.DEFAULT).replaceAll("\n", "");
        String publicKeyBase64 = Base64.encodeToString(mPubKey, Base64.DEFAULT).replaceAll("\n", "");

        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetAccountWalletInfo requestGetAccountWalletInfo = new RequestGetAccountWalletInfo();
        requestGetAccountWalletInfo.setChannelCode(Constant.CHANNEL_CODE);
        requestGetAccountWalletInfo.setChannelId(String.valueOf(accountInfo.getChannelId()));
        requestGetAccountWalletInfo.setCustomerId(accountInfo.getCustomerId());
        requestGetAccountWalletInfo.setEcKeyPublicValue(publicKeyBase64);
        requestGetAccountWalletInfo.setFunctionCode(Constant.FUNCTION_GET_WALLET_INFO);
        requestGetAccountWalletInfo.setKeyPublicAlias(CommonUtils.getKeyAlias());
        requestGetAccountWalletInfo.setSessionId(accountInfo.getSessionId());
        SharedPreferences prefs = application.getSharedPreferences(application.getPackageName(), Context.MODE_PRIVATE);
        String IMEI = prefs.getString(Constant.DEVICE_IMEI, null);
        requestGetAccountWalletInfo.setTerminalId(IMEI);
        requestGetAccountWalletInfo.setTerminalInfo(CommonUtils.getModelName());
        requestGetAccountWalletInfo.setToken(CommonUtils.getToken(accountInfo));
        requestGetAccountWalletInfo.setUsername(accountInfo.getUsername());
        requestGetAccountWalletInfo.setChannelSignature(Constant.STR_EMPTY);

        String alphabe = CommonUtils.getStringAlphabe(requestGetAccountWalletInfo);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetAccountWalletInfo));
        requestGetAccountWalletInfo.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetAccountWalletInfo> call = apiService.getWalletInfo(requestGetAccountWalletInfo);
        call.enqueue(new Callback<ResponseGetAccountWalletInfo>() {
            @Override
            public void onResponse(Call<ResponseGetAccountWalletInfo> call, Response<ResponseGetAccountWalletInfo> response) {
                homeView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    application.checkSessionByErrorCode(response.body().getResponseCode());
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            AccountInfo mAccountInfo = response.body().getAccountInfo();
                            ECashApplication.privateKey = privateKeyBase64;
                            ECashApplication.masterKey = mAccountInfo.getMasterKey();

                            //update key
                            DatabaseUtil.changePassDatabase(context, mAccountInfo.getMasterKey());
                            KeyStoreUtils.saveKeyPrivateWallet(privateKeyBase64, context);
                            KeyStoreUtils.saveMasterKey(mAccountInfo.getMasterKey(), context);
                            homeView.onActiveAccountSuccess(mAccountInfo);
                        } else {
                            homeView.showDialogError(response.body().getResponseMessage());
                        }
                    } else {
                        homeView.showDialogError(context.getString(R.string.err_upload));
                    }
                } else {
                    homeView.showDialogError(context.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetAccountWalletInfo> call, Throwable t) {
                homeView.dismissLoading();
                homeView.showDialogError(context.getString(R.string.err_upload));
            }
        });
    }
}
