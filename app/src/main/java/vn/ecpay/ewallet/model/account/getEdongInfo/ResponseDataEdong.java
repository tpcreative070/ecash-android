
package vn.ecpay.ewallet.model.account.getEdongInfo;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseDataEdong {

    @SerializedName("auditNumber")
    private String mAuditNumber;
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
    @SerializedName("listAcc")
    private ArrayList<EdongInfo> mListAcc;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("token")
    private String mToken;
    @SerializedName("totalPages")
    private Long mTotalPages;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("walletId")
    private Long mWalletId;

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

    public ArrayList<EdongInfo> getListAcc() {
        return mListAcc;
    }

    public void setListAcc(ArrayList<EdongInfo> listAcc) {
        mListAcc = listAcc;
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

    public Long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Long totalPages) {
        mTotalPages = totalPages;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public Long getWaletId() {
        return mWalletId;
    }

    public void setWaletId(Long waletId) {
        mWalletId = waletId;
    }

}
