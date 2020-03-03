package vn.ecpay.ewallet.common.api_request;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.ecpay.ewallet.model.OTP.RequestGetOTP;
import vn.ecpay.ewallet.model.OTP.response.ResponseGetOTP;
import vn.ecpay.ewallet.model.account.active.RequestActiveAccount;
import vn.ecpay.ewallet.model.account.active.ResponseActiveAccount;
import vn.ecpay.ewallet.model.account.cancelAccount.RequestCancelAccount;
import vn.ecpay.ewallet.model.account.cancelAccount.response.ResponseCancelAccount;
import vn.ecpay.ewallet.model.account.checkIDNumberAccount.RequestCheckIDNumberAccount;
import vn.ecpay.ewallet.model.account.checkIDNumberAccount.ResponseCheckIDNumberAccount;
import vn.ecpay.ewallet.model.account.checkUserName.RequestCheckUserNameAccount;
import vn.ecpay.ewallet.model.account.checkUserName.ResponseCheckUserNameAccount;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.RequestOTPActiveAccount;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount.ResponseOTPActiveAccount;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.RequestGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.ResponseGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.getEdongInfo.RequestEdongInfo;
import vn.ecpay.ewallet.model.account.getEdongInfo.ResponseEdongInfo;
import vn.ecpay.ewallet.model.account.login.RequestLogin;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.ResponseLoginAfterRegister;
import vn.ecpay.ewallet.model.account.logout.RequestLogOut;
import vn.ecpay.ewallet.model.account.logout.response.ResponseLogOut;
import vn.ecpay.ewallet.model.account.register.RequestRegister;
import vn.ecpay.ewallet.model.account.register.register_response.ResponseRegister;
import vn.ecpay.ewallet.model.account.updateAvartar.request.RequestUpdateAvartar;
import vn.ecpay.ewallet.model.account.updateAvartar.response.ResponseUpdateAvartar;
import vn.ecpay.ewallet.model.account.updateInfo.request.RequestUpdateAccountInfo;
import vn.ecpay.ewallet.model.account.updateInfo.response.ResponseUpdateAccountInfo;
import vn.ecpay.ewallet.model.cashChange.RequestECashChange;
import vn.ecpay.ewallet.model.cashValue.request.RequestGetMoneyValue;
import vn.ecpay.ewallet.model.cashValue.response.ResponseGetMoneyValue;
import vn.ecpay.ewallet.model.changePass.RequestChangePassword;
import vn.ecpay.ewallet.model.changePass.response.ResponseChangePassword;
import vn.ecpay.ewallet.model.contact.RequestSyncContact;
import vn.ecpay.ewallet.model.contact.ResponseSyncContact;
import vn.ecpay.ewallet.model.contactAdd.RequestAddContact;
import vn.ecpay.ewallet.model.contactAdd.ResponseAddContact;
import vn.ecpay.ewallet.model.contactDelete.RequestDeleteContact;
import vn.ecpay.ewallet.model.contactDelete.ResponseDeleteContact;
import vn.ecpay.ewallet.model.ecashToEdong.RequestEcashToEdong;
import vn.ecpay.ewallet.model.ecashToEdong.ResponseECashToEdong;
import vn.ecpay.ewallet.model.edongToEcash.RequestEdongToECash;
import vn.ecpay.ewallet.model.edongToEcash.response.ResponseEdongToECash;
import vn.ecpay.ewallet.model.forgotPassword.changePass.request.ChangePassRequest;
import vn.ecpay.ewallet.model.forgotPassword.changePass.response.ChangePassResponse;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.request.ForgotPassOTPRequest;
import vn.ecpay.ewallet.model.forgotPassword.getOTP.response.ForgotPassOTPResponse;
import vn.ecpay.ewallet.model.getPublicKeyCash.RequestGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyCash.ResponseGetPublicKeyCash;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.RequestGetPublicKeyOrganizetion;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.ResponseGetPublickeyOrganization;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone.ResponseGetPublicKeyByPhone;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.RequestUpdateMasterKey;
import vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response.ResponseUpdateMasterKey;

public interface APIService {

    @POST("ecgateway/execute/FU0001")
    Call<ResponseRegister> registerAccount(@Body RequestRegister body);

    @POST("ecgateway/execute/FU100")
    Call<ResponseCheckIDNumberAccount> checkIDNumberAccount(@Body RequestCheckIDNumberAccount body);

