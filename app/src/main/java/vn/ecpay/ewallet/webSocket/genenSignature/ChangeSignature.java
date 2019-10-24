
package vn.ecpay.ewallet.webSocket.genenSignature;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ChangeSignature extends BaseObject {

    @SerializedName("auditNumber")
    private String mAuditNumber;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("walletId")
    private String mWalletId;

    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String auditNumber) {
        mAuditNumber = auditNumber;
    }

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

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getmWalletId() {
        return mWalletId;
    }

    public void setmWalletId(String mWalletId) {
        this.mWalletId = mWalletId;
    }
}
