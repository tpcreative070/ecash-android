
package vn.ecpay.ewallet.model.forgotPassword.changePass.request;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ChangePassRequest extends BaseObject {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("otpvalue")
    private String mOtpvalue;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("transactionCode")
    private String mTransactionCode;
    @SerializedName("userId")
    private String mUserId;

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public String getOtpvalue() {
        return mOtpvalue;
    }

    public void setOtpvalue(String otpvalue) {
        mOtpvalue = otpvalue;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
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
