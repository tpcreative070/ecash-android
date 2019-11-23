
package vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestOTPActiveAccount extends BaseObject {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelId")
    private String mChannelId;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("ecKeyPublicValue")
    private String mEcKeyPublicValue;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("keyPublicAlias")
    private String mKeyPublicAlias;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("token")
    private String mToken;
    @SerializedName("username")
    private String mUsername;

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public void setChannelId(String channelId) {
        mChannelId = channelId;
    }

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public String getEcKeyPublicValue() {
        return mEcKeyPublicValue;
    }

    public void setEcKeyPublicValue(String ecKeyPublicValue) {
        mEcKeyPublicValue = ecKeyPublicValue;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public String getKeyPublicAlias() {
        return mKeyPublicAlias;
    }

    public void setKeyPublicAlias(String keyPublicAlias) {
        mKeyPublicAlias = keyPublicAlias;
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

    public String getTerminalInfo() {
        return mTerminalInfo;
    }

    public void setTerminalInfo(String terminalInfo) {
        mTerminalInfo = terminalInfo;
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

}
