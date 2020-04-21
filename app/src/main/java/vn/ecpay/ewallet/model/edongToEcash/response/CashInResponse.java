
package vn.ecpay.ewallet.model.edongToEcash.response;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class CashInResponse implements Serializable {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("amountValid")
    private Long mAmountValid;
    @SerializedName("cashEnc")
    private String mCashEnc;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("content")
    private String mContent;
    @SerializedName("id")
    private String mId;
    @SerializedName("invalidSerials")
    private String mInvalidSerials;
    @SerializedName("receiver")
    private Long mReceiver;
    @SerializedName("sender")
    private Long mSender;
    @SerializedName("time")
    private String mTime;
    @SerializedName("type")
    private String mType;

    @SerializedName("auditNumber")
    private String mAuditNumber;
    @SerializedName("channelId")
    private Long mChannelId;
    @SerializedName("creditAccount")
    private String mCreditAccount;
    @SerializedName("customerId")
    private Long mCustomerId;
    @SerializedName("debitAccount")
    private String mDebitAccount;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("functionId")
    private Long mFunctionId;
    @SerializedName("quantities")
    private List<Long> mQuantities;
    @SerializedName("refId")
    private Long mRefId;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("token")
    private String mToken;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("values")
    private List<Long> mValues;

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
    }

    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String auditNumber) {
        mAuditNumber = auditNumber;
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

    public String getCreditAccount() {
        return mCreditAccount;
    }

    public void setCreditAccount(String creditAccount) {
        mCreditAccount = creditAccount;
    }

    public Long getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(Long customerId) {
        mCustomerId = customerId;
    }

    public String getDebitAccount() {
        return mDebitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        mDebitAccount = debitAccount;
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

//    public Long getGlAccRefId() {
//        return mGlAccRefId;
//    }
//
//    public void setGlAccRefId(Long glAccRefId) {
//        mGlAccRefId = glAccRefId;
//    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public List<Long> getQuantities() {
        return mQuantities;
    }

    public void setQuantities(List<Long> quantities) {
        mQuantities = quantities;
    }

    public Long getReceiver() {
        return mReceiver;
    }

    public void setReceiver(Long receiver) {
        mReceiver = receiver;
    }

    public Long getRefId() {
        return mRefId;
    }

    public void setRefId(Long refId) {
        mRefId = refId;
    }

    public Long getSender() {
        return mSender;
    }

    public void setSender(Long sender) {
        mSender = sender;
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

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

//    public Long getTransLockRefId() {
//        return mTransLockRefId;
//    }
//
//    public void setTransLockRefId(Long transLockRefId) {
//        mTransLockRefId = transLockRefId;
//    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public List<Long> getValues() {
        return mValues;
    }

    public void setValues(List<Long> values) {
        mValues = values;
    }

}
