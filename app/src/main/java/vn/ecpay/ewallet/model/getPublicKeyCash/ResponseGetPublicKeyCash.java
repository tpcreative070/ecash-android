
package vn.ecpay.ewallet.model.getPublicKeyCash;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseGetPublicKeyCash {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private ResponseDataGetPublicKeyCash mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public ResponseDataGetPublicKeyCash getResponseData() {
        return mResponseData;
    }

    public void setResponseData(ResponseDataGetPublicKeyCash responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
