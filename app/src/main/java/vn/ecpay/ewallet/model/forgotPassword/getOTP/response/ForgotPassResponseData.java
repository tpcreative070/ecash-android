
package vn.ecpay.ewallet.model.forgotPassword.getOTP.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ForgotPassResponseData implements Serializable {

    @SerializedName("channelId")
    private String mChannelId;
    @SerializedName("transactionCode")
    private String mTransactionCode;
    @SerializedName("userId")
    private String mUserId;

    public String getChannelId() {
        return mChannelId;
    }

    public void setChannelId(String channelId) {
        mChannelId = channelId;
    }

    public String getTransactionCode() {
        return mTransactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        mTransactionCode = transactionCode;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

}
