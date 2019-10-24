
package vn.ecpay.ewallet.model.account.checkIDNumberAccount;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestCheckIDNumberAccount extends BaseObject {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("idNumber")
    private String mIdNumber;
    @SerializedName("personMobilePhone")
    private String mPersonMobilePhone;

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

    public String getmPersonMobilePhone() {
        return mPersonMobilePhone;
    }

    public void setmPersonMobilePhone(String mPersonMobilePhone) {
        this.mPersonMobilePhone = mPersonMobilePhone;
    }
}
