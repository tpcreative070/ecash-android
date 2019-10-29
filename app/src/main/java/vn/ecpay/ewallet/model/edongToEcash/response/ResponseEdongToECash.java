
package vn.ecpay.ewallet.model.edongToEcash.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseEdongToECash {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private CashInResponse mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public CashInResponse getResponseData() {
        return mResponseData;
    }

    public void setResponseData(CashInResponse responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
