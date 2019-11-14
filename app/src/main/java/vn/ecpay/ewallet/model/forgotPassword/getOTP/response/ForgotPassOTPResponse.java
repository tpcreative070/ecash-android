
package vn.ecpay.ewallet.model.forgotPassword.getOTP.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ForgotPassOTPResponse {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private ForgotPassResponseData mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public ForgotPassResponseData getResponseData() {
        return mResponseData;
    }

    public void setResponseData(ForgotPassResponseData responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
