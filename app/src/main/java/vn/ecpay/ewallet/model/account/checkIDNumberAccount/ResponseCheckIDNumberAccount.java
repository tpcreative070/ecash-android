
package vn.ecpay.ewallet.model.account.checkIDNumberAccount;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseCheckIDNumberAccount extends BaseObject {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private Object mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public Object getResponseData() {
        return mResponseData;
    }

    public void setResponseData(Object responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
