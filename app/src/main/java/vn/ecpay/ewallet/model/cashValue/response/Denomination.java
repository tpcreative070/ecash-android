
package vn.ecpay.ewallet.model.cashValue.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Denomination {

    @SerializedName("denominationId")
    private Long mDenominationId;
    @SerializedName("denominationName")
    private String mDenominationName;
    @SerializedName("issuerId")
    private Long mIssuerId;
    @SerializedName("value")
    private int mValue;

    public Long getDenominationId() {
        return mDenominationId;
    }

    public void setDenominationId(Long denominationId) {
        mDenominationId = denominationId;
    }

    public String getDenominationName() {
        return mDenominationName;
    }

    public void setDenominationName(String denominationName) {
        mDenominationName = denominationName;
    }

    public Long getIssuerId() {
        return mIssuerId;
    }

    public void setIssuerId(Long issuerId) {
        mIssuerId = issuerId;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }

}
