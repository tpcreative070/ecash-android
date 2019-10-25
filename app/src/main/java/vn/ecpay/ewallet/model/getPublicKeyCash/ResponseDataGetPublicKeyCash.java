
package vn.ecpay.ewallet.model.getPublicKeyCash;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseDataGetPublicKeyCash {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelId")
    private Long mChannelId;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("customerId")
    private Long mCustomerId;
    @SerializedName("decisionAcckp")
    private String mDecisionAcckp;
    @SerializedName("decisionCode")
    private String mDecisionCode;
    @SerializedName("decisionTrekp")
    private String mDecisionTrekp;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("functionId")
    private Long mFunctionId;
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

    public String getDecisionAcckp() {
        return mDecisionAcckp;
    }

    public void setDecisionAcckp(String decisionAcckp) {
        mDecisionAcckp = decisionAcckp;
    }

    public String getDecisionCode() {
        return mDecisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        mDecisionCode = decisionCode;
    }

    public String getDecisionTrekp() {
        return mDecisionTrekp;
    }

    public void setDecisionTrekp(String decisionTrekp) {
        mDecisionTrekp = decisionTrekp;
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
