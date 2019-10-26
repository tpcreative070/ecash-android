
package vn.ecpay.ewallet.model.cashChange.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseCashChangeData {

    @SerializedName("cashEnc")
    private String mCashEnc;
    @SerializedName("id")
    private String mId;
    @SerializedName("receiver")
    private Long mReceiver;
    @SerializedName("sender")
    private String mSender;
    @SerializedName("time")
    private Long mTime;
    @SerializedName("type")
    private String mType;

    public String getCashEnc() {
        return mCashEnc;
    }

    public void setCashEnc(String cashEnc) {
        mCashEnc = cashEnc;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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

    public Long getTime() {
        return mTime;
    }

    public void setTime(Long time) {
        mTime = time;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
