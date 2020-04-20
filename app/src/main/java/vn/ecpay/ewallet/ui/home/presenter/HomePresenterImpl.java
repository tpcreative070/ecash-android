package vn.ecpay.ewallet.ui.home.presenter;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.util.Collections;

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
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.RequestOTPActiveAccount;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.ResponseData;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.ResponseOTPActiveAccount;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.RequestGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.ResponseGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.request.RequestGetMoneyValue;
import vn.ecpay.ewallet.model.cashValue.response.ResponseGetMoneyValue;
import vn.ecpay.ewallet.model.contact.RequestSyncContact;
import vn.ecpay.ewallet.model.contact.ResponseSyncContact;
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

    private String privateKeyBase64;

    @Override
    public void getOTPActiveAccount(AccountInfo accountInfo, Context context) {
        homeView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        EllipticCurve ec = EllipticCurve.getSecp256k1();

        ECPrivateKeyParameters ks = ec.generatePrivateKeyParameters();
        ECPublicKeyParameters kp = ec.getPublicKeyParameters(ks);

        byte[] mPriKey = ks.getD().toByteArray();
        byte[] mPubKey = kp.getQ().getEncoded(false);
        privateKeyBase64 = Base64.encodeToString(mPriKey, Base64.DEFAULT).replaceAll("\n", "");
        String publicKeyBase64 = Base64.encodeToString(mPubKey, Base64.DEFAULT).replaceAll("\n", "");

        RequestOTPActiveAccount requestOTPActiveAccount = new RequestOTPActiveAccount();
        requestOTPActiveAccount.setChannelCode(Constant.CHANNEL_CODE);
        requestOTPActiveAccount.setFunctionCode(Constant.FUNCTION_GET_OTP_ACTIVE_ACCOUNT);
        requestOTPActiveAccount.setChannelId(String.valueOf(accountInfo.getChannelId()));
        requestOTPActiveAccount.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestOTPActiveAccount.setEcKeyPublicValue(publicKeyBase64);
        requestOTPActiveAccount.setKeyPublicAlias(CommonUtils.getKeyAlias());
        requestOTPActiveAccount.setTerminalId(CommonUtils.getIMEI(context));
        requestOTPActiveAccount.setTerminalInfo(CommonUtils.getModelName());
        requestOTPActiveAccount.setUsername(accountInfo.getUsername());
        requestOTPActiveAccount.setToken(CommonUtils.getToken(context));
        requestOTPActiveAccount.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestOTPActiveAccount));
        requestOTPActiveAccount.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseOTPActiveAccount> call = apiService.getOTPActivieAccount(requestOTPActiveAccount);
        call.enqueue(new Callback<ResponseOTPActiveAccount>() {
            @Override
            public void onResponse(Call<ResponseOTPActiveAccount> call, Response<ResponseOTPActiveAccount> response) {
                homeView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                homeView.onGetOTPActiveAccountSuccess(response.body().getResponseData(), publicKeyBase64);
                            } else {
                                homeView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        homeView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    homeView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseOTPActiveAccount> call, Throwable t) {
                homeView.dismissLoading();
                //   homeView.onSyncContactFail(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> getOTPActiveAccount(accountInfo, context));
            }
        });
    }

    @Override
    public void getCashValues(AccountInfo accountInfo, Context context) {
        homeView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetMoneyValue requestGetMoneyValue = new RequestGetMoneyValue();
        requestGetMoneyValue.setChannelCode(Constant.CHANNEL_CODE);
        requestGetMoneyValue.setFunctionCode(Constant.FUNCTION_GET_MONEY_VALUE);
        requestGetMoneyValue.setSessionId(CommonUtils.getSessionId(context));
        requestGetMoneyValue.setIssuerCodes(Collections.singletonList(Constant.ISSUER_CODE));
        requestGetMoneyValue.setUsername(accountInfo.getUsername());
        requestGetMoneyValue.setToken(CommonUtils.getToken(context));
        requestGetMoneyValue.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetMoneyValue));
        requestGetMoneyValue.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetMoneyValue> call = apiService.getMoneyValue(requestGetMoneyValue);
        call.enqueue(new Callback<ResponseGetMoneyValue>() {
            @Override
            public void onResponse(Call<ResponseGetMoneyValue> call, Response<ResponseGetMoneyValue> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            DatabaseUtil.deleteAllCashValue(context);
                            homeView.getCashValuesSuccess(response.body().getResponseData().getListDenomination());
                        } else {
                            homeView.dismissLoading();
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    }
                } else {
                    homeView.dismissLoading();
                    homeView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetMoneyValue> call, Throwable t) {
                homeView.dismissLoading();
                //   homeView.onSyncContactFail(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> {
                    getCashValues(accountInfo, context);
                });
            }
        });
    }

    @Override
    public void activeAccWalletInfo(AccountInfo accountInfo, ResponseData responseData, String otp, Context context) {
        homeView.showLoading();

        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetAccountWalletInfo requestGetAccountWalletInfo = new RequestGetAccountWalletInfo();
        requestGetAccountWalletInfo.setAppName(Constant.app_name);
        requestGetAccountWalletInfo.setFirebaseToken(ECashApplication.FBToken);
        requestGetAccountWalletInfo.setChannelCode(Constant.CHANNEL_CODE);
        requestGetAccountWalletInfo.setFunctionCode(Constant.FUNCTION_GET_WALLET_INFO);
        requestGetAccountWalletInfo.setOtpvalue(otp);
        requestGetAccountWalletInfo.setSessionId(accountInfo.getSessionId());
        requestGetAccountWalletInfo.setTerminalId(CommonUtils.getIMEI(context));
        requestGetAccountWalletInfo.setTerminalInfor(CommonUtils.getModelName());
        requestGetAccountWalletInfo.setToken(CommonUtils.getToken(context));
        requestGetAccountWalletInfo.setTransactionCode(responseData.getTransactionCode());
        requestGetAccountWalletInfo.setUsername(accountInfo.getUsername());
        requestGetAccountWalletInfo.setWalletId(responseData.getWalletId());
        requestGetAccountWalletInfo.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetAccountWalletInfo));
        requestGetAccountWalletInfo.setChannelSignature(CommonUtils.generateSignature(dataSign));
        Gson gson = new Gson();
        String json = gson.toJson(requestGetAccountWalletInfo);
        Log.e("json.", json);

        Call<ResponseGetAccountWalletInfo> call = apiService.getWalletInfo(requestGetAccountWalletInfo);
        call.enqueue(new Callback<ResponseGetAccountWalletInfo>() {
            @Override
            public void onResponse(Call<ResponseGetAccountWalletInfo> call, Response<ResponseGetAccountWalletInfo> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                AccountInfo mAccountInfo = response.body().getResponseData();
                                mAccountInfo.setTerminalId(CommonUtils.getIMEI(context));
                                mAccountInfo.setTerminalInfo(CommonUtils.getModelName());
                                mAccountInfo.setEcKeyPublicValue(accountInfo.getEcKeyPublicValue());
                                mAccountInfo.setMasterKey(responseData.getMasterKey());
                                mAccountInfo.setWalletId(responseData.getWalletId());
                                mAccountInfo.setLastAccessTime(responseData.getLastAccessTime());
                                mAccountInfo.setUserId(accountInfo.getUserId());
                                ECashApplication.privateKey = privateKeyBase64;
                                ECashApplication.masterKey = mAccountInfo.getMasterKey();

                                //update key
                                DatabaseUtil.changeMasterKeyDatabase(context, mAccountInfo.getMasterKey());
                                KeyStoreUtils.saveKeyPrivateWallet(privateKeyBase64, context);
                                KeyStoreUtils.saveMasterKey(mAccountInfo.getMasterKey(), context);
                                homeView.onActiveAccountSuccess(mAccountInfo);
                            } else {
                                homeView.dismissLoading();
                                homeView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else if (response.body().getResponseCode().equals("3014") ||
                                response.body().getResponseCode().equals("0998")) {
                            homeView.dismissLoading();
                            homeView.requestOTPFail(context.getResources().getString(R.string.err_otp_fail), responseData);
                        } else {
                            homeView.dismissLoading();
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        homeView.dismissLoading();
                        homeView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    homeView.dismissLoading();
                    homeView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetAccountWalletInfo> call, Throwable t) {
                homeView.dismissLoading();
                // homeView.showDialogError(context.getString(R.string.err_upload));
                ECashApplication.getInstance().showErrorConnection(t, () -> activeAccWalletInfo(accountInfo, responseData, otp, context));
            }
        });
    }

    @Override
    public void syncContact(Context context, AccountInfo accountInfo) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestSyncContact requestSyncContact = new RequestSyncContact();
        requestSyncContact.setChannelCode(Constant.CHANNEL_CODE);
        requestSyncContact.setFunctionCode(Constant.FUNCTION_SYNC_CONTACT);
        requestSyncContact.setSessionId(CommonUtils.getSessionId(context));
        requestSyncContact.setListContacts(CommonUtils.getListPhoneNumber(context));
        requestSyncContact.setPhoneNumber(accountInfo.getPersonMobilePhone());
        requestSyncContact.setUsername(accountInfo.getUsername());
        requestSyncContact.setWalletId(accountInfo.getWalletId());
        requestSyncContact.setToken(CommonUtils.getToken(context));
        requestSyncContact.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestSyncContact));
        requestSyncContact.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseSyncContact> call = apiService.syncContacts(requestSyncContact);
        call.enqueue(new Callback<ResponseSyncContact>() {
            @Override
            public void onResponse(Call<ResponseSyncContact> call, Response<ResponseSyncContact> response) {
                homeView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            SharedPrefs.getInstance().put(SharedPrefs.contactMaxDate, ECashApplication.lastTimeAddContact);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseSyncContact> call, Throwable t) {
                homeView.dismissLoading();
            }
        });
    }
}
