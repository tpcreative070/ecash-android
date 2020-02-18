
package vn.ecpay.ewallet.model.contactAdd;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestAddContact extends BaseObject {

    @SerializedName("addNewWalletId")
    private String mAddNewWalletId;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("listWallets")
    private List<String> mListWallets;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("token")
    private String mToken;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("walletId")
    private String mWalletId;
    @SerializedName("auditNumber")
    private String mAuditNumber;
    @SerializedName("channelSignature")
    private String mChannelSignature;

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String mChannelSignature) {
        this.mChannelSignature = mChannelSignature;
    }

    public String getAddNewWalletId() {
        return mAddNewWalletId;
    }

    public void setAddNewWalletId(String addNewWalletId) {
        mAddNewWalletId = addNewWalletId;
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

    public List<String> getListWallets() {
        return mListWallets;
    }

    public void setListWallets(List<String> listWallets) {
        mListWallets = listWallets;
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

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getWalletId() {
        return mWalletId;
    }

    public void setWalletId(String walletId) {
        mWalletId = walletId;
    }

    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String mAuditNumber) {
        this.mAuditNumber = mAuditNumber;
    }
}
