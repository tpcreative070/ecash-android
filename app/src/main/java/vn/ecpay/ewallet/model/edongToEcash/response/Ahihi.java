
package vn.ecpay.ewallet.model.edongToEcash.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Ahihi {

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

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
    }

    public Long getAmountValid() {
        return mAmountValid;
    }

    public void setAmountValid(Long amountValid) {
        mAmountValid = amountValid;
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

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getInvalidSerials() {
        return mInvalidSerials;
    }

    public void setInvalidSerials(String invalidSerials) {
        mInvalidSerials = invalidSerials;
    }

    public Long getReceiver() {
        return mReceiver;
    }

    public void setReceiver(Long receiver) {
        mReceiver = receiver;
    }

    public Long getSender() {
        return mSender;
    }

    public void setSender(Long sender) {
        mSender = sender;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
