
package vn.ecpay.ewallet.model.getPublicKeyWallet;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseGetPublicKeyWallet {

    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseData")
    private ResponseDataGetPublicKeyWallet mResponseData;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public ResponseDataGetPublicKeyWallet getResponseData() {
        return mResponseData;
    }

    public void setResponseData(ResponseDataGetPublicKeyWallet responseData) {
        mResponseData = responseData;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

}
