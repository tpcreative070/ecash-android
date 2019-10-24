
package vn.ecpay.ewallet.model.cash.edongToEcash;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestEdongToECash extends BaseObject {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("creditAccount")
    private String mCreditAccount;
    @SerializedName("debitAccount")
    private String mDebitAccount;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("quantities")
    private List<Integer> mQuantities;
    @SerializedName("receiver")
    private String mReceiver;
    @SerializedName("sender")
    private String mSender;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("token")
    private String mToken;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("values")
    private List<Integer> mValues;

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
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

    public List<Integer> getQuantities() {
        return mQuantities;
    }

    public void setQuantities(List<Integer> quantities) {
        mQuantities = quantities;
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

}
