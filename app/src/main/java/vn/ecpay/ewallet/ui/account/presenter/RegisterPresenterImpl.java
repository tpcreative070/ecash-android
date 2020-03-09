package vn.ecpay.ewallet.ui.account.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

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
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.GetStringErrorCode;
import vn.ecpay.ewallet.model.OTP.RequestGetOTP;
import vn.ecpay.ewallet.model.OTP.response.ResponseGetOTP;
import vn.ecpay.ewallet.model.account.register.RequestRegister;
import vn.ecpay.ewallet.model.account.active.RequestActiveAccount;
import vn.ecpay.ewallet.model.account.active.ResponseActiveAccount;
import vn.ecpay.ewallet.model.account.checkIDNumberAccount.RequestCheckIDNumberAccount;
import vn.ecpay.ewallet.model.account.checkIDNumberAccount.ResponseCheckIDNumberAccount;
import vn.ecpay.ewallet.model.account.checkUserName.RequestCheckUserNameAccount;
import vn.ecpay.ewallet.model.account.checkUserName.ResponseCheckUserNameAccount;
import vn.ecpay.ewallet.model.account.getEdongInfo.RequestEdongInfo;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseEdongInfo;
import vn.ecpay.ewallet.model.account.login.RequestLogin;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.ResponseLoginAfterRegister;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.account.register.register_response.ResponseRegister;
import vn.ecpay.ewallet.model.contact.RequestSyncContact;
import vn.ecpay.ewallet.model.contact.ResponseSyncContact;
import vn.ecpay.ewallet.ui.account.view.RegisterView;

public class RegisterPresenterImpl implements RegisterPresenter {

    private RegisterView registerView;
    @Inject
    ECashApplication application;

    @Inject
    public RegisterPresenterImpl() {
    }

