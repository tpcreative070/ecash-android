
package vn.ecpay.ewallet.model.ecashToEdong;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseECashToEdong {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private ECashToEDong mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public ECashToEDong getResponseData() {
        return mResponseData;
    }

    public void setResponseData(ECashToEDong responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
