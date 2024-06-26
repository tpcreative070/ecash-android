
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
    private AccountInfo mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public AccountInfo getResponseData() {
        return mResponseData;
    }

    public void setResponseData(AccountInfo responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
