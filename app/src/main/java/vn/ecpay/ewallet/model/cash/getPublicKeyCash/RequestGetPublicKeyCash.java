
package vn.ecpay.ewallet.model.cash.getPublicKeyCash;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestGetPublicKeyCash extends BaseObject {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("decisionCode")
    private String mDecisionCode;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
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

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public String getDecisionCode() {
        return mDecisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        mDecisionCode = decisionCode;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
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

}
