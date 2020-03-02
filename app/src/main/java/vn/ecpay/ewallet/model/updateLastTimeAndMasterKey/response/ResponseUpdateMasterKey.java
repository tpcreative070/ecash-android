
package vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseUpdateMasterKey {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private ResponseDataUpdateMasterKey mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public ResponseDataUpdateMasterKey getResponseData() {
        return mResponseData;
    }

    public void setResponseData(ResponseDataUpdateMasterKey responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
