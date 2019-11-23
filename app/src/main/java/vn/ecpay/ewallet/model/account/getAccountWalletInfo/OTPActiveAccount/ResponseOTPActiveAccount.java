
package vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseOTPActiveAccount {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private ResponseData mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public ResponseData getResponseData() {
        return mResponseData;
    }

    public void setResponseData(ResponseData responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
