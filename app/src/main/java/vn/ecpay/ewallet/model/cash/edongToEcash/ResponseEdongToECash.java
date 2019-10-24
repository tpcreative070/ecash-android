
package vn.ecpay.ewallet.model.cash.edongToEcash;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseEdongToECash {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private EDongToECash mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public EDongToECash getResponseData() {
        return mResponseData;
    }

    public void setResponseData(EDongToECash responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
