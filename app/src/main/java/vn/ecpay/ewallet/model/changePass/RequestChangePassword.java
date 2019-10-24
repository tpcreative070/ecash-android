
package vn.ecpay.ewallet.model.changePass;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestChangePassword extends BaseObject {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("token")
    private String mToken;
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("channelSignature")
    private String mChannelSignature;

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getmChannelSignature() {
        return mChannelSignature;
    }

    public void setmChannelSignature(String mChannelSignature) {
        this.mChannelSignature = mChannelSignature;
    }
}
