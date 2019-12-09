
package vn.ecpay.ewallet.model.cashChange;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestECashChange extends BaseObject {

    @SerializedName("cashEnc")
    private String mCashEnc;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("quantities")
    private List<Integer> mQuantities;
    @SerializedName("receiver")
    private Long mReceiver;
    @SerializedName("sender")
    private String mSender;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("token")
    private String mToken;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("values")
    private List<Integer> mValues;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("auditNumber")
    private String mAuditNumber;

    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String mAuditNumber) {
        this.mAuditNumber = mAuditNumber;
    }

    public String getCashEnc() {
        return mCashEnc;
    }

    public void setCashEnc(String cashEnc) {
        mCashEnc = cashEnc;
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

    public List<Integer> getQuantities() {
        return mQuantities;
    }

    public void setQuantities(List<Integer> quantities) {
        mQuantities = quantities;
    }

    public Long getReceiver() {
        return mReceiver;
    }

    public void setReceiver(Long receiver) {
        mReceiver = receiver;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
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

    public List<Integer> getValues() {
        return mValues;
    }

    public void setValues(List<Integer> values) {
        mValues = values;
    }

    public String getmChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String mChannelSignature) {
        this.mChannelSignature = mChannelSignature;
    }
}