    @POST("ecgateway/execute/FU00003")
    Call<ResponseCheckUserNameAccount> checkUserNameAccount(@Body RequestCheckUserNameAccount body);

    @POST("ecgateway/execute/FU0003")
    Call<ResponseActiveAccount> activeAccount(@Body RequestActiveAccount body);

    @POST("ecgateway/execute/FU00004")
    Call<ResponseLoginAfterRegister> login(@Body RequestLogin body);

    @POST("ecgateway/execute/GW000001")
    Call<ResponseLogOut> logOut(@Body RequestLogOut body);

    @POST("ecgateway/execute/FU0002")
    Call<ResponseEdongInfo> getEdongInfo(@Body RequestEdongInfo body);

    @POST("ecgateway/execute/GWEC0007")
    Call<ResponseGetPublicKeyWallet> getPublicKeyWallet(@Body RequestGetPublicKeyWallet body);

    @POST("ecgateway/execute/GWEC0016")
    Call<ResponseGetPublicKeyByPhone> getPublicKeyWalletByPhone(@Body RequestGetPublicKeyWallet body);

    @POST("ecgateway/execute/FU00012")
    Call<ResponseGetAccountWalletInfo> getWalletInfo(@Body RequestGetAccountWalletInfo body);

    @POST("ecgateway/execute/GWEC0007")
    Call<ResponseGetAccountWalletInfo> getWalletAccountInfo(@Body RequestGetAccountWalletInfo body);

    @POST("ecgateway/execute/GWEC0005")
    Call<ResponseGetPublicKeyCash> getPublicKeyCash(@Body RequestGetPublicKeyCash body);

    @POST("ecgateway/execute/GWEC0004")
    Call<ResponseEdongToECash> transferMoneyEdongToECash(@Body RequestEdongToECash body);

    @POST("ecgateway/execute/GW000001")
    Call<ResponseECashToEdong> transferMoneyECashToEDong(@Body RequestEcashToEdong body);

    @POST("ecgateway/execute/GWEC0006")
    Call<ResponseGetPublickeyOrganization> getPublicKeyOrganization(@Body RequestGetPublicKeyOrganizetion body);

    @POST("ecgateway/execute/GW000004")
    Call<ResponseGetOTP> getOTP(@Body RequestGetOTP body);

    @POST("ecgateway/execute/GW000003")
    Call<ResponseChangePassword> changePassword(@Body RequestChangePassword body);

    @POST("ecgateway/execute/GWEC0020")
    Call<ResponseCancelAccount> cancelAccount(@Body RequestCancelAccount body);

    @POST("ecgateway/execute/GWEC0011")
    Call<ResponseEdongToECash> changeCash(@Body RequestECashChange body);

    @POST("ecgateway/execute/GWEC0017")
    Call<ResponseSyncContact> syncContacts(@Body RequestSyncContact body);

    @POST("ecgateway/execute/GWEC0023")
    Call<ResponseAddContact> addContacts(@Body RequestAddContact body);

    @POST("ecgateway/execute/GWEC0038")
    Call<ResponseDeleteContact> deleteContacts(@Body RequestDeleteContact body);

    @POST("ecgateway/execute/GWEC0024")
    Call<ForgotPassOTPResponse> getOTPForgotPass(@Body ForgotPassOTPRequest body);

    @POST("ecgateway/execute/GWEC0025")
    Call<ChangePassResponse> changePass(@Body ChangePassRequest body);

    @POST("ecgateway/execute/GWEC0029")
    Call<ResponseOTPActiveAccount> getOTPActivieAccount(@Body RequestOTPActiveAccount body);

    @POST("ecgateway/execute/GWEC0021")
    Call<ResponseUpdateAccountInfo> updateAccountInfo(@Body RequestUpdateAccountInfo body);

    @POST("ecgateway/execute/GWEC0022")
    Call<ResponseUpdateAvartar> updateAvartar(@Body RequestUpdateAvartar body);

    @POST("ecgateway/execute/GWEC0031")
    Call<ResponseGetMoneyValue> getMoneyValue(@Body RequestGetMoneyValue body);

    @POST("ecgateway/execute/GWEC0008")
    Call<ResponseUpdateMasterKey> updateLastTimeAndMasterKey(@Body RequestUpdateMasterKey body);
}