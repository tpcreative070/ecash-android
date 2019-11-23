
package vn.ecpay.ewallet.model.account.getAccountWalletInfo.OTPActiveAccount;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseData {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelId")
    private String mChannelId;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("lastAccessTime")
    private String mLastAccessTime;
    @SerializedName("masterKey")
    private String mMasterKey;
    @SerializedName("transactionCode")
    private String mTransactionCode;
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("walletId")
    private Long mWalletId;

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

    public Long getWalletId() {
        return mWalletId;
    }

    public void setWalletId(Long walletId) {
        mWalletId = walletId;
    }

}
