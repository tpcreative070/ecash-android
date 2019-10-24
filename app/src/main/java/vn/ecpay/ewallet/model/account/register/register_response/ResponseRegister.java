
package vn.ecpay.ewallet.model.account.register.register_response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseRegister {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private AccountInfo mAccountInfo;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public AccountInfo getResponseData() {
        return mAccountInfo;
    }

    public void setResponseData(AccountInfo accountInfo) {
        mAccountInfo = accountInfo;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
