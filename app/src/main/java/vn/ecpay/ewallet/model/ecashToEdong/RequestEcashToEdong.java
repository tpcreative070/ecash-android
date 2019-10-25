
package vn.ecpay.ewallet.model.ecashToEdong;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestEcashToEdong extends BaseObject {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("cashEnc")
    private String mCashEnc;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("content")
    private String mContent;
    @SerializedName("creditAccount")
    private String mCreditAccount;
    @SerializedName("debitAccount")
    private String mDebitAccount;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("id")
    private String mId;
    @SerializedName("receiver")
    private String mReceiver;
    @SerializedName("sender")
    private String mSender;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("time")
    private String mTime;
    @SerializedName("token")
    private String mToken;
    @SerializedName("type")
    private String mType;
    @SerializedName("username")
    private String mUsername;

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
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

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getCreditAccount() {
        return mCreditAccount;
    }

    public void setCreditAccount(String creditAccount) {
        mCreditAccount = creditAccount;
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

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getReceiver() {
        return mReceiver;
    }

    public void setReceiver(String receiver) {
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

}
