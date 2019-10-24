
package vn.ecpay.ewallet.model.account.getEdongInfo;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseEdongInfo {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private ResponseDataEdong mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public ResponseDataEdong getResponseData() {
        return mResponseData;
    }

    public void setResponseData(ResponseDataEdong responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
