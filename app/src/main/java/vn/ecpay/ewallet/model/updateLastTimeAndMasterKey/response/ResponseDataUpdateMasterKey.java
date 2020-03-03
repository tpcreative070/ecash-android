
package vn.ecpay.ewallet.model.updateLastTimeAndMasterKey.response;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseDataUpdateMasterKey {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelId")
    private Long mChannelId;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("customerId")
    private Long mCustomerId;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("functionId")
    private Long mFunctionId;
    @SerializedName("lastAccessTime")
    private String mLastAccessTime;
    @SerializedName("masterKey")
    private String mMasterKey;
    @SerializedName("phone")
    private Object mPhone;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("token")
    private String mToken;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("walletId")
    private Long mWalletId;

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public Long getChannelId() {
        return mChannelId;
    }

    public void setChannelId(Long channelId) {
        mChannelId = channelId;
    }

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public Long getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(Long customerId) {
        mCustomerId = customerId;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public Long getFunctionId() {
        return mFunctionId;
    }

    public void setFunctionId(Long functionId) {
        mFunctionId = functionId;
    }

    public String getLastAccessTime() {
        return mLastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        mLastAccessTime = lastAccessTime;
    }

    public String getMasterKey() {
        return mMasterKey;
    }

    public void setMasterKey(String masterKey) {
        mMasterKey = masterKey;
    }

    public Object getPhone() {
        return mPhone;
    }

    public void setPhone(Object phone) {
        mPhone = phone;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public String getTerminalId() {
        return mTerminalId;
    }

    public void setTerminalId(String terminalId) {
        mTerminalId = terminalId;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public Long getWalletId() {
        return mWalletId;
    }

    public void setWalletId(Long walletId) {
        mWalletId = walletId;
    }

}
