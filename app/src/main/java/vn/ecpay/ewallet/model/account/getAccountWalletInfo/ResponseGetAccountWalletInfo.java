
package vn.ecpay.ewallet.model.account.getAccountWalletInfo;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseGetAccountWalletInfo {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private AccountInfo accountInfo;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }
}
