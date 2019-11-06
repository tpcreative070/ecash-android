
package vn.ecpay.ewallet.model.account.active;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestActiveAccount extends BaseObject {

    @SerializedName("accountIdt")
    private String mAccountIdt;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("customerId")
    private String mCustomerId;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("idNumber")
    private String mIdNumber;
    @SerializedName("otpvalue")
    private String mOtpvalue;
    @SerializedName("transactionCode")
    private String mTransactionCode;
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("walletId")
    private Long mWalletId;

    public Long getmWalletId() {
        return mWalletId;
    }

    public void setWalletId(Long mWalletId) {
        this.mWalletId = mWalletId;
    }

    public String getAccountIdt() {
        return mAccountIdt;
    }

    public void setAccountIdt(String accountIdt) {
        mAccountIdt = accountIdt;
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

    public String getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(String customerId) {
        mCustomerId = customerId;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public String getIdNumber() {
        return mIdNumber;
    }

    public void setIdNumber(String idNumber) {
        mIdNumber = idNumber;
    }

    public String getOtpvalue() {
        return mOtpvalue;
    }

    public void setOtpvalue(String otpvalue) {
        mOtpvalue = otpvalue;
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

}
