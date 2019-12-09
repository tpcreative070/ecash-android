
package vn.ecpay.ewallet.model.account.getAccountWalletInfo;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestGetAccountWalletInfo extends BaseObject {
    @SerializedName("appName")
    private String mAppName;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("firebaseToken")
    private String mFirebaseToken;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("otpvalue")
    private String mOtpvalue;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("terminalInfor")
    private String mTerminalInfor;
    @SerializedName("token")
    private String mToken;
    @SerializedName("transactionCode")
    private String mTransactionCode;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("walletId")
    private Long mWalletId;
    @SerializedName("auditNumber")
    private String mAuditNumber;
    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String mAuditNumber) {
        this.mAuditNumber = mAuditNumber;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

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

    public String getFirebaseToken() {
        return mFirebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
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

    public String getTerminalInfor() {
        return mTerminalInfor;
    }

    public void setTerminalInfor(String terminalInfor) {
        mTerminalInfor = terminalInfor;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getTransactionCode() {
        return mTransactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        mTransactionCode = transactionCode;
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
