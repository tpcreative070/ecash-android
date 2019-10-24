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
import vn.ecpay.ewallet.model.cash.ecashToEdong.RequestEcashToEdong;
import vn.ecpay.ewallet.model.cash.ecashToEdong.ResponseECashToEdong;
import vn.ecpay.ewallet.model.cash.edongToEcash.RequestEdongToECash;
import vn.ecpay.ewallet.model.cash.edongToEcash.ResponseEdongToECash;
import vn.ecpay.ewallet.model.cash.getPublicKeyCash.RequestGetPublicKeyCash;
import vn.ecpay.ewallet.model.cash.getPublicKeyCash.ResponseGetPublicKeyCash;
import vn.ecpay.ewallet.model.cash.getPublicKeyOrganization.RequestGetPublicKeyOrganizetion;
import vn.ecpay.ewallet.model.cash.getPublicKeyOrganization.ResponseGetPublickeyOrganization;
import vn.ecpay.ewallet.model.cash.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.cash.getPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.model.chanel.RequestUpdateChanel;
import vn.ecpay.ewallet.model.chanel.response.ResponseUpdateChanel;
import vn.ecpay.ewallet.model.changePass.RequestChangePassword;
import vn.ecpay.ewallet.model.changePass.response.ResponseChangePassword;

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

    @POST("ecgateway/execute/FU00012")
    Call<ResponseGetAccountWalletInfo> getWalletInfo(@Body RequestGetAccountWalletInfo body);

    @POST("ecgateway/execute/GWEC0005")
    Call<ResponseGetPublicKeyCash> getPublicKeyCash(@Body RequestGetPublicKeyCash body);

    @POST("ecgateway/execute/GWEC0005")
    Call<ResponseEdongToECash> transferMoneyEdongToECash(@Body RequestEdongToECash body);

    @POST("ecgateway/execute/GW000001")
    Call<ResponseECashToEdong> transferMoneyECashToEDong(@Body RequestEcashToEdong body);

    @POST("ecgateway/execute/GWEC0006")
    Call<ResponseGetPublickeyOrganization> getPublicKeyOrganization(@Body RequestGetPublicKeyOrganizetion body);

    @POST("ecgateway/execute/GW000004")
    Call<ResponseGetOTP> getOTP(@Body RequestGetOTP body);

    @POST("ecgateway/execute/GW000003")
    Call<ResponseChangePassword> changePassword(@Body RequestChangePassword body);

    @POST("ecgateway/execute/GWEC0010")
    Call<ResponseCancelAccount> cancelAccount(@Body RequestCancelAccount body);

    @POST("UpdateChannel")
    Call<ResponseUpdateChanel> updateChannel(@Body RequestUpdateChanel body);
}