    @Override
    public void checkUSerNameAccount(String userName) {
        registerView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestCheckUserNameAccount requestCheckUserNameAccount = new RequestCheckUserNameAccount();
        requestCheckUserNameAccount.setChannelCode(Constant.CHANNEL_CODE);
        requestCheckUserNameAccount.setFunctionCode(Constant.FUNCTION_CHECK_USER_NAME);
        requestCheckUserNameAccount.setUsername(userName);
        requestCheckUserNameAccount.setChannelSignature(Constant.STR_EMPTY);
        requestCheckUserNameAccount.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestCheckUserNameAccount));
        requestCheckUserNameAccount.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseCheckUserNameAccount> call = apiService.checkUserNameAccount(requestCheckUserNameAccount);
        call.enqueue(new Callback<ResponseCheckUserNameAccount>() {
            @Override
            public void onResponse(Call<ResponseCheckUserNameAccount> call, Response<ResponseCheckUserNameAccount> response) {
                registerView.dismissLoading();
                if (response.isSuccessful()) {
                    if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                        registerView.onUserNameFail();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCheckUserNameAccount> call, Throwable t) {
                registerView.dismissLoading();
                registerView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void checkIdAndPhoneNumberAccount(String idNumber, String phone) {
        registerView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestCheckIDNumberAccount requestCheckIDNumberAccount = new RequestCheckIDNumberAccount();
        requestCheckIDNumberAccount.setChannelCode(Constant.CHANNEL_CODE);
        requestCheckIDNumberAccount.setFunctionCode(Constant.FUNCTION_CHECK_ID_NUMBER);
        requestCheckIDNumberAccount.setIdNumber(idNumber);
        requestCheckIDNumberAccount.setmPersonMobilePhone(phone);
        requestCheckIDNumberAccount.setChannelSignature(Constant.STR_EMPTY);
        requestCheckIDNumberAccount.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestCheckIDNumberAccount));
        requestCheckIDNumberAccount.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseCheckIDNumberAccount> call = apiService.checkIDNumberAccount(requestCheckIDNumberAccount);
        call.enqueue(new Callback<ResponseCheckIDNumberAccount>() {
            @Override
            public void onResponse(Call<ResponseCheckIDNumberAccount> call, Response<ResponseCheckIDNumberAccount> response) {
                registerView.dismissLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getResponseCode() != null) {
                            String code = response.body().getResponseCode();
                            if (code.equals(Constant.CODE_SUCCESS)) {
                                registerView.onIDNumberFail(application.getString(R.string.err_nmnd_is_exit));
                            }
                            if (code.equals("1007")) {
                                registerView.onPhoneNumberFail(application.getString(R.string.err_validate_phone_fail));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCheckIDNumberAccount> call, Throwable t) {
                registerView.dismissLoading();
                registerView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void requestRegister(Context context, String userName, String CMND, String pass, String name, String phone) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestRegister requestRegister = new RequestRegister();
        requestRegister.setChannelCode(Constant.CHANNEL_CODE);
        requestRegister.setFunctionCode(Constant.FUNCTION_REGISTER);
        requestRegister.setUsername(userName);
        requestRegister.setIdNumber(CMND);
        requestRegister.setPassword(CommonUtils.encryptPassword(pass));
        requestRegister.setNickname(userName);

        EllipticCurve ec = EllipticCurve.getSecp256k1();
        ECPrivateKeyParameters ks = ec.generatePrivateKeyParameters();
        ECPublicKeyParameters kp = ec.getPublicKeyParameters(ks);

        byte[] mPriKey = ks.getD().toByteArray();
        byte[] mPubKey = kp.getQ().getEncoded(false);

        String privateKeyBase64 = Base64.encodeToString(mPriKey, Base64.DEFAULT).replaceAll("\n", "");
        String publicKeyBase64 = Base64.encodeToString(mPubKey, Base64.DEFAULT).replaceAll("\n", "");


        String[] separated = name.split(" ");
        if (separated.length == 1) {
            requestRegister.setPersonFirstName(name);
            requestRegister.setPersonMiddleName("");
            requestRegister.setPersonLastName("");
        } else if (separated.length == 2) {
            requestRegister.setPersonFirstName(separated[0]);
            requestRegister.setPersonMiddleName("");
            requestRegister.setPersonLastName(separated[1]);
        } else if (separated.length == 3) {
            requestRegister.setPersonFirstName(separated[0]);
            requestRegister.setPersonMiddleName(separated[1]);
            requestRegister.setPersonLastName(separated[2]);
        } else {
            requestRegister.setPersonFirstName(separated[0]);
            StringBuilder middleName = new StringBuilder();
            for (int i = 1; i < separated.length - 1; i++) {
                if (i == 1) {
                    middleName.append(separated[i]);
                } else {
                    middleName.append(" ").append(separated[i]);
                }
            }
            requestRegister.setPersonMiddleName(middleName.toString());
            requestRegister.setPersonLastName(separated[separated.length - 1]);
        }

        requestRegister.setKeyPublicAlias(CommonUtils.getKeyAlias());
        requestRegister.setEcKeyPublicValue(publicKeyBase64);
        requestRegister.setPersonMobilePhone(phone);

        requestRegister.setTerminalId(CommonUtils.getIMEI(context));
        requestRegister.setTerminalInfo(CommonUtils.getModelName());
        requestRegister.setAppName(Constant.app_name);
        requestRegister.setFirebaseToken(ECashApplication.FBToken);
        requestRegister.setAuditNumber(CommonUtils.getAuditNumber());
        requestRegister.setChannelSignature("");

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestRegister));
        requestRegister.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Gson gson = new Gson();
        String json = gson.toJson(requestRegister);
        Log.e("json", json);

        Call<ResponseRegister> call = apiService.registerAccount(requestRegister);
        call.enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                registerView.dismissLoading();
                String code = response.body().getResponseCode();
                if (response.isSuccessful()) {
                    if (code.equals(Constant.CODE_SUCCESS)) {
                        AccountInfo accountInfo = response.body().getResponseData();
                        accountInfo.setTerminalId(CommonUtils.getIMEI(context));
                        accountInfo.setTerminalInfo(CommonUtils.getModelName());
                        accountInfo.setPassword(CommonUtils.encryptPassword(pass));
                        accountInfo.setUsername(userName);
                        accountInfo.setIdNumber(CMND);
                        accountInfo.setPersonMobilePhone(phone);
                        accountInfo.setPersonFirstName(requestRegister.getPersonFirstName());
                        accountInfo.setPersonMiddleName(requestRegister.getPersonMiddleName());
                        accountInfo.setPersonLastName(requestRegister.getPersonLastName());
                        accountInfo.setEcKeyPublicValue(requestRegister.getEcKeyPublicValue());
                        accountInfo.setLastAccessTime(accountInfo.getLastAccessTime());
                        registerView.registerSuccess(accountInfo, privateKeyBase64, publicKeyBase64);
                    } else {
                        if(response.body().getResponseCode()!=null){
                            registerView.registerFail(new GetStringErrorCode().errorMessage(context,response.body().getResponseCode(),response.body().getResponseMessage()));
                        }

                    }
                } else {
                    registerView.registerFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {
                registerView.dismissLoading();
                registerView.registerFail(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void retryOTP(Context context,AccountInfo accountInfo) {
        registerView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetOTP requestGetOTP = new RequestGetOTP();
        requestGetOTP.setChannelCode(Constant.CHANNEL_CODE);
        requestGetOTP.setFunctionCode(Constant.FUNCTION_GET_OTP);
        requestGetOTP.setToken(CommonUtils.getToken());
        requestGetOTP.setUsername(accountInfo.getUsername());
        requestGetOTP.setWalletId(String.valueOf(accountInfo.getWalletId()));
        requestGetOTP.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetOTP));
        requestGetOTP.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetOTP> call = apiService.getOTP(requestGetOTP);
        call.enqueue(new Callback<ResponseGetOTP>() {
            @Override
            public void onResponse(Call<ResponseGetOTP> call, Response<ResponseGetOTP> response) {
                registerView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            accountInfo.setTransactionCode(String.valueOf(response.body().getResponseData().getTransactionCode()));
                            registerView.retryOTPSuccess(accountInfo);
                        } else if (response.body().getResponseCode().equals("3015")) {
                            registerView.requestOtpErr(accountInfo, response.body().getResponseMessage());
                        } else {
                          //  registerView.showDialogError(response.body().getResponseMessage());
                            registerView.showDialogError(new GetStringErrorCode().errorMessage(context,response.body().getResponseCode(),response.body().getResponseMessage()));
                        }
                    } else {
                        registerView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    registerView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOTP> call, Throwable t) {
                registerView.dismissLoading();
                registerView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void activeAccount(Context context,AccountInfo accountInfo, String otp) {
        registerView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestActiveAccount requestActiveAccount = new RequestActiveAccount();
        requestActiveAccount.setAccountIdt(String.valueOf(accountInfo.getAccountIdt()));
        requestActiveAccount.setChannelCode(Constant.CHANNEL_CODE);
        requestActiveAccount.setCustomerId(String.valueOf(accountInfo.getCustomerId()));
        requestActiveAccount.setIdNumber(accountInfo.getIdNumber());
        requestActiveAccount.setUserId(accountInfo.getUserId());
        requestActiveAccount.setFunctionCode(Constant.FUNCTION_ACTIVE_ACCOUNT);
        requestActiveAccount.setOtpvalue(otp);
        requestActiveAccount.setWalletId(accountInfo.getWalletId());
        requestActiveAccount.setTransactionCode(accountInfo.getTransactionCode());
        requestActiveAccount.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestActiveAccount));
        requestActiveAccount.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Gson gson = new Gson();
        String json = gson.toJson(requestActiveAccount);
        Log.e("json", json);

        Call<ResponseActiveAccount> call = apiService.activeAccount(requestActiveAccount);
        call.enqueue(new Callback<ResponseActiveAccount>() {
            @Override
            public void onResponse(Call<ResponseActiveAccount> call, Response<ResponseActiveAccount> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            registerView.activeAccountSuccess(accountInfo);
                        } else if (response.body().getResponseCode().equals("3014") ||
                                response.body().getResponseCode().equals("0998")) {
                            registerView.dismissLoading();
                            registerView.requestOtpErr(accountInfo, application.getString(R.string.err_otp_fail));
                        } else {
                            registerView.dismissLoading();
                           // registerView.showDialogError(response.body().getResponseMessage());
                            registerView.showDialogError(new GetStringErrorCode().errorMessage(context,response.body().getResponseCode(),response.body().getResponseMessage()));
                        }
                    } else {
                        registerView.dismissLoading();
                        registerView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    registerView.dismissLoading();
                    registerView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseActiveAccount> call, Throwable t) {
                registerView.dismissLoading();
                registerView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void loginAccount(Context context,AccountInfo accountInfo) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setChannelCode(Constant.CHANNEL_CODE);
        requestLogin.setFunctionCode(Constant.FUNCTION_LOGIN);
        requestLogin.setUsername(accountInfo.getUsername());
        requestLogin.setToken(CommonUtils.getToken(accountInfo));
        requestLogin.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestLogin));
        requestLogin.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseLoginAfterRegister> call = apiService.login(requestLogin);
        call.enqueue(new Callback<ResponseLoginAfterRegister>() {
            @Override
            public void onResponse(Call<ResponseLoginAfterRegister> call, Response<ResponseLoginAfterRegister> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            accountInfo.setSessionId(response.body().getAccountInfo().getSessionId());
                            registerView.loginSuccess(accountInfo);
                        } else {
                            registerView.dismissLoading();
                            registerView.showDialogError(new GetStringErrorCode().errorMessage(context,response.body().getResponseCode(),response.body().getResponseMessage()));
                        }
                    } else {
                        registerView.dismissLoading();
                        registerView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    registerView.dismissLoading();
                    registerView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginAfterRegister> call, Throwable t) {
                registerView.dismissLoading();
                registerView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void getEDongInfo(Context context,AccountInfo accountInfo) {
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
                registerView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            registerView.getEDongInfoSuccess(accountInfo, response.body().getResponseData());
                        } else {
                           // registerView.showDialogError(response.body().getResponseMessage());
                            registerView.showDialogError(new GetStringErrorCode().errorMessage(context,response.body().getResponseCode(),response.body().getResponseMessage()));

                        }
                    } else {
                        registerView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    registerView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongInfo> call, Throwable t) {
                registerView.dismissLoading();
                registerView.showDialogError(application.getString(R.string.err_upload));
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
        requestSyncContact.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestSyncContact.setListContacts(CommonUtils.getListPhoneNumber(context));
        requestSyncContact.setPhoneNumber(accountInfo.getPersonMobilePhone());
        requestSyncContact.setUsername(accountInfo.getUsername());
        requestSyncContact.setWalletId(accountInfo.getWalletId());
        requestSyncContact.setToken(CommonUtils.getToken(accountInfo));
        requestSyncContact.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestSyncContact));
        requestSyncContact.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseSyncContact> call = apiService.syncContacts(requestSyncContact);
        call.enqueue(new Callback<ResponseSyncContact>() {
            @Override
            public void onResponse(Call<ResponseSyncContact> call, Response<ResponseSyncContact> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
//                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
//                            registerView.onSyncContactSuccess();
//                        } else {
//                            registerView.onSyncContactFail(response.body().getResponseMessage());
//                        }
                        registerView.onSyncContactFail(response.body().getResponseMessage());
                    }
                } else {
                    registerView.onSyncContactFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseSyncContact> call, Throwable t) {
                registerView.onSyncContactFail(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void setView(RegisterView view) {
        this.registerView = view;
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
}
